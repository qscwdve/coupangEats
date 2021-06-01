package com.example.coupangeats.src.menuSelect.model

import com.google.gson.annotations.SerializedName

data class MenuDetailResponseResult(
    @SerializedName("menuName") val menuName: String,
    @SerializedName("imageUrls") val url: ArrayList<String>?,
    @SerializedName("introduction") val introduce: String?,
    @SerializedName("price") val price: Int,
    @SerializedName("menuOptions") val menuOption: ArrayList<MenuOption>?
)
