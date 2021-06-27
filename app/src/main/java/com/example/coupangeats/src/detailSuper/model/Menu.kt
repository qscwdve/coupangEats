package com.example.coupangeats.src.detailSuper.model

import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("menuCategoryName") val categoryName: String,
    @SerializedName("introduction") val categoryIntroduce: String?,
    @SerializedName("menuList") val menuList: ArrayList<MenuList>
)
