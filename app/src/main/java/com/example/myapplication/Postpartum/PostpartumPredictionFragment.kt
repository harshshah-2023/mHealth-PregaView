package com.example.myapplication.Postpartum

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.myapplication.R
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PostpartumPredictionFragment : Fragment() {

    private lateinit var ppdInterpreter: Interpreter
    private lateinit var infInterpreter: Interpreter
    private lateinit var fatigueInterpreter: Interpreter

    private val yesNoOptions = arrayOf("Yes", "No")
    private val deliveryOptions = arrayOf("Normal", "C-section")
    private val supportOptions = arrayOf("Low", "Medium", "High")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_postpartum_prediction, container, false)

        // Input fields
        val ageInput = view.findViewById<EditText>(R.id.ageInput)
        val sleepInput = view.findViewById<EditText>(R.id.sleepInput)
        val deliverySpinner = view.findViewById<Spinner>(R.id.deliverySpinner)
        val moodSpinner = view.findViewById<Spinner>(R.id.moodSpinner)
        val fatigueSpinner = view.findViewById<Spinner>(R.id.fatigueSpinner)
        val supportSpinner = view.findViewById<Spinner>(R.id.supportSpinner)
        val mentalSpinner = view.findViewById<Spinner>(R.id.mentalSpinner)
        val feverSpinner = view.findViewById<Spinner>(R.id.feverSpinner)
        val dischargeSpinner = view.findViewById<Spinner>(R.id.dischargeSpinner)
        val predictBtn = view.findViewById<Button>(R.id.predictBtn)

        // Results views
        val resultsCard = view.findViewById<CardView>(R.id.resultsCard)
        val ppdResultText = view.findViewById<TextView>(R.id.ppdResultText)
        val infectionResultText = view.findViewById<TextView>(R.id.infectionResultText)
        val fatigueResultText = view.findViewById<TextView>(R.id.fatigueResultText)

        // Set up spinners
        setupSpinner(deliverySpinner, deliveryOptions)
        setupSpinner(moodSpinner, yesNoOptions)
        setupSpinner(fatigueSpinner, yesNoOptions)
        setupSpinner(supportSpinner, supportOptions)
        setupSpinner(mentalSpinner, yesNoOptions)
        setupSpinner(feverSpinner, yesNoOptions)
        setupSpinner(dischargeSpinner, yesNoOptions)

        // Load models
        try {
            ppdInterpreter = Interpreter(loadModelFile(requireContext(), "ppd_model.tflite"))
            infInterpreter = Interpreter(loadModelFile(requireContext(), "infection_model.tflite"))
            fatigueInterpreter = Interpreter(loadModelFile(requireContext(), "fatigue_model.tflite"))
        } catch (e: Exception) {
            showError("Error loading prediction models")
        }

        predictBtn.setOnClickListener {
            try {
                // Validate inputs
                if (ageInput.text.isNullOrEmpty() || sleepInput.text.isNullOrEmpty()) {
                    showError("Please fill all fields")
                    return@setOnClickListener
                }

                // Prepare input data
                val inputData = floatArrayOf(
                    ageInput.text.toString().toFloat(),
                    if (deliverySpinner.selectedItem.toString() == "C-section") 1f else 0f,
                    sleepInput.text.toString().toFloat(),
                    if (moodSpinner.selectedItem.toString() == "Yes") 1f else 0f,
                    if (fatigueSpinner.selectedItem.toString() == "Yes") 1f else 0f,
                    when (supportSpinner.selectedItem.toString()) {
                        "Low" -> 0f
                        "Medium" -> 1f
                        else -> 2f
                    },
                    if (mentalSpinner.selectedItem.toString() == "Yes") 1f else 0f,
                    if (feverSpinner.selectedItem.toString() == "Yes") 1f else 0f,
                    if (dischargeSpinner.selectedItem.toString() == "Yes") 1f else 0f
                )

                // Run predictions
                val outputPPD = Array(1) { FloatArray(1) }
                val outputINF = Array(1) { FloatArray(1) }
                val outputFAT = Array(1) { FloatArray(1) }

                ppdInterpreter.run(inputData, outputPPD)
                infInterpreter.run(inputData, outputINF)
                fatigueInterpreter.run(inputData, outputFAT)

                // Display results
                val ppdRisk = outputPPD[0][0]
                val ppdText = when {
                    ppdRisk > 0.75 -> "Postpartum Depression: High Risk (${"%.1f".format(ppdRisk * 100)}%)"
                    ppdRisk > 0.5 -> "Postpartum Depression: Moderate Risk (${"%.1f".format(ppdRisk * 100)}%)"
                    else -> "Postpartum Depression: Low Risk (${"%.1f".format(ppdRisk * 100)}%)"
                }

                val infectionRisk = outputINF[0][0]
                val infectionText = if (infectionRisk > 0.5) {
                    "Infection: Detected (${"%.1f".format(infectionRisk * 100)}% confidence)"
                } else {
                    "Infection: Not Detected"
                }

                val fatigueRisk = outputFAT[0][0]
                val fatigueText = when {
                    fatigueRisk > 0.75 -> "Fatigue Level: Severe (${"%.1f".format(fatigueRisk * 100)}%)"
                    fatigueRisk > 0.5 -> "Fatigue Level: Moderate (${"%.1f".format(fatigueRisk * 100)}%)"
                    else -> "Fatigue Level: Normal"
                }

                // Update UI
                ppdResultText.text = ppdText
                infectionResultText.text = infectionText
                fatigueResultText.text = fatigueText
                resultsCard.visibility = View.VISIBLE

                // Scroll to results
                (view.parent as? ScrollView)?.post {
                    (view.parent as ScrollView).smoothScrollTo(0, resultsCard.bottom)
                }

            } catch (e: Exception) {
                showError("Prediction error: ${e.message}")
            }
        }

        return view
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        ppdInterpreter.close()
        infInterpreter.close()
        fatigueInterpreter.close()
    }
}