package com.example.coupangeats.src.searchDetail.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class SearchDetailResponse(
    @SerializedName("result") val result: SearchDetailResponseResult
):BaseResponse()
