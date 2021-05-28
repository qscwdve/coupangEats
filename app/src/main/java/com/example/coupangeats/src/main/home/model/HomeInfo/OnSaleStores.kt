package com.example.coupangeats.src.main.home.model.HomeInfo

import com.google.gson.annotations.SerializedName

data class OnSaleStores(
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("imageUrl") val url: String,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("totalReview") val totalReview: String?,
    @SerializedName("distance") val distance: String,
    @SerializedName("coupon") val coupon: String?
)
