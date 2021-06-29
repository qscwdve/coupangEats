package com.example.coupangeats.src.favorites.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class FavoritesInfoResponse(
    @SerializedName("result") val result: ArrayList<FavoritesSuperInfo>?
) : BaseResponse()
