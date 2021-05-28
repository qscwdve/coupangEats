package com.example.coupangeats.src.main.home.model.HomeInfo

import com.google.gson.annotations.SerializedName

data class RecommendStores(
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("imageUrls") val url: List<String>,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("markIcon") val markIcon: String?,
    @SerializedName("totalReview") val totalReview: String?,
    @SerializedName("distance") val distance: String,
    @SerializedName("deliveryPrice") val deliveryPrice: String?,
    @SerializedName("deliveryTime") val deliveryTime: String,
    @SerializedName("coupon") val coupon: String?
)
