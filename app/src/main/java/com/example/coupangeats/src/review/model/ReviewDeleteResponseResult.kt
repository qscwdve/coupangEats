package com.example.coupangeats.src.review.model

import com.google.gson.annotations.SerializedName

data class ReviewDeleteResponseResult(
    @SerializedName("updatedCount") val updatedCount: Int
)
