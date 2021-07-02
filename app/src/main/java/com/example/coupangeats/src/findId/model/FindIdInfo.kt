package com.example.coupangeats.src.findId.model

import com.google.gson.annotations.SerializedName

data class FindIdInfo(
    @SerializedName("phone") val phone: String,
    @SerializedName("authNumber") val authNumber: String
)
