package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseUser(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: UserData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class CreatedAt(

	@field:SerializedName("_nanoseconds")
	val nanoseconds: Int? = null,

	@field:SerializedName("_seconds")
	val seconds: Int? = null
)

data class UserData(

	@field:SerializedName("createdAt")
	val createdAt: CreatedAt? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("foto_profile")
	val fotoProfile: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
