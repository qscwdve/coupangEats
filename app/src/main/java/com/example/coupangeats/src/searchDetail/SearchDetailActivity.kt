package com.example.coupangeats.src.searchDetail

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivitySearchDetailBinding
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.main.home.model.HomeInfo.RecommendStores
import com.example.coupangeats.src.main.order.past.OrderPastService
import com.example.coupangeats.src.searchDetail.adapter.ResentSearchAdapter
import com.example.coupangeats.src.searchDetail.adapter.SuperStoreAdapter
import com.example.coupangeats.src.searchDetail.dialog.FilterRecommendSearchDetailDialog
import com.example.coupangeats.src.searchDetail.dialog.FilterSearchDetailDialog
import com.example.coupangeats.src.searchDetail.model.ResentSearchDate
import com.example.coupangeats.src.searchDetail.model.SearchDetailRequest
import com.example.coupangeats.src.searchDetail.model.SearchDetailResponse
import com.example.coupangeats.util.CartMenuDatabase
import com.example.coupangeats.util.ResentSearchDatabase
import com.softsquared.template.kotlin.config.BaseActivity

@RequiresApi(Build.VERSION_CODES.O)
class SearchDetailActivity :
    BaseActivity<ActivitySearchDetailBinding>(ActivitySearchDetailBinding::inflate),
    SearchDetailActivityView {
    private var mSearchAble = false
    private var mSearchDetailRequest = SearchDetailRequest()
    var keyword = ""
    private var mKeywordSearch = false
    private var mIsSearch = false
    private var mIsFinish = true
    var mLat = ""
    var mLon = ""
    private var whiteColor = "#FFFFFF"
    private var blackColor = "#000000"
    private var mRecommSelect = 1
    private var mUse = false
    var filterSelected = Array(5) { i -> false }  // 필터를 선택했는지 안했는데
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var mDBHelper: ResentSearchDatabase
    private lateinit var mDB: SQLiteDatabase
    private var mSelectMinOrder = -1
    private var mSelectDelivery = -1
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition( R.anim.horizon_start_enter, R.anim.horizon_start_exit)

        inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // 데이타베이스 셋팅
        mDBHelper = ResentSearchDatabase(this, "Search.db", null, 1)
        mDB = mDBHelper.writableDatabase

        // 최근 검색어 셋팅
        binding.searchDetailResentSearchRecyclerview.adapter =
            ResentSearchAdapter(mDBHelper.getResentDate(mDB), this)
        binding.searchDetailResentSearchRecyclerview.layoutManager = LinearLayoutManager(this)

        mLat = intent.getStringExtra("lat") ?: ""
        mLon = intent.getStringExtra("lon") ?: ""
        keyword = intent.getStringExtra("keyword") ?: ""

        mSearchDetailRequest.lat = mLat
        mSearchDetailRequest.lon = mLon

        if (keyword == "") {
            binding.searchDetailKeywordParent.visibility = View.VISIBLE
        }

        binding.searchDetailSuperRecyclerview.adapter =
            SuperStoreAdapter(ArrayList<RecommendStores>(), this)
        binding.searchDetailSuperRecyclerview.layoutManager = LinearLayoutManager(this)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        val stateListAnimator = StateListAnimator()
        stateListAnimator.addState(
            intArrayOf(),
            ObjectAnimator.ofFloat(binding.appBar, "elevation", 0F)
        )
        binding.appBar.stateListAnimator = stateListAnimator

        // swipeRefresh
        binding.searchDetailSwipeRefresh.setOnRefreshListener {
            startSearch()
        }

        // 필터 그림자 설정
        binding.searchDetailRealContent.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY == 0){
                binding.searchDetailLine.visibility = View.GONE
            } else if(binding.searchDetailLine.visibility == View.GONE){
                binding.searchDetailLine.visibility = View.VISIBLE
            }
        }

        // 이미 키워드 값이 있으면 이걸로 함
        if (keyword != "" && mLat != "" && mLon != "") {
            mSearchDetailRequest.lat = mLat
            mSearchDetailRequest.lon = mLon
            mSearchDetailRequest.keyword = keyword
            binding.searchDetailEditText.setText(keyword)
            startSearch()
            mKeywordSearch = true
            binding.searchEditDelete.visibility = View.GONE
        }

        // 최근 검색어 전체 삭제
        binding.searchDetailTotalDelete.setOnClickListener {
            mDBHelper.deleteTotal(mDB)
            val list = ArrayList<ResentSearchDate>()
            (binding.searchDetailResentSearchRecyclerview.adapter as ResentSearchAdapter).refresh(
                list
            )
        }

        // 엔터키 누름
        binding.searchDetailEditText.setOnKeyListener { v, keyCode, event ->

            if ((event != null) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // 엔터키가 눌림
                //showCustomToast("엔터키 눌림")
                if (mSearchAble) {
                    // 서버한테 검색 요청
                    // showCustomToast("검색 가능 상태")
                    mKeywordSearch = true
                    keyword = binding.searchDetailEditText.text.toString()
                    mSearchDetailRequest.keyword = keyword
                    // 최근 검색어에 넣기
                    mDBHelper.addKeyword(mDB, keyword)
                    //Log.d("resentSearch", "keyword 넣음")
                    inputMethodManager.hideSoftInputFromWindow(
                        binding.searchDetailEditText.windowToken,
                        0
                    )
                    binding.searchEditDelete.visibility = View.GONE
                    binding.searchDetailEditText.clearFocus()
                    (binding.searchDetailResentSearchRecyclerview.adapter as ResentSearchAdapter).refresh(
                        mDBHelper.getResentDate(mDB)
                    )
                    startSearch()
                }
                true
            } else false


        }

        binding.searchDetailEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.searchDetailKeywordParent.visibility = View.VISIBLE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.GONE
                binding.searchDetailNoSearchParent.itemNoSuperParent.visibility = View.GONE
                binding.searchDetailSwipeRefresh.visibility = View.GONE
                // binding.searchDetailSuperParent.visibility = View.GONE
                mIsSearch = true
                mIsFinish = false
                //Log.d("keyword", "포커스 상태")
                val str = binding.searchDetailEditText.text.toString()
                if (str.isNotEmpty()) {
                    binding.searchEditDelete.visibility = View.VISIBLE
                }
            }
        }

        // 뒤로가기 눌렀을 경우
        binding.toolbarBack.setOnClickListener {
            if (!mIsFinish && mUse) {
                changeSuperStatus()
            } else {
                finish()
            }
        }

        // 검색 눌렀을 때
        binding.searchSearchImg.setOnClickListener {
            if (mSearchAble) {
                // 서버한테 검색 요청
                // showCustomToast("검색 가능 상태")
                keyword = binding.searchDetailEditText.text.toString()
                mSearchDetailRequest.keyword = keyword
                // 최근 검색어에 넣기
                mDBHelper.addKeyword(mDB, keyword)
                Log.d("resentSearch", "keyword 넣음")
                binding.searchEditDelete.visibility = View.GONE
                binding.searchDetailEditText.clearFocus()
                (binding.searchDetailResentSearchRecyclerview.adapter as ResentSearchAdapter).refresh(
                    mDBHelper.getResentDate(mDB)
                )
                mKeywordSearch = true
                inputMethodManager.hideSoftInputFromWindow(binding.searchDetailEditText.windowToken, 0)
                startSearch()
            }
        }

        // 엑스 버튼 눌렀을 때
        binding.searchEditDelete.setOnClickListener {
            binding.searchSearchImg.setImageResource(R.drawable.ic_nav_search)
            binding.searchDetailEditText.setText("")
            binding.searchEditDelete.visibility = View.GONE
            mSearchAble = false
        }

        binding.searchDetailEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!mSearchAble && binding.searchDetailEditText.text.toString() != "") {
                    // 사용자가 검색하기 시작할 때
                    mSearchAble = true
                    binding.searchSearchImg.setImageResource(R.drawable.ic_nav_click_search)
                    binding.searchEditDelete.visibility = View.VISIBLE
                } else if (mSearchAble && binding.searchDetailEditText.text.toString() == "") {
                    // 아무것도 검색하지 않았을 때
                    mSearchAble = false
                    binding.searchSearchImg.setImageResource(R.drawable.ic_nav_search)
                    binding.searchEditDelete.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        // 필터
        // 배달비
        binding.homeFilterDeliveryPrice.setOnClickListener {
            val filter = FilterSearchDetailDialog(this, 1, mSelectDelivery)
            filter.show(supportFragmentManager, "deliveryPrice")
        }
        // 최소주문
        binding.homeFilterMiniOrder.setOnClickListener {
            val filter = FilterSearchDetailDialog(this, 2, mSelectMinOrder)
            filter.show(supportFragmentManager, "orderMin")
        }
        // 추천순
        binding.homeFilterRecommend.setOnClickListener {
            val filter = FilterRecommendSearchDetailDialog(this, mRecommSelect)
            filter.show(supportFragmentManager, "recommend")
        }
        // 치타 배달
        binding.homeFilterCheetah.setOnClickListener {
            if (!filterSelected[1]) {
                // 선택
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText.visibility = View.VISIBLE
                binding.homeFilterCheetahTitle.visibility = View.GONE
                mSearchDetailRequest.cheetah = "Y"
                filterSelected[1] = true
            } else {
                // 취소
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText.visibility = View.GONE
                binding.homeFilterCheetahTitle.visibility = View.VISIBLE
                mSearchDetailRequest.cheetah = null
                filterSelected[1] = false
            }
            // 서버와 통신
            startSearch()
        }
        // 할인쿠폰
        binding.homeFilterCoupon.setOnClickListener {
            if (!filterSelected[4]) {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText.visibility = View.VISIBLE
                binding.homeFilterCouponTitle.visibility = View.GONE
                // 선택
                mSearchDetailRequest.coupon = "Y"
                filterSelected[4] = true
            } else {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText.visibility = View.GONE
                binding.homeFilterCouponTitle.visibility = View.VISIBLE
                // 선택 취소
                mSearchDetailRequest.coupon = null
                filterSelected[4] = false
            }
            // 서버와 통신해야 함
            startSearch()
        }
        // 초기화 누름
        binding.homeFilterClear.setOnClickListener {
            // 초기화 시키기
            mSearchDetailRequest = SearchDetailRequest(
                mLat,
                mLon,
                keyword,
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
            // 로딩 애니메이션
            binding.searchDetailSwipeRefresh.isRefreshing = true
            SearchDetailService(this).tryGetSearchSuper(mSearchDetailRequest)
            refreshFilter()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition( R.anim.horiaon_exit, R.anim.horizon_enter)
    }

    // 촤근 검색어로 검색
    fun startResentSearch(key: String, id: Int) {
        mKeywordSearch = true
        keyword = key
        mSearchDetailRequest.keyword = key
        binding.searchDetailEditText.setText(key)
        mDBHelper.modifyDate(mDB, id)
        inputMethodManager.hideSoftInputFromWindow(binding.searchDetailEditText.windowToken, 0)
        binding.searchEditDelete.visibility = View.GONE
        binding.searchDetailEditText.clearFocus()
        (binding.searchDetailResentSearchRecyclerview.adapter as ResentSearchAdapter).refresh(
            mDBHelper.getResentDate(mDB)
        )
        startSearch()
    }

    fun deleteResentSearchItem(id: Int) {
        mDBHelper.deleteToId(mDB, id)
    }

    fun changeSuperStatus() {
        binding.searchDetailKeywordParent.visibility = View.GONE
        binding.searchDetailSwipeRefresh.visibility = View.VISIBLE
        // binding.searchDetailSuperParent.visibility = View.VISIBLE
        binding.searchDetailEditText.setText(keyword)
        mIsSearch = true
        mIsFinish = true
        binding.searchDetailEditText.clearFocus()
        binding.searchEditDelete.visibility = View.GONE
        inputMethodManager.hideSoftInputFromWindow(binding.searchDetailEditText.windowToken, 0);
    }

    private fun changeSuperList(list: ArrayList<RecommendStores>) {
        (binding.searchDetailSuperRecyclerview.adapter as SuperStoreAdapter).changeList(list)
    }

    fun startSuper(storeIdx: Int) {
        val intent = Intent(this, DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }

    fun startSearch() {
        binding.searchDetailSwipeRefresh.isRefreshing = true
        SearchDetailService(this).tryGetSearchSuper(mSearchDetailRequest)
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
            mSearchDetailRequest.sort = sendServerString
        } else {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down_white)
            filterSelected[0] = true
            mSearchDetailRequest.sort = sendServerString
        }
        mSearchDetailRequest.sort = sendServerString
        startSearch()
        mRecommSelect = option
    }

    // 배달비 필터 바꾸는 함수 다이얼로그에서 호출
    fun changeDeliveryFilter(value: Int, valueString: String, select: Int) {
        if (value != -1) {
            val str = if(valueString == "무료배달") "무료배달" else "배달비 ${valueString}원 이하"
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterDeliveryPriceText.text = str
            binding.homeFilterDeliveryPriceText.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceTitle.visibility = View.GONE
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down_white)
            // 배달비 바꾸기
            mSearchDetailRequest.deliverymin = value
            filterSelected[2] = true
        } else {
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterDeliveryPriceText.visibility = View.GONE
            binding.homeFilterDeliveryPriceTitle.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
            mSearchDetailRequest.deliverymin = null
            filterSelected[2] = false
        }
        mSelectDelivery = select
        startSearch()
    }

    // 최소 주문 바꾸는 함수 다이얼로그에서 호출
    fun changeOrderMinFilter(value: Int, valueString: String, select: Int) {
        if (value != -1) {
            val str = "최소주문 ${valueString}"
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterMiniOrderText.text = str
            binding.homeFilterMiniOrderText.visibility = View.VISIBLE
            binding.homeFilterMiniOrderTitle.visibility = View.GONE
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down_white)
            // 최소주문 바꾸기
            mSearchDetailRequest.ordermin = value
            filterSelected[3] = true
        } else {
            // 전체
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterMiniOrderText.visibility = View.GONE
            binding.homeFilterMiniOrderTitle.visibility = View.VISIBLE
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
            mSearchDetailRequest.ordermin = null
            filterSelected[3] = false
        }
        mSelectMinOrder = select
        startSearch()
    }

    // 초기화 필터 체크
    private fun changeClearFilter() {
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
    private fun refreshFilter() {
        mSelectMinOrder = 5
        mSelectDelivery = 5
        // 초기화 필터 다운
        binding.homeFilterClear.visibility = View.GONE
        // 최소 주문
        binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterMiniOrderText.visibility = View.GONE
        binding.homeFilterMiniOrderTitle.visibility = View.VISIBLE
        binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
        // 배달비
        binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterDeliveryPriceText.visibility = View.GONE
        binding.homeFilterDeliveryPriceTitle.visibility = View.VISIBLE
        binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
        // 추천순
        binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterRecommendText.text = "추천순"
        binding.homeFilterRecommendText.setTextColor(Color.parseColor(blackColor))
        binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down)
        mRecommSelect = 1
        // 할인쿠폰
        binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCouponText.visibility = View.GONE
        binding.homeFilterCouponTitle.visibility = View.VISIBLE
        // 치타배달
        binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
        binding.homeFilterCheetahTitle.visibility = View.VISIBLE
        binding.homeFilterCheetahText.visibility = View.GONE
    }

    override fun onGetSearchSuperSuccess(response: SearchDetailResponse) {
        binding.searchDetailSwipeRefresh.isRefreshing = false
        binding.searchDetailLine.visibility = View.GONE
        if (response.code == 1000) {
            if (response.result.searchStores != null) {
                binding.searchDetailKeywordParent.visibility = View.GONE
                binding.searchDetailSwipeRefresh.visibility = View.VISIBLE
                // binding.searchDetailSuperParent.visibility = View.VISIBLE
                binding.searchDetailSuperRecyclerview.visibility = View.VISIBLE
                binding.searchDetailFilterParent.visibility = View.VISIBLE
                binding.searchDetailRealContent.visibility = View.VISIBLE
                // 검색 없을 때 필요한 것들
                binding.searchDetailNoSearchParent.itemNoSuperParent.visibility = View.GONE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.GONE
                changeSuperList(response.result.searchStores)
                if (mIsSearch) changeSuperStatus()
                mUse = true
            } else {
                binding.searchDetailRealContent.visibility = View.GONE
                binding.searchDetailKeywordParent.visibility = View.GONE
                binding.searchDetailSwipeRefresh.visibility = View.VISIBLE
                // binding.searchDetailSuperParent.visibility = View.VISIBLE
                binding.searchDetailSuperRecyclerview.visibility = View.GONE
                if (mKeywordSearch) {
                    Log.d("super", "mKeywordSearch : $mKeywordSearch")
                    binding.searchDetailRealContent.visibility = View.GONE
                    binding.searchDetailNoFilterLinearLayout.visibility = View.GONE
                    binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.GONE
                    binding.searchDetailNoSearchParent.itemNoSuperParent.visibility = View.VISIBLE
                    binding.searchDetailFilterParent.visibility = View.GONE
                } else {
                    binding.searchDetailFilterParent.visibility = View.VISIBLE
                    binding.searchDetailNoFilterLinearLayout.visibility = View.VISIBLE
                    binding.searchDetailNoSearchParent.itemNoSuperParent.visibility = View.GONE
                    binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.VISIBLE
                }
            }
            mKeywordSearch = false
            binding.searchDetailSuperRecyclerview.scrollTo(0, 0)
        }
    }

    override fun onGetSearchSuperFailure(message: String) {
        binding.searchDetailSwipeRefresh.isRefreshing = false
    }

}