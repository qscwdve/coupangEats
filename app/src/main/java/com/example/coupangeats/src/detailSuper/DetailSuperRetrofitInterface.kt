package com.example.coupangeats.src.detailSuper

import com.example.coupangeats.src.detailSuper.model.*
import com.example.coupangeats.src.favorites.model.FavoritesSuperDeleteRequest
import com.softsquared.template.kotlin.config.BaseResponse
import retrofit2.Call
import retrofit2.http.*


interface DetailSuperRetrofitInterface {
    @GET("/stores/{storesIdx}")
    fun getSuperInfo(@Path("storesIdx") storesIdx: Int) : Call<SuperResponse>

    @POST("/stores/{storeIdx}/coupons")
    fun postCouponSave(@Path("storeIdx") storesIdx: Int, @Body params: CouponSaveRequest) : Call<CouponSaveResponse>

    @POST("/bookmarks")
    fun postBookMarkAdd(@Body params: BookMarkAddRequest) : Call<BookMarkAddResponse>

    @PATCH("/users/{userIdx}/bookmarks/status")
    fun postFavoritesSuperDelete(@Path("userIdx") userIdx: Int, @Body params: FavoritesSuperDeleteRequest) : Call<BaseResponse>

}