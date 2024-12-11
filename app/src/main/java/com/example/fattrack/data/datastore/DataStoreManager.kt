package com.example.fattrack.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("notification")

class DataStoreManager(private val context: Context) {

    private val NOTIFICATION_TOGGLE_KEY = booleanPreferencesKey("notification_toggle")

    val notificationToggleFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_TOGGLE_KEY] ?: false
        }

    suspend fun setNotificationToggle(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_TOGGLE_KEY] = enabled
        }
    }
}
