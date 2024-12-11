package com.example.fattrack.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fattrack.data.database.NotificationEntity
import com.example.fattrack.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(private val notifications: List<NotificationEntity>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationEntity) {
            binding.tvTitleNotification.text = notification.title
            binding.tvDescNotification.text = notification.message
            binding.tvTimeNotification.text = formatTimestamp(notification.timestamp)
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH.mm", Locale.getDefault())
            val date = Date(timestamp)
            return sdf.format(date)  // Returns the formatted time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size
}
