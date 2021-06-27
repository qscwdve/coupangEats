package com.example.coupangeats.src.SuperInfo

import com.example.coupangeats.src.SuperInfo.model.SuperInfoResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuperInfoService(val view: SuperInfoActivityView) {

    fun tryGetSuperInfo(storesIdx: Int) {
        val superInfoRetrofitInterface = ApplicationClass.sRetrofit.create(SuperInfoRetrofitInterface::class.java)
        superInfoRetrofitInterface.getSuperInfo(storesIdx)
            .enqueue(object : Callback<SuperInfoResponse>{
                override fun onResponse(
                    call: Call<SuperInfoResponse>,
                    response: Response<SuperInfoResponse>
                ) {
                    view.onGetSuperInfoSuccess(response.body() as SuperInfoResponse)
                }

                override fun onFailure(call: Call<SuperInfoResponse>, t: Throwable) {
                    view.onGetSuperInfoFailure(t.message ?: "통신 오류")
                }

            })
    }
}