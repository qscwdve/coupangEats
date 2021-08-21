package com.example.coupangeats.src.event.model

import com.example.coupangeats.R
import com.google.gson.annotations.SerializedName

data class EventDetail(
    @SerializedName("eventIdx") val eventIdx: Int,
    @SerializedName("bannerUrl") val bannerUrl: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    val bannerTemp : Int = R.drawable.bbq_event
)
