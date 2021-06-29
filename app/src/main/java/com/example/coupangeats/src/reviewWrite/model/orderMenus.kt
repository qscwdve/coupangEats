package com.example.coupangeats.src.reviewWrite.model

import com.google.gson.annotations.SerializedName

data class orderMenus(
    @SerializedName("orderMenuIdx") val orderMenuIdx: String,
    @SerializedName("orderMenuName") val orderMenuName: String,
    @SerializedName("orderMenuDetail") val orderMenuSide: String?
)
