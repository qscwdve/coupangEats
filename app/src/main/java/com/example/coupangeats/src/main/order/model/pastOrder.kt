package com.example.coupangeats.src.main.order.model

import com.example.coupangeats.src.main.order.past.model.PastOrderMenu
import com.google.gson.annotations.SerializedName

data class pastOrder(
    @SerializedName("orderIdx") val orderIdx: Int,
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("orderDate") val orderDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("orderMenus") val orderMenus: ArrayList<PastOrderMenu>,
    @SerializedName("orderPrice") val orderPrice: String,
    @SerializedName("deliveryPrice") val deliveryPrice: String,
    @SerializedName("discountPrice") val discountPrice: String,
    @SerializedName("totalPrice") val totalPrice: String,
    @SerializedName("payType") val payType: String,
    @SerializedName("reviewIdx") val reviewIdx: Int?,
    @SerializedName("reviewRating") val reviewRating: Int?
)
