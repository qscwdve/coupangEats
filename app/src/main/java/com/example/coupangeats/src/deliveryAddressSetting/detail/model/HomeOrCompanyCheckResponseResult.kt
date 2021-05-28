package com.example.coupangeats.src.deliveryAddressSetting.detail.model

import com.google.gson.annotations.SerializedName

data class HomeOrCompanyCheckResponseResult(
    @SerializedName("existsStatus") val exist: String
)
