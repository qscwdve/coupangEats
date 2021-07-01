package com.example.coupangeats.src.discount

import com.example.coupangeats.src.discount.model.ApplyCouponRequest
import com.example.coupangeats.src.discount.model.ApplyCouponResponse
import com.example.coupangeats.src.discount.model.CouponInfoResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscountService(val view: DiscountActivityView) {

    fun tryGetCouponInfo(storeIdx: Int, userIdx: Int) {
        val discountActivityRetrofitInterface = ApplicationClass.sRetrofit.create(DiscountActivityRetrofitInterface::class.java)
        discountActivityRetrofitInterface.getCouponInfo(storeIdx, userIdx)
            .enqueue(object : Callback<CouponInfoResponse>{
                override fun onResponse(
                    call: Call<CouponInfoResponse>,
                    response: Response<CouponInfoResponse>
                ) {
                    view.onGetCouponInfoSuccess(response.body() as CouponInfoResponse)
                }

                override fun onFailure(call: Call<CouponInfoResponse>, t: Throwable) {
                    view.onGetCouponInfoFailure(t.message ?: "통신 오류")
                }
            })
    }

    fun tryPostApplyCoupon(request: ApplyCouponRequest){
        val discountActivityRetrofitInterface = ApplicationClass.sRetrofit.create(DiscountActivityRetrofitInterface::class.java)
        discountActivityRetrofitInterface.postApplyCoupon(request)
            .enqueue(object : Callback<ApplyCouponResponse>{
                override fun onResponse(
                    call: Call<ApplyCouponResponse>,
                    response: Response<ApplyCouponResponse>
                ) {
                    view.onPostApplyCouponSuccess(response.body() as ApplyCouponResponse)
                }

                override fun onFailure(call: Call<ApplyCouponResponse>, t: Throwable) {
                    view.onPostApplyCouponFailure(t.message ?: "통신 오류")
                }
            })
    }

    fun tryGetMyEatsDiscount(userIdx: Int){
        val discountActivityRetrofitInterface = ApplicationClass.sRetrofit.create(DiscountActivityRetrofitInterface::class.java)
        discountActivityRetrofitInterface.getMyEatsDiscount(userIdx)
            .enqueue(object : Callback<CouponInfoResponse>{
                override fun onResponse(
                    call: Call<CouponInfoResponse>,
                    response: Response<CouponInfoResponse>
                ) {
                    view.onGetMyEatsDiscountSuccess(response.body() as CouponInfoResponse)
                }

                override fun onFailure(call: Call<CouponInfoResponse>, t: Throwable) {
                    view.onGetMyEatsDiscountFailure(t.message ?: "통신 오류")
                }

            })
    }
}