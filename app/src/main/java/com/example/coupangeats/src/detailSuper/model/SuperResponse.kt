package com.example.coupangeats.src.detailSuper.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class SuperResponse(
    @SerializedName("result") val result: SuperResponseResult
) : BaseResponse()
