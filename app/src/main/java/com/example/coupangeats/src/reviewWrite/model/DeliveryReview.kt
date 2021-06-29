package com.example.coupangeats.src.reviewWrite.model

import com.google.gson.annotations.SerializedName

data class DeliveryReview(
    @SerializedName("deliveryLiked") val deliveryLiked: String?,
    @SerializedName("deliveryBadReason") val deliveryBadReason: String?,
    @SerializedName("deliveryComment") val deliveryComment: String?
)
