package com.example.coupangeats.src.menuSelect.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class MenuDetailResponse(
    @SerializedName("result") val result: MenuDetailResponseResult
) : BaseResponse()
