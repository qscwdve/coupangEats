package com.example.coupangeats.src.main.order.model

import com.google.gson.annotations.SerializedName

data class OrderMenu(
    @SerializedName("count") val count: Int,
    @SerializedName("menuName") val menuName: String,
    @SerializedName("menuDetail") val menuDetail: String?,
    @SerializedName("menuPrice") val menuPrice: String
)
