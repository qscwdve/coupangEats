package com.example.coupangeats.src.login

import com.example.coupangeats.src.login.model.UserLoginRequest
import com.example.coupangeats.src.login.model.UserLoginResponse
import com.example.coupangeats.src.signUp.SignUpRetrofitInterface
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginService(val view: LoginActivityView) {

    fun tryPostLogin(userLoginRequest: UserLoginRequest) {
        val signUpRetrofitInterface = ApplicationClass.sRetrofit.create(LoginRetrofitInterface::class.java)
        signUpRetrofitInterface.postLogin(userLoginRequest)
            .enqueue(object : Callback<UserLoginResponse>{
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