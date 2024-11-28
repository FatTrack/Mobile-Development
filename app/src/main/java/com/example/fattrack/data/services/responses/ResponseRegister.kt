package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseRegister(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: RegisterData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class RegisterData(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("message")
	val message: String? = null,
)
