package com.example.coupangeats.src.main.KaKao

import com.google.gson.annotations.SerializedName

data class KaKaoLoginResponseResult(
    @SerializedName("userIdx") val userIdx: Int,
    @SerializedName("jwt") val jwt: String
)
