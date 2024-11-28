package com.example.fattrack.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.profileDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProfilePreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val themeKey = booleanPreferencesKey("theme_setting")

    fun getThemeApp(): Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[themeKey] ?: false }

    suspend fun saveThemeApp(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ProfilePreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): ProfilePreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfilePreferences(dataStore).also { INSTANCE = it }
            }
    }
}