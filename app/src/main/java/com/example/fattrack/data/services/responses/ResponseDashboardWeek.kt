package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseDashboardWeek(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<WeekDataDash?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class WeekDataDash(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("total_calories")
	val totalCalories: Int? = null
)
