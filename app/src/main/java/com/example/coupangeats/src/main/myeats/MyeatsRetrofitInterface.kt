package com.example.coupangeats.src.main.myeats

import com.example.coupangeats.src.main.myeats.model.UserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MyeatsRetrofitInterface {
    @GET("/users/{userIdx}")
    fun getUserInfo(@Path("userIdx") userIdx: Int) : Call<UserInfoResponse>
}