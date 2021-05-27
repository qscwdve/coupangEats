package com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList

import com.example.lastapp.address.SearchAddrListResponseResult
import com.google.gson.annotations.SerializedName

data class SearchAddrListResponse(
    @SerializedName("results") val results: SearchAddrListResponseResult
)
