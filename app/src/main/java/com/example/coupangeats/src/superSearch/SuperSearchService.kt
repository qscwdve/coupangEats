package com.example.coupangeats.src.superSearch

import com.example.coupangeats.src.superSearch.model.DiscountSuperResponse
import com.example.coupangeats.src.superSearch.model.NewSuperResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuperSearchService(val view: SuperSearchActivityView) {

    fun tryGetNewSuperSuccess(lat:String, lon:String, sort:String, cheetah:String?, coupon: String?,
                            ordermin: Int?, delivery: Int?, cursor: Int=1, numOfRows: Int=10){
        val superSearchActivityRetrofitInterface = ApplicationClass.sRetrofit.create(SuperSearchActivityRetrofitInterface::class.java)
        superSearchActivityRetrofitInterface.getNewSuper(lat, lon, sort, cheetah, coupon, ordermin, delivery, cursor, numOfRows)
            .enqueue(object : Callback<NewSuperResponse>{
                override fun onResponse(
                    call: Call<NewSuperResponse>,
                    response: Response<NewSuperResponse>
                ) {
                    view.onGetNewSuperSuccess(response.body() as NewSuperResponse)
                }

                override fun onFailure(call: Call<NewSuperResponse>, t: Throwable) {
                    view.onGetNewSuperFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryGetDiscountSuper(lat:String, lon:String, sort:String, cheetah:String?, coupon: String?,
                            ordermin: Int?, delivery: Int?, cursor: Int=1, numOfRows: Int=10){
        val superSearchActivityRetrofitInterface = ApplicationClass.sRetrofit.create(SuperSearchActivityRetrofitInterface::class.java)
        superSearchActivityRetrofitInterface.getDiscountSuper(lat, lon, sort, cheetah, coupon, ordermin, delivery, cursor, numOfRows)
            .enqueue(object : Callback<DiscountSuperResponse>{
                override fun onResponse(
                    call: Call<DiscountSuperResponse>,
                    response: Response<DiscountSuperResponse>
                ) {
                    view.onGetDiscountSuperSuccess(response.body() as DiscountSuperResponse)
                }

                override fun onFailure(call: Call<DiscountSuperResponse>, t: Throwable) {
                    view.onGetDiscountSuperFailure(t.message ?: "통신 오류")
                }

            })
    }
}