package com.example.fattrack.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fattrack.data.repositories.AuthRepository
import com.example.fattrack.data.services.responses.ResponseRegister

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    //live data for response
    private val _registerResponse = MutableLiveData<Result<ResponseRegister>>()
    val registerResponse: LiveData<Result<ResponseRegister>> = _registerResponse

    private val _errorMessages = MutableLiveData<String>()
    val errorMessages: LiveData<String> = _errorMessages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    //register
    suspend fun register(email: String, nama: String, password: String) {
        _isLoading.value = true

        try {
            val response = authRepository.register(email, nama, password)
            _registerResponse.value = response
        } catch (e: Exception) {
            _errorMessages.value = "Something went wrong. Please try again later!"
        } finally {
            _isLoading.value = false
        }

    }
}