package com.example.fattrack.data.services.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {

    companion object {
        fun getApiService(): ApiService {

            // Logging untuk membantu debugging
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Konfigurasi OkHttpClient dengan timeout
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS) // Timeout koneksi
                .readTimeout(30, TimeUnit.SECONDS)   // Timeout membaca data
                .writeTimeout(30, TimeUnit.SECONDS)  // Timeout menulis data
                .retryOnConnectionFailure(true)      // Retry jika koneksi gagal
                .build()

            // Konfigurasi Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("https://fat-track-api-123661394110.asia-southeast2.run.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
