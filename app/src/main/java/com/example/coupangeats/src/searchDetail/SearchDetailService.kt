package com.example.coupangeats.src.searchDetail

import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponse
import com.example.coupangeats.src.searchDetail.model.SearchDetailRequest
import com.example.coupangeats.src.searchDetail.model.SearchDetailResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchDetailService(val view: SearchDetailActivityView) {

    fun tryGetSearchSuper(request: SearchDetailRequest){
        val searchDetailRetrofitInterface = ApplicationClass.sRetrofit.create(SearchDetailRetrofitInterface::class.java)
        searchDetailRetrofitInterface.getSearchSuper(
            request.lat, request.lon, request.keyword,
            request.sort, request.cheetah, request.coupon,
            request.ordermin, request.deliverymin, request.cursor, request.numOfRows
        ).enqueue(object : Callback<SearchDetailResponse>{
            override fun onResponse(
                call: Call<SearchDetailResponse>,
                response: Response<SearchDetailResponse>
            ) {
                view.onGetSearchSuperSuccess(response.body() as SearchDetailResponse)
            }

            override fun onFailure(call: Call<SearchDetailResponse>, t: Throwable) {
                view.onGetSearchSuperFailure(t.message ?: "통신 오류")
            }

        })
    }


}