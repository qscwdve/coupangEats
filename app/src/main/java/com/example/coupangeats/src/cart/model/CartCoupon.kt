package com.example.coupangeats.src.cart.model

import com.google.gson.annotations.SerializedName

data class CartCoupon(
    @SerializedName("redeemStatus") val redeemStatus: String?,
    @SerializedName("couponCount") val couponCount: Int,
    @SerializedName("couponIdx") val couponIdx: Int?,
    @SerializedName("discountPrice") val price: Int
)
