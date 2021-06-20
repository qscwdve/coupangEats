package com.example.coupangeats.src.detailSuper.detailSuperFragment.model

import com.google.gson.annotations.SerializedName

data class CouponSaveRequest(
    @SerializedName("couponIdx") val couponNumber: Int,
    @SerializedName("userIdx") val userIdx: Int
)
