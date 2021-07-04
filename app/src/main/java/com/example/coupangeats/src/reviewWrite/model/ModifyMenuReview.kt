package com.example.coupangeats.src.reviewWrite.model

import com.google.gson.annotations.SerializedName

data class ModifyMenuReview(
    @SerializedName("orderMenuIdx") val orderMenuIdx: Int,
    @SerializedName("menuName") val menuName: String,
    @SerializedName("menuDetail") val menuDetail: String?,
    @SerializedName("menuLiked") val menuLiked: String?,
    @SerializedName("menuBadReason") val badReason: String?,
    @SerializedName("menuComment") val menuComment: String?
)
