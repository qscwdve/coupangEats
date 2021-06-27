package com.example.coupangeats.src.SuperInfo

import com.example.coupangeats.src.SuperInfo.model.SuperInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SuperInfoRetrofitInterface {
    @GET("/stores/{storesIdx}/detail")
    fun getSuperInfo(@Path("storesIdx") storesIdx: Int) : Call<SuperInfoResponse>

}