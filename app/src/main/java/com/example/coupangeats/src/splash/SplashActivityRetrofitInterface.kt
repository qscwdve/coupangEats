package com.example.coupangeats.src.splash

import com.example.coupangeats.src.splash.model.AutoLoginResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SplashActivityRetrofitInterface {
    @GET("/users/{userIdx}/auto-login")
    fun getAutoLogin(@Path("userIdx") userIdx: Int) : Call<AutoLoginResponse>
}