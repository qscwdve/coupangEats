package com.example.coupangeats.src.deliveryAddressSetting.model.SearchXY


import com.google.gson.annotations.SerializedName

data class Common(
    @SerializedName("errorCode")
    val errorCode: String,
    @SerializedName("errorMessage")
    val errorMessage: String,
    @SerializedName("totalCount")
    val totalCount: String
)