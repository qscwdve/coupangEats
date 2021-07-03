package com.example.coupangeats.src.favorites

import com.example.coupangeats.src.favorites.model.FavoritesInfoResponse
import com.example.coupangeats.src.favorites.model.FavoritesSuperDeleteRequest
import com.softsquared.template.kotlin.config.BaseResponse
import retrofit2.Call
import retrofit2.http.*

interface FavoritesRetrofitInterface {
    @GET("/users/{userIdx}/bookmarks")
    fun getFavoritesInfoSort(@Path("userIdx") userIdx: Int, @Query("sort") sort: String) : Call<FavoritesInfoResponse>

    @PATCH("/users/{userIdx}/bookmarks/status")
    fun postFavoritesSuperDelete(@Path("userIdx") userIdx: Int, @Body params: FavoritesSuperDeleteRequest) : Call<BaseResponse>
}