package com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList

import com.google.gson.annotations.SerializedName

data class SearchAddrListRequest(
    val currentPage: Int,
    val keyword: String,
    val countPerPage: Int = 25
)
