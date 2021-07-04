package com.example.coupangeats.src.reviewWrite.model

import com.google.gson.annotations.SerializedName

data class ReviewWriteModifyApplyRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("badReason") val badReason: String?,
    @SerializedName("contents") val contents: String,
    @SerializedName("modifiedImageFlag") val imageFlag: String,
    @SerializedName("imageUrls") val imageUrls: ArrayList<String>?,
    @SerializedName("menuReviews") val menuReview: ArrayList<menuReview>?,
    @SerializedName("deliveryReview") val deliveryReview: DeliveryReview?
)
