package com.example.coupangeats.src.deliveryAddressSetting.model.SearchXY


import com.google.gson.annotations.SerializedName

data class Results(
    @SerializedName("common")
    val common: Common,
    @SerializedName("juso")
    val juso: List<Juso>
)