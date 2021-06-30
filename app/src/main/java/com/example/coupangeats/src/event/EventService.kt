package com.example.coupangeats.src.event

import com.example.coupangeats.src.event.model.EventDetailResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventService(val view: EventActivityView) {

    fun tryGetEventInfo(){
        val eventRetrofitInterface = ApplicationClass.sRetrofit.create(EventRetrofitInterface::class.java)
        eventRetrofitInterface.getEventInfo()
            .enqueue(object : Callback<EventDetailResponse>{
                override fun onResponse(
                    call: Call<EventDetailResponse>,
                    response: Response<EventDetailResponse>
                ) {
                    view.onGetEventInfoSuccess(response.body() as EventDetailResponse)
                }

                override fun onFailure(call: Call<EventDetailResponse>, t: Throwable) {
                    view.onGetEventInfoFailure(t.message ?: "통신 오류")
                }

            })
    }
}