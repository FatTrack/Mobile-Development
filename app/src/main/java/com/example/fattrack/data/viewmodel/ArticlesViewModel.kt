package com.example.fattrack.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fattrack.data.repositories.ArticleRepository
import com.example.fattrack.data.services.responses.DataItem
import kotlinx.coroutines.launch

class ArticlesViewModel(private val articleRepository: ArticleRepository) : ViewModel() {

    // LiveData untuk menampung daftar artikel
    private val _articles = MutableLiveData<List<DataItem>>()
    val articles: LiveData<List<DataItem>> = _articles

    // LiveData untuk menampung status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData untuk menampung pesan error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    fun fetchArticles() {
        _isLoading.value = true // Set loading state ke true
        _errorMessage.value = null // Reset error message sebelum memulai

        viewModelScope.launch {
            try {
                val result = articleRepository.getListArticles()
                result.onSuccess { response ->
                    _articles.value = response.data?.filterNotNull() // Set hasil ke LiveData
                }.onFailure { throwable ->
                    _errorMessage.value = throwable.message // Set pesan error
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message // Handle error tak terduga
            } finally {
                _isLoading.value = false // Set loading state ke false
            }
        }
    }
}

