package com.example.coupangeats.src.reviewWrite.model

import com.google.gson.annotations.SerializedName

data class ReviewInfo(
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("orderMenus") val orderMenu: ArrayList<orderMenus>
)
