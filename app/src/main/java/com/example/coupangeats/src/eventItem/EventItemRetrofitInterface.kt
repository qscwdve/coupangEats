package com.example.coupangeats.src.eventItem

import com.example.coupangeats.src.eventItem.model.EventItemResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EventItemRetrofitInterface {
    @GET("/events/{eventIdx}")
    fun getEventItem(@Path("eventIdx") eventIdx: Int) : Call<EventItemResponse>
}