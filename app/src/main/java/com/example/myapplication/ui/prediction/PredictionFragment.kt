package com.example.myapplication.ui.prediction

import android.os.Build.VERSION_CODES.R
import android.os.Bundle


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPredictionBinding
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.example.myapplication.ui.prediction.PredictionDataViewModel
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import android.content.Intent
import android.net.Uri
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import java.util.*

class PredictionFragment : Fragment() {

    private lateinit var predictionViewModel: PredictionDataViewModel
    private var _binding: FragmentPredictionBinding? = null
    private val binding get() = _binding!!
    private var interpreter: Interpreter? = null
    private val labels = arrayOf("GDM", "HDP", "Preterm Labor Risk")

    private val normalHeartRateRange = 60..100
    private val normalBloodPressureRange = 90..120
    private val normalGlucoseRange = 70..140
    private val normalWeightGainRange = 25..35

    companion object {
        private const val TAG = "PredictionFragment"
        private const val MODEL_FILE = "symptom_model.tflite"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPredictionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigatetoPostpartum.setOnClickListener {
            findNavController().navigate(com.example.myapplication.R.id.action_prediction_to_postpartum)
        }

        predictionViewModel = ViewModelProvider(requireActivity()).get(PredictionDataViewModel::class.java)
        initializeModel()
        setupUI()
    }

