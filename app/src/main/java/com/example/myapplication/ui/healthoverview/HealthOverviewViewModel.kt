package com.example.myapplication.ui.healthoverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

class HealthOverviewViewModel : ViewModel() {
    // Health metrics
    val heartRate = MutableLiveData<String>().apply { value = "72" }
    val bloodPressure = MutableLiveData<String>().apply { value = "122/80" }
    val glucoseLevel = MutableLiveData<String>().apply { value = "96 mg/dL" }
    val bloodGroup = MutableLiveData<String>().apply { value = "A+" }
    val weight = MutableLiveData<String>().apply { value = "80 kg" }
    val lastUpdated = MutableLiveData<String>().apply { value = "April 22, 2025" }
    val lastCheck = MutableLiveData<String>().apply { value = "March 2024" }
}