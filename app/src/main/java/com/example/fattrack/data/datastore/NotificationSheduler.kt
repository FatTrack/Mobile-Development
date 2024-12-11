package com.example.fattrack.data.datastore

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.fattrack.data.NotificationReceiver
import java.util.Calendar

class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNotifications(times: List<Pair<Int, Int>>) {
        for (time in times) {
            val (hour, minute) = time
            scheduleNotification(hour, minute)
        }
    }

    // Schedule notifications at specific times
    fun scheduleNotification(hour: Int, minute: Int) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", "Reminder")
            putExtra("message", "It's time to check your calories!")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hour * 100 + minute,  // Unique request code based on time
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Set calendar for scheduled time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

// Schedule an alarm for notification
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // Repeat every day
            pendingIntent
        )
    }

    // Cancels a scheduled notification
    fun cancelNotifications() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,  // Fixed request code
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }
}