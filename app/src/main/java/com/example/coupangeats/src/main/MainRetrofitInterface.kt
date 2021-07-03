package com.example.coupangeats.src.main

import com.example.coupangeats.src.main.KaKao.KaKaoLoginRequest
import com.example.coupangeats.src.main.KaKao.KaKaoLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MainRetrofitInterface {
    @POST("/users/login/kakao")
    fun postKaKaoLogin(@Body params: KaKaoLoginRequest): Call<KaKaoLoginResponse>
}