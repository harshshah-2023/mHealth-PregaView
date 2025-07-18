package com.example.myapplication.ui.appointments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.data.Appointment
import com.example.myapplication.data.AppointmentRepository
import com.example.myapplication.databinding.FragmentScheduleAppointmentBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleAppointmentFragment : Fragment() {
    private var _binding: FragmentScheduleAppointmentBinding? = null
    private val binding get() = _binding!!
    private val repository = AppointmentRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDateTimePickers()  // Initialize date/time pickers

        binding.submitButton.setOnClickListener {
            scheduleAppointment()
        }
    }

    private fun scheduleAppointment() {
        val doctor = binding.doctorInput.text.toString()

        val date = binding.dateInput.text.toString()
        val time = binding.timeInput.text.toString()
        val reason = binding.reasonInput.text.toString()

        if (doctor.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val appointment = Appointment(
            doctorName = doctor,
            date = date,
            time = time,
            type = if (binding.typeClinic.isChecked) "Clinic" else "Video",
            notes = reason
        )

        lifecycleScope.launch {
            try {
                repository.addAppointment(appointment)
                Toast.makeText(context, "Appointment scheduled!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupDateTimePickers() {
        binding.dateInput.setOnClickListener { showDatePicker() }
        binding.timeInput.setOnClickListener { showTimePicker() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                binding.dateInput.setText("${month+1}/${day}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = if (hour > 12) hour - 12 else hour
                binding.timeInput.setText("$displayHour:${minute.toString().padStart(2, '0')} $amPm")
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}