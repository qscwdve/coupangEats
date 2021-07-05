package com.example.coupangeats.src.categorySuper

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityCategorySuperBinding
import com.example.coupangeats.src.categorySuper.adapter.CategorySuperAdapter
import com.example.coupangeats.src.categorySuper.adapter.RecommendCategoryAdapter
import com.example.coupangeats.src.categorySuper.dialog.FilterCateforyDialog
import com.example.coupangeats.src.categorySuper.dialog.FilterRecommendCategoryDialog
import com.example.coupangeats.src.categorySuper.model.CategorySuperRequest
import com.example.coupangeats.src.categorySuper.model.CategorySuperResponse
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponse
import com.example.coupangeats.src.searchDetail.SearchDetailActivity
import com.example.coupangeats.src.searchDetail.SearchDetailService
import com.example.coupangeats.src.searchDetail.dialog.FilterRecommendSearchDetailDialog
import com.example.coupangeats.src.searchDetail.dialog.FilterSearchDetailDialog
import com.example.coupangeats.src.searchDetail.model.SearchDetailRequest
import com.softsquared.template.kotlin.config.BaseActivity

class CategorySuperActivity : BaseActivity<ActivityCategorySuperBinding>(ActivityCategorySuperBinding::inflate), CategorySuperActivityView {
    private lateinit var option : String
    private lateinit var mLat : String
    private lateinit var mLon : String
    private var mCategorySuperRequest = CategorySuperRequest()
    private var whiteColor = "#FFFFFF"
    private var blackColor = "#000000"
    private var mRecommSelect = 1
    var filterSelected = Array(5) { i -> false }  // 필터를 선택했는지 안했는데

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카테고리 받아옴
        option = intent.getStringExtra("categoryName") ?: ""
        mLat = intent.getStringExtra("lat") ?: ""
        mLon = intent.getStringExtra("lon") ?: ""
        mCategorySuperRequest.lat = mLat
        mCategorySuperRequest.lon = mLon
        Log.d("위도", "lat: $mLat lon: $mLon")
        mCategorySuperRequest.category = option
        binding.cartTitle.text = option
        // 카테고리 선택
        CategorySuperService(this).tryGetSuperCategory()
        startSuperSearch()

        // 뒤로가기
        binding.cartBack.setOnClickListener { finish() }