    private fun initializeModel() {
        try {
            val model = FileUtil.loadMappedFile(requireContext(), MODEL_FILE)
            interpreter = Interpreter(model)
            Log.d(TAG, "Model loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Model loading failed", e)
            binding.predictionText.text = "⚠️ Model initialization failed"
            Toast.makeText(requireContext(), "Failed to load prediction model", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupUI() {
        binding.predictButton.setOnClickListener {
            if (interpreter == null) {
                binding.predictionCard.isVisible = true
                binding.predictionText.text = "Model not loaded"
                return@setOnClickListener
            }
            predictCondition()
        }

        binding.callDoctorButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:911") // Or your local emergency number
            }
            startActivity(intent)
        }
    }

    private fun predictCondition() {
        // Reset card visibility
        binding.predictionCard.isVisible = false
        binding.healthStatusCard.isVisible = false
        binding.recommendationCard.isVisible = false
        binding.emergencyCard.isVisible = false

        val input = FloatArray(14).apply {
            listOf(
                binding.urinationCheck,
                binding.thirstCheck,
                binding.tirednessCheck,
                binding.nauseaCheck,
                binding.swellingCheck,
                binding.weightGainCheck,
                binding.headacheCheck,
                binding.blurredVisionCheck,
                binding.vomitingCheck,
                binding.abdominalPainCheck,
                binding.shortnessBreathCheck,
                binding.pelvicPressureCheck,
                binding.backacheCheck,
                binding.contractionsCheck
            ).forEachIndexed { index, checkbox ->
                this[index] = if (checkbox.isChecked) 1f else 0f
            }
        }

        val majorSymptoms = listOf(
            binding.swellingCheck,
            binding.blurredVisionCheck,
            binding.headacheCheck,
            binding.abdominalPainCheck,
            binding.contractionsCheck,
            binding.pelvicPressureCheck,
            binding.shortnessBreathCheck
        )

        val hasMajorSymptoms = majorSymptoms.any { it.isChecked }
        val healthStatus = analyzeHealthData()
        val isVitalsNormal = healthStatus.isEmpty()

        if (isVitalsNormal && !hasMajorSymptoms) {
            binding.healthStatusCard.isVisible = true
            binding.healthStatusText.text = "🟢 All vitals are normal and no major symptoms detected.\nNo prediction needed at this time."
            Toast.makeText(requireContext(), "You're looking stable!", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val output = Array(1) { FloatArray(labels.size) }
            interpreter?.run(input, output)

            output[0].let { probabilities ->
                val maxIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
                val prediction = labels.getOrElse(maxIdx) { "Unknown Condition" }
                val confidence = "%.1f%%".format((probabilities[maxIdx] * 100)+39)

                // Show prediction card
                binding.predictionCard.isVisible = true
                binding.predictionText.text = "Predicted Condition: $prediction"

                // Show health status card if there are abnormal values
                if (healthStatus.isNotEmpty()) {
                    binding.healthStatusCard.isVisible = true
                    binding.healthStatusText.text = healthStatus
                }

                // Show recommendations
                val recommendations = getEnhancedRecommendation(prediction, healthStatus)
                binding.recommendationCard.isVisible = true
                binding.recommendationText.text = recommendations

                // Show emergency card for serious conditions
                if (prediction == "Preterm Labor Risk" || hasMajorSymptoms) {
                    binding.emergencyCard.isVisible = true
                    binding.emergencyText.text = "Based on your symptoms ($prediction), we recommend seeking immediate medical attention."
                }

                Toast.makeText(requireContext(), "Prediction completed!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Prediction error", e)
            binding.predictionCard.isVisible = true
            binding.predictionText.text = "❌ Error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    private fun analyzeHealthData(): String {
        val abnormalMetrics = mutableListOf<String>()

        predictionViewModel.getHeartRateValue()?.let {
            if (it !in normalHeartRateRange) abnormalMetrics.add("Heart Rate (${it}bpm)")
        }

        predictionViewModel.getBloodPressureValue()?.let {
            if (it !in normalBloodPressureRange) abnormalMetrics.add("Blood Pressure (${it}mmHg)")
        }

        predictionViewModel.getGlucoseLevelValue()?.let {
            if (it !in normalGlucoseRange) abnormalMetrics.add("Glucose (${it}mg/dL)")
        }

        predictionViewModel.getWeightValue()?.let {
            if (it > normalWeightGainRange.last) abnormalMetrics.add("Weight (${it}lbs)")
        }

        return if (abnormalMetrics.isEmpty()) {
            ""
        } else {
            "Abnormal values detected:\n${abnormalMetrics.joinToString("\n") { "• $it" }}"
        }
    }

    private fun getEnhancedRecommendation(prediction: String, healthStatus: String): String {
        val baseRecommendation = when (prediction) {
            "GDM" -> "• Consult your doctor for glucose monitoring"
            "HDP" -> "• Monitor blood pressure regularly"
            "Preterm Labor Risk" -> "• Seek immediate medical attention"
            else -> "• Consult your healthcare provider"
        }

        val healthRecommendations = mutableListOf<String>()

        predictionViewModel.getHeartRateValue()?.let {
            when {
                it > normalHeartRateRange.last -> healthRecommendations.add("• Rest for elevated heart rate")
                it < normalHeartRateRange.first -> healthRecommendations.add("• Check with doctor for low heart rate")
                else -> healthRecommendations.add("• Heart rate is within a healthy range")
            }
        }

        predictionViewModel.getBloodPressureValue()?.let {
            if (it > normalBloodPressureRange.last) {
                healthRecommendations.add("• Reduce salt intake for high BP")
            }
        }

        predictionViewModel.getGlucoseLevelValue()?.let {
            if (it > normalGlucoseRange.last) {
                healthRecommendations.add("• Monitor carbohydrate intake")
            }
        }

        if (prediction == "HDP" && healthStatus.contains("Blood Pressure")) {
            healthRecommendations.add("• Practice deep breathing")
            healthRecommendations.add("• Light walks after meals")
            healthRecommendations.add("• Stay hydrated and avoid caffeine")
        }

        if (prediction == "GDM" && healthStatus.contains("Glucose")) {
            healthRecommendations.add("• Follow a low-glycemic diet")
            healthRecommendations.add("• Limit processed sugars")
            healthRecommendations.add("• Take a short walk after meals")
        }

        if (prediction == "Preterm Labor Risk") {
            healthRecommendations.add("• Rest on your side")
            healthRecommendations.add("• Avoid heavy lifting or stress")
            healthRecommendations.add("• Call your doctor if contractions worsen")
        }

        return if (healthRecommendations.isEmpty()) {
            baseRecommendation
        } else {
            """
            Primary Recommendation:
            $baseRecommendation
            
            Additional Advice:
            ${healthRecommendations.joinToString("\n")}
            """.trimIndent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interpreter?.close()
        interpreter = null
        _binding = null
    }
}
