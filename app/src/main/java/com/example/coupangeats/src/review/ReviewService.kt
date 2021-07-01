package com.example.coupangeats.src.review

import com.example.coupangeats.src.review.model.ReviewDeleteResponse
import com.example.coupangeats.src.review.model.ReviewHelpRequest
import com.example.coupangeats.src.review.model.ReviewHelpResponse
import com.example.coupangeats.src.review.model.ReviewInfoResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseResponse
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

    fun tryPostReviewHelpLike(request: ReviewHelpRequest){
        val reviewActivityRetrofitInterface = ApplicationClass.sRetrofit.create(ReviewActivityRetrofitInterface::class.java)
        reviewActivityRetrofitInterface.postReviewHelpLike(request)
            .enqueue(object : Callback<ReviewHelpResponse>{
                override fun onResponse(
                    call: Call<ReviewHelpResponse>,
                    response: Response<ReviewHelpResponse>
                ) {
                    view.onPostReviewHelpLikeSuccess(response.body() as ReviewHelpResponse)
                }

                override fun onFailure(call: Call<ReviewHelpResponse>, t: Throwable) {
                    view.onPostReviewHelpLikeFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPostReviewHelpUnlike(request: ReviewHelpRequest) {
        val reviewActivityRetrofitInterface =
            ApplicationClass.sRetrofit.create(ReviewActivityRetrofitInterface::class.java)
        reviewActivityRetrofitInterface.postReviewHelpUnlike(request)
            .enqueue(object : Callback<ReviewHelpResponse>{
                override fun onResponse(
                    call: Call<ReviewHelpResponse>,
                    response: Response<ReviewHelpResponse>
                ) {
                    view.onPostReviewHelpUnlikeSuccess(response.body() as ReviewHelpResponse)
                }

                override fun onFailure(call: Call<ReviewHelpResponse>, t: Throwable) {
                    view.onPostReviewHelpUnlikeFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPatchReviewHelpDelete(userIdx: Int, reviewIdx: Int){
        val reviewActivityRetrofitInterface =
            ApplicationClass.sRetrofit.create(ReviewActivityRetrofitInterface::class.java)
        reviewActivityRetrofitInterface.patchReviewHelpDelete(userIdx, reviewIdx)
            .enqueue(object : Callback<BaseResponse>{
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    view.onPatchReviewHelpDeleteSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPatchReviewHelpDeleteFailure(t.message ?: "통신 오류")
                }

            })
    }
}