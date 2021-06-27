package com.example.coupangeats.src.review

import com.example.coupangeats.src.review.model.ReviewInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewActivityRetrofitInterface {
    @GET("/stores/{storeIdx}/reviews")
    fun getReviewInfo(@Path("storeIdx") storeIdx: Int, @Query("type") type: String?,
                      @Query("sort") sort: String?) : Call<ReviewInfoResponse>
}