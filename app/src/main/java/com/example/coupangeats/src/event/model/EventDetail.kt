package com.example.coupangeats.src.event.model

import com.google.gson.annotations.SerializedName

data class EventDetail(
    @SerializedName("eventIdx") val eventIdx: Int,
    @SerializedName("bannerUrl") val bannerUrl: String,
    @SerializedName("endDate") val endDate: String
)
