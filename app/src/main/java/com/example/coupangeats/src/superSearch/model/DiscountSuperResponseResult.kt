package com.example.coupangeats.src.superSearch.model

import com.google.gson.annotations.SerializedName

data class DiscountSuperResponseResult(
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("onSaleStores") val onSaleStores: ArrayList<BaseSuperInfo>?
)
