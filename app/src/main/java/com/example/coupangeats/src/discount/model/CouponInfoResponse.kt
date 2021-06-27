package com.example.coupangeats.src.discount.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class CouponInfoResponse(
    @SerializedName("result") val result: ArrayList<SuperCoupon>?
) : BaseResponse()
