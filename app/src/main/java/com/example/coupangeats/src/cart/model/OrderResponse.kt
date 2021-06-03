package com.example.coupangeats.src.cart.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class OrderResponse(
    @SerializedName("result") val result: OrderResponseResult
) : BaseResponse()
