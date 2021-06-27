package com.example.coupangeats.src.superSearch.model

import com.google.gson.annotations.SerializedName

data class FilterRequest(
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String,
    @SerializedName("sort") var sort: String,
    @SerializedName("cheetah") var cheetah: String?,
    @SerializedName("coupon") var coupon: String?,
    @SerializedName("ordermin") var ordermin: Int?,
    @SerializedName("deliverymin") var deliverymin: Int?,
    @SerializedName("cursor") val cursor: Int,
    @SerializedName("numOfRows") val numOfRows: Int
)
