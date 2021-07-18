package com.example.coupangeats.src.superSearch

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivitySuperSearchBinding
import com.example.coupangeats.src.main.home.HomeService
import com.example.coupangeats.src.main.home.model.HomeInfo.HomeInfoRequest
import com.example.coupangeats.src.searchDetail.SearchDetailActivity
import com.example.coupangeats.src.superSearch.adapter.BaseInfoAdapter
import com.example.coupangeats.src.superSearch.model.BaseSuperInfo
import com.example.coupangeats.src.superSearch.model.DiscountSuperResponse
import com.example.coupangeats.src.superSearch.model.FilterRequest
import com.example.coupangeats.src.superSearch.model.NewSuperResponse
import com.example.coupangeats.src.superSearch.util.FilterRecommendSuperSearch
import com.example.coupangeats.src.superSearch.util.FilterSuperSearch
import com.example.coupangeats.util.FilterRecommendBottomSheetDialog
import com.example.coupangeats.util.FilterSuperBottomSheetDialog
import com.softsquared.template.kotlin.config.BaseActivity
import java.util.logging.Filter

class SuperSearchActivity :
    BaseActivity<ActivitySuperSearchBinding>(ActivitySuperSearchBinding::inflate),
    SuperSearchActivityView {
    var lat = ""
    var lon = ""
    var version = 1   // 1이면 할인중인 맛집 , 2이면 새로들어온 가게
    var filterSelected = Array(5) { i -> false }  // 필터를 선택했는지 안했는지
    private var whiteColor = "#FFFFFF"
    private var blackColor = "#000000"
    lateinit var mFilterRequest: FilterRequest
    private var mRecommSelect = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition( R.anim.horizon_start_enter, R.anim.horizon_start_exit)

        lat = intent.getStringExtra("lat") ?: ""
        lon = intent.getStringExtra("lon") ?: ""
        version = intent.getIntExtra("version", 1) ?: 1
        // 호출!!
        binding.superSearchTitle.text = if (version == 1) "할인 중인 맛집" else "새로 들어온 가게!"
        mRecommSelect = if(version == 1) 1 else 5
        if (version == 1) {
            binding.homeFilterCoupon.visibility = View.GONE
            mFilterRequest = FilterRequest(lat, lon, "recomm", null, "Y", null, null, 25, 1)
        } else {
            mFilterRequest = FilterRequest(lat, lon, "new", null, null, null, null, 25, 1)
            binding.homeFilterRecommendText.text = "신규매장순"
        }
        startFilterSuper()

        binding.superSearchBack.setOnClickListener {
            finish()
        }

        // 디테일한 검색으로 가기
        binding.superSearchDetailSearch.setOnClickListener {
            val intent = Intent(this, SearchDetailActivity::class.java).apply {
                this.putExtra("lat", lat)
                this.putExtra("lon", lon)
            }
            startActivity(intent)
        }

        // 치타 배달
        binding.homeFilterCheetah.setOnClickListener {
            if (!filterSelected[1]) {
                // 선택
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText.setTextColor(Color.parseColor(whiteColor))
                mFilterRequest.cheetah = "Y"
                filterSelected[1] = true
            } else {
                // 취소
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText.setTextColor(Color.parseColor(blackColor))
                mFilterRequest.cheetah = null
                filterSelected[1] = false
            }
            startFilterSuper()
        }
        // 할인쿠폰
        binding.homeFilterCoupon.setOnClickListener {
            if (!filterSelected[4]) {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText.setTextColor(Color.parseColor(whiteColor))
                // 선택
                mFilterRequest.coupon = "Y"
                filterSelected[4] = true
            } else {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText.setTextColor(Color.parseColor(blackColor))
                // 선택 취소
                mFilterRequest.coupon = null
                filterSelected[4] = false
            }
            // 서버와 통신해야 함
            startFilterSuper()
        }
        // 배달비
        binding.homeFilterDeliveryPrice.setOnClickListener {
            val filterSuperSearch = FilterSuperSearch(this, 1)
            filterSuperSearch.show(supportFragmentManager, "deliveryPrice")
        }
        // 최소주문
        binding.homeFilterMiniOrder.setOnClickListener {
            val filterSuperSearch = FilterSuperSearch(this, 2)
            filterSuperSearch.show(supportFragmentManager, "deliveryPrice")
        }
        // 추천순
        binding.homeFilterRecommend.setOnClickListener {
            val filterRecomemndSuperSearch = FilterRecommendSuperSearch(this, mRecommSelect)
            filterRecomemndSuperSearch.show(supportFragmentManager, "recommend")
        }
        // 초기화 누름
        binding.homeFilterClear.setOnClickListener {
            // 초기화 시키기
            if (version == 1) {
                mFilterRequest = FilterRequest(lat, lon, "recomm", null, "Y", null, null, 25, 1)
            } else {
                mFilterRequest = FilterRequest(lat, lon, "new", null, null, null, null, 25, 1)
                binding.homeFilterRecommendText.text = "신규매장순"
            }
            for (i in 0..4) {
                filterSelected[i] = false
            }
            startFilterSuper()
            refreshFilter()
        }

        // 스크롤
        binding.searchRecommendRecyclerview.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.d("scroll", "y : $scrollY")
            if(scrollY > 400){
                binding.superSearchScrollUpBtn.visibility = View.VISIBLE
            } else {
                binding.superSearchScrollUpBtn.visibility = View.GONE
            }
        }

        binding.superSearchScrollUpBtn.setOnClickListener {
            binding.searchRecommendRecyclerview.scrollTo(0, 0)
        }

        // 종료
        binding.superSearchBack.setOnClickListener { finish() }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition( R.anim.horiaon_exit, R.anim.horizon_enter)
    }

    fun startFilterSuper() {
        if (version == 1) {
            // 할인중인 맛집
            SuperSearchService(this).tryGetDiscountSuper(
                mFilterRequest.lat,
                mFilterRequest.lon,
                mFilterRequest.sort,
                mFilterRequest.cheetah,
                mFilterRequest.coupon,
                mFilterRequest.ordermin,
                mFilterRequest.deliverymin
            )
        } else {
            SuperSearchService(this).tryGetNewSuper(
                mFilterRequest.lat,
                mFilterRequest.lon,
                mFilterRequest.sort,
                mFilterRequest.cheetah,
                mFilterRequest.coupon,
                mFilterRequest.ordermin,
                mFilterRequest.deliverymin
            )
        }
        changeClearFilter()
    }

    // 추천순 필터 바꾸는 함수 다이얼로그에서 호출
    fun changeRecommendFilter(value: String, sendServerString: String, option: Int) {
        if (value == "추천순") {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down)
            filterSelected[0] = false
            mFilterRequest.sort = sendServerString
        } else {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down_white)
            filterSelected[0] = true
            mFilterRequest.sort = sendServerString
        }
        mFilterRequest.sort = sendServerString
        startFilterSuper()
        mRecommSelect = option
    }

    // 배달비 필터 바꾸는 함수 다이얼로그에서 호출
    fun changeDeliveryFilter(value: Int, valueString: String) {
        if (value != -1) {
            val str = if(valueString == "무료배달") "무료배달" else "배달비 ${valueString}원 이하"
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterDeliveryPriceText.text = str
            binding.homeFilterDeliveryPriceText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down_white)
            // 배달비 바꾸기
            mFilterRequest.deliverymin = value
            filterSelected[2] = true
        } else {
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterDeliveryPriceText.text = "배달비"
            binding.homeFilterDeliveryPriceText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
            mFilterRequest.deliverymin = null
            filterSelected[2] = false
        }

        startFilterSuper()
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
            mFilterRequest.ordermin = value
            filterSelected[3] = true
        } else {
            // 전체
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterMiniOrderText.text = "최소주문"
            binding.homeFilterMiniOrderText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
            mFilterRequest.ordermin = null
            filterSelected[3] = false
        }
        startFilterSuper()
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
        // 추천순 & 신규매장순
        binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterRecommendText.text = if(version == 1) "추천순" else "신규매장순"
        binding.homeFilterRecommendText.setTextColor(Color.parseColor(blackColor))
        binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down)
        mRecommSelect = if(version == 1) 1 else 5
        // 할인쿠폰
        binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCouponText.setTextColor(Color.parseColor(blackColor))
        // 치타배달
        binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
        binding.homeFilterCheetahText.setTextColor(Color.parseColor(blackColor))

    }

    override fun onGetNewSuperSuccess(response: NewSuperResponse) {
        if (response.code == 1000 ) {
            if( response.result.totalCount > 0){
                binding.searchRecommendRecyclerview.visibility = View.VISIBLE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.GONE
                setRecycler(response.result.newStores!!)
            } else{
                binding.searchRecommendRecyclerview.visibility = View.GONE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.VISIBLE
            }
        }
    }

    override fun onGetNewSuperFailure(message: String) {
        showCustomToast("새로들어온 매장 조회 실패")
    }

    override fun onGetDiscountSuperSuccess(response: DiscountSuperResponse) {
        if (response.code == 1000 && response.result.totalCount > 0) {
            if(response.result.totalCount > 0){
                binding.searchRecommendRecyclerview.visibility = View.VISIBLE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.GONE
                setRecycler(response.result.onSaleStores!!)
            } else {
                binding.searchRecommendRecyclerview.visibility = View.GONE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.VISIBLE
            }
            binding.searchRecommendRecyclerview.scrollTo(0,0)
        }
    }

    override fun onGetDiscountSuperFailure(message: String) {
        //showCustomToast("할인 매장 조회 실패")
    }

    fun setRecycler(baseSperList: ArrayList<BaseSuperInfo>) {
        binding.searchRecommendRecyclerview.adapter = BaseInfoAdapter(baseSperList)
        binding.searchRecommendRecyclerview.layoutManager = LinearLayoutManager(this)
    }
}