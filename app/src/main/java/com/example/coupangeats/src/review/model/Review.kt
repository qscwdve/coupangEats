package com.example.coupangeats.src.review.model

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("reviewIdx") val reviewIdx: Int,
    @SerializedName("isWriter") val isWriter: String?,
    @SerializedName("writerName") val writerName: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("writingTimeStamp") val writingTimeStamp: String,
    @SerializedName("imageUrls") val imageUrls: ArrayList<String>?,
    @SerializedName("contents") val text: String,
    @SerializedName("orderMenus") val orderMenus: String,
    @SerializedName("likeCount") var likeCount: Int,
    @SerializedName("isLiked") var isLiked: String?,
    @SerializedName("isModifiable") val isModifiable: String? = null
)
