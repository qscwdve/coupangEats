package com.example.coupangeats.src.detailSuper.model

import com.google.gson.annotations.SerializedName

data class MenuList(
    @SerializedName("menuIdx") val menuIdx : Int,
    @SerializedName("menuName") val menuName: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("price") val price: Int,
    @SerializedName("introduction") val introduce: String?,
    @SerializedName("bestOrderMenu") val manyOrder: String?,
    @SerializedName("bestReview") val manyReview: String?
)
