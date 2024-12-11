package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseHome(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: HomeData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class HomeData(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("totalKarbohidrat")
	val totalKarbohidrat: Any? = null,

	@field:SerializedName("totalProtein")
	val totalProtein: Any? = null,

	@field:SerializedName("totalKalori")
	val totalKalori: Int? = null,

	@field:SerializedName("totalLemak")
	val totalLemak: Any? = null,

	@field:SerializedName("message")
	val message: String? = null
)
