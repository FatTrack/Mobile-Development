package com.example.fattrack.data.di

import android.content.Context
import com.example.fattrack.data.database.NotificationDatabase
import com.example.fattrack.data.datastore.DataStoreManager
import com.example.fattrack.data.datastore.NotificationScheduler
import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.pref.ProfilePreferences
import com.example.fattrack.data.pref.authSession
import com.example.fattrack.data.pref.profileDataStore
import com.example.fattrack.data.repositories.ArticleRepository
import com.example.fattrack.data.repositories.AuthRepository
import com.example.fattrack.data.repositories.DashboardRepository
import com.example.fattrack.data.repositories.MainRepository
import com.example.fattrack.data.repositories.NotificationRepository
import com.example.fattrack.data.services.retrofit.ApiConfig

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val authPreferences = AuthPreferences.getInstance(context.authSession)
        return AuthRepository.getInstance(authPreferences, apiService)
    }

    fun provideMainRepository(context: Context): MainRepository {
        val apiService = ApiConfig.getApiService()
        val authPreferences = AuthPreferences.getInstance(context.authSession)
        return MainRepository.getInstance(apiService, authPreferences)
    }

    fun provideDashboardRepository(context: Context): DashboardRepository {
        val apiService = ApiConfig.getApiService()
        val authPreferences = AuthPreferences.getInstance(context.authSession)
        return DashboardRepository.getInstance(apiService, authPreferences)
    }

    fun provideArticlesRepository(context: Context): ArticleRepository {
        val authPreferences = AuthPreferences.getInstance(context.authSession)
        val apiService = ApiConfig.getApiService()
        return ArticleRepository.getInstance(apiService, authPreferences)
    }

    fun provideProfilePreferences(context: Context): ProfilePreferences {
        return ProfilePreferences.getInstance(context.profileDataStore)
    }

    fun provideNotificationRepository(context: Context): NotificationRepository {
        val dataStoreManager = DataStoreManager(context)
        val notificationDao = NotificationDatabase.getDatabase(context).notificationDao()
        return NotificationRepository(dataStoreManager, notificationDao)
    }

    fun provideNotificationScheduler(context: Context): NotificationScheduler {
        return NotificationScheduler(context)
    }
}