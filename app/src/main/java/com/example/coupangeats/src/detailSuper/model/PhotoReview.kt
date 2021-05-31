package com.example.coupangeats.src.detailSuper.model

import com.google.gson.annotations.SerializedName

data class PhotoReview(
    @SerializedName("reviewIdx") val reviewIdx: Int,
    @SerializedName("imageUrl") val img: String,
    @SerializedName("contents") val content: String,
    @SerializedName("rating") val rating: Double
)
