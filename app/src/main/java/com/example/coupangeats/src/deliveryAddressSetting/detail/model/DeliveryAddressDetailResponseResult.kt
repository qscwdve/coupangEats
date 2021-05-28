package com.example.coupangeats.src.deliveryAddressSetting.detail.model

import com.google.gson.annotations.SerializedName

data class DeliveryAddressDetailResponseResult(
    @SerializedName("address") val address: String,
    @SerializedName("roadAddress") val roadAddress: String,
    @SerializedName("detailAddress") val detailAddress: String?,
    @SerializedName("aliasType") val aliasType: String,
    @SerializedName("alias") val alias: String?
)
