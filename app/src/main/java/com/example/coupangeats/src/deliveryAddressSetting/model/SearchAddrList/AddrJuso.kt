package com.example.lastapp.address


import com.google.gson.annotations.SerializedName

data class AddrJuso(
    @SerializedName("roadAddr")
    val roadAddr: String?,
    @SerializedName("roadAddrPart1")
    val roadAddrPart1: String?,
    @SerializedName("roadAddrPart2")
    val roadAddrPart2: String?,
    @SerializedName("admCd")
    val admCd: String?,
    @SerializedName("bdKdcd")
    val bdKdcd: String?,
    @SerializedName("bdMgtSn")
    val bdMgtSn: String?,
    @SerializedName("bdNm")
    val bdNm: String?,
    @SerializedName("buldMnnm")
    val buldMnnm: Int?,
    @SerializedName("buldSlno")
    val buldSlno: Int?,
    @SerializedName("detBdNmList")
    val detBdNmList: String?,
    @SerializedName("emdNm")
    val emdNm: String?,
    @SerializedName("engAddr")
    val engAddr: String?,
    @SerializedName("jibunAddr")
    val jibunAddr: String?,
    @SerializedName("liNm")
    val liNm: String?,
    @SerializedName("lnbrMnnm")
    val lnbrMnnm: Int?,
    @SerializedName("lnbrSlno")
    val lnbrSlno: Int?,
    @SerializedName("mtYn")
    val mtYn: String?,
    @SerializedName("rn")
    val rn: String?,
    @SerializedName("rnMgtSn")
    val rnMgtSn: String?,
    @SerializedName("sggNm")
    val sggNm: String?,
    @SerializedName("siNm")
    val siNm: String?,
    @SerializedName("udrtYn")
    val udrtYn: String?,
    @SerializedName("zipNo")
    val zipNo: String?,
    @SerializedName("hstryYn")
    val hstryYn: String?
)