package com.example.fattrack.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fattrack.data.services.responses.HistoryDataItem
import com.example.fattrack.databinding.ItemHistoryScanBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryPredictAdapter(private var items: List<HistoryDataItem>) :
    RecyclerView.Adapter<HistoryPredictAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHistoryScanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: HistoryDataItem) {
            binding.tvNameRiwayat.text = item.foodName?.let { capitalizeWords(it) }
            binding.tvDateRiwayat.text = item.predictionDate?.let { formatDate(it) }
            binding.tvTimeRiwayat.text = "${item.predictionDate?.let { formatTime(it) }} WIB"
            binding.tvTotalCek.text = "${item.calories} kcal"
            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .into(binding.ivPhotoRiwayat)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryScanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<HistoryDataItem>) {
        items = newItems.asReversed()
        notifyDataSetChanged()
    }


    //parse date
    private fun formatDate(inputDate: String): String {
        return try {
            // get date part, before comma
            val datePart = inputDate.split(",")[0].trim()

            // Format input dan parsing
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = inputFormat.parse(datePart)

            // Format output
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            inputDate
        }
    }

    //parse time
    private fun formatTime(inputDateTime: String): String {
        return try {
            // get time part, after comma
            val timePart = inputDateTime.split(" ")[1].trim()

            // Format input dan parsing
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            val date = inputFormat.parse(timePart)

            // Format output
            val outputFormat = SimpleDateFormat("HH.mm", Locale.ENGLISH)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            inputDateTime
        }
    }



    //capitalize format
    fun capitalizeWords(input: String): String {
        return input.split(" ") // Memisahkan string berdasarkan spasi
            .joinToString(" ") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
            }
    }

}
