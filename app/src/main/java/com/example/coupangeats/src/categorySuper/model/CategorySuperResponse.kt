package com.example.coupangeats.src.categorySuper.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class CategorySuperResponse(
    @SerializedName("result") val result : CategorySuperResponseResult
) : BaseResponse()
