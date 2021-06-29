package com.example.coupangeats.src.review

import com.example.coupangeats.src.review.model.ReviewDeleteResponse
import com.example.coupangeats.src.review.model.ReviewInfoResponse

interface ReviewActivityView {
    fun onGetReviewInfoSuccess(response: ReviewInfoResponse)
    fun onGetReviewInfoFailure(message: String)

    fun onPatchReviewDeleteSuccess(response: ReviewDeleteResponse)
    fun onPatchReviewDeleteFailure(message: String)
}