package com.example.fattrack.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fattrack.data.repositories.ArticleRepository
import com.example.fattrack.data.services.responses.DetailArticleData
import kotlinx.coroutines.launch

class DetailViewModel(private val articleRepository: ArticleRepository) : ViewModel() {

    // LiveData
    private val _articleDetail = MutableLiveData<DetailArticleData?>()
    val articleDetail: LiveData<DetailArticleData?> = _articleDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    // detail article
    fun fetchArticleDetail(articleId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val tokenError = "Token not found"
            try {
                // get detail article
                val result = articleRepository.getDetailArticle(articleId)

                result.onSuccess { response ->
                    _articleDetail.value = response.data
                }.onFailure { throwable ->
                    _errorMessage.value = if (throwable.message == tokenError) {
                        "Autentikasi gagal, silakan login ulang"
                    } else {
                        "Something went wrong. Please try again later!"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again later!"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
