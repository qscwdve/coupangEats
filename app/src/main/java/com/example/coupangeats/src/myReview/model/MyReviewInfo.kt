package com.example.coupangeats.src.myReview.model

import com.google.gson.annotations.SerializedName

data class MyReviewInfo(
    @SerializedName("reviewIdx") val reviewIdx: Int,
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("writingTimeStamp") val writingTimeStamp: String,
    @SerializedName("contents") val content: String,
    @SerializedName("orderMenus") val orderMenus: String,
    @SerializedName("reviewImageUrls") val imgList: ArrayList<String>?,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("remainingReviewTime") val remainingReviewTime: Int
)
