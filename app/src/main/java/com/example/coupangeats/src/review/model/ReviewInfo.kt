package com.example.coupangeats.src.review.model

import com.google.gson.annotations.SerializedName

data class ReviewInfo(
    @SerializedName("title") val title: String,
    @SerializedName("totalRating") val totalRating: Double,
    @SerializedName("reviewCount") val reviewCount: String,
    @SerializedName("reviews") val reviews: ArrayList<Review>?
)
