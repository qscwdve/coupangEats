package com.example.coupangeats.src.main.order.prepare

import com.example.coupangeats.src.main.order.prepare.model.OrderPrepareInfoResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderPrepareService(val view: OrderPrepareFragmentView) {
    fun tryGetOrderPrepareInfo(userIdx: Int){
        val orderPrepareRetrofitInterface = ApplicationClass.sRetrofit.create(OrderPrepareRetrofitInterface::class.java)
        orderPrepareRetrofitInterface.getOrderPrepareInfo(userIdx)
            .enqueue(object : Callback<OrderPrepareInfoResponse>{
                override fun onResponse(
                    call: Call<OrderPrepareInfoResponse>,
                    response: Response<OrderPrepareInfoResponse>
                ) {
                    view.onGetOrderPrepareInfoSuccess(response.body() as OrderPrepareInfoResponse)
                }

                override fun onFailure(call: Call<OrderPrepareInfoResponse>, t: Throwable) {
                    view.onGetOrderPrepareInfoFailure(t.message ?: "통신 오류")
                }

            })
    }
}