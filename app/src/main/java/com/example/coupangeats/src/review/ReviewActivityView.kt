package com.example.coupangeats.src.review

import com.example.coupangeats.src.review.model.ReviewInfoResponse

interface ReviewActivityView {
    fun onGetReviewInfoSuccess(response: ReviewInfoResponse)
    fun onGetReviewInfoFailure(message: String)
}