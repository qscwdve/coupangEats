package com.example.coupangeats.src.main.order.past

import com.example.coupangeats.src.main.order.past.model.OrderPastInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderPastRetrofitInterface {
    @GET("/users/{userIdx}/orders/past")
    fun getOrderPastInfo(@Path("userIdx") userIdx: Int,
                         @Query("search") serch: String? = null): Call<OrderPastInfoResponse>
}