package edu.pe.cibertec.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.pe.cibertec.model.LocationPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LocationState(
    val currentLocation: LatLng? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class LocationViewModel : ViewModel() {
    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        viewModelScope.launch {
            try {
                _locationState.value = _locationState.value.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null
                )

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    _locationState.value = _locationState.value.copy(
                        currentLocation = latLng,
                        isLoading = false
                    )
                } else {
                    _locationState.value = _locationState.value.copy(
                        isLoading = false,
                        error = "No se pudo obtener la ubicación"
                    )
                }
            } catch (e: Exception) {
                _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al obtener ubicación"
                )
            }
        }
    }

    fun saveLocationToFirestore() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val currentLoc = _locationState.value.currentLocation ?: return@launch

                _locationState.value = _locationState.value.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null
                )

                val locationPoint = LocationPoint(
                    latitude = currentLoc.latitude,
                    longitude = currentLoc.longitude
                )

                firestore.collection("locations")
                    .document(userId)
                    .collection("points")
                    .add(locationPoint)
                    .await()

                _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    successMessage = "Ubicación guardada correctamente"
                )
            } catch (e: Exception) {
                _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al guardar ubicación"
                )
            }
        }
    }
}