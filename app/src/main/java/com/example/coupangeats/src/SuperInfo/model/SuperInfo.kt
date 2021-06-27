package com.example.coupangeats.src.SuperInfo.model

import com.google.gson.annotations.SerializedName

data class SuperInfo(
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("ceoName") val ceoName: String,
    @SerializedName("businessNumber") val businessNumber: String,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("businessHours") val businessHours: String,
    @SerializedName("introduction") val introduction: String,
    @SerializedName("notice") val notice: String?,
    @SerializedName("countryOfOrigin") val countryOfOrigin: String?
)
