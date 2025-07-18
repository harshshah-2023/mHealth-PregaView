package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.HorizontalDividerItemDecoration
import com.example.myapplication.R
import com.example.myapplication.data.Appointment
import com.example.myapplication.data.AppointmentRepository
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.example.myapplication.ui.adapters.AppointmentAdapter
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var appointmentAdapter: AppointmentAdapter
    private val repository = AppointmentRepository()
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.seeAllText.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_schedule)
        }

        view.post {
            val newWidth = (binding.root.width * 0.89f).toInt()
            binding.pregnancyProgressBar.layoutParams.width = newWidth
            binding.pregnancyProgressBar.requestLayout()
            binding.progressText.text = "89%"
        }

        setupAppointmentsRecycler()
        setupProgressBar()
        loadAppointments()
        loadPregnancyProgress()
        loadHealthData() // Add this line to load health data
    }

    private fun loadHealthData() {
        val userId = "default_user"
        val ref = database.reference.child("users").child(userId).child("healthData")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var latestTimestamp: Long = 0
                var latestEntry: DataSnapshot? = null

                for (child in snapshot.children) {
                    val key = child.key?.toLongOrNull() ?: continue
                    if (key > latestTimestamp) {
                        latestTimestamp = key
                        latestEntry = child
                    }
                }

                latestEntry?.let { data ->
                    val heartRateStr = data.child("heartRate").getValue(String::class.java) ?: "72"
                    val bloodPressureStr = data.child("bloodPressure").getValue(String::class.java) ?: "122/80"
                    val glucoseLevelStr = data.child("glucoseLevel").getValue(String::class.java) ?: "96 mg/dL"
                    val lastUpdatedStr = data.child("lastUpdated").getValue(String::class.java) ?: "Unknown"

                    // Update UI with the latest health data
                    binding.heartRateValue.text = heartRateStr
                    binding.bloodPressureValue.text = bloodPressureStr
                    binding.glucoseValue.text = glucoseLevelStr
                    binding.lastUpdatedText.text = "Last updated: $lastUpdatedStr"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    // Rest of the existing code remains unchanged...
    private fun setupProgressBar() {
        binding.root.post {
            val rootWidth = binding.root.width
            if (rootWidth > 0) {
                val newWidth = (rootWidth * 0.89f).toInt()
                binding.pregnancyProgressBar.layoutParams.width = newWidth
                binding.pregnancyProgressBar.requestLayout()
                binding.progressText.text = "89%"
            } else {
                binding.pregnancyProgressBar.layoutParams.width = (resources.displayMetrics.widthPixels * 0.89f).toInt()
                binding.pregnancyProgressBar.requestLayout()
                binding.progressText.text = "89%"
            }
        }
    }

    private fun setupAppointmentsRecycler() {
        appointmentAdapter = AppointmentAdapter()
        binding.appointmentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = appointmentAdapter
            addItemDecoration(
                HorizontalDividerItemDecoration(
                    requireContext(),
                    resources.getDimensionPixelSize(R.dimen.item_margin)
                )
            )
        }
    }

    private fun showEmptyState() {
        binding.emptyView.visibility = View.VISIBLE
        binding.appointmentsRecyclerView.visibility = View.GONE
    }

    private fun showAppointments(appointments: List<Appointment>) {
        binding.emptyView.visibility = View.GONE
        binding.appointmentsRecyclerView.visibility = View.VISIBLE
        appointmentAdapter.submitList(appointments)
    }

    private fun loadAppointments() {
        lifecycleScope.launch {
            try {
                repository.getAppointmentsFlow().collect { appointments ->
                    if (appointments.isEmpty()) {
                        showEmptyState()
                    } else {
                        showAppointments(appointments)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading appointments", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPregnancyProgress() {
        val userId = "default_user"
        val dbRef = Firebase.database.reference.child("users").child(userId)

        dbRef.child("pregnancyStartDate").get().addOnSuccessListener {
            val startDateStr = it.getValue(String::class.java)

            if (startDateStr.isNullOrEmpty()) {
                binding.weeksText.text = "Start date not set"
                return@addOnSuccessListener
            }

            try {
                val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                val startDate = sdf.parse(startDateStr)
                val today = Date()

                val diffInMillis = today.time - startDate.time
                val weeks = (diffInMillis / (1000 * 60 * 60 * 24 * 7)).toInt()
                val days = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

                binding.pregnancyProgressBar.max = 40
                binding.pregnancyProgressBar.progress = weeks.coerceAtMost(40)

                val trimester = when {
                    weeks <= 13 -> "First Trimester"
                    weeks <= 26 -> "Second Trimester"
                    else -> "Third Trimester"
                }

                binding.trimesterText.text = "Current: $trimester"
                binding.weeksText.text = "$weeks weeks / $days days"

            } catch (e: Exception) {
                binding.weeksText.text = "Invalid date format"
            }
        }.addOnFailureListener {
            binding.weeksText.text = "Failed to load progress"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}