package com.example.coupangeats.src.deliveryAddressSetting.detail.model

import com.google.gson.annotations.SerializedName

data class DeliveryAddressModifyRequest(
    @SerializedName("address") val address: String,
    @SerializedName("roadAddress") val roadAddress: String,
    @SerializedName("detailAddress") val detailAddress: String?,
    @SerializedName("aliasType") val aliasType: String,
    @SerializedName("alias") val alias: String?,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String
)
