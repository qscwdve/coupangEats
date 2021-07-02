package com.example.coupangeats.src.findId

import com.example.coupangeats.src.findId.model.FindEmailResponse
import com.example.coupangeats.src.findId.model.FindIdRequest
import com.example.coupangeats.src.findId.model.FindIdResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindIdService(val view: FindIdActivityView) {

    fun tryPostFindIdAuthNumber(request: FindIdRequest){
        val findIdRetrofitInterface = ApplicationClass.sRetrofit.create(FindIdRetrofitInterface::class.java)
        findIdRetrofitInterface.postFindIdAuthNumber(request)
            .enqueue(object : Callback<FindIdResponse>{
                override fun onResponse(
                    call: Call<FindIdResponse>,
                    response: Response<FindIdResponse>
                ) {
                    view.onPostFindIdAuthNumberSuccess(response.body() as FindIdResponse)
                }

                override fun onFailure(call: Call<FindIdResponse>, t: Throwable) {
                    view.onPostFindIdAuthNumberFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryGetFindEmail(phone: String){
        val findIdRetrofitInterface = ApplicationClass.sRetrofit.create(FindIdRetrofitInterface::class.java)
        findIdRetrofitInterface.getFindEmail(phone)
            .enqueue(object : Callback<FindEmailResponse>{
                override fun onResponse(
                    call: Call<FindEmailResponse>,
                    response: Response<FindEmailResponse>
                ) {
                    view.onGetFindEmailSuccess(response.body() as FindEmailResponse)
                }

                override fun onFailure(call: Call<FindEmailResponse>, t: Throwable) {
                    view.onGetFindEmailFailure(t.message ?: "통신 오류")
                }

            })
    }
}