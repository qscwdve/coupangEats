package com.example.coupangeats.src.main.home.model.HomeInfo

import com.google.gson.annotations.SerializedName

data class HomeInfoRequest(
    @SerializedName("lat") val lat: String = "",
    @SerializedName("lon") val lon: String = "",
    @SerializedName("sort") var sort: String = "",
    @SerializedName("cheetah") var cheetah: String? = null,
    @SerializedName("coupon") var coupon: String? = null,
    @SerializedName("ordermin") var ordermin: Int? = null,
    @SerializedName("deliverymin") var deliverymin: Int? = null,
    @SerializedName("cursor") val cursor: Int = 1,
    @SerializedName("numOfRows") val numOfRows: Int = 25
)
