package com.example.coupangeats.src.main.home.model.HomeInfo

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class HomeInfoResponse(
    @SerializedName("result") val result: HomeInfoResponseResult
) : BaseResponse()
