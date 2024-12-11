package com.example.fattrack.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fattrack.R
import com.example.fattrack.data.data.HistoryDayData
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryDayAdapter(private var dataList: List<HistoryDayData>) : RecyclerView.Adapter<HistoryDayAdapter.SlideViewHolder>() {
    // ViewHolder for recycler view
    class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.tv_date)
        val caloriesTextView: TextView = itemView.findViewById(R.id.tv_kalori)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        // Inflate layout item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_day, parent, false)
        return SlideViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val item = dataList[position]

        // set data
        holder.dateTextView.text = formatDate(item.date)
        holder.caloriesTextView.text = "${item.totalCalories} Kcal"
    }

    override fun getItemCount(): Int = dataList.size

    //fill data
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<HistoryDayData?>) {
        dataList = newData.asReversed() as List<HistoryDayData>
        notifyDataSetChanged()
    }


    //parse date
    private fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = inputFormat.parse(inputDate)

            // Format output
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            inputDate
        }
    }
}