package edu.pe.cibertec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import edu.pe.cibertec.utils.rememberLocationPermissionState
import edu.pe.cibertec.viewmodel.LocationViewModel

@Composable
fun MapScreen(
    viewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current

    // Ubicación por defecto (Lima, Perú)
    val defaultLocation = LatLng(-12.046374, -77.042793)

    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationState by viewModel.locationState.collectAsState()

    val permissionState = rememberLocationPermissionState { granted ->
        hasLocationPermission = granted
        if (granted) {
            viewModel.getCurrentLocation(context)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    // Mover cámara cuando se obtenga ubicación
    LaunchedEffect(locationState.currentLocation) {
        locationState.currentLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = hasLocationPermission
            )
        ) {
            // Marker en ubicación actual
            locationState.currentLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Mi ubicación"
                )
            }
        }

        // Botón flotante para obtener ubicación
        FloatingActionButton(
            onClick = {
                if (!permissionState.hasPermission) {
                    permissionState.requestPermission()
                } else {
                    viewModel.getCurrentLocation(context)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            if (locationState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Text("GPS")
            }
        }


        if (!permissionState.hasPermission) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Presiona el botón GPS para activar la ubicación",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        locationState.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(error)
            }
        }
    }
}