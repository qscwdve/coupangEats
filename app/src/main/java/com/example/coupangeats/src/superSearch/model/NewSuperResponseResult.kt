package com.example.coupangeats.src.superSearch.model

import com.google.gson.annotations.SerializedName

data class NewSuperResponseResult(
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("newStores") val newStores: ArrayList<BaseSuperInfo>?
)
