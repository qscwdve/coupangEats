package com.example.coupangeats.src.deliveryAddressSetting

import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponseResult
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface DeliveryAddressSettingRetrofitInterface {
    @GET("/users/{userIdx}/addresses")
    fun getUserAddressList(@Path("userIdx") userIdx: Int) : Call<UserAddrListResponse>

    @PATCH("/users/{userIdx}/pick/addresses/{addressIdx}")
    fun pathUserCheckedAddress(@Path("userIdx") userIdx: Int, @Path("addressIdx") addressIdx: Int) : Call<UserCheckedAddressResponse>
}