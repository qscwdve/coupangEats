package com.example.coupangeats.src.reviewWrite.model

import com.google.gson.annotations.SerializedName

data class menuReview(
    @SerializedName("orderMenuIdx") val orderMenuIdx: Int,
    @SerializedName("menuLiked") val menuLiked: String?,
    @SerializedName("menuBadReason") val badReason: String?,
    @SerializedName("menuComment") val menuComment: String?
)
