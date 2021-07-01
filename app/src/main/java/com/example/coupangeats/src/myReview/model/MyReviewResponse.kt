package com.example.coupangeats.src.myReview.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class MyReviewResponse(
    @SerializedName("result") val result: MyReviewInfo
) : BaseResponse()
