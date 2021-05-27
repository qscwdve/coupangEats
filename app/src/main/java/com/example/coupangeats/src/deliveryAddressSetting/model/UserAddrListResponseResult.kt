package com.example.coupangeats.src.deliveryAddressSetting.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class UserAddrListResponseResult(
    @SerializedName("selectedAddressIdx") val selectedAddressIdx : Int,
    @SerializedName("home") val home : BaseAddress?,
    @SerializedName("company") val company : BaseAddress?,
    @SerializedName("addressList") val addressList : List<BaseAddress>?
)
