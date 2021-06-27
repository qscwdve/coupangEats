package com.example.coupangeats.src.superSearch.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.coupangeats.databinding.DialogFilterRecommendBinding
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.superSearch.SuperSearchActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterRecommendSuperSearch(val superSearchActivity: SuperSearchActivity, val version: Int): BottomSheetDialogFragment() {
    private lateinit var binding: DialogFilterRecommendBinding
    private var select = version
    private val selectString = arrayListOf<String>("recomm", "orders", "nearby", "rating", "new")
    private val selectStingText = arrayListOf<String>("추천순", "주문 많은순", "가까운순", "별점높은순", "신규매장순")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFilterRecommendBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (version) {
            1 -> binding.dialogRecommendRecommendImg.visibility = View.VISIBLE
            2 -> binding.dialogRecommendManyOrder.visibility = View.VISIBLE
            3 -> binding.dialogRecommendDistanceImg.visibility = View.VISIBLE
            4 -> binding.dialogRecommendHighStarImg.visibility = View.VISIBLE
            5 -> binding.dialogRecommendNewImg.visibility = View.VISIBLE
            else -> binding.dialogRecommendRecommendImg.visibility = View.VISIBLE
        }
        binding.dialogFilterRecommendExit.setOnClickListener {
            dismiss()
        }
        binding.dialogRecommendNew.setOnClickListener {
            // 5번
            finishResult(5)
        }
        binding.dialogRecommendDistance.setOnClickListener {
            // 3번
            finishResult(3)
        }
        binding.dialogRecommendHighStar.setOnClickListener {
            // 4번
            finishResult(4)
        }
        binding.dialogRecommendManyOrder.setOnClickListener {
            // 2번
            finishResult(2)
        }
        binding.dialogRecommendRecommend.setOnClickListener {
            // 1번
            finishResult(1)
        }
    }

    private fun finishResult(option: Int) {
        if (option != select) {
            superSearchActivity.changeRecommendFilter(
                selectStingText[option - 1],
                selectString[option - 1],
                option
            )
            dismiss()
        }
    }
}