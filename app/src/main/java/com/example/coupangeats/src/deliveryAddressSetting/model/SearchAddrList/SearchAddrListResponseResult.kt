package com.example.lastapp.address


import com.google.gson.annotations.SerializedName

data class SearchAddrListResponseResult(
    @SerializedName("common")  val common: AddrCommon,
    @SerializedName("juso") val juso: List<AddrJuso>
)