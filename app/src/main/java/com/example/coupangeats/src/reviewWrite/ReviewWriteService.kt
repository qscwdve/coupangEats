package com.example.coupangeats.src.reviewWrite

import com.example.coupangeats.src.reviewWrite.model.ReviewWriteCreateRequest
import com.example.coupangeats.src.reviewWrite.model.ReviewWriteCreateResponse
import com.example.coupangeats.src.reviewWrite.model.ReviewWriteInfoResponse
import com.example.coupangeats.src.reviewWrite.model.ReviewWriteModifyResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewWriteService(val view: ReviewWriteActivityView) {

    fun tryPostReviewWriteCreate(request: ReviewWriteCreateRequest){
        val reviewWriteRetrofitInterface = ApplicationClass.sRetrofit.create(ReviewWriteRetrofitInterface::class.java)
        reviewWriteRetrofitInterface.postReviewWriteCreate(request)
            .enqueue(object : Callback<ReviewWriteCreateResponse> {
                override fun onResponse(
                    call: Call<ReviewWriteCreateResponse>,
                    response: Response<ReviewWriteCreateResponse>
                ) {
                    view.onPostReviewWriteCreateSuccess(response.body() as ReviewWriteCreateResponse)
                }

                override fun onFailure(call: Call<ReviewWriteCreateResponse>, t: Throwable) {
                    view.onPostReviewWriteCreateFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryGetReviewWriteInfo(userIdx: Int, orderIdx: Int){
        val reviewWriteRetrofitInterface = ApplicationClass.sRetrofit.create(ReviewWriteRetrofitInterface::class.java)
        reviewWriteRetrofitInterface.getReviewWriteInfo(userIdx, orderIdx)
            .enqueue(object : Callback<ReviewWriteInfoResponse>{
                override fun onResponse(
                    call: Call<ReviewWriteInfoResponse>,
                    response: Response<ReviewWriteInfoResponse>
                ) {
                    view.onGetReviewWriteInfoSuccess(response.body() as ReviewWriteInfoResponse)
                }

                override fun onFailure(call: Call<ReviewWriteInfoResponse>, t: Throwable) {
                    view.onGetReviewWriteInfoFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryGetReviewWriteModify(userIdx: Int, reviewIdx: Int){
        val reviewWriteRetrofitInterface = ApplicationClass.sRetrofit.create(ReviewWriteRetrofitInterface::class.java)
        reviewWriteRetrofitInterface.getReviewWriteModify(userIdx, reviewIdx)
            .enqueue(object : Callback<ReviewWriteModifyResponse>{
                override fun onResponse(
                    call: Call<ReviewWriteModifyResponse>,
                    response: Response<ReviewWriteModifyResponse>
                ) {
                    view.onGetReviewWriteModifySuccess(response.body() as ReviewWriteModifyResponse)
                }

                override fun onFailure(call: Call<ReviewWriteModifyResponse>, t: Throwable) {
                    view.onGetReviewWriteModifyFailure(t.message ?: "통신 오류")
                }

            })
    }
}