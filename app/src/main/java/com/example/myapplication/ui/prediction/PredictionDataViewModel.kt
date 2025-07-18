package com.example.myapplication.ui.prediction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class PredictionDataViewModel : ViewModel() {
    // Raw data with numeric values for calculations
    private val _heartRate = MutableLiveData<Int>()
    private val _bloodPressure = MutableLiveData<Int>()
    private val _glucoseLevel = MutableLiveData<Int>()
    private val _weight = MutableLiveData<Int>()
    private val _lastUpdated = MutableLiveData<Long>()
    private val _lastCheck = MutableLiveData<Long>()

    // String representations for display
    val heartRate = MutableLiveData<String>()
    val bloodPressure = MutableLiveData<String>()
    val glucoseLevel = MutableLiveData<String>()
    val weight = MutableLiveData<String>()
    val lastUpdated = MutableLiveData<String>()
    val lastCheck = MutableLiveData<String>()

    fun updateHeartRate(value: Int) {
        _heartRate.value = value
        heartRate.value = "$value bpm"
    }

    fun updateBloodPressure(value: Int) {
        _bloodPressure.value = value
        bloodPressure.value = "$value mmHg"
    }

    fun updateGlucoseLevel(value: Int) {
        _glucoseLevel.value = value
        glucoseLevel.value = "$value mg/dL"
    }

    fun updateWeight(value: Int) {
        _weight.value = value
        weight.value = "$value lbs"
    }

    fun updateTimestamps() {
        val now = System.currentTimeMillis()
        _lastUpdated.value = now
        _lastCheck.value = now

        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        lastUpdated.value = dateFormat.format(Date(now))
        lastCheck.value = dateFormat.format(Date(now))
    }

    // Helper functions to get numeric values
    fun getHeartRateValue(): Int? = _heartRate.value
    fun getBloodPressureValue(): Int? = _bloodPressure.value
    fun getGlucoseLevelValue(): Int? = _glucoseLevel.value
    fun getWeightValue(): Int? = _weight.value
}