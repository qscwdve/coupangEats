package com.example.coupangeats.src.favorites.model

import com.google.gson.annotations.SerializedName

data class FavoritesSuperDeleteRequest(
    @SerializedName("storeIdxList") val storeIdx: ArrayList<Int>
)
