package com.example.coupangeats.src.findId

import com.example.coupangeats.src.findId.model.FindEmailResponse
import com.example.coupangeats.src.findId.model.FindIdRequest
import com.example.coupangeats.src.findId.model.FindIdResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FindIdRetrofitInterface {
    @POST("/users/find-email/auth")
    fun postFindIdAuthNumber(@Body params: FindIdRequest): Call<FindIdResponse>

    @GET("/users/find-email/{phone}")
    fun getFindEmail(@Path("phone") phone: String) : Call<FindEmailResponse>
}