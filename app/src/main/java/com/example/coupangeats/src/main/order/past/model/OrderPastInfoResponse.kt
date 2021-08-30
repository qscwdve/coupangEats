package com.example.coupangeats.src.main.order.past.model

import com.example.coupangeats.src.main.order.model.PastOrder
import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class OrderPastInfoResponse(
    @SerializedName("result") val result : ArrayList<PastOrder>?
) : BaseResponse()
