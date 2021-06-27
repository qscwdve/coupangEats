package com.example.coupangeats.src.main.order.past.model

import com.google.gson.annotations.SerializedName

data class PastOrderMenu(
    @SerializedName("count") val count: Int,
    @SerializedName("menuName") val menuName: String,
    @SerializedName("menuDetail") val menuDetail: String?,
    @SerializedName("menuPrice") val menuPrice: String,
    @SerializedName("menuLiked") val menuLiked: String?
)
