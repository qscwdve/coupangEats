package com.example.coupangeats.src.review.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.coupangeats.databinding.DialogReviewFilterBinding
import com.example.coupangeats.src.review.ReviewActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReviewFilterBottomSheetDialog(val reviewActivity: ReviewActivity, val version: Int): BottomSheetDialogFragment() {
    private lateinit var binding : DialogReviewFilterBinding
    private var select = version
    private val selectString = arrayListOf<String>("new", "reviewliked", "rating-desc", "rating-asc")
    private val selectStingText = arrayListOf<String>("최신순", "리뷰 도움순", "별점 높은 순", "별점 낮은 순")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogReviewFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(version){
            1 -> binding.dialogReviewFilterNewImg.visibility = View.VISIBLE
            2 -> binding.dialogReviewFilterReviewlikedImg.visibility = View.VISIBLE
            3 -> binding.dialogReviewFilterRatingAscImg.visibility = View.VISIBLE
            4 -> binding.dialogReviewFilterRatingDescImg.visibility = View.VISIBLE
            else -> binding.dialogReviewFilterNewImg.visibility = View.VISIBLE
        }
        binding.dialogReviewFilterBack.setOnClickListener { dismiss() }

        binding.dialogReviewFilterNew.setOnClickListener { finishResult(1) }
        binding.dialogReviewFilterReviewliked.setOnClickListener { finishResult(2) }
        binding.dialogReviewFilterRatingAsc.setOnClickListener { finishResult(3) }
        binding.dialogReviewFilterRatingDesc.setOnClickListener { finishResult(4) }
    }

    private fun finishResult(option : Int) {
        if(option != select){
            reviewActivity.changeRecommendFilter(selectString[option - 1], selectStingText[option - 1], option)
            dismiss()
        }
    }
}