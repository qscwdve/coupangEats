package com.example.coupangeats.src.findId.model

import com.google.gson.annotations.SerializedName

data class FindIdRequest(
    @SerializedName("userName") val userName: String,
    @SerializedName("phone") val phone: String
)
