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
    var filterSelected = Array(5) { i -> false }  // ????????? ??????????????? ????????????
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

        // ?????????????????? ??????
        mDBHelper = ResentSearchDatabase(this, "Search.db", null, 1)
        mDB = mDBHelper.writableDatabase

        // ?????? ????????? ??????
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

        // ?????? ??????
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

        // ?????? ????????? ??????
        binding.searchDetailRealContent.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY == 0){
                binding.searchDetailLine.visibility = View.GONE
            } else if(binding.searchDetailLine.visibility == View.GONE){
                binding.searchDetailLine.visibility = View.VISIBLE
            }
        }

        // ?????? ????????? ?????? ????????? ????????? ???
        if (keyword != "" && mLat != "" && mLon != "") {
            mSearchDetailRequest.lat = mLat
            mSearchDetailRequest.lon = mLon
            mSearchDetailRequest.keyword = keyword
            binding.searchDetailEditText.setText(keyword)
            startSearch()
            mKeywordSearch = true
            binding.searchEditDelete.visibility = View.GONE
        }

        // ?????? ????????? ?????? ??????
        binding.searchDetailTotalDelete.setOnClickListener {
            mDBHelper.deleteTotal(mDB)
            val list = ArrayList<ResentSearchDate>()
            (binding.searchDetailResentSearchRecyclerview.adapter as ResentSearchAdapter).refresh(
                list
            )
        }

        // ????????? ??????
        binding.searchDetailEditText.setOnKeyListener { v, keyCode, event ->

            if ((event != null) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // ???????????? ??????
                //showCustomToast("????????? ??????")
                if (mSearchAble) {
                    // ???????????? ?????? ??????
                    // showCustomToast("?????? ?????? ??????")
                    mKeywordSearch = true
                    keyword = binding.searchDetailEditText.text.toString()
                    mSearchDetailRequest.keyword = keyword
                    // ?????? ???????????? ??????
                    mDBHelper.addKeyword(mDB, keyword)
                    //Log.d("resentSearch", "keyword ??????")
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
                //Log.d("keyword", "????????? ??????")
                val str = binding.searchDetailEditText.text.toString()
                if (str.isNotEmpty()) {
                    binding.searchEditDelete.visibility = View.VISIBLE
                }
            }
        }

        // ???????????? ????????? ??????
        binding.toolbarBack.setOnClickListener {
            if (!mIsFinish && mUse) {
                changeSuperStatus()
            } else {
                finish()
            }
        }

        // ?????? ????????? ???
        binding.searchSearchImg.setOnClickListener {
            if (mSearchAble) {
                // ???????????? ?????? ??????
                // showCustomToast("?????? ?????? ??????")
                keyword = binding.searchDetailEditText.text.toString()
                mSearchDetailRequest.keyword = keyword
                // ?????? ???????????? ??????
                mDBHelper.addKeyword(mDB, keyword)
                Log.d("resentSearch", "keyword ??????")
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

        // ?????? ?????? ????????? ???
        binding.searchEditDelete.setOnClickListener {
            binding.searchSearchImg.setImageResource(R.drawable.ic_nav_search)
            binding.searchDetailEditText.setText("")
            binding.searchEditDelete.visibility = View.GONE
            mSearchAble = false
        }

        binding.searchDetailEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!mSearchAble && binding.searchDetailEditText.text.toString() != "") {
                    // ???????????? ???????????? ????????? ???
                    mSearchAble = true
                    binding.searchSearchImg.setImageResource(R.drawable.ic_nav_click_search)
                    binding.searchEditDelete.visibility = View.VISIBLE
                } else if (mSearchAble && binding.searchDetailEditText.text.toString() == "") {
                    // ???????????? ???????????? ????????? ???
                    mSearchAble = false
                    binding.searchSearchImg.setImageResource(R.drawable.ic_nav_search)
                    binding.searchEditDelete.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        // ??????
        // ?????????
        binding.homeFilterDeliveryPrice.setOnClickListener {
            val filter = FilterSearchDetailDialog(this, 1, mSelectDelivery)
            filter.show(supportFragmentManager, "deliveryPrice")
        }
        // ????????????
        binding.homeFilterMiniOrder.setOnClickListener {
            val filter = FilterSearchDetailDialog(this, 2, mSelectMinOrder)
            filter.show(supportFragmentManager, "orderMin")
        }
        // ?????????
        binding.homeFilterRecommend.setOnClickListener {
            val filter = FilterRecommendSearchDetailDialog(this, mRecommSelect)
            filter.show(supportFragmentManager, "recommend")
        }
        // ?????? ??????
        binding.homeFilterCheetah.setOnClickListener {
            if (!filterSelected[1]) {
                // ??????
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText.visibility = View.VISIBLE
                binding.homeFilterCheetahTitle.visibility = View.GONE
                mSearchDetailRequest.cheetah = "Y"
                filterSelected[1] = true
            } else {
                // ??????
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText.visibility = View.GONE
                binding.homeFilterCheetahTitle.visibility = View.VISIBLE
                mSearchDetailRequest.cheetah = null
                filterSelected[1] = false
            }
            // ????????? ??????
            startSearch()
        }
        // ????????????
        binding.homeFilterCoupon.setOnClickListener {
            if (!filterSelected[4]) {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText.visibility = View.VISIBLE
                binding.homeFilterCouponTitle.visibility = View.GONE
                // ??????
                mSearchDetailRequest.coupon = "Y"
                filterSelected[4] = true
            } else {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText.visibility = View.GONE
                binding.homeFilterCouponTitle.visibility = View.VISIBLE
                // ?????? ??????
                mSearchDetailRequest.coupon = null
                filterSelected[4] = false
            }
            // ????????? ???????????? ???
            startSearch()
        }
        // ????????? ??????
        binding.homeFilterClear.setOnClickListener {
            // ????????? ?????????
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
            // ?????? ???????????????
            binding.searchDetailSwipeRefresh.isRefreshing = true
            SearchDetailService(this).tryGetSearchSuper(mSearchDetailRequest)
            refreshFilter()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition( R.anim.horiaon_exit, R.anim.horizon_enter)
    }

    // ?????? ???????????? ??????
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

    // ????????? ?????? ????????? ?????? ????????????????????? ??????
    fun changeRecommendFilter(value: String, sendServerString: String, option: Int) {
        if (value == "?????????") {
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

    // ????????? ?????? ????????? ?????? ????????????????????? ??????
    fun changeDeliveryFilter(value: Int, valueString: String, select: Int) {
        if (value != -1) {
            val str = if(valueString == "????????????") "????????????" else "????????? ${valueString}??? ??????"
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterDeliveryPriceText.text = str
            binding.homeFilterDeliveryPriceText.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceTitle.visibility = View.GONE
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down_white)
            // ????????? ?????????
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

    // ?????? ?????? ????????? ?????? ????????????????????? ??????
    fun changeOrderMinFilter(value: Int, valueString: String, select: Int) {
        if (value != -1) {
            val str = "???????????? ${valueString}"
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterMiniOrderText.text = str
            binding.homeFilterMiniOrderText.visibility = View.VISIBLE
            binding.homeFilterMiniOrderTitle.visibility = View.GONE
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down_white)
            // ???????????? ?????????
            mSearchDetailRequest.ordermin = value
            filterSelected[3] = true
        } else {
            // ??????
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

    // ????????? ?????? ??????
    private fun changeClearFilter() {
        var num = 0
        for (value in filterSelected) {
            if (value) num++
        }
        if (num == 0) {
            // ????????? ?????? ??????
            binding.homeFilterClear.visibility = View.GONE
        } else {
            // ????????? ?????? ??????
            binding.homeFilterClear.visibility = View.VISIBLE
            binding.homeFilterClearNum.text = num.toString()
        }
    }

    // ?????????
    private fun refreshFilter() {
        mSelectMinOrder = 5
        mSelectDelivery = 5
        // ????????? ?????? ??????
        binding.homeFilterClear.visibility = View.GONE
        // ?????? ??????
        binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterMiniOrderText.visibility = View.GONE
        binding.homeFilterMiniOrderTitle.visibility = View.VISIBLE
        binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
        // ?????????
        binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterDeliveryPriceText.visibility = View.GONE
        binding.homeFilterDeliveryPriceTitle.visibility = View.VISIBLE
        binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
        // ?????????
        binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterRecommendText.text = "?????????"
        binding.homeFilterRecommendText.setTextColor(Color.parseColor(blackColor))
        binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down)
        mRecommSelect = 1
        // ????????????
        binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCouponText.visibility = View.GONE
        binding.homeFilterCouponTitle.visibility = View.VISIBLE
        // ????????????
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
                // ?????? ?????? ??? ????????? ??????
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