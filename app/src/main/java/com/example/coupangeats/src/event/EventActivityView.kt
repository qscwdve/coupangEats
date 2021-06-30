package com.example.coupangeats.src.event

import com.example.coupangeats.src.event.model.EventDetailResponse

interface EventActivityView {
    fun onGetEventInfoSuccess(response: EventDetailResponse)
    fun onGetEventInfoFailure(message: String)
}