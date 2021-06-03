package com.example.coupangeats.src.signUp.model.phoneCertification

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class PhoneCertificationResponse(
    @SerializedName("result") val result: PhoneCertificationResponseResult
) : BaseResponse()
