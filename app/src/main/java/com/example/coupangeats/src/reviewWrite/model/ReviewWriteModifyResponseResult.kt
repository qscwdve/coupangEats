package com.example.coupangeats.src.reviewWrite.model

import com.google.gson.annotations.SerializedName

data class ReviewWriteModifyResponseResult(
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("badReason") val badReason: String?,
    @SerializedName("contents") val contents: String,
    @SerializedName("imageUrls") val imgList: ArrayList<String>?,
    @SerializedName("menuReviews") val menuReviews: ArrayList<ModifyMenuReview>?,
    @SerializedName("deliveryReview") val deliveryReview: DeliveryReview?
)
