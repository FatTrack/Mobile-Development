package com.example.fattrack.data.repositories

import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.services.responses.ResponseArticle
import com.example.fattrack.data.services.responses.ResponseDetailArticle
import com.example.fattrack.data.services.responses.ResponseSearchArticle
import com.example.fattrack.data.services.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull

class ArticleRepository (private val apiService: ApiService, private val authPreferences: AuthPreferences){

    suspend fun getListArticles(): Result<ResponseArticle> {
        val token = authPreferences.getSession().firstOrNull()?.token
        if (token == null) {
            Result.failure<Throwable>(Exception("Token not found"))
        }

        return try {
            // get API
            val response = token?.let { apiService.getArticles("Bearer $token") }

            if (response !== null && response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                // Log error
                val errorMessage = response?.message() ?: "Unknown error"
                Result.failure(Exception("Error: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    //article by id
    suspend fun getDetailArticle(id: String): Result<ResponseDetailArticle> {
        val token = authPreferences.getSession().firstOrNull()?.token
        if (token == null) {
            Result.failure<Throwable>(Exception("Token not found"))
        }

        return try {
            // get API
            val response = token?.let { apiService.getDetailArticle("Bearer $token", id) }

            if (response !== null && response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                // Log error
                val errorMessage = response?.message() ?: "Unknown error"
                Result.failure(Exception("Error: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    //search article by title
    suspend fun searchArticle(title: String): Result<ResponseSearchArticle> {
        val token = authPreferences.getSession().firstOrNull()?.token
        if (token == null) {
            Result.failure<Throwable>(Exception("Token not found"))
        }

        return try {
            // get API
            val response = token?.let { apiService.searchArticle("Bearer $token", title) }

            if (response !== null && response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                // Log error
                val errorMessage = response?.message() ?: "Unknown error"
                Result.failure(Exception("Error: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    companion object {
        fun getInstance (apiService: ApiService, authPreferences: AuthPreferences): ArticleRepository {
            return ArticleRepository(apiService, authPreferences)
        }
    }

}