package com.example.coupangeats.src.deliveryAddressSetting.detail.model

import com.google.gson.annotations.SerializedName

data class DeliveryAddressModifyRequest(
    @SerializedName("detailAddress") val detailAddress: String?,
    @SerializedName("aliasType") val aliasType: String,
    @SerializedName("alias") val alias: String?
)
