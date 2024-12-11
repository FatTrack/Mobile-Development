package com.example.fattrack.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fattrack.data.repositories.AuthRepository
import com.example.fattrack.data.services.responses.ResponseResetPassword

class ForgotPassViewModel(private val authRepository: AuthRepository) : ViewModel()  {
    //live data for response
    private val _forgotResponse = MutableLiveData<Result<ResponseResetPassword>>()
    val forgotPassword: LiveData<Result<ResponseResetPassword>> = _forgotResponse

    private val _errorMessages = MutableLiveData<String>()
    val errorMessages: LiveData<String> = _errorMessages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    //login
    suspend fun forgotPassword(email: String) {
        _isLoading.value = true

        try {
            val response = authRepository.forgotPassword(email)

            if (response.isSuccess) {
                _forgotResponse.value = response
            } else {
                _errorMessages.value = "Failed to send link for your email. PLease try again and enter your correct email."
            }
        } catch (e: Exception) {
            _errorMessages.value = "Something went wrong. Please try again later!"
        } finally {
            _isLoading.value = false
        }
    }
}