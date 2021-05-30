package com.example.coupangeats.src.superSearch.model

import com.google.gson.annotations.SerializedName

data class BaseSuperInfo(
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("imageUrl") val url: ArrayList<String>,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("totalReview") val totalReview: String?,
    @SerializedName("distance") val distance: String,
    @SerializedName("coupon") val coupon: String?,
    @SerializedName("deliveryPrice") val deliveryPrice: String?,
    @SerializedName("markIcon") val markIcon: String?,
    @SerializedName("deliveryTime") val deliveryTime: String
)
