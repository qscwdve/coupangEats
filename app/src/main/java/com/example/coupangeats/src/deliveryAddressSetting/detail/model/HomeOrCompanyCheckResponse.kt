package com.example.coupangeats.src.deliveryAddressSetting.detail.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class HomeOrCompanyCheckResponse(
    @SerializedName("result") val result : HomeOrCompanyCheckResponseResult
) : BaseResponse()
