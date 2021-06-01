package com.example.coupangeats.src.menuSelect

import com.example.coupangeats.src.menuSelect.model.MenuDetailResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuSelectService(val view: MenuSelectActivityView) {

    fun tryGetMenuDetail(storeIdx: Int, menuIdx: Int){
        val menuSelectActivityRetrofitInterface = ApplicationClass.sRetrofit.create(MenuSelectActivityRetrofitInterface::class.java)
        menuSelectActivityRetrofitInterface.getMenuDetail(storeIdx, menuIdx)
            .enqueue(object : Callback<MenuDetailResponse>{
                override fun onResponse(
                    call: Call<MenuDetailResponse>,
                    response: Response<MenuDetailResponse>
                ) {
                    view.onGetMenuDetailSuccess(response.body() as MenuDetailResponse)
                }

                override fun onFailure(call: Call<MenuDetailResponse>, t: Throwable) {
                    view.onGetMenuDetailFailure(t.message ?: "통신 오류")
                }

            })
    }
}