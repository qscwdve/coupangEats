package com.example.coupangeats.src.main.home.model.HomeInfo

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class StoreCategories(
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("imageUrl") val url: String
)
