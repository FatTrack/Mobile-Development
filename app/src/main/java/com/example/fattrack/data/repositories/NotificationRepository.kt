package com.example.fattrack.data.repositories

import com.example.fattrack.data.database.NotificationDao
import com.example.fattrack.data.database.NotificationEntity
import com.example.fattrack.data.datastore.DataStoreManager
import kotlinx.coroutines.flow.Flow

class NotificationRepository(
    private val dataStoreManager: DataStoreManager,
    private val notificationDao: NotificationDao
) {

    val notificationToggleFlow: Flow<Boolean> = dataStoreManager.notificationToggleFlow

    suspend fun setNotificationToggle(enabled: Boolean) {
        dataStoreManager.setNotificationToggle(enabled)
    }


    suspend fun getNotifications(): List<NotificationEntity> {
        return notificationDao.getAllNotifications()
    }

    suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }

}
