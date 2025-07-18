package com.example.myapplication.ui.healthdatainput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHealthDataInputBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class HealthDataInputFragment : Fragment() {
    private var _binding: FragmentHealthDataInputBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthDataInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Database
        database = Firebase.database.reference

        binding.btnSubmitHealthData.setOnClickListener {
            saveHealthData()
        }
    }

    private fun saveHealthData() {
        val userId = "default_user" // Or use device ID for multiple users

        // Get input values with null checks
        val heartRateInput = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.heartRateInput)
        val systolicInput = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.systolicInput)
        val diastolicInput = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.diastolicInput)
        val glucoseInput = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.glucoseInput)
        val weightInput = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.weightInput)

        val heartRate = heartRateInput.text?.toString()?.takeIf { it.isNotEmpty() } ?: run {
            heartRateInput.error = "Please enter heart rate"
            return
        }

        val systolic = systolicInput.text?.toString()?.takeIf { it.isNotEmpty() } ?: run {
            systolicInput.error = "Please enter systolic value"
            return
        }

        val diastolic = diastolicInput.text?.toString()?.takeIf { it.isNotEmpty() } ?: run {
            diastolicInput.error = "Please enter diastolic value"
            return
        }

        val glucose = glucoseInput.text?.toString()?.takeIf { it.isNotEmpty() } ?: run {
            glucoseInput.error = "Please enter glucose level"
            return
        }

        val weight = weightInput.text?.toString()?.takeIf { it.isNotEmpty() } ?: run {
            weightInput.error = "Please enter weight"
            return
        }

        // Create health data map
        val healthData = mapOf(
            "heartRate" to heartRate,
            "bloodPressure" to "$systolic/$diastolic",
            "glucoseLevel" to "$glucose mg/dL",
            "weight" to "$weight kg",
            "lastUpdated" to SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date()),
            "lastCheck" to SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        )

        // Save to Firebase with better error logging
        val timestamp = System.currentTimeMillis().toString()
        database.child("users").child(userId).child("healthData").child(timestamp).setValue(healthData)

            .addOnSuccessListener {
                Toast.makeText(context, "Health data saved successfully", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace() // Check Logcat for detailed error
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}