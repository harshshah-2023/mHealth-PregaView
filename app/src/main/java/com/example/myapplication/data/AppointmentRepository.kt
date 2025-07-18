package com.example.myapplication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AppointmentRepository {
    private val database = Firebase.database
    private val appointmentsRef = database.getReference("default_user/appointments")

    suspend fun addAppointment(appointment: Appointment): String {
        val newRef = appointmentsRef.push()
        val appointmentWithId = appointment.copy(
            id = newRef.key ?: "",
            createdAt = System.currentTimeMillis()
        )
        newRef.setValue(appointmentWithId).await()
        return newRef.key ?: ""
    }

    fun getAppointmentsFlow(): Flow<List<Appointment>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointments = snapshot.children.mapNotNull {
                    it.getValue<Appointment>()?.copy(id = it.key ?: "")
                }.sortedByDescending { it.createdAt }
                trySend(appointments)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        appointmentsRef.addValueEventListener(listener)
        awaitClose { appointmentsRef.removeEventListener(listener) }
    }
}