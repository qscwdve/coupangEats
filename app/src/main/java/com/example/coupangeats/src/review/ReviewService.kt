package com.example.coupangeats.src.review

import com.example.coupangeats.src.review.model.ReviewDeleteResponse
import com.example.coupangeats.src.review.model.ReviewInfoResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewService(val view: ReviewActivityView) {

    fun tryGetReviewInfo(storeIdx: Int, type: String?, sort: String?){
        val reviewActivityRetrofitInterface = ApplicationClass.sRetrofit.create(ReviewActivityRetrofitInterface::class.java)
        reviewActivityRetrofitInterface.getReviewInfo(storeIdx, type, sort)
            .enqueue(object : Callback<ReviewInfoResponse>{
                override fun onResponse(
                    call: Call<ReviewInfoResponse>,
                    response: Response<ReviewInfoResponse>
                ) {
                    view.onGetReviewInfoSuccess(response.body() as ReviewInfoResponse)
                }

                override fun onFailure(call: Call<ReviewInfoResponse>, t: Throwable) {
                    view.onGetReviewInfoFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPatchReviewDelete(userIdx: Int, reviewIdx: Int){
        val reviewActivityRetrofitInterface = ApplicationClass.sRetrofit.create(ReviewActivityRetrofitInterface::class.java)
        reviewActivityRetrofitInterface.patchReviewDelete(userIdx, reviewIdx)
            .enqueue(object : Callback<ReviewDeleteResponse>{
                override fun onResponse(
                    call: Call<ReviewDeleteResponse>,
                    response: Response<ReviewDeleteResponse>
                ) {
                    view.onPatchReviewDeleteSuccess(response.body() as ReviewDeleteResponse)
                }

                override fun onFailure(call: Call<ReviewDeleteResponse>, t: Throwable) {
                    view.onPatchReviewDeleteFailure(t.message ?: "통신 오류")
                }

            })
    }
}