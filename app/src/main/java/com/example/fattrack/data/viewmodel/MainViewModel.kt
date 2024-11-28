package com.example.fattrack.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.fattrack.data.repositories.AuthRepository
import com.example.fattrack.data.repositories.SessionModel
import kotlinx.coroutines.launch

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // get session
    fun getSession(): LiveData<SessionModel> {
        return authRepository.getSession().asLiveData()
    }

}