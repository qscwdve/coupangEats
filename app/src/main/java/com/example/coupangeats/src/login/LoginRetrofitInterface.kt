package com.example.coupangeats.src.login

import com.example.coupangeats.src.login.model.UserLoginRequest
import com.example.coupangeats.src.login.model.UserLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginRetrofitInterface {
    @POST("/users/login")
    fun postLogin(@Body params: UserLoginRequest) : Call<UserLoginResponse>
}