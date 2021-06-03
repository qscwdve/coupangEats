package com.example.coupangeats.src.cart.model

import com.google.gson.annotations.SerializedName

data class OrderMenu(
    @SerializedName("menuIdx") val menuIdx: Int,
    @SerializedName("menuName") val menuName: String,
    @SerializedName("menuDetail") val menuDetail: String?,
    @SerializedName("count") val count: Int,
    @SerializedName("totalPrice") val totalPrice: Int
)
