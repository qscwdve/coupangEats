package com.example.coupangeats.src.discount.model

import com.google.gson.annotations.SerializedName

data class SuperCoupon(
    @SerializedName("userCouponIdx") val userCouponIdx: Int,
    @SerializedName("couponName") val couponName: String,
    @SerializedName("discountPrice") val discountPrice: String,
    @SerializedName("minOrderPrice") val minOrderPrice: String,
    @SerializedName("expirationDate") val expirationDate: String,
    @SerializedName("isAvailable") val isAvailable: String
)
