package com.example.coupangeats.src.main.order.prepare

import com.example.coupangeats.src.main.order.prepare.model.OrderPrepareInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderPrepareRetrofitInterface {
    @GET("/users/{userIdx}/orders/preparing")
    fun getOrderPrepareInfo(@Path("userIdx") userIdx: Int) : Call<OrderPrepareInfoResponse>
}