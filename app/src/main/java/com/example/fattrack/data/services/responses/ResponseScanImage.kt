package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseScanImage(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: PredictData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class NutritionalInfo(

	@field:SerializedName("kalori")
	val kalori: Int? = null,

	@field:SerializedName("karbohidrat")
	val karbohidrat: Any? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("protein")
	val protein: Any? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("lemak")
	val lemak: Any? = null,

	@field:SerializedName("image")
	val image: String? = null
)

data class PredictData(

	@field:SerializedName("nutritional_info")
	val nutritionalInfo: NutritionalInfo? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("predicted_class")
	val predictedClass: String? = null,

	@field:SerializedName("confidence")
	val confidence: Any? = null,

	//if error message
	@field:SerializedName("message")
	val message: String? = null
)
