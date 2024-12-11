package com.example.fattrack.data.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NutritionData(
    val deskripsi: String?,
    val kalori: Int?,
    val karbohidrat: Double?,
    val lemak: Double?,
    val nama: String?,
    val protein: Double?,
    val image: String?
) : Parcelable

