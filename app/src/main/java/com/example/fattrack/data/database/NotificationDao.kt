package com.example.fattrack.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    suspend fun getAllNotifications(): List<NotificationEntity>

    @Insert
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()

}
