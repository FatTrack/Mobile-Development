package com.example.fattrack.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.fattrack.data.database.NotificationEntity
import com.example.fattrack.data.datastore.NotificationScheduler
import com.example.fattrack.data.repositories.NotificationRepository
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: NotificationRepository, private val notificationScheduler: NotificationScheduler) : ViewModel() {

    val notificationToggleState = repository.notificationToggleFlow.asLiveData()

    fun setNotificationToggle(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationToggle(enabled)

            if (enabled) {
                scheduleNotifications()
            } else {
                cancelScheduledNotifications()
            }
        }
    }

    suspend fun getNotifications(): List<NotificationEntity> {
        return repository.getNotifications()
    }

    private fun scheduleNotifications() {
        val times = listOf(
            8 to 0,   // 8 AM
            12 to 0,  // 12 PM
            19 to 0   // 7 PM
        )
        notificationScheduler.scheduleNotifications(times)
    }

    private fun cancelScheduledNotifications() {
        notificationScheduler.cancelNotifications()
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            repository.deleteAllNotifications()
        }
    }

}
