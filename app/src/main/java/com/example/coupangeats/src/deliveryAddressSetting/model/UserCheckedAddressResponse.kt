package com.example.coupangeats.src.deliveryAddressSetting.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class UserCheckedAddressResponse(
    @SerializedName("result") val result: UserCheckedAddressResponseResult
) : BaseResponse()
