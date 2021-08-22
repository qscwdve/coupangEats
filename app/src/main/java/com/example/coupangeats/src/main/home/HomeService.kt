package com.example.coupangeats.src.main.home

import com.example.coupangeats.src.login.LoginRetrofitInterface
import com.example.coupangeats.src.login.model.UserLoginRequest
import com.example.coupangeats.src.login.model.UserLoginResponse
import com.example.coupangeats.src.main.home.model.HomeInfo.HomeInfoRequest
import com.example.coupangeats.src.main.home.model.HomeInfo.HomeInfoResponse
import com.example.coupangeats.src.main.home.model.cheetahCount.CheetahCountResponse
import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeService(val view: HomeFragmentView) {
    fun tryGetUserCheckAddress(userIdx: Int, version: String){
        val homeRetrofitInterface = ApplicationClass.sRetrofit.create(HomeRetrofitInterface::class.java)
        homeRetrofitInterface.getUserCheckAddress(userIdx)
            .enqueue(object : Callback<UserCheckResponse>{
                override fun onResponse(
                    call: Call<UserCheckResponse>,
                    response: Response<UserCheckResponse>
                ) {
                    view.onUserCheckAddressSuccess(response.body() as UserCheckResponse, version)
                }

                override fun onFailure(call: Call<UserCheckResponse>, t: Throwable) {
                    view.onUserCheckAddressFailure(t.message ?: "통신 오류")
                }
            })
    }

    fun tryGetHomeData(re: HomeInfoRequest){
        val homeRetrofitInterface = ApplicationClass.sRetrofit.create(HomeRetrofitInterface::class.java)
        homeRetrofitInterface.getHomeData(re.lat, re.lon, re.sort, re.cheetah, re.coupon, re.ordermin, re.deliverymin, re.cursor, re.numOfRows)
            .enqueue(object : Callback<HomeInfoResponse>{
                override fun onResponse(
                    call: Call<HomeInfoResponse>,
                    response: Response<HomeInfoResponse>
                ) {
                    view.onGetHomeDataSuccess(response.body() as HomeInfoResponse)
                }

                override fun onFailure(call: Call<HomeInfoResponse>, t: Throwable) {
                    view.onGetHomeDataFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryGetCheetahCount(lat: String, lon: String){
        val homeRetrofitInterface = ApplicationClass.sRetrofit.create(HomeRetrofitInterface::class.java)
        homeRetrofitInterface.getCheetahCount(lat, lon)
            .enqueue(object : Callback<CheetahCountResponse>{
                override fun onResponse(
                    call: Call<CheetahCountResponse>,
                    response: Response<CheetahCountResponse>
                ) {
                    view.onGetCheetahCountSuccess(response.body() as CheetahCountResponse)
                }

                override fun onFailure(call: Call<CheetahCountResponse>, t: Throwable) {
                    view.onGetCheetahCountFailure(t.message ?: "통신 오류")
                }

            })
    }
}