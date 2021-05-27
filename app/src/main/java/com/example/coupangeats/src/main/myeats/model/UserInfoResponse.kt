package com.example.coupangeats.src.main.myeats.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class UserInfoResponse(
    @SerializedName("result") val result: UserInfoResponseResult?
) : BaseResponse()
