package com.example.coupangeats.src.main.home.model.HomeInfo

import com.google.gson.annotations.SerializedName

data class HomeInfoRequest(
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String,
    @SerializedName("sort") val sort: String,
    @SerializedName("cheetah") val cheetah: String?,
    @SerializedName("coupon") val coupon: String?,
    @SerializedName("ordermin") val ordermin: Int?,
    @SerializedName("deliverymin") val deliverymin: Int?,
    @SerializedName("cursor") val cursor: Int,
    @SerializedName("numOfRows") val numOfRows: Int
)
