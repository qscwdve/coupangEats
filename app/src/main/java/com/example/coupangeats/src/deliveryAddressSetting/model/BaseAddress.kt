package com.example.coupangeats.src.deliveryAddressSetting.model

import com.google.gson.annotations.SerializedName

data class BaseAddress(
    @SerializedName("addressIdx") val addressIdx: Int,
    @SerializedName("mainAddress") val mainAddress: String,
    @SerializedName("subAddress") val subAddress: String
)
