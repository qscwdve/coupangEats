package com.example.coupangeats.src.categorySuper

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityCategorySuperBinding
import com.example.coupangeats.src.categorySuper.adapter.CategorySuperAdapter
import com.example.coupangeats.src.categorySuper.adapter.RecommendCategoryAdapter
import com.example.coupangeats.src.categorySuper.dialog.FilterCateforyDialog
import com.example.coupangeats.src.categorySuper.dialog.FilterRecommendCategoryDialog
import com.example.coupangeats.src.categorySuper.model.CategorySuperRequest
import com.example.coupangeats.src.categorySuper.model.CategorySuperResponse
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.main.home.model.HomeInfo.RecommendStores
import com.example.coupangeats.src.main.home.model.HomeInfo.StoreCategories
import com.example.coupangeats.src.main.search.model.SuperCategoryResponse
import com.example.coupangeats.src.main.search.model.SuperCategoryResponseResult
import com.example.coupangeats.src.searchDetail.SearchDetailActivity
import com.softsquared.template.kotlin.config.BaseActivity
import kotlin.math.abs

class CategorySuperActivity :
    BaseActivity<ActivityCategorySuperBinding>(ActivityCategorySuperBinding::inflate),
    CategorySuperActivityView {
    private lateinit var option: String
    private lateinit var mLat: String
    private lateinit var mLon: String
    private var mCategorySuperRequest = CategorySuperRequest()
    private var whiteColor = "#FFFFFF"
    private var blackColor = "#000000"
    private var mRecommSelect = 1
    private var mStickyScroll = 0
    var mRecommendFlag = false
    private var mScrollHeight = 0
    private var mScrollFlag = true
    private var mHeightcheck = false
    var filterSelected = Array(5) { i -> false }  // ????????? ??????????????? ????????????
    private var mCheetahBannerFlag = true  // ???????????? ???????????? ?????????????????? ???????????? ??????
    private var mSelectMinOrder = -1
    private var mSelectDelivery = -1
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.horizon_start_enter, R.anim.horizon_start_exit)

        // ???????????? ?????????
        option = intent.getStringExtra("categoryName") ?: ""
        mLat = intent.getStringExtra("lat") ?: ""
        mLon = intent.getStringExtra("lon") ?: ""
        val cheetahNum = intent.getIntExtra("cheetah", 0)
        val cheetahText = "??? ???????????? ?????? ${cheetahNum}???!"
        binding.categorySuperCheetahBannerText.text = cheetahText

        mCategorySuperRequest.lat = mLat
        mCategorySuperRequest.lon = mLon

        mCategorySuperRequest.category = option
        binding.cartTitle.text = option
        // ???????????? ??????
        CategorySuperService(this).tryGetSuperCategory()

        // ????????????
        binding.cartBack.setOnClickListener { finish() }

        // ??????
        binding.categorySearchImg.setOnClickListener {
            val intent = Intent(this, SearchDetailActivity::class.java).apply {
                this.putExtra("lat", mLat)
                this.putExtra("lon", mLon)
            }
            startActivity(intent)
        }

        // swipeRefresh
        binding.categorySuperSwipeRefresh.setOnRefreshListener {
            startSuperSearch()
        }

        // ???????????? ?????????
        binding.categorySuperBack.translationZ = 1f
        var nowScrollY = 0
        binding.categorySuperRecommendRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && nowScrollY < mStickyScroll) {
                    // ??? ??? ??????
                    val position = abs(binding.categorySuperBody.translationY)
                    if (position > mStickyScroll / 2) {
                        // ??????
                        binding.categorySuperNestedScrollView.smoothScrollTo(0, mStickyScroll)

                        if(binding.categorySuperCheetahBannerParent.translationY != 0F)
                            binding.categorySuperCheetahBannerParent.translationY = 0f
                    } else {
                        // ??????
                        binding.categorySuperNestedScrollView.smoothScrollTo(0, 0)
                        binding.categorySuperCheetahBannerParent.translationY = -mStickyScroll.toFloat()
                    }
                }
            }
        })
        binding.categorySuperNestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (mScrollFlag) {
                if (scrollY <= mStickyScroll) {
                    binding.categorySuperBody.translationY = -(scrollY).toFloat()
                    binding.categorySuperNestedScrollView.translationY = scrollY.toFloat()
                    binding.categorySuperFilterLine.visibility = View.GONE

                } else {
                    val position = binding.categorySuperBody.translationY
                    if (abs(position) <= mStickyScroll) {
                        binding.categorySuperBody.translationY = -(mStickyScroll).toFloat()
                        binding.categorySuperNestedScrollView.translationY =
                            mStickyScroll.toFloat()

                        if(binding.categorySuperCheetahBannerParent.translationY != 0F)
                            binding.categorySuperCheetahBannerParent.translationY = 0f

                    }
                    binding.categorySuperFilterLine.visibility = View.VISIBLE
                }
            } else {
                binding.categorySuperBody.translationY = 0f
                binding.categorySuperNestedScrollView.translationY = 0F
                if (scrollY > 10) {
                    binding.categorySuperFilterLine.visibility = View.VISIBLE

                    if(binding.categorySuperCheetahBannerParent.translationY != 0F)
                        binding.categorySuperCheetahBannerParent.translationY = 0f
                } else {
                    binding.categorySuperFilterLine.visibility = View.GONE
                }
            }
            nowScrollY = scrollY
        }

        // ???????????? ?????????
        binding.categorySuperRecommendRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(mCheetahBannerFlag) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        startCheetahAnimation("finish")
                        Log.d("scrollPosition", "state : finish")
                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        startCheetahAnimation("start")
                        Log.d("scrollPosition", "state : start")
                    }
                }
                Log.d("scrollPosition", "state : ${newState}")
            }
        })

        // ???????????? ?????? ??????
        binding.categorySuperCheetahBannerCancel.setOnClickListener {
            binding.categorySuperCheetahBannerParent.visibility = View.GONE
            mCheetahBannerFlag = false
        }

        // ??????
        // ?????????
        binding.homeFilterDeliveryPrice.setOnClickListener {
            val filter = FilterCateforyDialog(this, 1, mSelectDelivery)
            filter.show(supportFragmentManager, "deliveryPrice")
        }
        // ????????????
        binding.homeFilterMiniOrder.setOnClickListener {
            val filter = FilterCateforyDialog(this, 2, mSelectMinOrder)
            filter.show(supportFragmentManager, "orderMin")
        }
        // ?????????
        binding.homeFilterRecommend.setOnClickListener {
            val filter = FilterRecommendCategoryDialog(this, mRecommSelect)
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
                mCategorySuperRequest.cheetah = "Y"
                filterSelected[1] = true
            } else {
                // ??????
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText.visibility = View.GONE
                binding.homeFilterCheetahTitle.visibility = View.VISIBLE
                mCategorySuperRequest.cheetah = null
                filterSelected[1] = false
            }
            // ????????? ??????
            startSuperSearch()
        }
        // ????????????
        binding.homeFilterCoupon.setOnClickListener {
            if (!filterSelected[4]) {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText.visibility = View.VISIBLE
                binding.homeFilterCouponTitle.visibility = View.GONE
                // ??????
                mCategorySuperRequest.coupon = "Y"
                filterSelected[4] = true
            } else {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText.visibility = View.GONE
                binding.homeFilterCouponTitle.visibility = View.VISIBLE
                // ?????? ??????
                mCategorySuperRequest.coupon = null
                filterSelected[4] = false
            }
            // ????????? ???????????? ???
            startSuperSearch()
        }
        // ????????? ??????
        binding.homeFilterClear.setOnClickListener {
            // ????????? ?????????
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
            // ????????? ???????????????
            binding.categorySuperSwipeRefresh.isRefreshing = true
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

    // ???????????? ???????????????
    fun startCheetahAnimation(version: String){
        when(version){
            "start" -> {
                val animation = AnimationUtils.loadAnimation(this, R.anim.cheetah_start)
                binding.categorySuperCheetahBannerParent.startAnimation(animation)
                binding.categorySuperCheetahBannerParent.visibility = View.GONE
            }
            "finish" -> {
                val animation = AnimationUtils.loadAnimation(this, R.anim.cheetah_finish)
                binding.categorySuperCheetahBannerParent.startAnimation(animation)
                binding.categorySuperCheetahBannerParent.visibility = View.VISIBLE
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.horiaon_exit, R.anim.horizon_enter)
    }

    // ????????? ?????? ????????? ?????? ????????????????????? ??????
    fun changeRecommendFilter(value: String, sendServerString: String, option: Int) {
        if (value == "?????????") {
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

    // ????????? ?????? ????????? ?????? ????????????????????? ??????
    fun changeDeliveryFilter(value: Int, valueString: String, selectNum: Int) {
        if (value != -1) {
            val str = if (valueString == "????????????") "????????????" else "????????? ${valueString}??? ??????"
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterDeliveryPriceText.text = str
            binding.homeFilterDeliveryPriceText.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceTitle.visibility = View.GONE
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down_white)
            // ????????? ?????????
            mCategorySuperRequest.deliverymin = value
            filterSelected[2] = true
        } else {
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterDeliveryPriceText.visibility = View.GONE
            binding.homeFilterDeliveryPriceTitle.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
            mCategorySuperRequest.deliverymin = null
            filterSelected[2] = false
        }
        mSelectDelivery = selectNum
        startSuperSearch()
    }

    // ?????? ?????? ????????? ?????? ????????????????????? ??????
    fun changeOrderMinFilter(value: Int, valueString: String, selectNum: Int) {
        if (value != -1) {
            val str = "???????????? $valueString"
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterMiniOrderText.text = str
            binding.homeFilterMiniOrderText.visibility = View.VISIBLE
            binding.homeFilterMiniOrderTitle.visibility = View.GONE
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down_white)
            // ???????????? ?????????
            mCategorySuperRequest.ordermin = value
            filterSelected[3] = true
        } else {
            // ??????
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterMiniOrderText.visibility = View.GONE
            binding.homeFilterMiniOrderTitle.visibility = View.VISIBLE
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
            mCategorySuperRequest.ordermin = null
            filterSelected[3] = false
        }
        mSelectMinOrder = selectNum
        startSuperSearch()
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

    private fun startSuperSearch() {
        // ????????? ???????????????
        binding.categorySuperSwipeRefresh.isRefreshing = true
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

    fun setInitStartRecommend(){
        if(!mRecommendFlag){
            // recommend ??????
            startSuperSearch()
        }
    }

    override fun onGetCategorySuperSuccess(response: CategorySuperResponse) {
        binding.categorySuperSwipeRefresh.isRefreshing = false
        if (response.code == 1000) {
            if (response.result.recommendStores != null) {
                if(!mRecommendFlag){
                    mStickyScroll =
                        (binding.categorySuperCategoryRecyclerView.adapter as CategorySuperAdapter).getHeight()
                    binding.categorySuperRecommendRecyclerView.adapter =
                        RecommendCategoryAdapter(response.result.recommendStores, this, mStickyScroll)
                    binding.categorySuperRecommendRecyclerView.layoutManager = LinearLayoutManager(this)
                    mRecommendFlag = true
                    // ?????? ?????? ??????
                    binding.categorySuperCheetahBannerParent.translationY = -mStickyScroll.toFloat()
                    setBodyHeight()
                } else {
                    (binding.categorySuperRecommendRecyclerView.adapter as RecommendCategoryAdapter).changeDate(
                        response.result.recommendStores
                    )
                    // stickyScroll check
                    changeStickyScroll(response.result.recommendStores.size)
                }

                binding.categorySuperRecommendRecyclerView.visibility = View.VISIBLE
                binding.categorySuperNoSuperParent.itemNoSuperParent.visibility = View.GONE
                binding.categorySuperCoupon.visibility = View.VISIBLE

                if(binding.categorySuperNestedScrollView.translationY != 0F){
                    // ??????
                    binding.categorySuperNestedScrollView.scrollTo(0, mStickyScroll)
                } else {
                    // ??????
                    binding.categorySuperNestedScrollView.scrollTo(0, 0)
                }
                Log.d("position", "translationY : ${binding.categorySuperNestedScrollView.translationY}")
            } else {
                binding.categorySuperCoupon.visibility = View.GONE
                binding.categorySuperRecommendRecyclerView.visibility = View.GONE
                binding.categorySuperNoSuperParent.itemNoSuperParent.visibility = View.VISIBLE
            }
        }
    }

    override fun onGetCategorySuperFailure(message: String) {
        binding.categorySuperSwipeRefresh.isRefreshing = false

        if((0..1).random() == 0){
            val recommendList = ArrayList<RecommendStores>()
            recommendList.add(
                RecommendStores(
                    1, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dbreowkd.jpg?alt=media&token=d549713a-b673-45d9-9404-3db41b5d4f3b", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/EJrrnr.jpg?alt=media&token=b05bb795-91d1-4b1c-977d-712286720c4e")
                    ,"???????????? ??????", "????????????", "4.5", "0.7km",
                    "2,000???", "10~20???", null
                )
            )
            recommendList.add(
                RecommendStores(
                    2, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/tkaruq.jpg?alt=media&token=cc1dc826-43c4-4f0c-9041-24559ccc1a24")
                    ,"????????????", "Y", "2.5", "2.7km",
                    "3,000???", "20~30???", "3,000???"
                )
            )
            recommendList.add(
                RecommendStores(
                    3, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/EJrrnr.jpg?alt=media&token=b05bb795-91d1-4b1c-977d-712286720c4e")
                    ,"????????????", "??????", "3.5", "1.7km",
                    "3,000???", "20~30???", "3,000???"
                )
            )
            recommendList.add(
                RecommendStores(
                    4, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dlxkffldk.jpg?alt=media&token=3740442c-6d97-40ba-a486-0f756c4fa0ff", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/EJrrnr.jpg?alt=media&token=b05bb795-91d1-4b1c-977d-712286720c4e", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19")
                    ,"????????? ??????", "Y", "3.6", "2.5km",
                    "3,000???", "10~20???", "4,000???"
                )
            )
            recommendList.add(
                RecommendStores(
                    5, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19")
                    ,"????????? ??????", "??????", "3.0", "1.4km",
                    "3,000???", "30~40???", "2,000???"
                )
            )
            recommendList.add(
                RecommendStores(
                    2, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/ekfrqkf.jpg?alt=media&token=ee65ead3-0f0c-4036-9605-43459745690f")
                    ,"?????? ??????", "????????????", "4.2", "0.9km",
                    "3,000???", "10~30???", "1,000???"
                )
            )

            if(!mRecommendFlag){
                mStickyScroll =
                    (binding.categorySuperCategoryRecyclerView.adapter as CategorySuperAdapter).getHeight()
                binding.categorySuperRecommendRecyclerView.adapter =
                    RecommendCategoryAdapter(recommendList, this, mStickyScroll)
                binding.categorySuperRecommendRecyclerView.layoutManager = LinearLayoutManager(this)
                mRecommendFlag = true
                // ?????? ?????? ??????
                binding.categorySuperCheetahBannerParent.translationY = -mStickyScroll.toFloat()
                setBodyHeight()
            } else {
                (binding.categorySuperRecommendRecyclerView.adapter as RecommendCategoryAdapter).changeDate(
                    recommendList
                )
                // stickyScroll check
                changeStickyScroll(recommendList.size)
            }

            binding.categorySuperRecommendRecyclerView.visibility = View.VISIBLE
            binding.categorySuperNoSuperParent.itemNoSuperParent.visibility = View.GONE
            binding.categorySuperCoupon.visibility = View.VISIBLE

            if(binding.categorySuperNestedScrollView.translationY != 0F){
                // ??????
                binding.categorySuperNestedScrollView.scrollTo(0, mStickyScroll)
            } else {
                // ??????
                binding.categorySuperNestedScrollView.scrollTo(0, 0)
            }
            //Log.d("position", "translationY : ${binding.categorySuperNestedScrollView.translationY}")

        } else {
            binding.categorySuperCoupon.visibility = View.GONE
            binding.categorySuperRecommendRecyclerView.visibility = View.GONE
            binding.categorySuperNoSuperParent.itemNoSuperParent.visibility = View.VISIBLE
        }

    }

    override fun onGetSuperCategorySuccess(response: SuperCategoryResponse) {
        if (response.code == 1000) {
            binding.categorySuperCategoryRecyclerView.adapter =
                CategorySuperAdapter(response.result, this, option)
            binding.categorySuperCategoryRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        }
    }

    override fun onGetSuperCategoryFailure(message: String) {
        val categoryList = ArrayList<SuperCategoryResponseResult>()
        categoryList.add(SuperCategoryResponseResult("?????? ??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_new.png?alt=media&token=4b476b9a-ab86-4577-b114-c9b9fe6ac674"))
        categoryList.add(SuperCategoryResponseResult("1??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_one.png?alt=media&token=8b966c78-266f-48ea-a794-ba9604d83ada"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_korea.png?alt=media&token=ddae262b-93d9-4a24-8350-da29d0d1b740"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_chicken.png?alt=media&token=59b2d3c9-ddd8-4c2e-a2cc-39870c242d32"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_snackbar.png?alt=media&token=4f7fba7d-6c46-4aa4-927a-e118a0390bf8"))
        categoryList.add(SuperCategoryResponseResult("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_pork.png?alt=media&token=01abe6ea-04c6-491d-98ea-6119825d2e9d"))
        categoryList.add(SuperCategoryResponseResult("??????/??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_porkfeet.png?alt=media&token=cc1c1fd1-7ec2-4ba5-a8ca-6643e6d8c8fb"))
        categoryList.add(SuperCategoryResponseResult("???/???", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_steamed.png?alt=media&token=1a7a1214-a8d5-4663-83d5-78f9cf60fc5b"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_girll.png?alt=media&token=d427657a-a9ba-4771-9fc3-c6c1399118bc"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_pizza.jpg?alt=media&token=da1479d3-6a2c-4a48-8e1b-1d00d511541e"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_china.png?alt=media&token=bf4106b2-43cc-493a-8053-b4d514c9a781"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_japan.png?alt=media&token=c98e802b-8c3e-4aad-a010-d67502a8098c"))
        categoryList.add(SuperCategoryResponseResult("???/??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_seafood.png?alt=media&token=557775e1-9fac-4371-a935-aa14de4df839"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_western.png?alt=media&token=3207834b-a36f-46a9-8fbe-17368105f5d0"))
        categoryList.add(SuperCategoryResponseResult("??????/???", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_cha.png?alt=media&token=06c5c630-4581-4864-9b0f-0fdad5fd1bae"))
        categoryList.add(SuperCategoryResponseResult("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_dessert.png?alt=media&token=ecffe007-0159-44e1-b247-c8f747b83d68"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_snack.png?alt=media&token=d784db43-cce7-4e81-b69c-c0f3686fd2cc"))
        categoryList.add(SuperCategoryResponseResult("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_asian.png?alt=media&token=55a56695-d41a-4e22-8c5c-7c0cc9dcd000"))
        categoryList.add(SuperCategoryResponseResult("????????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_sandwich.png?alt=media&token=22427d8a-a491-46e8-8afb-923aa3918a3a"))
        categoryList.add(SuperCategoryResponseResult("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_salad.png?alt=media&token=b8636554-6652-47d2-b25f-be6ae867022a"))
        categoryList.add(SuperCategoryResponseResult("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_buger.png?alt=media&token=b3e892a7-776b-4650-a7ac-1ba069a8198c"))
        categoryList.add(SuperCategoryResponseResult("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_mexican.png?alt=media&token=22df2540-3529-4ca3-857e-b42426905e2c"))
        categoryList.add(SuperCategoryResponseResult("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_lunchbox.png?alt=media&token=e4d7aff1-6b3f-4bed-93e4-6f7554436292"))
        categoryList.add(SuperCategoryResponseResult("???", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_rice.png?alt=media&token=8efdebc6-7e0c-492d-98ce-2591bfc655f0"))
        categoryList.add(SuperCategoryResponseResult("???????????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_franchise.png?alt=media&token=fc05bb7e-0a16-4423-92cd-5dcedb3c16a1"))

        binding.categorySuperCategoryRecyclerView.adapter =
            CategorySuperAdapter(categoryList, this, option)
        binding.categorySuperCategoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    }

    fun categoryChange(value: String, version: Int) {
        // ???????????? ?????? ?????? ?????? ???
        mCategorySuperRequest.category = value

        (binding.categorySuperCategoryRecyclerView.adapter as CategorySuperAdapter).changeCategoryOnly(
            value
        )

        startSuperSearch()
    }

    fun startSuper(storeIdx: Int) {
        val intent = Intent(this, DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }

    private fun setBodyHeight() {
        val vto2: ViewTreeObserver = binding.categorySuperBody.viewTreeObserver
        vto2.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.categorySuperBody.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height = binding.categorySuperBody.measuredHeight
                binding.categorySuperBody.layoutParams.height = height + mStickyScroll

            }
        })
    }

    private fun changeStickyScroll(num: Int) {
        if (mStickyScroll < 100) {
            mStickyScroll =
                (binding.categorySuperCategoryRecyclerView.adapter as CategorySuperAdapter).getHeight()
        } else {
            mScrollFlag = num > 0
        }
    }
}