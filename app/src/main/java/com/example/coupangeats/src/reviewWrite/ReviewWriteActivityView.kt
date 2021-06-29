package com.example.coupangeats.src.reviewWrite

import com.example.coupangeats.src.reviewWrite.model.ReviewWriteCreateResponse
import com.example.coupangeats.src.reviewWrite.model.ReviewWriteInfoResponse
import com.example.coupangeats.src.reviewWrite.model.ReviewWriteModifyResponse

interface ReviewWriteActivityView {
    fun onPostReviewWriteCreateSuccess(response: ReviewWriteCreateResponse)
    fun onPostReviewWriteCreateFailure(message: String)

    fun onGetReviewWriteInfoSuccess(response: ReviewWriteInfoResponse)
    fun onGetReviewWriteInfoFailure(message: String)

    fun onGetReviewWriteModifySuccess(response: ReviewWriteModifyResponse)
    fun onGetReviewWriteModifyFailure(message: String)
}