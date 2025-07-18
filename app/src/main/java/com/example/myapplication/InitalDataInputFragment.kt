package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentInitalDataInputBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InitalDataInputFragment : Fragment() {

    private var _binding: FragmentInitalDataInputBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInitalDataInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference

        binding.btnSavePregnancyDate.setOnClickListener {
            val dateInput = binding.startDateInput.text.toString()
            val userId = "default_user"

            if (dateInput.isEmpty()) {
                binding.startDateInput.error = "Enter date (e.g. MM/dd/yyyy)"
                return@setOnClickListener
            }

            database.child("users").child(userId).child("pregnancyStartDate").setValue(dateInput)
                .addOnSuccessListener {
                    Toast.makeText(context, "Date saved successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
