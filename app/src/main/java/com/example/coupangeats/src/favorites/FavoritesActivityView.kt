package com.example.coupangeats.src.favorites

import com.example.coupangeats.src.favorites.model.FavoritesInfoResponse
import com.softsquared.template.kotlin.config.BaseResponse

interface FavoritesActivityView {
    fun onGetFavoritesInfoSortSuccess(response: FavoritesInfoResponse)
    fun onGetFavoritesInfoSortFailure(message: String)

    fun onPostFavoritesSuperDeleteSuccess(response: BaseResponse)
    fun onPostFavoritesSuperDeleteFailure(message: String)
}