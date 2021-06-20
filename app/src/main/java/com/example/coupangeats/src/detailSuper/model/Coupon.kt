package com.example.coupangeats.src.detailSuper.detailSuperFragment.model

import com.google.gson.annotations.SerializedName

data class Coupon(
    @SerializedName("couponIdx") val conponIdx: Int,
    @SerializedName("discountPrice") val price: Int,
    @SerializedName("hasCoupon") val hasCoupon: String
)
