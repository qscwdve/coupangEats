package com.example.coupangeats.src.myReview

import com.example.coupangeats.src.myReview.model.MyReviewResponse
import com.example.coupangeats.src.review.model.ReviewDeleteResponse

interface MyReviewActivityView {
    fun onGetMyReviewInfoSuccess(response: MyReviewResponse)
    fun onGetMyReviewInfoFailure(message: String)

    fun onPatchReviewDeleteSuccess(response: ReviewDeleteResponse)
    fun onPatchReviewDeleteFailure(message: String)
}