package com.example.coupangeats.src.cart.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class CartMenuLookResponse(
    @SerializedName("result") val result: CartMenuLookResponseResult
) : BaseResponse()
