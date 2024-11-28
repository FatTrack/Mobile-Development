package com.example.fattrack.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.fattrack.data.pref.ProfilePreferences
import com.example.fattrack.data.repositories.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val profilePreferences: ProfilePreferences, private val authRepository: AuthRepository) : ViewModel() {

    fun getThemeApp(): LiveData<Boolean> {
        return profilePreferences.getThemeApp().asLiveData()
    }

    fun saveThemeApp(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            profilePreferences.saveThemeApp(isDarkModeActive)
        }
    }


    // logout
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}