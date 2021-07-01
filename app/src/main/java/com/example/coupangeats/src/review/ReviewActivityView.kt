package com.example.coupangeats.src.review

import com.example.coupangeats.src.review.model.ReviewDeleteResponse
import com.example.coupangeats.src.review.model.ReviewHelpResponse
import com.example.coupangeats.src.review.model.ReviewInfoResponse
import com.softsquared.template.kotlin.config.BaseResponse

interface ReviewActivityView {
    fun onGetReviewInfoSuccess(response: ReviewInfoResponse)
    fun onGetReviewInfoFailure(message: String)

    fun onPatchReviewDeleteSuccess(response: ReviewDeleteResponse)
    fun onPatchReviewDeleteFailure(message: String)

    // 리뷰 관련
    fun onPostReviewHelpLikeSuccess(response: ReviewHelpResponse)
    fun onPostReviewHelpLikeFailure(message: String)

    fun onPostReviewHelpUnlikeSuccess(response: ReviewHelpResponse)
    fun onPostReviewHelpUnlikeFailure(message: String)

    fun onPatchReviewHelpDeleteSuccess(response: BaseResponse)
    fun onPatchReviewHelpDeleteFailure(message: String)
}