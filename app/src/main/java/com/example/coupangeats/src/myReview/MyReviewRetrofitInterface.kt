package com.example.coupangeats.src.myReview

import com.example.coupangeats.src.myReview.model.MyReviewResponse
import com.example.coupangeats.src.review.model.ReviewDeleteResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface MyReviewRetrofitInterface {
    @GET("/users/{userIdx}/reviews/{reviewIdx}/preview")
    fun getMyReviewInfo(@Path("userIdx") userIdx: Int,
                        @Path("reviewIdx") reviewIdx: Int) : Call<MyReviewResponse>

    @PATCH("/users/{userIdx}/reviews/{reviewIdx}/status")
    fun patchReviewDelete(@Path("userIdx") userIdx: Int, @Path("reviewIdx") reviewIdx: Int)
            : Call<ReviewDeleteResponse>
}