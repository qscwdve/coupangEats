package com.example.coupangeats.src.main.home.model.userCheckAddress

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class UserCheckResponse(
    @SerializedName("result") val result: UserCheckResponseResult
) : BaseResponse()
