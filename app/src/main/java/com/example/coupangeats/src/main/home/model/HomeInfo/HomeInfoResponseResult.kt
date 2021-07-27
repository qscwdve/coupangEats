package com.example.coupangeats.src.main.home.model.HomeInfo

import com.google.gson.annotations.SerializedName

data class HomeInfoResponseResult(
    @SerializedName("events") val events: ArrayList<Events>?,
    @SerializedName("storeCategories") val storeCategories: ArrayList<StoreCategories>,
    @SerializedName("onSaleStores") val onSaleStores: ArrayList<OnSaleStores>?,
    @SerializedName("newStores") val newStores: ArrayList<NewStores>?,
    @SerializedName("recommendStores") val recommendStores: ArrayList<RecommendStores>?,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("cursor") val cursor: Int,
    @SerializedName("numOfRows") val numOrRows: Int
)
