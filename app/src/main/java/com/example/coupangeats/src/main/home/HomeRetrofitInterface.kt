package com.example.coupangeats.src.main.home

import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse
import com.example.coupangeats.src.login.model.UserLoginRequest
import com.example.coupangeats.src.login.model.UserLoginResponse
import com.example.coupangeats.src.main.home.model.HomeInfo.HomeInfoResponse
import com.example.coupangeats.src.main.home.model.cheetahCount.CheetahCountResponse
import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponse
import retrofit2.Call
import retrofit2.http.*

interface HomeRetrofitInterface {
    @GET("/users/{userIdx}/pick/addresses")
    fun getUserCheckAddress(@Path("userIdx") userIdx: Int) : Call<UserCheckResponse>

    @GET("/stores")
    fun getHomeData(@Query("lat") latitude: String, @Query("lon") longitude: String,
                    @Query("sort") sort: String = "recomm", @Query("cheetah") cheetah: String?,
                    @Query("coupon") coupon: String?, @Query("ordermin") ordermin: Int?,
                    @Query("deliverymin") deliverymin: Int?, @Query("cursor") cursor: Int = 1,
                    @Query("numOfRows") numOfRows: Int = 10) : Call<HomeInfoResponse>

    @GET("/stores/cheetah/count")
    fun getCheetahCount(@Query("lat") lat: String, @Query("lon") lon: String) : Call<CheetahCountResponse>
}