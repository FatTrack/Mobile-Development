package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseSearchFood(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<FoodDataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class FoodDataItem(

	@field:SerializedName("kalori")
	val kalori: Int? = null,

	@field:SerializedName("karbohidrat")
	val karbohidrat: Any? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("protein")
	val protein: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("lemak")
	val lemak: Any? = null,

	@field:SerializedName("image")
	val image: String? = null
)
