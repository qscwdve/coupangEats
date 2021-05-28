package com.example.coupangeats.src.main.home.model.HomeInfo

import com.google.gson.annotations.SerializedName

data class Events(
    @SerializedName("eventIdx") val eventIdx: Int,
    @SerializedName("bannerUrl") val url: String
)
