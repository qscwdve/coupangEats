package com.example.coupangeats.src.searchDetail.model

import com.example.coupangeats.src.main.home.model.HomeInfo.RecommendStores
import com.google.gson.annotations.SerializedName

data class SearchDetailResponseResult(
    @SerializedName("searchStores") val searchStores: ArrayList<RecommendStores>?,
    @SerializedName("totalCount") val totalCount: Int

)
