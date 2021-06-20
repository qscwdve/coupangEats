package com.example.coupangeats.src.detailSuper.detailSuperFragment.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class CouponSaveResponse(
    @SerializedName("result") val result: CouponSaveResponseResult
) : BaseResponse()
