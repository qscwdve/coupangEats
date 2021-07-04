package com.example.coupangeats.src.reviewWrite

import com.example.coupangeats.src.reviewWrite.model.*
import com.softsquared.template.kotlin.config.BaseResponse
import retrofit2.Call
import retrofit2.http.*

interface ReviewWriteRetrofitInterface {
    @POST("/reviews")
    fun postReviewWriteCreate(@Body params: ReviewWriteCreateRequest) : Call<ReviewWriteCreateResponse>

    @GET("/users/{userIdx}/orders/{orderIdx}")
    fun getReviewWriteInfo(@Path("userIdx") userIdx:Int, @Path("orderIdx") orderIdx: Int) : Call<ReviewWriteInfoResponse>

    @GET("/users/{userIdx}/reviews/{reviewIdx}")
    fun getReviewWriteModify(@Path("userIdx") userIdx:Int, @Path("reviewIdx") reviewIdx: Int) : Call<ReviewWriteModifyResponse>

    @PATCH("/users/{userIdx}/reviews/{reviewIdx}")
    fun patchReviewWriteModifyApply(@Path("userIdx") userIdx: Int, @Path("reviewIdx") reviewIdx: Int,
                                @Body params: ReviewWriteModifyApplyRequest): Call<BaseResponse>
}