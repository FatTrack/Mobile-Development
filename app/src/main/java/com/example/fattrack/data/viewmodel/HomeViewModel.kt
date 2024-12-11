package com.example.fattrack.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fattrack.data.repositories.MainRepository
import com.example.fattrack.data.services.responses.HomeData
import com.example.fattrack.data.services.responses.ResponseUpdateProfile
import com.example.fattrack.data.services.responses.UserData
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel (private val  mainRepository: MainRepository ) : ViewModel()  {

    private val _userResponse = MutableLiveData<UserData?>(null)
    val userResponse: LiveData<UserData?> = _userResponse

    private val _homeResponse = MutableLiveData<HomeData?>()
    val homeResponse: LiveData<HomeData?> = _homeResponse

    private val _totalKalori = MutableLiveData<Int?>()
    val totalKalori: LiveData<Int?> = _totalKalori

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _targetKalori = MutableLiveData<Int>(2100)
    val targetKalori: LiveData<Int> = _targetKalori

    private val _updateProfileResponse = MutableLiveData<ResponseUpdateProfile>()
    val updateProfileResponse: LiveData<ResponseUpdateProfile> = _updateProfileResponse


    //update profile
    fun updateProfile(photoProfile: File? = null, name: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = mainRepository.updateProfile(photoProfile, name)

                //response handling
                response.onSuccess{
                    _updateProfileResponse.value = it
                }.onFailure {
                    _errorMessage.value = "Update unsuccessfully. Please try again!"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again later!"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserById() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val result = mainRepository.getUserById()
                result.onSuccess { response ->
                    _userResponse.value = response.data
                }.onFailure {
                    _errorMessage.value = "Data Not Found. Please try again!"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again later!"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchHomeData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = mainRepository.homeData()
                result.onSuccess { response ->
                    _homeResponse.value = response.data
                    _totalKalori.value = response.data?.totalKalori
                }.onFailure {
                    _errorMessage.value = "Data is not available."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again later!"
            } finally {
                _isLoading.value = false
            }
        }
    }


}