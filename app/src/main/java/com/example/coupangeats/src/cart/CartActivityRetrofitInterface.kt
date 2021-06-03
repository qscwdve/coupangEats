package com.example.coupangeats.src.cart

import com.example.coupangeats.src.cart.model.CartMenuLookResponse
import com.example.coupangeats.src.cart.model.OrderRequest
import com.example.coupangeats.src.cart.model.OrderResponse
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CartActivityRetrofitInterface {
    @GET("/users/{userIdx}/stores/{storeIdx}/cart")
    fun getCartLook(@Path("userIdx") userIdx: Int, @Path("storeIdx") storeIdx: Int) : Call<CartMenuLookResponse>

    @POST("/orders")
    fun postOrder(@Body params: OrderRequest) : Call<OrderResponse>
}