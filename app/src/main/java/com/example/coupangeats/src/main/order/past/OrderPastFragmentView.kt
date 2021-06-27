package com.example.coupangeats.src.main.order.past

import com.example.coupangeats.src.main.order.past.model.OrderPastInfoResponse

interface OrderPastFragmentView {
    fun onGetOrderPastInfoSuccess(response: OrderPastInfoResponse)
    fun onGetOrderPastInfoFailure(message: String)
}