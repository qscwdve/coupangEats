package com.example.coupangeats.src.map

import com.example.coupangeats.src.deliveryAddressSetting.model.DeliveryAddressAddRequest
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.DeliveryAddressResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface MapActivityRetrofitInterface {
    @POST("/addresses")
    fun postDeliveryAddressAdd(@Body param: DeliveryAddressAddRequest) : Call<DeliveryAddressResponse>

    @PATCH("/users/{userIdx}/pick/addresses/{addressIdx}")
    fun pathUserCheckedAddress(@Path("userIdx") userIdx: Int, @Path("addressIdx") addressIdx: Int) : Call<UserCheckedAddressResponse>

}