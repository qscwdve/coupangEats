package com.example.coupangeats.src.deliveryAddressSetting.model

import com.google.gson.annotations.SerializedName

data class DeliveryAddressAddRequest(
    @SerializedName("address") val address : String,
    @SerializedName("roadAddress") val roadAddress : String,
    @SerializedName("detailAddress") val detailAddress : String? = null,
    @SerializedName("aliasType") val aliasType : String,
    @SerializedName("alias") val alias : String? = null,
    @SerializedName("userIdx") val userIdx : Int,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String
)
