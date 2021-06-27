package com.example.coupangeats.src.main.order.past

import com.example.coupangeats.src.main.order.past.model.OrderPastInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderPastRetrofitInterface {
    @GET("/users/{userIdx}/orders/past")
    fun getOrderPastInfo(@Path("userIdx") userIdx: Int): Call<OrderPastInfoResponse>
}