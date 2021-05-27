package com.example.coupangeats.src.main.myeats.model

import com.google.gson.annotations.SerializedName

data class UserInfoResponseResult(
    @SerializedName("userName") val userName: String,
    @SerializedName("phone") val phone: String,
)
