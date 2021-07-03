package com.example.coupangeats.src.main.KaKao

import com.google.gson.annotations.SerializedName

data class KaKaoLoginRequest(
    @SerializedName("accessToken") val accessToken: String
)
