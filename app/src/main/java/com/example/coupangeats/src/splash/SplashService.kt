package com.example.coupangeats.src.splash

import com.example.coupangeats.src.splash.model.AutoLoginResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashService(val view: SplashActivityView) {

    fun tryGetAutoLogin(userIdx: Int){
        val splashActivityRetrofitInterface = ApplicationClass.sRetrofit.create(SplashActivityRetrofitInterface::class.java)
        splashActivityRetrofitInterface.getAutoLogin(userIdx)
            .enqueue(object : Callback<AutoLoginResponse>{
                override fun onResponse(
                    call: Call<AutoLoginResponse>,
                    response: Response<AutoLoginResponse>
                ) {
                    view.onGetAutoLoginSuccess(response.body() as AutoLoginResponse)
                }

                override fun onFailure(call: Call<AutoLoginResponse>, t: Throwable) {
                    view.onGetAutoLoginFailure(t.message ?: "통신 오류")
                }

            })
    }
}