        // 검색
        binding.categorySearchImg.setOnClickListener {
            val intent = Intent(this, SearchDetailActivity::class.java).apply {
                this.putExtra("lat", mLat)
                this.putExtra("lon", mLon)
            }
            startActivity(intent)
        }
        // 필터
        // 배달비
        binding.homeFilterDeliveryPrice.setOnClickListener {
            val filter = FilterCateforyDialog(this, 1)
            filter.show(supportFragmentManager, "deliveryPrice")
        }
        // 최소주문
        binding.homeFilterMiniOrder.setOnClickListener {
            val filter = FilterCateforyDialog(this, 2)
            filter.show(supportFragmentManager, "orderMin")
        }
        // 추천순
        binding.homeFilterRecommend.setOnClickListener {
            val filter = FilterRecommendCategoryDialog(this, mRecommSelect)
            filter.show(supportFragmentManager, "recommend")
        }
        // 치타 배달
        binding.homeFilterCheetah.setOnClickListener {
            if (!filterSelected[1]) {
                // 선택
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText.setTextColor(Color.parseColor(whiteColor))
                mCategorySuperRequest.cheetah = "Y"
                filterSelected[1] = true
            } else {
                // 취소
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText.setTextColor(Color.parseColor(blackColor))
                mCategorySuperRequest.cheetah = null
                filterSelected[1] = false
            }
            // 서버와 통신
            startSuperSearch()
        }
        // 할인쿠폰
        binding.homeFilterCoupon.setOnClickListener {
            if (!filterSelected[4]) {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText.setTextColor(Color.parseColor(whiteColor))
                // 선택
                mCategorySuperRequest.coupon = "Y"
                filterSelected[4] = true
            } else {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText.setTextColor(Color.parseColor(blackColor))
                // 선택 취소
                mCategorySuperRequest.coupon = null
                filterSelected[4] = false
            }
            // 서버와 통신해야 함
            startSuperSearch()
        }
        // 초기화 누름
        binding.homeFilterClear.setOnClickListener {
            // 초기화 시키기
            val category = mCategorySuperRequest.category
            mCategorySuperRequest = CategorySuperRequest(
                mLat,
                mLon,
                category,
                "recomm",
                null,
                null,
                null,
                null,
                1,
                10
            )
            for (i in 0..4) {
                filterSelected[i] = false
            }
            CategorySuperService(this).tryGetCategorySuper(
                mCategorySuperRequest.lat,
                mCategorySuperRequest.lon,
                mCategorySuperRequest.category,
                mCategorySuperRequest.sort,
                mCategorySuperRequest.cheetah,
                mCategorySuperRequest.coupon,
                mCategorySuperRequest.ordermin,
                mCategorySuperRequest.deliverymin
            )
            binding.cartTitle.text = mCategorySuperRequest.category
            refreshFilter()
        }
     }
    // 추천순 필터 바꾸는 함수 다이얼로그에서 호출
    fun changeRecommendFilter(value: String, sendServerString: String, option: Int) {
        if (value == "추천순") {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down)
            filterSelected[0] = false
            mCategorySuperRequest.sort = sendServerString
        } else {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down_white)
            filterSelected[0] = true
            mCategorySuperRequest.sort = sendServerString
        }
        mCategorySuperRequest.sort = sendServerString
        startSuperSearch()
        mRecommSelect = option
    }

    // 배달비 필터 바꾸는 함수 다이얼로그에서 호출
    fun changeDeliveryFilter(value: Int, valueString: String) {
        if (value != -1) {
            val str = "배달비 ${valueString}원 이하"
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterDeliveryPriceText.text = str
            binding.homeFilterDeliveryPriceText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down_white)
            // 배달비 바꾸기
            mCategorySuperRequest.deliverymin = value
            filterSelected[2] = true
        } else {
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterDeliveryPriceText.text = "배달비"
            binding.homeFilterDeliveryPriceText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
            mCategorySuperRequest.deliverymin = null
            filterSelected[2] = false
        }

        startSuperSearch()
    }

    // 최소 주문 바꾸는 함수 다이얼로그에서 호출
    fun changeOrderMinFilter(value: Int, valueString: String) {
        if (value != -1) {
            val str = "최소주문 $valueString"
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterMiniOrderText.text = str
            binding.homeFilterMiniOrderText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down_white)
            // 최소주문 바꾸기
            mCategorySuperRequest.ordermin = value
            filterSelected[3] = true
        } else {
            // 전체
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterMiniOrderText.text = "최소주문"
            binding.homeFilterMiniOrderText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
            mCategorySuperRequest.ordermin = null
            filterSelected[3] = false
        }
        startSuperSearch()
    }

    // 초기화 필터 체크
    fun changeClearFilter() {
        var num = 0
        for (value in filterSelected) {
            if (value) num++
        }
        if (num == 0) {
            // 초기화 필터 다운
            binding.homeFilterClear.visibility = View.GONE
        } else {
            // 초기화 필터 오픈
            binding.homeFilterClear.visibility = View.VISIBLE
            binding.homeFilterClearNum.text = num.toString()
        }
    }

    // 초기화
    fun refreshFilter() {
        // 초기화 필터 다운
        binding.homeFilterClear.visibility = View.GONE
        // 최소 주문
        binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterMiniOrderText.text = "최소주문"
        binding.homeFilterMiniOrderText.setTextColor(Color.parseColor(blackColor))
        binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
        // 배달비
        binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterDeliveryPriceText.text = "배달비"
        binding.homeFilterDeliveryPriceText.setTextColor(Color.parseColor(blackColor))
        binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
        // 추천순
        binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterRecommendText.text = "추천순"
        binding.homeFilterRecommendText.setTextColor(Color.parseColor(blackColor))
        binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down)
        mRecommSelect = 1
        // 할인쿠폰
        binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCouponText.setTextColor(Color.parseColor(blackColor))
        // 치타배달
        binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
        binding.homeFilterCheetahText.setTextColor(Color.parseColor(blackColor))

    }
    fun startSuperSearch(){
        CategorySuperService(this).tryGetCategorySuper(
            mCategorySuperRequest.lat,
            mCategorySuperRequest.lon,
            mCategorySuperRequest.category,
            mCategorySuperRequest.sort,
            mCategorySuperRequest.cheetah,
            mCategorySuperRequest.coupon,
            mCategorySuperRequest.ordermin,
            mCategorySuperRequest.deliverymin
        )
        binding.cartTitle.text = mCategorySuperRequest.category
        changeClearFilter()
    }

    override fun onGetCategorySuperSuccess(response: CategorySuperResponse) {
        if(response.code == 1000){
            if(response.result.recommendStores != null){
                binding.categorySuperRecommendRecyclerView.adapter = RecommendCategoryAdapter(response.result.recommendStores, this)
                binding.categorySuperRecommendRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.categorySuperRecommendRecyclerView.visibility = View.VISIBLE
            } else {
                binding.categorySuperRecommendRecyclerView.visibility = View.GONE
            }
        }
    }

    override fun onGetCategorySuperFailure(message: String) {

    }

    override fun onGetSuperCategorySuccess(response: SuperCategoryResponse) {
        if(response.code == 1000){

            binding.categorySuperCategoryRecyclerView.adapter = CategorySuperAdapter(response.result, this, option)
            binding.categorySuperCategoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onGetSuperCategoryFailure(message: String) {

    }

    fun categoryChange(value: String) {
        // 바뀐걸로 서버 통신 해야 함
        mCategorySuperRequest.category = value
        startSuperSearch()
    }

    fun startSuper(storeIdx: Int){
        val intent = Intent(this, DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }
}