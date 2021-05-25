package com.example.coupangeats.src.signUp.model.phoneDuplicated

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class PhoneDuplicatedResponse(
    @SerializedName("result") val result: PhoneDuplicatedResponseResult
) : BaseResponse()
