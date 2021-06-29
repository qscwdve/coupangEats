package com.example.coupangeats.src.review.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class ReviewDeleteResponse(
    @SerializedName("result") val result: ReviewDeleteResponseResult
) : BaseResponse()
