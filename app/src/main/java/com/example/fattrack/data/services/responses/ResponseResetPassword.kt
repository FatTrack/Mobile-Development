package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseResetPassword(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: ResetData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ResetData(

	@field:SerializedName("message")
	val message: String? = null
)
