package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseDashboardMonth(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<MonthDataDash?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class MonthDataDash(

	@field:SerializedName("week")
	val week: Int? = null,

	@field:SerializedName("start-end")
	val startEnd: String? = null,

	@field:SerializedName("total_calories")
	val totalCalories: Int? = null
)
