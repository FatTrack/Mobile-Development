package com.example.fattrack.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.fattrack.MainActivity
import com.example.fattrack.R
import com.example.fattrack.data.data.NutritionData
import com.example.fattrack.databinding.BottomSheetBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@Suppress("DEPRECATION")
class MyBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!
    private var nutritionalInfo: NutritionData? = null


    companion object {
        private const val ARG_NUTRITIONAL_INFO = "arg_nutritional_info"

        fun newInstance(nutritionalInfo: NutritionData): MyBottomSheetFragment {
            val fragment = MyBottomSheetFragment()
            val args = Bundle()
            args.putParcelable(ARG_NUTRITIONAL_INFO, nutritionalInfo)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nutritionalInfo = arguments?.getParcelable(ARG_NUTRITIONAL_INFO)
    }


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
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

//        // set height 3/4
        val displayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels * 3) / 4
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = height
            bottomSheet?.requestLayout()

            // false to drag
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.isDraggable = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Nonaktifkan gesture drag dan close otomatis saat mengetuk luar area
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup
        viewSetup()
        chartSetup()
        finishSetup()
    }

    @SuppressLint("SetTextI18n")
    private fun viewSetup() {
        // Update UI
        nutritionalInfo?.let { info ->
            binding.titleSheet.text= info.nama
            binding.descSheet.text = info.deskripsi
            binding.tvKalori.text = "${info.kalori} kkal"
            binding.tvKarbohidrat.text = "${info.karbohidrat} g"
            binding.tvLemak.text = "${info.lemak} g"
            binding.tvProtein.text = "${info.protein} g"
        }
    }


    private fun finishSetup() {
        binding.btnFinish.setOnClickListener {
            navigateToMain()
        }
    }

    // to MainActiviy
    private fun navigateToMain() {
        // go to mainActivity
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }



    // setup for chart
    private fun chartSetup() {
        var pieEntries: List<PieEntry> = emptyList()
        // Dummy data for PieChart
        nutritionalInfo?.let { info ->
            pieEntries = listOf(
                PieEntry(info.kalori?.toFloat() ?: 0f, "Kalori"),
                PieEntry(info.protein?.toFloat() ?: 0f, "Protein"),
                PieEntry(info.karbohidrat?.toFloat() ?: 0f, "Karbohidrat"),
                PieEntry(info.lemak?.toFloat() ?: 0f, "Lemak")
            )
        }

        //colors
        val colorChart = listOf(
            ContextCompat.getColor(requireContext(), R.color.Error),
            ContextCompat.getColor(requireContext(), R.color.Primary),
            ContextCompat.getColor(requireContext(), R.color.TersierVariant),
            ContextCompat.getColor(requireContext(), R.color.Tersier)
        )

        // Create PieDataSet
        val pieDataSet = PieDataSet(pieEntries, "").apply {
            colors = colorChart
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}%" // Hanya tampilkan bilangan bulat dengan simbol '%'
                }
            }
        }

// Aktifkan mode persentase pada PieChart
        val pieChart: PieChart = binding.chartSheet
        pieChart.setUsePercentValues(true)

        // Create PieData
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData

        // custom PieChart
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 40f
        pieChart.setDrawEntryLabels(false)
        pieChart.description.isEnabled = false
        pieChart.legend.apply {
            isEnabled = true
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            orientation = Legend.LegendOrientation.VERTICAL
            textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
        }

        // Refresh chart
        pieChart.invalidate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
