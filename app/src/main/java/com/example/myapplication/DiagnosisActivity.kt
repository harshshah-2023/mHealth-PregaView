package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import android.widget.Toast
import com.example.myapplication.databinding.ActivityDiagnosisBinding
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType

class DiagnosisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiagnosisBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnosisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDiagnose.setOnClickListener {
            val symptoms = mapOf(
                "nausea" to if (binding.nauseaCheckbox.isChecked) 1 else 0,
                "vomiting" to if (binding.vomitingCheckbox.isChecked) 1 else 0,
                "back pain" to if (binding.backPainCheckbox.isChecked) 1 else 0,
                "headache" to if (binding.headacheCheckbox.isChecked) 1 else 0,
                "swelling" to if (binding.swellingCheckbox.isChecked) 1 else 0,
                "blurred vision" to if (binding.blurredVisionCheckbox.isChecked) 1 else 0,
                "high blood pressure" to if (binding.hbpCheckbox.isChecked) 1 else 0,
                "dizziness" to if (binding.dizzinessCheckbox.isChecked) 1 else 0,
                "fatigue" to if (binding.fatigueCheckbox.isChecked) 1 else 0,
                "abdominal pain" to if (binding.abdominalPainCheckbox.isChecked) 1 else 0
            )

            if (symptoms.values.all { it == 0 }) {
                Toast.makeText(this, "Please select at least one symptom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val result = diagnoseSymptoms(symptoms)
                    runOnUiThread {
                        binding.tvResult.text =
                            "Diagnosis: ${result.disease}\nConfidence: ${(result.confidence * 100).toInt()}%"
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        binding.tvResult.text = "Error: ${e.message}"
                        Toast.makeText(
                            this@DiagnosisActivity,
                            "Failed to connect to server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private suspend fun diagnoseSymptoms(symptoms: Map<String, Int>): DiagnosisResult {
        val json = JSONObject(symptoms).toString()
        val request = Request.Builder()
            .url("http://192.168.71.228:5000/predict") // Replace with your server IP
            .post(json.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Server error: ${response.code}")
            val jsonString = response.body?.string() ?: throw Exception("Empty response")
            return Gson().fromJson(jsonString, DiagnosisResult::class.java)
        }
    }

    companion object {
        private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }
}

data class DiagnosisResult(
    val disease: String,
    val confidence: Double
)