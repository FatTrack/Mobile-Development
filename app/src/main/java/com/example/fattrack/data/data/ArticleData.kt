package com.example.fattrack.data.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleData(
    val title: String,
    val name: String,
    val date: String,
    val description: String,
    val photo: String
) : Parcelable