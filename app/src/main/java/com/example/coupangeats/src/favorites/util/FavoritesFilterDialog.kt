package com.example.coupangeats.src.favorites.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.coupangeats.databinding.DialogFavoritesFilterBinding
import com.example.coupangeats.src.favorites.FavoritesActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FavoritesFilterDialog(val activity: FavoritesActivity, val version: String) : BottomSheetDialogFragment() {
    private lateinit var binding : DialogFavoritesFilterBinding
    val mFilterNameString = arrayOf("manyOrder", "recentOrder", "recentAdd")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFavoritesFilterBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogFavoritesFilterManyOrderImg.visibility = View.INVISIBLE
        binding.dialogFavoritesFilterResentOrderImg.visibility = View.INVISIBLE
        binding.dialogFavoritesFilterResentAddImg.visibility = View.INVISIBLE

        when(version){
            mFilterNameString[0] -> { binding.dialogFavoritesFilterManyOrderImg.visibility = View.VISIBLE  }
            mFilterNameString[1] -> { binding.dialogFavoritesFilterResentOrderImg.visibility = View.VISIBLE }
            else -> { binding.dialogFavoritesFilterResentAddImg.visibility = View.VISIBLE }
        }

        binding.dialogFavoritesFilterResentAdd.setOnClickListener {
            // 2번 최근 추가 순
            activity.startSortSearch(mFilterNameString[2], binding.dialogFavoritesFilterResentAddText.text.toString())
        }
        binding.dialogFavoritesFilterResentOrder.setOnClickListener {
            // 1번 최근 주문 순
            activity.startSortSearch(mFilterNameString[1], binding.dialogFavoritesFilterResentOrderText.text.toString())
        }
        binding.dialogFavoritesFilterManyOrder.setOnClickListener {
            // 0번 자주 주문한 순
            activity.startSortSearch(mFilterNameString[0], binding.dialogFavoritesFilterManyOrderText.text.toString())
        }
    }


}