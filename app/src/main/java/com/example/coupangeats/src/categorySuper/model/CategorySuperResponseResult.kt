package com.example.coupangeats.src.categorySuper.model

import com.example.coupangeats.src.main.home.model.HomeInfo.RecommendStores
import com.google.gson.annotations.SerializedName

data class CategorySuperResponseResult(
    @SerializedName("recommendStores") val recommendStores: ArrayList<RecommendStores>?,
    @SerializedName("totalCount") val totalCount: Int
)
