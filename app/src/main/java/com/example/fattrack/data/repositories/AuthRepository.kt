package com.example.fattrack.data.repositories

import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.services.responses.ResponseLogin
import com.example.fattrack.data.services.responses.ResponseRegister
import com.example.fattrack.data.services.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class AuthRepository (private val apiService: ApiService, private val authPreferences: AuthPreferences) {
    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            authPreferences: AuthPreferences,
            apiService: ApiService
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, authPreferences)
            }.also { instance = it }
    }


    //register
    suspend fun register(email: String, nama: String, password: String) : Result<ResponseRegister> {
        return try {
            // call API
            val response = apiService.register(email, nama, password)

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


    //login
    suspend fun login(email: String, password: String) : Result<ResponseLogin> {
        return try {
            // call API
            val response = apiService.login(email, password)

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
                Result.failure(Exception("Articles Error with code: ${response.code()}, message: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Save session
    suspend fun saveSession(user: SessionModel) {
        authPreferences.saveSession(user)
    }

    // Get session
    fun getSession(): Flow<SessionModel> {
        return authPreferences.getSession()
    }

    // Logout
    suspend fun logout() {
        authPreferences.logout()
    }
}