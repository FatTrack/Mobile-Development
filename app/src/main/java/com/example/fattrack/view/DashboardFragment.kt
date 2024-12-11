package com.example.fattrack.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.adapter.HistoryDayAdapter
import com.example.fattrack.data.adapter.HistoryPredictAdapter
import com.example.fattrack.data.data.HistoryDayData
import com.example.fattrack.data.viewmodel.DashboardViewModel
import com.example.fattrack.databinding.FragmentDashboardBinding
import com.example.fattrack.view.loadingDialog.DialogLoading
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _bindingDashboard: FragmentDashboardBinding? = null
    private val bindingDashboard get() = _bindingDashboard!!
    private lateinit var viewModel: DashboardViewModel
    private var adapter = HistoryDayAdapter(emptyList())
    private lateinit var adapterhistory: HistoryPredictAdapter
    private lateinit var dialogLoading: DialogLoading


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //loading setup
        dialogLoading = DialogLoading(requireContext())
        dialogLoading.startLoading()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Init binding
        _bindingDashboard = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = bindingDashboard.root

        // Init viewModel
        val factory = ViewModelFactory.getInstance(this.requireContext())
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]

        //init recycler and adapter Day
        val recyclerView = bindingDashboard.rvHistoryDay
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        //init recycler and adapter history scan
        adapterhistory = HistoryPredictAdapter(listOf())
        bindingDashboard.rvHistoryScan.layoutManager = LinearLayoutManager(requireContext())
        bindingDashboard.rvHistoryScan.adapter = adapterhistory

        initAllViewModel()
        observeViewModel()
        dashboardWeekSetup()
        buttonClick()
        historyDay()
        return root
    }


    private fun initAllViewModel() {
        viewModel.dashboardWeek()
        viewModel.dashboardMonth()
        viewModel.getHistory()
        viewModel.getHistoryScan()
    }


    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        //loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                dialogLoading.startLoading()
                bindingDashboard.barChart.visibility = View.GONE
            } else {
                bindingDashboard.barChart.visibility = View.VISIBLE
                dialogLoading.stopLoading()
            }
        }

        //error
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Log.e("DashboardFragmentTest", "Error: $errorMessage")
            }
        }

        //history scan/data
        viewModel.historyData.observe(viewLifecycleOwner) { historyList ->
            if (historyList != null) {
                adapterhistory.setData(historyList)
            } else {
                bindingDashboard.rvHistoryScan.visibility = View.GONE
                bindingDashboard.errorMessage.text = "No history available"
                bindingDashboard.errorMessage.visibility = View.VISIBLE
            }
        }
    }


    private fun buttonClick() {
        bindingDashboard.btnWeek.setOnClickListener {
            dashboardWeekSetup()
        }
        bindingDashboard.btnMonth.setOnClickListener {
            dashboardMonthSetup()
        }
    }


    //WRONG FOR NAMED FEATURE : WEEK TO DAY & MONTH TO WEEKLY
    //week
    private fun dashboardWeekSetup() {
        //set button color
        bindingDashboard.btnWeek.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.Primary)
        bindingDashboard.btnWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        bindingDashboard.btnMonth.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.lightGray)
        bindingDashboard.btnMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        //data
        viewModel.dashboardWeekResponse.observe(viewLifecycleOwner) { response ->
                    response?.data?.let { dataList ->
                        // list date for x-axis
                        val dates = dataList.mapNotNull { it?.date }
                        val formattedDates = dates.map { originalDate ->
                            try {
                                //parse date
                                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(originalDate)
                                SimpleDateFormat("dd MMM", Locale.getDefault()).format(date ?: originalDate)
                            } catch (e: Exception) {
                                originalDate
                            }
                        }


                        val barEntries = dataList.mapIndexedNotNull { index, item ->
                            val totalCalories = item?.totalCalories
                            if (totalCalories != null) {
                                BarEntry(index.toFloat(), totalCalories.toFloat())
                            } else {
                                null
                            }
                        }

                        // value formatter for x-axis
                        val xAxis = bindingDashboard.barChart.xAxis
                        xAxis.valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val index = value.toInt()
                                return if (index in formattedDates.indices) formattedDates[index] else ""
                            }
                        }

                        // init and set data ke chart
                        val barDataSet = BarDataSet(barEntries, "Daily Calories").apply {
                            colors = ColorTemplate.MATERIAL_COLORS.toList()
                            valueTextColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
                            valueTextSize = 12f
                        }
                        val barData = BarData(barDataSet)

                        val barChart: BarChart = bindingDashboard.barChart
                        barChart.data = barData
                        barChart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chart_background))
                        barChart.description.isEnabled = false
                        barChart.setDrawGridBackground(false)
                        barChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
                        barChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
                        barChart.axisRight.isEnabled = false
                        barChart.legend.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
                        barChart.invalidate()
                    }
        }
    }

    //Month
    private fun dashboardMonthSetup() {
        //set button color
        bindingDashboard.btnWeek.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.lightGray)
        bindingDashboard.btnWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        bindingDashboard.btnMonth.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.Primary)
        bindingDashboard.btnMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        //data
        viewModel.dashboardMonthResponse.observe(viewLifecycleOwner) { response ->
            response?.data?.let { dataList ->
                // list date for x-axis
                val dates = dataList.mapNotNull { it?.startEnd.toString() }

                val barEntries = dataList.mapIndexedNotNull { no, item ->
                    val totalCalories = item?.totalCalories
                    if (totalCalories != null) {
                        BarEntry(no.toFloat(), totalCalories.toFloat())
                    } else {
                        null
                    }
                }

                // value formatter for x-axis
                val usedValues = mutableSetOf<String>()
               val xAxis = bindingDashboard.barChart.xAxis
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()

                        if (index in dates.indices) {
                            val formattedDate = dates[index]
                            if (usedValues.contains(formattedDate)) {
                                return ""
                            } else {
                                usedValues.add(formattedDate)
                                return formattedDate
                            }
                        } else {
                            return ""
                        }
                    }
                }

                // init and set data ke chart
                val barDataSet = BarDataSet(barEntries, "Weekly Calories").apply {
                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                    valueTextColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
                    valueTextSize = 12f
                }
                val barData = BarData(barDataSet)

                val barChart: BarChart = bindingDashboard.barChart
                barChart.data = barData
                barChart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chart_background))
                barChart.description.isEnabled = false
                barChart.setDrawGridBackground(false)
                barChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
                barChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
                barChart.axisRight.isEnabled = false
                barChart.legend.textColor = ContextCompat.getColor(requireContext(), R.color.chart_text_color)
                barChart.invalidate()
            }
        }
    }

    //history day
    private fun historyDay() {
        //observe
        viewModel.dashboardWeekResponse.observe(viewLifecycleOwner) { response ->
            response?.data?.let { dataList ->
                //mapping data
                val items = dataList.map { it?.date?.let { it1 -> HistoryDayData(it1,
                    it.totalCalories.toString()
                ) } }

                adapter.updateData(items)
            }
        }
    }

}