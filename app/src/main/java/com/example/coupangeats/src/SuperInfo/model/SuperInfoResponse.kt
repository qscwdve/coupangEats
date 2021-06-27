package com.example.coupangeats.src.SuperInfo.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class SuperInfoResponse(
    @SerializedName("result") val result: SuperInfo
) : BaseResponse()
