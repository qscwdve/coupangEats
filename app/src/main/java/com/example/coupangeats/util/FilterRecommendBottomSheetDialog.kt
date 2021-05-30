package com.example.coupangeats.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.coupangeats.databinding.DialogFilterRecommendBinding
import com.example.coupangeats.databinding.DialogFilterSuperBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterRecommendBottomSheetDialog(val fragment: Fragment, val version: Int): BottomSheetDialogFragment() {
    private lateinit var binding : DialogFilterRecommendBinding

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
        when(version){
            1 -> binding.dialogRecommendRecommendImg.visibility = View.VISIBLE
            2 -> binding.dialogRecommendManyOrder.visibility = View.VISIBLE
            3 -> binding.dialogRecommendDistanceImg.visibility = View.VISIBLE
            4 -> binding.dialogRecommendHighStarImg.visibility = View.VISIBLE
            5 -> binding.dialogRecommendNewImg.visibility = View.VISIBLE
            else -> binding.dialogRecommendRecommendImg.visibility = View.VISIBLE
        }
        binding.dialogFilterRecommendExit.setOnClickListener { dismiss() }
        binding.dialogRecommendNew.setOnClickListener { dismiss() }
        binding.dialogRecommendDistance.setOnClickListener { dismiss() }
        binding.dialogRecommendHighStar.setOnClickListener { dismiss() }
        binding.dialogRecommendManyOrder.setOnClickListener { dismiss() }
        binding.dialogRecommendRecommend.setOnClickListener { dismiss() }
    }
}