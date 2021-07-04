package com.example.coupangeats.src.discount

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityDiscountBinding
import com.example.coupangeats.src.discount.adapter.CouponInfoAdapter
import com.example.coupangeats.src.discount.model.ApplyCouponRequest
import com.example.coupangeats.src.discount.model.ApplyCouponResponse
import com.example.coupangeats.src.discount.model.CouponInfoResponse
import com.example.coupangeats.src.discount.model.SuperCoupon
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class DiscountActivity : BaseActivity<ActivityDiscountBinding>(ActivityDiscountBinding::inflate), DiscountActivityView {
    var mStoreIdx : Int = -1
    var mCouponSelect : Int = -1
    var mVersion: Int = -1  // -1 이면 해당 쿠폰 조회 아니면 myeats
    var mDiscountApplyCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStoreIdx = intent.getIntExtra("storeIdx", -1)
        mCouponSelect = intent.getIntExtra("selectCouponIdx", -1)
        mVersion = intent.getIntExtra("version", -1)

        if(mCouponSelect == -1 && mVersion == -1) binding.discountApply.text = "쿠폰적용 안함"

        if(mVersion == -1){
            DiscountService(this).tryGetCouponInfo(mStoreIdx, getUserIdx())
        } else {
            DiscountService(this).tryGetMyEatsDiscount(getUserIdx())
        }

        binding.discountBack.setOnClickListener{ finish() }

        binding.discountCouponNumText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textNum = binding.discountCouponNumText.text.toString().length
                if(textNum == 8 || textNum == 16){
                    binding.discountCouponApply.setBackgroundResource(R.drawable.dialog_login_basic)
                    binding.discountCouponApply.setTextColor(Color.parseColor("#00AFFE"))
                    mDiscountApplyCheck = true
                } else if(mDiscountApplyCheck){
                    binding.discountCouponApply.setBackgroundResource(R.drawable.round_gray_box)
                    binding.discountCouponApply.setTextColor(Color.parseColor("#5E5E5E"))
                    mDiscountApplyCheck = false
                }
                Log.d("count", "textNum  $textNum")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.discountCouponApply.setOnClickListener {
            if(mDiscountApplyCheck){
                // 쿠폰번호 조회 가능 -> 서버
                DiscountService(this).tryPostApplyCoupon(ApplyCouponRequest(binding.discountCouponNumText.text.toString(), getUserIdx()))
            } else {
                binding.discountCouponNumError.visibility = View.VISIBLE
            }
        }
        // 쿠폰 등록
        binding.discountApply.setOnClickListener {
            val intent = Intent()
            if(mDiscountApplyCheck){
                if((binding.discountInfoRecyclerView.adapter as CouponInfoAdapter).isCouponCheck()){
                    val price = (binding.discountInfoRecyclerView.adapter as CouponInfoAdapter).getCouponPrice()
                    intent.putExtra("exist", true)
                    intent.putExtra("coupon", price)
                    intent.putExtra("couponIdx", mCouponSelect)
                } else{
                    intent.getBooleanExtra("exist", false)
                }
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    fun changeCouponIdx(id: Int){
        if(id != -1) binding.discountApply.visibility = View.VISIBLE
        mCouponSelect = id
    }

    fun changeApplyText(text: String){
        binding.discountApply.text = text
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    override fun onGetCouponInfoSuccess(response: CouponInfoResponse) {
        if(response.code == 1000){
            if(response.result != null){
                binding.discountInfoRecyclerView.adapter = CouponInfoAdapter(response.result, mCouponSelect, mVersion, this)
                binding.discountInfoRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        } else {
            // 사용자 쿠폰 조회 실패
            dummyDate()
        }
    }

    override fun onGetCouponInfoFailure(message: String) {
        // 사용자 쿠폰 정보 조회 실패
        dummyDate()
    }

    override fun onPostApplyCouponSuccess(response: ApplyCouponResponse) {
        if(response.code == 1000){
            DiscountService(this).tryGetCouponInfo(mStoreIdx, getUserIdx())
        } else if(response.code == 3056){
            // 이미 지급받은 쿠폰임
            binding.discountCouponNumError.visibility = View.VISIBLE
            binding.discountCouponNumError.text = "이미 지급받은 쿠폰입니다."
        }
    }

    override fun onPostApplyCouponFailure(message: String) {
        // 쿠폰 조회 실패 -> 서버 통신 오류
        binding.discountCouponNumError.text = "잘못된 번호입니다."
        binding.discountCouponNumError.visibility = View.VISIBLE
    }

    override fun onGetMyEatsDiscountSuccess(response: CouponInfoResponse) {
        if(response.code == 1000){
            if(response.result != null){
                binding.discountInfoRecyclerView.adapter = CouponInfoAdapter(response.result, mCouponSelect, mVersion, this)
                binding.discountInfoRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        } else dummyDate()
    }

    override fun onGetMyEatsDiscountFailure(message: String) {
        dummyDate()
    }

    fun dummyDate() {
        val couponInfoList = ArrayList<SuperCoupon>()
        couponInfoList.add(SuperCoupon(-1, "해당가게 적용 가능 쿠폰", "3,000원 할인", "13,000이상 주문 시", "12/30 까지", "Y"))
        couponInfoList.add(SuperCoupon(-2, "쿠팡이츠 치킨마루", "5,000원 할인", "13,000이상 주문 시", "12/30 까지", "Y"))
        couponInfoList.add(SuperCoupon(-3, "쿠팡이츠 마피아떡볶이", "2,000원 할인", "13,000이상 주문 시", "12/30 까지", "N"))
        couponInfoList.add(SuperCoupon(-4, "쿠팡이츠 마피아떡볶이포장", "3,000원 할인", "13,000이상 주문 시", "12/30 까지", "N"))
        binding.discountInfoRecyclerView.adapter = CouponInfoAdapter(couponInfoList, -1, mVersion, this)
        binding.discountInfoRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}