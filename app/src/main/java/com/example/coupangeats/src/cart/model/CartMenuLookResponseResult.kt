package com.example.coupangeats.src.cart.model

import com.google.gson.annotations.SerializedName

data class CartMenuLookResponseResult(
    @SerializedName("mainAddress") val mainAddress: String,
    @SerializedName("address") val address: String,
    @SerializedName("coupon") val coupon: CartCoupon,
    @SerializedName("payType") val payType: String?,
    @SerializedName("deliveryPrice") val deliveryPrice: Int,
    @SerializedName("cheetahDelivery") val cheetahDelivery: String
)
