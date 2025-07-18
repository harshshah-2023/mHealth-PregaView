package com.example.myapplication.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val userId = "default_user"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference

        // Set static user info
        binding.userName.text = "Aditi Sharma"


        // Navigate to Postpartum screen
        binding.btnCheckPostpartum.setOnClickListener {
            findNavController().navigate(R.id.action_topospatrm)
        }

        // Save pregnancy date on click
        binding.btnSavePregnancyDate.setOnClickListener {
            val dateInput = binding.startDateInput.text.toString()

            if (dateInput.isEmpty()) {
                binding.startDateInput.error = "Enter date (e.g. MM/dd/yyyy)"
                return@setOnClickListener
            }

            try {
                val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                val startDate = sdf.parse(dateInput)
                val calendar = Calendar.getInstance()
                calendar.time = startDate
                calendar.add(Calendar.DAY_OF_YEAR, 280)
                val dueDateStr = sdf.format(calendar.time)

                val dataMap = mapOf(
                    "pregnancyStartDate" to dateInput,
                    "dueDate" to dueDateStr
                )
                database.child("users").child(userId).updateChildren(dataMap)

                database.child("users").child(userId).child("healthData").updateChildren(dataMap)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Date saved successfully", Toast.LENGTH_SHORT).show()
                        binding.pregnancyDueDate.text = "Due Date: $dueDateStr"
                        loadPregnancyProgress(dateInput)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to save: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                binding.startDateInput.error = "Invalid date format"
            }
        }

        // Handle "Born" selection
        binding.radioGroupBorn.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioBornYes -> {
                    binding.btnCheckPostpartum.visibility = View.VISIBLE
                    database.child("users").child(userId).child("healthData").child("babyBorn").setValue(true)
                }
                R.id.radioBornNo -> {
                    binding.btnCheckPostpartum.visibility = View.GONE
                    database.child("users").child(userId).child("healthData").child("babyBorn").setValue(false)
                }
            }
        }

        // Load pregnancyStartDate and update UI
        database.child("users").child(userId).child("pregnancyStartDate").get()
            .addOnSuccessListener {
                val dateStr = it.getValue(String::class.java)
                if (!dateStr.isNullOrEmpty()) {
                    binding.startDateInput.setText(dateStr)
                    loadPregnancyProgress(dateStr)
                }
            }

        // Load due date
        database.child("users").child(userId).child("dueDate").get()
            .addOnSuccessListener {
                val dueStr = it.getValue(String::class.java)
                if (!dueStr.isNullOrEmpty()) {
                    binding.pregnancyDueDate.text = "Due Date: $dueStr"
                }
            }
    }

    private fun loadPregnancyProgress(startDateStr: String) {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val startDate = sdf.parse(startDateStr)
            val today = Date()
            val diff = today.time - startDate.time
            val weeks = (diff / (1000 * 60 * 60 * 24 * 7)).toInt()

            binding.weeksText.text = "$weeks weeks"
            binding.pregnancyProgress.max = 40
            binding.pregnancyProgress.progress = weeks.coerceAtMost(40)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
