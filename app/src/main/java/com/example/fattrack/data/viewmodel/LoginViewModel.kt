package com.example.fattrack.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fattrack.data.repositories.AuthRepository
import com.example.fattrack.data.repositories.SessionModel
import com.example.fattrack.data.services.responses.ResponseLogin
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    //live data for response
    private val _loginResponse = MutableLiveData<Result<ResponseLogin>>()
    val loginResponse: LiveData<Result<ResponseLogin>> = _loginResponse

    private val _errorMessages = MutableLiveData<String>()
    val errorMessages: LiveData<String> = _errorMessages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    //login
    suspend fun login(email: String, password: String) {
        _isLoading.value = true

        try {
            val response = authRepository.login(email, password)
            _loginResponse.value = response
            Log.d("RegisterViewModelTest", "Login response: $response")
        } catch (e: Exception) {
            _errorMessages.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    fun saveSession(user: SessionModel) {
        viewModelScope.launch {
            authRepository.saveSession(user)
        }
    }
}