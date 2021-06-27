package com.example.coupangeats.src.main.home.model.cheetahCount

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class CheetahCountResponse(
    @SerializedName("result") val result: CheetahCount
): BaseResponse()
