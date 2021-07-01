package com.example.coupangeats.src.review.model

import com.google.gson.annotations.SerializedName

data class ReviewHelpRequest(
    @SerializedName("userIdx") val userIdx: Int,
    @SerializedName("reviewIdx") val reviewIdx: Int
)
