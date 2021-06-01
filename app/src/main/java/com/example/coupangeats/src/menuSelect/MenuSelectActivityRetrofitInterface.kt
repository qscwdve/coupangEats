package com.example.coupangeats.src.menuSelect

import com.example.coupangeats.src.menuSelect.model.MenuDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MenuSelectActivityRetrofitInterface {
    @GET("/stores/{storesIdx}/menus/{menuIdx}")
    fun getMenuDetail(@Path("storesIdx") storeIdx: Int, @Path("menuIdx") menuIdx: Int) : Call<MenuDetailResponse>

}