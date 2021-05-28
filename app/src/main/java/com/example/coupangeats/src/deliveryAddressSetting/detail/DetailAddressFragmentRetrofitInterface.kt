package com.example.coupangeats.src.deliveryAddressSetting.detail

import com.example.coupangeats.src.deliveryAddressSetting.detail.model.*
import retrofit2.Call
import retrofit2.http.*

interface DetailAddressFragmentRetrofitInterface {
    @GET("/users/{userIdx}/addresses/check")
    fun getHomeOrCompanyChecked(@Path("userIdx") userIdx: Int, @Query("type") type: String) : Call<HomeOrCompanyCheckResponse>

    @GET("/users/{userIdx}/addresses/{addressIdx}")
    fun getDeliveryAddressDetailLook(@Path("userIdx") userIdx: Int, @Path("addressIdx") addressIdx : Int) : Call<DeliveryAddressDetailResponse>

    @PATCH("/users/{userIdx}/addresses/{addressIdx}/status")
    fun pathDeliveryAddressDelete(@Path("userIdx") userIdx: Int, @Path("addressIdx") addressIdx : Int) : Call<DeliveryAddressDeleteResponse>

    @PATCH("/users/{userIdx}/addresses/{addressIdx}")
    fun pathDeliveryAddressModify(@Path("userIdx") userIdx: Int, @Path("addressIdx") addressIdx : Int, @Body params: DeliveryAddressModifyRequest) : Call<DeliveryAddressModifyResponse>
}