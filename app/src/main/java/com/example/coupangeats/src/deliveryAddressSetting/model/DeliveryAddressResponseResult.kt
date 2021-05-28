package com.example.coupangeats.src.deliveryAddressSetting.model

import com.google.gson.annotations.SerializedName

data class DeliveryAddressResponseResult(
    @SerializedName("createdIdx") val addressIdx : Int
)
