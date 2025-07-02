package com.example.useryoumat.data.repository
import com.example.useryoumat.data.model.AppointmentModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AppointmentRepository {

    private val db = FirebaseFirestore.getInstance()

    fun bookAppointment(
        appointment: AppointmentModel,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val docId = UUID.randomUUID().toString()
        val newAppointment = appointment.copy(appointmentId = docId)

        db.collection("appointments")
            .document(docId)
            .set(newAppointment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { ex -> onFailure(ex.message ?: "Error occurred") }
    }
}
