package com.example.coupangeats.src.cart.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName

data class OrderRequest(
   @SerializedName("address") val address: String,
   @SerializedName("storeIdx") val storeIdx: Int,
   @SerializedName("orderMenus") val orderMenu: ArrayList<OrderMenu>,
   @SerializedName("couponIdx") val couponIdx: Int?,
   @SerializedName("orderPrice") val orderPrice: Int,
   @SerializedName("deliveryPrice") val deliveryPrice: Int,
   @SerializedName("discountPrice") val discountPrice: Int,
   @SerializedName("totalPrice") val totalPrice: Int,
   @SerializedName("storeRequests") val storeRequest: String?,
   @SerializedName("checkEchoProduct") val checkEchoProduct: String,
   @SerializedName("deliveryRequests") val deliveryRequest: String?,
   @SerializedName("payType") val payType: String,
   @SerializedName("userIdx") val userIdx: Int
)
