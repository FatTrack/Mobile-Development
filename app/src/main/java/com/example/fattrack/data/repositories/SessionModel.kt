package com.example.fattrack.data.repositories

data class SessionModel(
    val idUser: String,
    val token: String,
    val email: String,
    val isLogin: Boolean = false
)