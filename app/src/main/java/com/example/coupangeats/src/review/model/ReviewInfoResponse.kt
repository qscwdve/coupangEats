package com.example.coupangeats.src.review.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class ReviewInfoResponse(
    @SerializedName("result") val result: ReviewInfo
) : BaseResponse()
