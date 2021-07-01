package com.example.coupangeats.src.myReview

import com.example.coupangeats.src.myReview.model.MyReviewResponse
import com.example.coupangeats.src.review.model.ReviewDeleteResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyReviewService(val view: MyReviewActivityView) {

    fun tryGetMyReviewInfo(userIdx: Int, reviewIdx: Int){
        val myReviewRetrofitInterface = ApplicationClass.sRetrofit.create(MyReviewRetrofitInterface::class.java)
        myReviewRetrofitInterface.getMyReviewInfo(userIdx, reviewIdx)
            .enqueue(object : Callback<MyReviewResponse>{
                override fun onResponse(
                    call: Call<MyReviewResponse>,
                    response: Response<MyReviewResponse>
                ) {
                    view.onGetMyReviewInfoSuccess(response.body() as MyReviewResponse)
                }

                override fun onFailure(call: Call<MyReviewResponse>, t: Throwable) {
                    view.onGetMyReviewInfoFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPatchReviewDelete(userIdx: Int, reviewIdx: Int){
        val myReviewRetrofitInterface = ApplicationClass.sRetrofit.create(MyReviewRetrofitInterface::class.java)
        myReviewRetrofitInterface.patchReviewDelete(userIdx, reviewIdx)
            .enqueue(object : Callback<ReviewDeleteResponse>{
                override fun onResponse(
                    call: Call<ReviewDeleteResponse>,
                    response: Response<ReviewDeleteResponse>
                ) {
                    view.onPatchReviewDeleteSuccess(response.body() as ReviewDeleteResponse)
                }

                override fun onFailure(call: Call<ReviewDeleteResponse>, t: Throwable) {
                    view.onPatchReviewDeleteFailure(t.message ?: "통신 오류류")
               }

            })
    }
}