package com.example.coupangeats.src.eventItem.model

import com.google.gson.annotations.SerializedName

data class EventItemInfo(
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("title") val title: String
)
