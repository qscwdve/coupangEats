package com.example.coupangeats.src.superSearch.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class DiscountSuperResponse(
    @SerializedName("result") val result: DiscountSuperResponseResult
) : BaseResponse()
