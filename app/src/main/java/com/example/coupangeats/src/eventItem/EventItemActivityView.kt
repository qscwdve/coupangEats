package com.example.coupangeats.src.eventItem

import com.example.coupangeats.src.eventItem.model.EventItemResponse


interface EventItemActivityView {
    fun onGetEventItemSuccess(response: EventItemResponse)
    fun onGetEventItemFailure(message: String)
}