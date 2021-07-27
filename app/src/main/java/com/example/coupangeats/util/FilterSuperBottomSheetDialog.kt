package com.example.coupangeats.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.example.coupangeats.R
import com.example.coupangeats.databinding.DialogFilterSuperBinding
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.main.home.HomeFragmentView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterSuperBottomSheetDialog(val fragment: HomeFragment, val version: Int, val select: Int): BottomSheetDialogFragment() {
    private lateinit var binding : DialogFilterSuperBinding
    lateinit var mValue1 : String
    lateinit var mValue2 : String
    lateinit var mValue3 : String
    lateinit var mValue4 : String
    lateinit var mValue5 : String
    val black = "#000000"
    val gray = "#949DA6"
    var mSelect = 5
    var mSelectString = "전체"
    val priceDeliveryArray = arrayListOf<Int>(0, 1000, 2000, 3000, -1)
    val priceMinorderArray = arrayListOf<Int>(5000, 10000, 12000, 15000, -1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFilterSuperBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(version == 1){
            // 배달비 순
            mValue2 = "1,000"
            mValue3 = "2,000"
            mValue4 = "3,000"
            mValue1 = "무료배달"
            mValue5 = "배달비 전체"
            binding.dialogFilterValue1.text = "무료배달"
            binding.dialogFilterValue2.text = "1,000"
            binding.dialogFilterValue3.text = "2,000"
            binding.dialogFilterValue4.text = "3,000"
            binding.dialogFilterValue5.text = "전체"

            binding.dialogFilterSelect.text = "배달비 전체"
            binding.dialogFilterSelectSub.text = "더 멀리있는 매장까지 볼 수 있어요!"
            binding.dialogFilterSelectSub.visibility = View.VISIBLE

            binding.dialogFilterTitle.text = "배달비"
        } else {
            mValue1 = "5,000"
            mValue2 = "10,000"
            mValue3 = "12,000"
            mValue4 = "15,000"
            mValue5 = "전체"
            binding.dialogFilterValue1.text = "5,000"
            binding.dialogFilterValue2.text = "10,000"
            binding.dialogFilterValue3.text = "12,000"
            binding.dialogFilterValue4.text = "15,000"
            binding.dialogFilterValue5.text = mValue5
            binding.dialogFilterSelect.text = "전체"
            binding.dialogFilterSelectSub.visibility = View.GONE

            binding.dialogFilterTitle.text = "최소주문"
        }
        // 종료 버튼
        binding.dialogFilterExit.setOnClickListener { dismiss() }
        // 적용하기
        binding.dialogFilterApply.setOnClickListener {
            // 홈 프레그 먼트로 바꾸는거 함수 호출 필요
            if(version == 1){
                // 배달비 순
                fragment.changeDeliveryFilter(priceDeliveryArray[mSelect - 1], mSelectString, mSelect)
            } else {
                // 최소 주문 순
                fragment.changeOrderMinFilter(priceMinorderArray[mSelect - 1], mSelectString, mSelect)
            }
            dismiss()
        }
        // 선택시 그림 밑 숫자 바꾸기
        // 1번 선택
        binding.dialogFilterImg1.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue1()
            true
        }
        binding.dialogFilterValue1.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue1();
            true
        }
        // 2번
        binding.dialogFilterImg2.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue2()
            true
        }
        binding.dialogFilterValue2.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue2();
            true
        }
        // 3번
        binding.dialogFilterImg3.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue3()
            true
        }
        binding.dialogFilterValue3.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue3();
            true
        }
        // 4번
        binding.dialogFilterImg4.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue4()
            true
        }
        binding.dialogFilterValue4.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue4();
            true
        }
        // 5번
        binding.dialogFilterImg5.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue5()
            true
        }
        binding.dialogFilterValue5.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) clickValue5();
            true
        }

        // 이전에 선택했던 값
        when(select){
            1 -> { clickValue1() }
            2 -> { clickValue2() }
            3 -> { clickValue3() }
            4 -> { clickValue4() }
            else -> { clickValue5() }
        }
    }


    fun clickValue1() {
        if(version == 1)
            binding.dialogFilterSelectSub.visibility = View.INVISIBLE
        val text = if(version == 1){
            mValue1
        } else {
            "${mValue1}원 이하"
        }
        binding.dialogFilterSelect.text = text
        binding.dialogFilterValue1.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue2.setTextColor(Color.parseColor(gray))
        binding.dialogFilterValue3.setTextColor(Color.parseColor(gray))
        binding.dialogFilterValue4.setTextColor(Color.parseColor(gray))
        binding.dialogFilterValue5.setTextColor(Color.parseColor(gray))
        binding.dialogFilterImgParent.setBackgroundResource(R.drawable.ic_filter_selected_1)
        mSelect = 1;
        mSelectString = text
    }

    fun clickValue2() {
        if(version == 1)
            binding.dialogFilterSelectSub.visibility = View.INVISIBLE
        val text = "${mValue2}원 이하"
        binding.dialogFilterSelect.text = text
        binding.dialogFilterValue1.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue2.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue3.setTextColor(Color.parseColor(gray))
        binding.dialogFilterValue4.setTextColor(Color.parseColor(gray))
        binding.dialogFilterValue5.setTextColor(Color.parseColor(gray))
        binding.dialogFilterImgParent.setBackgroundResource(R.drawable.ic_filter_selected_2)
        mSelect = 2;
        mSelectString = text
    }

    fun clickValue3() {
        if(version == 1)
            binding.dialogFilterSelectSub.visibility = View.INVISIBLE
        val text = "${mValue3}원 이하"
        binding.dialogFilterSelect.text = text
        binding.dialogFilterValue1.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue2.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue3.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue4.setTextColor(Color.parseColor(gray))
        binding.dialogFilterValue5.setTextColor(Color.parseColor(gray))
        binding.dialogFilterImgParent.setBackgroundResource(R.drawable.ic_filter_selected_3)
        mSelect = 3;
        mSelectString = text
    }

    fun clickValue4() {
        if(version == 1)
            binding.dialogFilterSelectSub.visibility = View.INVISIBLE
        val text = "${mValue4}원 이하"
        binding.dialogFilterSelect.text = text
        binding.dialogFilterValue1.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue2.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue3.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue4.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue5.setTextColor(Color.parseColor(gray))
        binding.dialogFilterImgParent.setBackgroundResource(R.drawable.ic_filter_selected_4)
        mSelect = 4;
        mSelectString = text
    }

    fun clickValue5() {
        if(version == 1){
            binding.dialogFilterSelectSub.visibility = View.VISIBLE
        } else {
            binding.dialogFilterSelectSub.visibility = View.GONE
        }
        binding.dialogFilterSelect.text = mValue5
        binding.dialogFilterValue1.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue2.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue3.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue4.setTextColor(Color.parseColor(black))
        binding.dialogFilterValue5.setTextColor(Color.parseColor(black))
        binding.dialogFilterImgParent.setBackgroundResource(R.drawable.ic_filter_selected_5)
        mSelect = 5;
        mSelectString = mValue5
    }
}