package com.example.coupangeats.src.event

import com.example.coupangeats.src.event.model.EventDetailResponse
import retrofit2.Call
import retrofit2.http.GET

interface EventRetrofitInterface {
    @GET("/events")
    fun getEventInfo() : Call<EventDetailResponse>
}