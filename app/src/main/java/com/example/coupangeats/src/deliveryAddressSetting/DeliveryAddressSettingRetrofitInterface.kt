package com.example.coupangeats.src.deliveryAddressSetting

import com.example.coupangeats.src.deliveryAddressSetting.model.DeliveryAddressAddRequest
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.DeliveryAddressResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.SearchAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchXY.SearchXYResult
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponseResult
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.http.*

interface DeliveryAddressSettingRetrofitInterface {
    @GET("/users/{userIdx}/addresses")
    fun getUserAddressList(@Path("userIdx") userIdx: Int) : Call<UserAddrListResponse>

    @PATCH("/users/{userIdx}/pick/addresses/{addressIdx}")
    fun pathUserCheckedAddress(@Path("userIdx") userIdx: Int, @Path("addressIdx") addressIdx: Int) : Call<UserCheckedAddressResponse>

    @POST("/addresses")
    fun postDeliveryAddressAdd(@Body param: DeliveryAddressAddRequest) : Call<DeliveryAddressResponse>

    // 도로명주소 API
    @GET("/addrlink/addrLinkApi.do")
    fun getSearchAddrList(@Query("confmKey") confmKey:String, @Query("currentPage")currentPage:Int,
                          @Query("countPerPage") countPerPage :Int, @Query("keyword") keyword: String,
                          @Query("resultType") resultType : String = "json") : Call<SearchAddrListResponse>

}