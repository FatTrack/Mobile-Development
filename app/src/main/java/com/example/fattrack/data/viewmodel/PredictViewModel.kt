package com.example.fattrack.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.repositories.MainRepository
import com.example.fattrack.data.services.responses.ResponseScanImage
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class PredictViewModel(private val mainRepository: MainRepository) : ViewModel() {
    //live data
    private val _predictResponse = MutableLiveData<ResponseScanImage>()
    val predictResponse: LiveData<ResponseScanImage> = _predictResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    //predict image
    fun predictImage(image: MultipartBody.Part) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                //send
                val response = mainRepository.predictImage(image)

                //handle response
                response.onSuccess {
                    _predictResponse.value = it
                    Log.d("PredictResponse", "Success: $it")
                } .onFailure {
                    _errorMessage.value = it.message.toString()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message.toString()
            } finally {
                _isLoading.value = false
            }
        }
    }
}