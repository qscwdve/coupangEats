package com.example.coupangeats.src.discount.model

import com.google.gson.annotations.SerializedName

data class ApplyCouponRequest(
    @SerializedName("couponNumber") val couponNumber: String,
    @SerializedName("userIdx") val userIdx: Int
)
