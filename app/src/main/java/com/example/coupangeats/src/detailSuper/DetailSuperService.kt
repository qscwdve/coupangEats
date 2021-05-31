package com.example.coupangeats.src.detailSuper

import com.example.coupangeats.src.detailSuper.model.CouponSaveRequest
import com.example.coupangeats.src.detailSuper.model.CouponSaveResponse
import com.example.coupangeats.src.detailSuper.model.SuperResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailSuperService(val view: DetailSuperActivityView) {

    fun tryGetSuperInfo(storesIdx: Int){
        val detailSuperRetrofitInterface = ApplicationClass.sRetrofit.create(DetailSuperRetrofitInterface::class.java)
        detailSuperRetrofitInterface.getSuperInfo(storesIdx)
            .enqueue(object : Callback<SuperResponse>{
                override fun onResponse(
                    call: Call<SuperResponse>,
                    response: Response<SuperResponse>
                ) {
                    view.onGetSuperInfoSuccess(response.body() as SuperResponse)
                }

                override fun onFailure(call: Call<SuperResponse>, t: Throwable) {
                    view.onGetSuperInfoFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPostCouponSave(request: CouponSaveRequest){
        val detailSuperRetrofitInterface = ApplicationClass.sRetrofit.create(DetailSuperRetrofitInterface::class.java)
        detailSuperRetrofitInterface.postCouponSave(request)
            .enqueue(object : Callback<CouponSaveResponse>{
                override fun onResponse(
                    call: Call<CouponSaveResponse>,
                    response: Response<CouponSaveResponse>
                ) {
                    view.onPostCouponSaveSuccess(response.body() as CouponSaveResponse)
                }

                override fun onFailure(call: Call<CouponSaveResponse>, t: Throwable) {
                    view.onPostCouponSaveFailure(t.message ?: "통신 오류")
                }

            })
    }
}