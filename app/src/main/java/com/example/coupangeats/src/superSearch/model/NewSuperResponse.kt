package com.example.coupangeats.src.superSearch.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class NewSuperResponse(
    @SerializedName("result") val result: NewSuperResponseResult
) : BaseResponse()
