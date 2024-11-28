package com.example.fattrack.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.fattrack.R
import com.example.fattrack.databinding.BottomSheetBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // init binding
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // set style BottomSheetDialog
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup chart
        chartSetup()
    }


    // setup for chart
    private fun chartSetup() {
        // Dummy data for PieChart
        val pieEntries = listOf(
            PieEntry(40f, "Kalori"),
            PieEntry(30f, "Protein"),
            PieEntry(20f, "Lemak"),
            PieEntry(10f, "Karbohidrat")
        )

        //colors
        val colorChart = listOf(
            ContextCompat.getColor(requireContext(), R.color.Error),
            ContextCompat.getColor(requireContext(), R.color.Primary),
            ContextCompat.getColor(requireContext(), R.color.TersierVariant),
            ContextCompat.getColor(requireContext(), R.color.Tersier)
        )

        // create PieDataSet
        val pieDataSet = PieDataSet(pieEntries, "").apply {
//            colors = ColorTemplate.MATERIAL_COLORS.toList()
            colors = colorChart
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
            valueTextSize = 12f
        }

        // create PieData
        val pieData = PieData(pieDataSet)

        // ref to PieChart
        val pieChart: PieChart = binding.chartSheet
        pieChart.data = pieData

        // custom PieChart
        pieChart.isDrawHoleEnabled = true // hole
        pieChart.holeRadius = 40f // hole Radius
        pieChart.setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.chart_text_color)) // color entry label
        pieChart.setEntryLabelTextSize(12f) // size label
        pieChart.description.isEnabled = false
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        pieChart.legend.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)

        // Refresh chart
        pieChart.invalidate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
