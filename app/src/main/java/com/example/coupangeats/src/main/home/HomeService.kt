package com.example.coupangeats.src.main.home

import com.example.coupangeats.src.login.LoginRetrofitInterface
import com.example.coupangeats.src.login.model.UserLoginRequest
import com.example.coupangeats.src.login.model.UserLoginResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeService(val view: HomeFragmentView) {

    fun tryPostLogin(userLoginRequest: UserLoginRequest) {
        val homeRetrofitInterface = ApplicationClass.sRetrofit.create(HomeRetrofitInterface::class.java)
        homeRetrofitInterface.postLogin(userLoginRequest)
            .enqueue(object : Callback<UserLoginResponse> {
                override fun onResponse(
                    call: Call<UserLoginResponse>,
                    response: Response<UserLoginResponse>
                ) {
                    view.onPostLoginSuccess(response.body() as UserLoginResponse)
                }

                override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                    view.onPostLoginFailure(t.message ?: "통신 오류")
                }

            })
    }
}