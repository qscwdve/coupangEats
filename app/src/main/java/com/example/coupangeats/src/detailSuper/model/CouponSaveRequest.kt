package com.example.coupangeats.src.detailSuper.model

import com.google.gson.annotations.SerializedName

data class CouponSaveRequest(
    @SerializedName("couponNumber") val couponNumber: String,
    @SerializedName("userIdx") val userIdx: Int
)
