package com.example.coupangeats.src.reviewWrite.model

import com.google.gson.annotations.SerializedName

data class ReviewWriteCreateRequest(
    @SerializedName("orderIdx") val orderIdx: Int,
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("badReason") val badReason: String?,
    @SerializedName("contents") val contents: String,
    @SerializedName("imageUrls") val imageUrls: ArrayList<String>?,
    @SerializedName("menuReviews") val menuReview: ArrayList<menuReview>?,
    @SerializedName("deliveryReview") val deliveryReview: DeliveryReview?,
    @SerializedName("userIdx") val userIdx: Int
)
