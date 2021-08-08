package com.example.coupangeats.src.deliveryAddressSetting.model.SearchXY


import com.google.gson.annotations.SerializedName

data class Juso(
    @SerializedName("admCd")
    val admCd: String,
    @SerializedName("bdMgtSn")
    val bdMgtSn: String,
    @SerializedName("bdNm")
    val bdNm: String?,
    @SerializedName("buldMnnm")
    val buldMnnm: String,
    @SerializedName("buldSlno")
    val buldSlno: String,
    @SerializedName("entX")
    val entX: String,
    @SerializedName("entY")
    val entY: String,
    @SerializedName("rnMgtSn")
    val rnMgtSn: String,
    @SerializedName("udrtYn")
    val udrtYn: String
)