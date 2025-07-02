package com.example.useryoumat.data.model

data class AppointmentModel(
    val appointmentId: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val counselorId: String = "",   // For now hardcoded or selected later
    val issue: String = "",
    val dateTime: String = "",
    val status: String = "pending"  // can be 'pending', 'accepted', 'rejected'
)
