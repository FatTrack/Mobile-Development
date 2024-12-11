package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseUpdateProfile(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: UpdateProfileData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class UpdateProfileData(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("updates")
	val updates: Updates? = null
)

data class Updates(

	@field:SerializedName("foto_profile")
	val fotoProfile: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,
)
