package com.example.fattrack.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fattrack.data.repositories.MainRepository
import com.example.fattrack.data.services.responses.ResponseScanImage
import com.example.fattrack.data.services.responses.ResponseSearchFood
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class PredictViewModel(private val mainRepository: MainRepository) : ViewModel() {
    //live data
    private val _predictResponse = MutableLiveData<ResponseScanImage>()
    val predictResponse: LiveData<ResponseScanImage> = _predictResponse

    private val _searchResponse = MutableLiveData<ResponseSearchFood?>()
    val searchResponse: LiveData<ResponseSearchFood?> = _searchResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    //predict image
    fun predictImage(image: File) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                //send
                val response = mainRepository.predictImage(image)

                //handle response
                response.onSuccess {
                    _predictResponse.value = it
                } .onFailure {
                    _errorMessage.value = "Failed to predict image. Please try again with a different image."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again later."
            } finally {
                _isLoading.value = false
            }
        }
    }



    // Search by name
    fun searchFood(nama: String) {
        _isLoading.value = true
        _searchResponse.value = null

        // validate
        if (nama.isBlank()) {
            _errorMessage.value = "Please enter a food name."
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                val filteredName = nama.trim().lowercase(Locale.ROOT)
                val response = mainRepository.searchFood(filteredName)

                response.onSuccess{
                    _searchResponse.value = it
                }.onFailure {
                    _errorMessage.value = "An error occurred while searching for food."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred while searching for food."
            } finally {
                _isLoading.value = false
            }
        }
    }


}