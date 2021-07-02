package com.example.coupangeats.src.findId.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class FindEmailResponse(
    @SerializedName("result") val result: UserEmail
): BaseResponse()
