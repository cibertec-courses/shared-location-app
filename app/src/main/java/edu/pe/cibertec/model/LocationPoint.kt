package edu.pe.cibertec.model

import com.google.firebase.Timestamp

data class LocationPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now()
)