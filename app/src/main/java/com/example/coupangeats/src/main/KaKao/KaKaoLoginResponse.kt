package com.example.coupangeats.src.main.KaKao

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class KaKaoLoginResponse(
    @SerializedName("result") val result: KaKaoLoginResponseResult
): BaseResponse()
