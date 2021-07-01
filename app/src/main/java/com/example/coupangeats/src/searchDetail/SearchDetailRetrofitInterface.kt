package com.example.coupangeats.src.searchDetail

import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponse
import com.example.coupangeats.src.searchDetail.model.SearchDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchDetailRetrofitInterface {
    @GET("/stores/search")
    fun getSearchSuper(@Query("lat") lat: String, @Query("lon") lon: String,
                       @Query("keyword") keyword: String, @Query("sort") sort: String,
                       @Query("cheetah") cheetah: String?, @Query("coupon") coupon: String?,
                       @Query("ordermin") ordermin: Int?, @Query("deliverymin") deliverymin: Int?,
                       @Query("cursor") cursor: Int, @Query("numOfRows") numOfRows: Int) : Call<SearchDetailResponse>



}