package com.example.coupangeats.src.deliveryAddressSetting.model

import com.google.gson.annotations.SerializedName

data class UserCheckedAddressResponseResult(
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String
)
