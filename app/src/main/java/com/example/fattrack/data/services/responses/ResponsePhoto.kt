package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponsePhoto(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: PhotoData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PhotoData(

	@field:SerializedName("foto_profile")
	val fotoProfile: String? = null
)
