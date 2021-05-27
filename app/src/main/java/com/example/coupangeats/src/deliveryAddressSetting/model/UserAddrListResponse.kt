package com.example.coupangeats.src.deliveryAddressSetting.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class UserAddrListResponse(
    @SerializedName("result") val result: UserAddrListResponseResult
) : BaseResponse()
