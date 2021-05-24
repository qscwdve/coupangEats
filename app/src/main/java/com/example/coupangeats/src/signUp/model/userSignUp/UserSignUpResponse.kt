package com.example.coupangeats.src.signUp.model.userSignUp


import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class UserSignUpResponse(
    @SerializedName("result") val userSignUpResponseResult: UserSignUpResponseResult?
): BaseResponse()