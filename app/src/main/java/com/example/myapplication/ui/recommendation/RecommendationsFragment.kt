package com.example.myapplication.ui.recommendation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRecommendationsBinding
import com.example.myapplication.ui.prediction.PredictionDataViewModel

class RecommendationsFragment : Fragment() {
    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var predictionViewModel: PredictionDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        predictionViewModel = ViewModelProvider(requireActivity()).get(PredictionDataViewModel::class.java)
        setupRecommendations()
    }

    private fun setupRecommendations() {
        // Get all health data
        val heartRate = predictionViewModel.getHeartRateValue()
        val bloodPressure = predictionViewModel.getBloodPressureValue()
        val glucoseLevel = predictionViewModel.getGlucoseLevelValue()
        val weight = predictionViewModel.getWeightValue()

        // Generate recommendations based on vitals
        val vitalRecommendations = generateVitalRecommendations(heartRate, bloodPressure, glucoseLevel, weight)

        // Display recommendations
        binding.vitalRecommendations.text = vitalRecommendations

        // You can add more sections here for symptom-based recommendations
    }

    private fun generateVitalRecommendations(
        heartRate: Int?,
        bloodPressure: Int?,
        glucoseLevel: Int?,
        weight: Int?
    ): String {
        val recommendations = StringBuilder()

        // Heart Rate recommendations
        heartRate?.let { hr ->
            recommendations.append("### Heart Rate (${hr}bpm) ###\n\n")
            when {
                hr < 60 -> {
                    recommendations.append("🚩 Low Heart Rate (Bradycardia)\n\n")
                    recommendations.append("DOs:\n")
                    recommendations.append("• Stay hydrated\n")
                    recommendations.append("• Practice deep breathing exercises\n")
                    recommendations.append("• Get adequate sleep\n")
                    recommendations.append("• Monitor for dizziness or fainting\n\n")
                    recommendations.append("DON'Ts:\n")
                    recommendations.append("• Don't consume excessive caffeine\n")
                    recommendations.append("• Don't stand up too quickly\n")
                    recommendations.append("• Avoid strenuous exercise without medical clearance\n\n")
                    recommendations.append("Recommended Activities:\n")
                    recommendations.append("• Gentle yoga\n")
                    recommendations.append("• Short, slow walks\n")
                    recommendations.append("• Meditation for stress reduction\n\n")
                }
                hr > 100 -> {
                    recommendations.append("🚩 High Heart Rate (Tachycardia)\n\n")
                    recommendations.append("DOs:\n")
                    recommendations.append("• Practice relaxation techniques\n")
                    recommendations.append("• Stay hydrated\n")
                    recommendations.append("• Monitor your pulse regularly\n")
                    recommendations.append("• Keep a symptom diary\n\n")
                    recommendations.append("DON'Ts:\n")
                    recommendations.append("• Don't consume stimulants (caffeine, nicotine)\n")
                    recommendations.append("• Avoid excessive heat\n")
                    recommendations.append("• Don't ignore persistent rapid heart rate\n\n")
                    recommendations.append("Recommended Activities:\n")
                    recommendations.append("• Deep breathing exercises\n")
                    recommendations.append("• Moderate-paced walking\n")
                    recommendations.append("• Aquatic exercises (if approved by doctor)\n\n")
                }
                else -> {
                    recommendations.append("✅ Normal Heart Rate Range\n\n")
                    recommendations.append("Maintenance Tips:\n")
                    recommendations.append("• Continue regular moderate exercise\n")
                    recommendations.append("• Stay hydrated\n")
                    recommendations.append("• Practice stress management\n\n")
                }
            }
        }

        // Blood Pressure recommendations
        bloodPressure?.let { bp ->
            recommendations.append("\n### Blood Pressure (${bp}mmHg) ###\n\n")
            when {
                bp < 90 -> {
                    recommendations.append("🚩 Low Blood Pressure (Hypotension)\n\n")
                    recommendations.append("DOs:\n")
                    recommendations.append("• Increase fluid and salt intake (if advised by doctor)\n")
                    recommendations.append("• Rise slowly from sitting/lying positions\n")
                    recommendations.append("• Wear compression stockings if recommended\n")
                    recommendations.append("• Eat small, frequent meals\n\n")
                    recommendations.append("DON'Ts:\n")
                    recommendations.append("• Don't stand for long periods\n")
                    recommendations.append("• Avoid hot showers/baths\n")
                    recommendations.append("• Don't skip meals\n\n")
                    recommendations.append("Recommended Activities:\n")
                    recommendations.append("• Gentle leg exercises while seated\n")
                    recommendations.append("• Swimming (with supervision)\n")
                    recommendations.append("• Chair yoga\n\n")
                }
                bp > 120 -> {
                    recommendations.append("🚩 High Blood Pressure (Hypertension)\n\n")
                    recommendations.append("DOs:\n")
                    recommendations.append("• Reduce sodium intake\n")
                    recommendations.append("• Increase potassium-rich foods (bananas, spinach)\n")
                    recommendations.append("• Practice stress reduction techniques\n")
                    recommendations.append("• Monitor BP regularly\n\n")
                    recommendations.append("DON'Ts:\n")
                    recommendations.append("• Don't consume excessive alcohol\n")
                    recommendations.append("• Avoid processed foods high in salt\n")
                    recommendations.append("• Don't smoke\n\n")
                    recommendations.append("Recommended Activities:\n")
                    recommendations.append("• Brisk walking\n")
                    recommendations.append("• Swimming\n")
                    recommendations.append("• Tai chi\n")
                    recommendations.append("• Gardening\n\n")
                }
                else -> {
                    recommendations.append("✅ Normal Blood Pressure Range\n\n")
                    recommendations.append("Maintenance Tips:\n")
                    recommendations.append("• Continue heart-healthy diet\n")
                    recommendations.append("• Regular physical activity\n")
                    recommendations.append("• Annual blood pressure checks\n\n")
                }
            }
        }

        // Glucose Level recommendations
        glucoseLevel?.let { glucose ->
            recommendations.append("\n### Glucose Level (${glucose}mg/dL) ###\n\n")
            when {
                glucose < 70 -> {
                    recommendations.append("🚩 Low Blood Sugar (Hypoglycemia)\n\n")
                    recommendations.append("DOs:\n")
                    recommendations.append("• Consume 15g fast-acting carbs (juice, glucose tablets)\n")
                    recommendations.append("• Recheck after 15 minutes\n")
                    recommendations.append("• Follow with protein snack once stable\n")
                    recommendations.append("• Always carry emergency sugar source\n\n")
                    recommendations.append("DON'Ts:\n")
                    recommendations.append("• Don't ignore symptoms\n")
                    recommendations.append("• Avoid treating with chocolate/candy bars\n")
                    recommendations.append("• Don't drive until glucose normalizes\n\n")
                    recommendations.append("Recommended Activities:\n")
                    recommendations.append("• Light walking after meals\n")
                    recommendations.append("• Gentle stretching\n")
                    recommendations.append("• Avoid strenuous exercise when glucose is low\n\n")
                }
                glucose > 140 -> {
                    recommendations.append("🚩 High Blood Sugar (Hyperglycemia)\n\n")
                    recommendations.append("DOs:\n")
                    recommendations.append("• Drink plenty of water\n")
                    recommendations.append("• Monitor for ketones if type 1 diabetes\n")
                    recommendations.append("• Follow medication regimen\n")
                    recommendations.append("• Choose low-glycemic index foods\n\n")
                    recommendations.append("DON'Ts:\n")
                    recommendations.append("• Don't consume sugary drinks/foods\n")
                    recommendations.append("• Avoid skipping meals\n")
                    recommendations.append("• Don't ignore persistent high readings\n\n")
                    recommendations.append("Recommended Activities:\n")
                    recommendations.append("• Moderate exercise (if no ketones present)\n")
                    recommendations.append("• Walking after meals\n")
                    recommendations.append("• Water aerobics\n")
                    recommendations.append("• Resistance training\n\n")
                }
                else -> {
                    recommendations.append("✅ Normal Glucose Range\n\n")
                    recommendations.append("Maintenance Tips:\n")
                    recommendations.append("• Balanced meals with fiber\n")
                    recommendations.append("• Regular physical activity\n")
                    recommendations.append("• Annual glucose monitoring\n\n")
                }
            }
        }

        // Weight recommendations
        weight?.let { wt ->
            recommendations.append("\n### Weight (${wt}lbs) ###\n\n")
            when {
                wt > 35 -> {
                    recommendations.append("🚩 Excessive Weight Gain\n\n")
                    recommendations.append("DOs:\n")
                    recommendations.append("• Focus on nutrient-dense foods\n")
                    recommendations.append("• Stay active with doctor-approved exercises\n")
                    recommendations.append("• Monitor portion sizes\n")
                    recommendations.append("• Keep a food diary\n\n")
                    recommendations.append("DON'Ts:\n")
                    recommendations.append("• Don't diet excessively\n")
                    recommendations.append("• Avoid sugary drinks\n")
                    recommendations.append("• Don't skip meals\n\n")
                    recommendations.append("Recommended Activities:\n")
                    recommendations.append("• Walking (30 mins daily)\n")
                    recommendations.append("• Prenatal yoga\n")
                    recommendations.append("• Swimming\n")
                    recommendations.append("• Light strength training\n\n")
                }
                wt < 25 -> {
                    recommendations.append("🚩 Inadequate Weight Gain\n\n")
                    recommendations.append("DOs:\n")
                    recommendations.append("• Eat frequent, small meals\n")
                    recommendations.append("• Choose nutrient-rich foods\n")
                    recommendations.append("• Add healthy fats (avocados, nuts)\n")
                    recommendations.append("• Monitor weight weekly\n\n")
                    recommendations.append("DON'Ts:\n")
                    recommendations.append("• Don't skip meals\n")
                    recommendations.append("• Avoid empty calories\n")
                    recommendations.append("• Don't ignore persistent nausea\n\n")
                    recommendations.append("Recommended Activities:\n")
                    recommendations.append("• Gentle walking to stimulate appetite\n")
                    recommendations.append("• Light stretching\n")
                    recommendations.append("• Relaxation techniques for stress\n\n")
                }
                else -> {
                    recommendations.append("✅ Healthy Weight Gain Range\n\n")
                    recommendations.append("Maintenance Tips:\n")
                    recommendations.append("• Continue balanced diet\n")
                    recommendations.append("• Regular moderate exercise\n")
                    recommendations.append("• Monthly weight checks\n\n")
                }
            }
        }

        return recommendations.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}