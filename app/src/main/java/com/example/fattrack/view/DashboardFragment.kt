package com.example.fattrack.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.fattrack.R
import com.example.fattrack.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class DashboardFragment : Fragment() {
    private var _bindingDashboard: FragmentDashboardBinding? = null
    private val bindingDashboard get() = _bindingDashboard!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Init binding
        _bindingDashboard = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = bindingDashboard.root

        dashboardSetup()

        return root
    }

    private fun dashboardSetup() {
        // Dummy data
        val barEntries = listOf(
            BarEntry(0f, 50f),
            BarEntry(1f, 80f),
            BarEntry(2f, 60f),
            BarEntry(3f, 120f)
        )

        // Set data to chart
        val barDataSet = BarDataSet(barEntries, "Kalori per Hari").apply {
            color = ContextCompat.getColor(requireContext(), R.color.chart_bar_color) // Bar color
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color) // Value text color
            valueTextSize = 12f
        }
        val barData = BarData(barDataSet)

        // Setting BarChart
        val barChart: BarChart = bindingDashboard.barChart
        barChart.data = barData

        // Chart Customization
        barChart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chart_background))
        barChart.description.isEnabled = false // Disable description
        barChart.setDrawGridBackground(false) // Disable grid background
        barChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
        barChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
        barChart.axisRight.isEnabled = false // Hide right axis
        barChart.legend.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
        barChart.invalidate() // Refresh chart
    }


}