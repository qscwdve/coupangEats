package com.example.coupangeats.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.coupangeats.databinding.DialogFilterRecommendBinding
import com.example.coupangeats.databinding.DialogOrderRiderBinding
import com.example.coupangeats.src.cart.CartActivity
import com.example.coupangeats.src.main.home.HomeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CartOrderRider(val activity: CartActivity, val version: Int): BottomSheetDialogFragment() {
    private lateinit var binding : DialogOrderRiderBinding
    private val valueList = arrayListOf("문 앞에 두고 사진 (초인종 O)","문 앞에 두고 사진 (초인종 X)"
                        , "직접 수령(부재 시 문 앞)", "초인종 누르고 문 앞", "초인종 누르지 말고 문 앞"
                        , "도착하면 전화", "직접 입력하기")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogOrderRiderBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogOrderRiderOption1Text.text = valueList[0]
        binding.dialogOrderRiderOption2Text.text = valueList[1]
        binding.dialogOrderRiderOption3Text.text = valueList[2]
        binding.dialogOrderRiderOption4Text.text = valueList[3]
        binding.dialogOrderRiderOption5Text.text = valueList[4]
        binding.dialogOrderRiderOption6Text.text = valueList[5]
        binding.dialogOrderRiderOption7Text.text = valueList[6]

        when(version){
            1 -> binding.dialogOrderRiderOption1Img.visibility = View.VISIBLE
            2 -> binding.dialogOrderRiderOption2Img.visibility = View.VISIBLE
            3 -> binding.dialogOrderRiderOption3Img.visibility = View.VISIBLE
            4 -> binding.dialogOrderRiderOption4Img.visibility = View.VISIBLE
            5 -> binding.dialogOrderRiderOption5Img.visibility = View.VISIBLE
            6 -> binding.dialogOrderRiderOption6Img.visibility = View.VISIBLE
            7 -> binding.dialogOrderRiderOption7Img.visibility = View.VISIBLE
        }
        binding.dialogOrderRiderExit.setOnClickListener {
            dismiss()
        }
        binding.dialogOrderRiderOption1.setOnClickListener {
            finishResult(1)
        }
        binding.dialogOrderRiderOption1.setOnClickListener {
            finishResult(1)
        }
        binding.dialogOrderRiderOption1.setOnClickListener { finishResult(1) }
        binding.dialogOrderRiderOption2.setOnClickListener { finishResult(2) }
        binding.dialogOrderRiderOption3.setOnClickListener { finishResult(3) }
        binding.dialogOrderRiderOption4.setOnClickListener { finishResult(4) }
        binding.dialogOrderRiderOption5.setOnClickListener { finishResult(5) }
        binding.dialogOrderRiderOption6.setOnClickListener { finishResult(6) }
        binding.dialogOrderRiderOption7.setOnClickListener { finishResult(7) }
    }

    fun finishResult(option: Int){
        if(version != option){
            activity.changeRiderOrder(option, valueList[option - 1])
            dismiss()
        }
    }
}