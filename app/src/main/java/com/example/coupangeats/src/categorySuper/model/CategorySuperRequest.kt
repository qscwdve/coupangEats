package com.example.coupangeats.src.categorySuper.model

import com.google.gson.annotations.SerializedName

data class CategorySuperRequest(
    @SerializedName("lat") var lat: String = "",
    @SerializedName("lon") var lon: String = "",
    @SerializedName("category") var category: String = "",
    @SerializedName("sort") var sort: String = "recomm",
    @SerializedName("cheetah") var cheetah: String? = null,
    @SerializedName("coupon") var coupon: String? = null,
    @SerializedName("ordermin") var ordermin: Int? = null,
    @SerializedName("deliverymin") var deliverymin: Int? = null,
    @SerializedName("cursor") val cursor: Int = 1,
    @SerializedName("numOfRows") val numOfRows: Int = 25
)
