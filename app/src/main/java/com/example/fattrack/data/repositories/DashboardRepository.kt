package com.example.fattrack.data.repositories

import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.services.responses.ResponseDashboardMonth
import com.example.fattrack.data.services.responses.ResponseDashboardWeek
import com.example.fattrack.data.services.responses.ResponseHistory
import com.example.fattrack.data.services.retrofit.ApiService
import kotlinx.coroutines.flow.first

class DashboardRepository(private val apiService: ApiService, private val authPreferences: AuthPreferences)  {
    //history
    suspend fun getHistory(): Result<ResponseHistory> {
        return try {
            // Cek isToken ready
            val idUser = authPreferences.getSession().first().idUser
            val token = authPreferences.getSession().first().token
            if (token.isEmpty() || idUser.isEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // get API
            val response = apiService.getHistory("Bearer $token", idUser)

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


    //Dashboard Week
    suspend fun getDashboardWeek(): Result<ResponseDashboardWeek> {
        return try {
            // Cek isToken ready
            val idUser = authPreferences.getSession().first().idUser
            val token = authPreferences.getSession().first().token
            if (token.isEmpty() || idUser.isEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // get API
            val response = apiService.getDashboardWeek("Bearer $token", idUser)

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



    //Dashboard Month
    suspend fun getDashboardMonth(): Result<ResponseDashboardMonth> {
        return try {
            // Cek isToken ready
            val idUser = authPreferences.getSession().first().idUser
            val token = authPreferences.getSession().first().token
            if (token.isEmpty() || idUser.isEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // get API
            val response = apiService.getDashboardMonth("Bearer $token", idUser)

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
        fun getInstance (apiService: ApiService, authPreferences: AuthPreferences): DashboardRepository {
            return DashboardRepository(apiService, authPreferences)
        }
    }
}