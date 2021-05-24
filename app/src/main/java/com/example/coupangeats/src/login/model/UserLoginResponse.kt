package com.example.coupangeats.src.login.model


import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class UserLoginResponse(
    @SerializedName("result") val userLoginResponseResult: UserLoginResponseResult
): BaseResponse()