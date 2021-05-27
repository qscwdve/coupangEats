package com.example.coupangeats.src.main.myeats

import com.example.coupangeats.src.main.myeats.model.UserInfoResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MyeatsService(val view: MyeatsFragmentView) {

    fun tryGetUserInfo(userIdx: Int){
        val myeatsRetrofitInterface = ApplicationClass.sRetrofit.create(MyeatsRetrofitInterface::class.java)
        myeatsRetrofitInterface.getUserInfo(userIdx = userIdx)
            .enqueue(object : Callback<UserInfoResponse>{
                override fun onResponse(
                    call: Call<UserInfoResponse>,
                    response: Response<UserInfoResponse>
                ) {
                    view.onGetUserInfoSuccess(response.body() as UserInfoResponse)
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    view.onGetUserInfoFailure(t.message ?: "통신 오류")
                }
            })
    }
}