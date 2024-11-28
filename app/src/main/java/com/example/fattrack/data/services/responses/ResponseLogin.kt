package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: LoginData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class User(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,
)

data class LoginData(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: User? = null
)
