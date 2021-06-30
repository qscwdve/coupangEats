package com.example.coupangeats.src.event.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class EventDetailResponse(
    @SerializedName("result") val result: ArrayList<EventDetail>?
) : BaseResponse()
