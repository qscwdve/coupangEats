package com.example.coupangeats.src.main.search.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class SuperCategoryResponseResult(
    @SerializedName("categoryName") val name: String,
    @SerializedName("imageUrl") val img : String
)
