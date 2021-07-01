package com.example.coupangeats.src.review

import com.example.coupangeats.src.review.model.ReviewDeleteResponse
import com.example.coupangeats.src.review.model.ReviewHelpRequest
import com.example.coupangeats.src.review.model.ReviewHelpResponse
import com.example.coupangeats.src.review.model.ReviewInfoResponse
import com.softsquared.template.kotlin.config.BaseResponse
import retrofit2.Call
import retrofit2.http.*

interface ReviewActivityRetrofitInterface {
    @GET("/stores/{storeIdx}/reviews")
    fun getReviewInfo(@Path("storeIdx") storeIdx: Int, @Query("type") type: String?,
                      @Query("sort") sort: String?) : Call<ReviewInfoResponse>

    @PATCH("/users/{userIdx}/reviews/{reviewIdx}/status")
    fun patchReviewDelete(@Path("userIdx") userIdx: Int, @Path("reviewIdx") reviewIdx: Int)
            : Call<ReviewDeleteResponse>

    // 리뷰 도움 관련
    @POST("like")
    fun postReviewHelpLike(@Body params: ReviewHelpRequest) : Call<ReviewHelpResponse>

    @POST("unlike")
    fun postReviewHelpUnlike(@Body params: ReviewHelpRequest) : Call<ReviewHelpResponse>

    @PATCH("/users/{userIdx}/reviews/{reviewIdx}/like/status")
    fun patchReviewHelpDelete(@Path("userIdx") userIdx: Int,
                              @Path("reviewIdx") reviewIdx: Int) : Call<BaseResponse>

}