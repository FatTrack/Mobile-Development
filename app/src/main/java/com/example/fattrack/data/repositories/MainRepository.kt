package com.example.fattrack.data.repositories

import android.util.Log
import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.services.responses.ResponseHome
import com.example.fattrack.data.services.responses.ResponseScanImage
import com.example.fattrack.data.services.responses.ResponseSearchFood
import com.example.fattrack.data.services.responses.ResponseUpdateProfile
import com.example.fattrack.data.services.responses.ResponseUser
import com.example.fattrack.data.services.retrofit.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainRepository(private val apiService: ApiService, private val authPreferences: AuthPreferences) {
    //predict / scan image
    suspend fun predictImage(image: File): Result<ResponseScanImage> {
        return try {
            // Cek isToken ready
            val idUser = authPreferences.getSession().first().idUser
            val token = authPreferences.getSession().first().token
            if (token.isEmpty() || idUser.isEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            //converse to multipart
            val imagePart = image.toMultipartBody()
            val idUserPart = idUser.toRequestBody("text/plain".toMediaTypeOrNull())

            val url = "https://fastapi-tensorflow-app-123661394110.asia-southeast2.run.app/predict_image"

            // get API
            val response = apiService.predict( url, "Bearer $token", idUserPart, imagePart)

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


    //search food
    suspend fun searchFood(nama: String): Result<ResponseSearchFood> {
        return try {
            // Cek isToken ready
            val idUser = authPreferences.getSession().first().idUser
            val token = authPreferences.getSession().first().token
            if (token.isEmpty() || idUser.isEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // get API
            val response = apiService.searhFood("Bearer $token", nama)

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
            Log.d("SearchFood", e.message.toString())
            Result.failure(e)
        }
    }


    //user by id
    suspend fun getUserById(): Result<ResponseUser> {
        return try {
            // Cek isToken ready
            val idUser = authPreferences.getSession().first().idUser
            val token = authPreferences.getSession().first().token
            if (token.isEmpty() || idUser.isEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // get API
            val response = apiService.getUserById("Bearer $token", idUser)

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


    //update profile
    suspend fun updateProfile(image: File? = null, newName: String): Result<ResponseUpdateProfile> {
        return try {
            // Cek isToken ready
            val idUser = authPreferences.getSession().first().idUser
            val token = authPreferences.getSession().first().token
            if (token.isEmpty() || idUser.isEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // get API
            val response =
            if (image != null) {
                apiService.updateProfile("Bearer $token", idUser.toRequestBody(), newName.toRequestBody(), image.toMultipartBody2())
            } else {
                apiService.updateProfile("Bearer $token", idUser.toRequestBody(), newName.toRequestBody())
            }


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


    //Home
    suspend fun homeData(): Result<ResponseHome> {
        return try {
            // Cek isToken ready
            val idUser = authPreferences.getSession().first().idUser
            val token = authPreferences.getSession().first().token
            if (token.isEmpty() || idUser.isEmpty()) {
                Result.failure<Throwable>(Exception("Token not found"))
            }

            // get API
            val response = apiService.getHome("Bearer $token", idUser)

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



    private fun File.toMultipartBody(): MultipartBody.Part {
        val requestBody = this.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("uploaded_file", this.name, requestBody)
    }

    private fun File.toMultipartBody2(): MultipartBody.Part {
        val requestBody = this.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", this.name, requestBody)
    }


    companion object {
        fun getInstance (apiService: ApiService, authPreferences: AuthPreferences): MainRepository {
            return MainRepository(apiService, authPreferences)
        }
    }
}