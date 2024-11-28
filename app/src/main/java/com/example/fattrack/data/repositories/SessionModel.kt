package com.example.fattrack.data.repositories

data class SessionModel(
    val idUser: String,
    val email: String,
    val isLogin: Boolean = false
)