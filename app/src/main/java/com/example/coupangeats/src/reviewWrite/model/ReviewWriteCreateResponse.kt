package com.example.coupangeats.src.reviewWrite.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class ReviewWriteCreateResponse(
    @SerializedName("result") val result: ReviewWriteCreateResponseResult
): BaseResponse()
