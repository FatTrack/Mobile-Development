package com.example.fattrack.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fattrack.data.di.Injection
import com.example.fattrack.data.pref.ProfilePreferences
import com.example.fattrack.data.repositories.ArticleRepository
import com.example.fattrack.data.repositories.AuthRepository
import com.example.fattrack.data.repositories.MainRepository
import com.example.fattrack.data.viewmodel.ArticlesViewModel
import com.example.fattrack.data.viewmodel.LoginViewModel
import com.example.fattrack.data.viewmodel.MainViewModel
import com.example.fattrack.data.viewmodel.PredictViewModel
import com.example.fattrack.data.viewmodel.ProfileViewModel
import com.example.fattrack.data.viewmodel.RegisterViewModel

class ViewModelFactory(
    private val articleRepository: ArticleRepository,
    private val profilePreferences: ProfilePreferences,
    private val authRepository: AuthRepository,
    private val mainRepository: MainRepository,
    ) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context : Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideArticlesRepository(),
                        Injection.provideProfilePreferences(context),
                        Injection.provideAuthRepository(context),
                        Injection.provideMainRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(ArticlesViewModel::class.java) -> {
                ArticlesViewModel(articleRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(profilePreferences, authRepository) as T
            }
            modelClass.isAssignableFrom(PredictViewModel::class.java) -> {
                PredictViewModel(mainRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}