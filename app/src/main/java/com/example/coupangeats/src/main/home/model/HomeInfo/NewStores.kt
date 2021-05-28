package com.example.coupangeats.src.main.home.model.HomeInfo

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class NewStores(
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("imageUrl") val url: String,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("totalReview") val totalReview: String?,
    @SerializedName("distance") val distance: String,
    @SerializedName("coupon") val coupon: String?,
    @SerializedName("deliveryPrice") val deliveryPrice: String?
)
