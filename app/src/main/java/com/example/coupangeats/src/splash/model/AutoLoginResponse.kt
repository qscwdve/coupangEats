package com.example.coupangeats.src.splash.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class AutoLoginResponse(
    @SerializedName("result") val result: AutoLoginResponseResult
) : BaseResponse()
