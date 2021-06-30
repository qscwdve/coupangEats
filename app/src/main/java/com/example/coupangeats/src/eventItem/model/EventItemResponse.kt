package com.example.coupangeats.src.eventItem.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class EventItemResponse(
    @SerializedName("result") val result: EventItemInfo
) : BaseResponse()
