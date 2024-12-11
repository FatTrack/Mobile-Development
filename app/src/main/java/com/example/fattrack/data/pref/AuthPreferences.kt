package com.example.fattrack.data.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.fattrack.data.repositories.SessionModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// extension for make DataStore
val Context.authSession: DataStore<Preferences> by preferencesDataStore(name = "session")

class AuthPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val ID_USER_KEY = stringPreferencesKey("idUser")
        private val TOKEN = stringPreferencesKey("token")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        @Volatile
        private var INSTANCE: AuthPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }


    // save data session
    suspend fun saveSession(user: SessionModel) {
        dataStore.edit { preferences ->
            preferences[ID_USER_KEY] = user.idUser
            preferences[TOKEN] = user.token
            preferences[EMAIL_KEY] = user.email
            preferences[IS_LOGIN_KEY] = true
        }

        Log.d("UserPreference", "Session saved: ${dataStore.data}")
    }

    // get data session
    fun getSession(): Flow<SessionModel> {
        return dataStore.data.map { preferences ->
            val idUser = preferences[ID_USER_KEY] ?: ""
            val token = preferences[TOKEN] ?: ""
            val email = preferences[EMAIL_KEY] ?: ""
            val isLogin = preferences[IS_LOGIN_KEY] ?: false

            SessionModel(idUser, token, email, isLogin)
        }


    }

    // logout
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}