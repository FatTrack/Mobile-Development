package com.example.fattrack.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.fattrack.R
import com.example.fattrack.data.database.NotificationDatabase
import com.example.fattrack.data.database.NotificationEntity
import com.example.fattrack.view.notifications.NotificationsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Check whether POST_NOTIFICATIONS permission has been granted
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        // Retrieve data from Intent
            val title = intent.getStringExtra("title") ?: "Reminder"
            val message = intent.getStringExtra("message") ?: "It's time to check your calories!"
            val timestamp = System.currentTimeMillis()

        // Save notification to database
            saveNotification(context, title, message, timestamp)

            // Create a PendingIntent to open the NotificationActivity when the notification is clicked
            val notificationIntent = Intent(context, NotificationsActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,  // Request code
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // build notifikasi
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            // Show notification
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(timestamp.toInt(), builder.build())
        } else {
            // Handle if permission is not granted
            println("Permission for POST_NOTIFICATIONS is not granted.")
        }
    }

    private fun saveNotification(context: Context, title: String, message: String, timestamp: Long) {
        // Access database and save notification data
        val notificationDao = NotificationDatabase.getDatabase(context).notificationDao()
        val notification = NotificationEntity(
            id = 0,
            title = title,
            message = message,
            timestamp = timestamp
        )
        CoroutineScope(Dispatchers.IO).launch {
            notificationDao.insertNotification(notification)
        }
    }

    companion object {
        // Channel ID for notifications (make sure this channel is created in the app)
        private const val CHANNEL_ID = "default_channel"

        // Function to create a Notification Channel (must be called when the application is run)
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, "Reminder Notifications", importance).apply {
                    description = "Channel for reminder notifications"
                }

                // Get NotificationManager and create a channel
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
