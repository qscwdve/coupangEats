package com.example.coupangeats.src.eventItem

import com.example.coupangeats.src.eventItem.model.EventItemResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventItemService(val view: EventItemActivityView) {

    fun tryGetEventItem(eventIdx: Int){
        val eventItemRetrofitInterface = ApplicationClass.sRetrofit.create(EventItemRetrofitInterface::class.java)
        eventItemRetrofitInterface.getEventItem(eventIdx)
            .enqueue(object : Callback<EventItemResponse>{
                override fun onResponse(
                    call: Call<EventItemResponse>,
                    response: Response<EventItemResponse>
                ) {
                    view.onGetEventItemSuccess(response.body() as EventItemResponse)
                }

                override fun onFailure(call: Call<EventItemResponse>, t: Throwable) {
                    view.onGetEventItemFailure(t.message ?: "통신 오류")
                }

            })
    }
}