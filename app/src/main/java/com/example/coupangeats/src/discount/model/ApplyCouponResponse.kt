package com.example.coupangeats.src.discount.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class ApplyCouponResponse(
    @SerializedName("result") val result: ApplyCoupon
) : BaseResponse()
