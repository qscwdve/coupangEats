package com.example.coupangeats.src.discount

import com.example.coupangeats.src.discount.model.ApplyCouponRequest
import com.example.coupangeats.src.discount.model.ApplyCouponResponse
import com.example.coupangeats.src.discount.model.CouponInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DiscountActivityRetrofitInterface {
    @GET("/stores/{storeIdx}/users/{userIdx}/coupons")
    fun getCouponInfo(@Path("storeIdx") storeIdx: Int, @Path("userIdx") userIdx: Int) : Call<CouponInfoResponse>

    @POST("/coupons")
    fun postApplyCoupon(@Body params: ApplyCouponRequest) : Call<ApplyCouponResponse>

    // my-eats 할인쿠폰 등록
    @GET("/users/{userIdx}/coupons")
    fun getMyEatsDiscount(@Path("userIdx") userIdx: Int) : Call<CouponInfoResponse>
}