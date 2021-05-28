package com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList

import com.example.coupangeats.src.deliveryAddressSetting.model.DeliveryAddressResponseResult
import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class DeliveryAddressResponse(
    @SerializedName("result") val result : DeliveryAddressResponseResult
) : BaseResponse()
