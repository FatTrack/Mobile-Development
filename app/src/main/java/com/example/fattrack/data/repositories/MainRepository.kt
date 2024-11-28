package com.example.fattrack.data.repositories

import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.services.responses.ResponseScanImage
import com.example.fattrack.data.services.retrofit.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody

class MainRepository(private val apiService: ApiService, private val authPreferences: AuthPreferences) {
    suspend fun predictImage(image: MultipartBody.Part): Result<ResponseScanImage> {
        return try {
            // Cek isToken ready
            val token = authPreferences.getSession().first().idUser
            if (token.isEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // get API
            val response = apiService.predict(image, token)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                // Log error
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Error because $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    companion object {
        fun getInstance (apiService: ApiService, authPreferences: AuthPreferences): MainRepository {
            return MainRepository(apiService, authPreferences)
        }
    }
}