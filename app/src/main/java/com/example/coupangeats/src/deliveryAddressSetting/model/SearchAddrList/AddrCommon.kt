package com.example.lastapp.address


import com.google.gson.annotations.SerializedName

data class AddrCommon(
    @SerializedName("countPerPage")
    val countPerPage: Int,
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("errorCode")
    val errorCode: String,
    @SerializedName("errorMessage")
    val errorMessage: String,
    @SerializedName("totalCount")
    val totalCount: String
)