package com.example.fattrack.data.services.responses

import com.google.gson.annotations.SerializedName

data class ResponseHistory(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<HistoryDataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class HistoryDataItem(

	@field:SerializedName("food_name")
	val foodName: String? = null,

	@field:SerializedName("prediction_date")
	val predictionDate: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("calories")
	val calories: Int? = null
)
