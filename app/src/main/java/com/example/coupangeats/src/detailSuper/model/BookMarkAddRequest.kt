package com.example.coupangeats.src.detailSuper.model

import com.google.gson.annotations.SerializedName

data class BookMarkAddRequest(
    @SerializedName("userIdx") val uesrIdx: Int,
    @SerializedName("storeIdx") val storeIdx: Int
)
