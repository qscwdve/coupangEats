package com.example.coupangeats.src.main.order.past

import com.example.coupangeats.src.main.order.past.model.OrderPastInfoResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderPastService(val view: OrderPastFragmentView) {

    fun tryGetOrderPastInfo(userIdx: Int){
        val orderPastRetrofitInterface = ApplicationClass.sRetrofit.create(OrderPastRetrofitInterface::class.java)
        orderPastRetrofitInterface.getOrderPastInfo(userIdx)
            .enqueue(object : Callback<OrderPastInfoResponse>{
                override fun onResponse(
                    call: Call<OrderPastInfoResponse>,
                    response: Response<OrderPastInfoResponse>
                ) {
                    view.onGetOrderPastInfoSuccess(response.body() as OrderPastInfoResponse)
                }

                override fun onFailure(call: Call<OrderPastInfoResponse>, t: Throwable) {
                    view.onGetOrderPastInfoFailure(t.message ?: "통신 오류")
                }

            })
    }

}