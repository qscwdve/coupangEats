package com.example.coupangeats.src.main.order.prepare

import com.example.coupangeats.src.main.order.prepare.model.OrderPrepareInfoResponse

interface OrderPrepareFragmentView {
    fun onGetOrderPrepareInfoSuccess(response: OrderPrepareInfoResponse)
    fun onGetOrderPrepareInfoFailure(message: String)
}