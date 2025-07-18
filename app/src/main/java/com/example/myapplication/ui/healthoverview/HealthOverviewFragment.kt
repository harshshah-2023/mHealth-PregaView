package com.example.myapplication.ui.healthoverview

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.CustomMarkerView
import com.example.myapplication.databinding.FragmentHealthOverviewBinding
import com.example.myapplication.ui.prediction.PredictionDataViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HealthOverviewFragment : Fragment() {
    private var _binding: FragmentHealthOverviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HealthOverviewViewModel
    private lateinit var predictionViewModel: PredictionDataViewModel
    private val database = Firebase.database
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        setupClickListeners()
        setupCharts()
        loadHealthData()
    }

    private fun setupViewModels() {
        viewModel = ViewModelProvider(this).get(HealthOverviewViewModel::class.java)
        predictionViewModel = ViewModelProvider(requireActivity()).get(PredictionDataViewModel::class.java)
    }

    private fun setupClickListeners() {
        binding.connectSuggestion.setOnClickListener {
            findNavController().navigate(R.id.action_overviewtoRecommendation)
        }
        binding.btnUpdateHealth.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_healthInputFragment)
        }
    }

    private fun setupCharts() {
        val bpChart = binding.bloodPressureChart
        val glucoseChart = binding.glucoseChart
        val weightChart = binding.weightChart

        setupCommonChartStyles(bpChart)
        setupCommonChartStyles(glucoseChart)
        setupCommonChartStyles(weightChart)

        loadVitalsHistory(bpChart, glucoseChart, weightChart)
    }

    private fun setupCommonChartStyles(chart: LineChart) {
        with(chart) {
            setTouchEnabled(true)
            setPinchZoom(true)
            description.isEnabled = false

            // Legend styling
            legend.isEnabled = true
            legend.form = Legend.LegendForm.LINE
            legend.textColor = Color.DKGRAY
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)

            // X-axis styling
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.DKGRAY
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(true)
            xAxis.granularity = 1f
            xAxis.labelCount = 4

            // Left Y-axis styling
            axisLeft.textColor = Color.DKGRAY
            axisLeft.setDrawGridLines(true)
            axisLeft.gridColor = Color.LTGRAY
            axisLeft.axisMinimum = 0f
            axisLeft.setDrawZeroLine(false)

            // Disable right Y-axis
            axisRight.isEnabled = false
            extraBottomOffset = 10f
        }
    }

    private fun loadVitalsHistory(bpChart: LineChart, glucoseChart: LineChart, weightChart: LineChart) {
        val userId = "default_user"
        database.reference.child("users").child(userId).child("healthData")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = processSnapshotData(snapshot)
                    updateChartsWithData(entries, bpChart, glucoseChart, weightChart)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error appropriately
                }
            })
    }

    private fun processSnapshotData(snapshot: DataSnapshot): List<HealthDataEntry> {
        return snapshot.children.mapNotNull { entry ->
            val timestamp = entry.key?.toLongOrNull() ?: return@mapNotNull null
            val bp = entry.child("bloodPressure").getValue(String::class.java)?.split("/") ?: listOf("0", "0")

            HealthDataEntry(
                timestamp = timestamp,
                systolic = bp[0].filter { it.isDigit() }.toFloatOrNull() ?: 0f,
                diastolic = bp[1].filter { it.isDigit() }.toFloatOrNull() ?: 0f,
                glucose = entry.child("glucoseLevel").getValue(String::class.java)
                    ?.filter { it.isDigit() }?.toFloatOrNull() ?: 0f,
                weight = entry.child("weight").getValue(String::class.java)
                    ?.filter { it.isDigit() }?.toFloatOrNull() ?: 0f
            )
        }.sortedBy { it.timestamp }
    }

    private fun updateChartsWithData(
        entries: List<HealthDataEntry>,
        bpChart: LineChart,
        glucoseChart: LineChart,
        weightChart: LineChart
    ) {
        // Prepare date labels for X-axis
        val dateLabels = entries.map { dateFormat.format(Date(it.timestamp)) }
        val xAxisFormatter = IndexAxisValueFormatter(dateLabels)

        // Create entries for each chart
        val bpEntries = entries.mapIndexed { index, data ->
            Entry(index.toFloat(), data.systolic) to Entry(index.toFloat(), data.diastolic)
        }
        val glucoseEntries = entries.mapIndexed { index, data ->
            Entry(index.toFloat(), data.glucose)
        }
        val weightEntries = entries.mapIndexed { index, data ->
            Entry(index.toFloat(), data.weight)
        }

        // Configure charts
        configureBloodPressureChart(bpChart, bpEntries, xAxisFormatter)
        configureGlucoseChart(glucoseChart, glucoseEntries, xAxisFormatter)
        configureWeightChart(weightChart, weightEntries, xAxisFormatter)
    }

    private fun configureBloodPressureChart(
        chart: LineChart,
        entries: List<Pair<Entry, Entry>>,
        formatter: IndexAxisValueFormatter
    ) {
        val (systolicEntries, diastolicEntries) = entries.unzip()

        val systolicDataSet = LineDataSet(systolicEntries, "Systolic").apply {
            color = Color.rgb(255, 82, 82)
            setCircleColor(Color.rgb(255, 82, 82))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
            setDrawFilled(true)
            fillColor = Color.rgb(255, 82, 82)
            fillAlpha = 50
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val diastolicDataSet = LineDataSet(diastolicEntries, "Diastolic").apply {
            color = Color.rgb(66, 165, 245)
            setCircleColor(Color.rgb(66, 165, 245))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
            setDrawFilled(true)
            fillColor = Color.rgb(66, 165, 245)
            fillAlpha = 50
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        chart.apply {
            xAxis.valueFormatter = formatter
            data = LineData(systolicDataSet, diastolicDataSet)
            marker = CustomMarkerView(requireContext(), R.layout.custom_marker_view)
            invalidate()
        }
    }

    private fun configureGlucoseChart(
        chart: LineChart,
        entries: List<Entry>,
        formatter: IndexAxisValueFormatter
    ) {
        val dataSet = LineDataSet(entries, "Glucose Level").apply {
            color = Color.rgb(156, 39, 176)
            setCircleColor(Color.rgb(156, 39, 176))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
            setDrawFilled(true)
            fillColor = Color.rgb(156, 39, 176)
            fillAlpha = 50
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        chart.apply {
            xAxis.valueFormatter = formatter
            data = LineData(dataSet)
            marker = CustomMarkerView(requireContext(), R.layout.custom_marker_view)
            invalidate()
        }
    }

    private fun configureWeightChart(
        chart: LineChart,
        entries: List<Entry>,
        formatter: IndexAxisValueFormatter
    ) {
        val dataSet = LineDataSet(entries, "Weight").apply {
            color = Color.rgb(102, 187, 106)
            setCircleColor(Color.rgb(102, 187, 106))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
            setDrawFilled(true)
            fillColor = Color.rgb(102, 187, 106)
            fillAlpha = 50
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        chart.apply {
            xAxis.valueFormatter = formatter
            data = LineData(dataSet)
            marker = CustomMarkerView(requireContext(), R.layout.custom_marker_view)
            invalidate()
        }
    }

    private fun loadHealthData() {
        val userId = "default_user"
        database.reference.child("users").child(userId).child("healthData")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val latestEntry = getLatestEntry(snapshot)
                    latestEntry?.let { updateViewModels(it) }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        setupViewModelObservers()
    }

    private fun getLatestEntry(snapshot: DataSnapshot): DataSnapshot? {
        return snapshot.children.maxByOrNull { it.key?.toLongOrNull() ?: 0L }
    }

    private fun updateViewModels(data: DataSnapshot) {
        val heartRateStr = data.child("heartRate").getValue(String::class.java) ?: "72"
        val bloodPressureStr = data.child("bloodPressure").getValue(String::class.java) ?: "122/80"
        val glucoseLevelStr = data.child("glucoseLevel").getValue(String::class.java) ?: "96 mg/dL"
        val weightStr = data.child("weight").getValue(String::class.java) ?: "80 kg"
        val lastUpdatedStr = data.child("lastUpdated").getValue(String::class.java) ?: "Unknown"
        val lastCheckStr = data.child("lastCheck").getValue(String::class.java) ?: "Unknown"

        // Update HealthOverviewViewModel
        viewModel.apply {
            heartRate.value = heartRateStr
            bloodPressure.value = bloodPressureStr
            glucoseLevel.value = glucoseLevelStr
            weight.value = weightStr
            lastUpdated.value = lastUpdatedStr
            lastCheck.value = lastCheckStr
        }

        // Update PredictionDataViewModel
        predictionViewModel.apply {
            heartRateStr.filter { it.isDigit() }.toIntOrNull()?.let { updateHeartRate(it) }
            bloodPressureStr.split("/").firstOrNull()?.filter { it.isDigit() }?.toIntOrNull()?.let { updateBloodPressure(it) }
            glucoseLevelStr.filter { it.isDigit() }.toIntOrNull()?.let { updateGlucoseLevel(it) }
            weightStr.filter { it.isDigit() }.toIntOrNull()?.let { updateWeight(it) }
            lastUpdated.value = lastUpdatedStr
            lastCheck.value = lastCheckStr
        }
    }

    private fun setupViewModelObservers() {
        viewModel.heartRate.observe(viewLifecycleOwner) { binding.heartRateValue.text = it }
        viewModel.bloodPressure.observe(viewLifecycleOwner) { binding.bloodPressureValue.text = it }
        viewModel.glucoseLevel.observe(viewLifecycleOwner) { binding.glucoseValue.text = it }
        viewModel.weight.observe(viewLifecycleOwner) { binding.weightValue.text = it }
        viewModel.lastUpdated.observe(viewLifecycleOwner) {
            binding.lastUpdatedText.text = "Last updated: $it"
        }
        viewModel.lastCheck.observe(viewLifecycleOwner) {
            binding.lastCheckText.text = "Last check: $it"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private data class HealthDataEntry(
        val timestamp: Long,
        val systolic: Float,
        val diastolic: Float,
        val glucose: Float,
        val weight: Float
    )
}