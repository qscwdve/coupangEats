package com.example.coupangeats.src.review

import com.example.coupangeats.src.review.model.ReviewDeleteResponse
import com.example.coupangeats.src.review.model.ReviewInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewActivityRetrofitInterface {
    @GET("/stores/{storeIdx}/reviews")
    fun getReviewInfo(@Path("storeIdx") storeIdx: Int, @Query("type") type: String?,
                      @Query("sort") sort: String?) : Call<ReviewInfoResponse>

    @PATCH("/users/{userIdx}/reviews/{reviewIdx}/status")
    fun patchReviewDelete(@Path("userIdx") userIdx: Int, @Path("reviewIdx") reviewIdx: Int)
            : Call<ReviewDeleteResponse>
}