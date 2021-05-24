package com.example.coupangeats.src.signUp.model.emailDuplicated

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class EmailDuplicatedResponse(
    @SerializedName("result") val result: EmailDuplicatedResponseResult
): BaseResponse()
