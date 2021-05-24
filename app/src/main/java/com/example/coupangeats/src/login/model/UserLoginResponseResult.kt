package com.example.coupangeats.src.login.model


import com.google.gson.annotations.SerializedName

data class UserLoginResponseResult(
    @SerializedName("userIdx") val userIdx: Int,
    @SerializedName("jwt") val jwt: String?
)