package com.example.coupangeats.src.main

import com.example.coupangeats.src.main.KaKao.KaKaoLoginRequest
import com.example.coupangeats.src.main.KaKao.KaKaoLoginResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainService(val view: MainActivityView) {

    fun tryPostKaKaoLogin(request: KaKaoLoginRequest){
        val mainRetrofitInterface = ApplicationClass.sRetrofit.create(MainRetrofitInterface::class.java)
        mainRetrofitInterface.postKaKaoLogin(request)
            .enqueue(object : Callback<KaKaoLoginResponse>{
                override fun onResponse(
                    call: Call<KaKaoLoginResponse>,
                    response: Response<KaKaoLoginResponse>
                ) {
                    view.onPostKaKaoLoginSuccess(response.body() as KaKaoLoginResponse)
                }

                override fun onFailure(call: Call<KaKaoLoginResponse>, t: Throwable) {
                    view.onPostKaKaoLoginFailure(t.message ?: "통신오류")
                }

            })
    }
}