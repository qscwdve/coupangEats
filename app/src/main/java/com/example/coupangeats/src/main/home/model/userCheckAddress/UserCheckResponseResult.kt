package com.example.coupangeats.src.main.home.model.userCheckAddress

import com.google.gson.annotations.SerializedName

data class UserCheckResponseResult(
    @SerializedName("addressIdx") val addressIdx : Int,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("mainAddress") val mainAddress: String
)
