package com.example.coupangeats.src.superSearch

import com.example.coupangeats.src.superSearch.model.DiscountSuperResponse
import com.example.coupangeats.src.superSearch.model.NewSuperResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SuperSearchActivityRetrofitInterface {
    @GET("/stores/new")
    fun getNewSuper(@Query("lat") lat: String, @Query("lon") lon: String,
                    @Query("sort") sort: String, @Query("cheetah") cheetah: String?,
                    @Query("coupon") coupon: String?, @Query("ordermin") ordermin: Int?,
                    @Query("deliverymin") deliverymin: Int?, @Query("cursor") cursor: Int = 1,
                    @Query("numOfRows") numOfRows: Int = 10) : Call<NewSuperResponse>

    @GET("/stores/discount")
    fun getDiscountSuper(@Query("lat") lat: String, @Query("lon") lon: String,
                @Query("sort") sort: String, @Query("cheetah") cheetah: String?,
                @Query("coupon") coupon: String?, @Query("ordermin") ordermin: Int?,
                @Query("deliverymin") deliverymin: Int?, @Query("cursor") cursor: Int = 1,
                @Query("numOfRows") numOfRows: Int = 10) : Call<DiscountSuperResponse>
}