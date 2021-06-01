package com.example.coupangeats.src.detailSuper

import com.example.coupangeats.src.detailSuper.model.CouponSaveRequest
import com.example.coupangeats.src.detailSuper.model.CouponSaveResponse
import com.example.coupangeats.src.detailSuper.model.SuperResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DetailSuperRetrofitInterface {
    @GET("/stores/{storesIdx}")
    fun getSuperInfo(@Path("storesIdx") storesIdx: Int) : Call<SuperResponse>

    @POST("/stores/{storeIdx}/coupons")
    fun postCouponSave(@Path("storeIdx") storesIdx: Int, @Body params: CouponSaveRequest) : Call<CouponSaveResponse>
}