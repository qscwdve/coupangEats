package com.example.coupangeats.src.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentHomeBinding
import com.example.coupangeats.src.cart.CartActivity
import com.example.coupangeats.src.categorySuper.CategorySuperActivity
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.event.EventActivity
import com.example.coupangeats.src.eventItem.EventItemActivity
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.home.adapter.*
import com.example.coupangeats.src.main.home.model.HomeInfo.*
import com.example.coupangeats.src.main.home.model.cheetahCount.CheetahCountResponse
import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponse
import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponseResult
import com.example.coupangeats.src.searchDetail.SearchDetailActivity
import com.example.coupangeats.src.superSearch.SuperSearchActivity
import com.example.coupangeats.util.CartMenuDatabase
import com.example.coupangeats.util.FilterRecommendBottomSheetDialog
import com.example.coupangeats.util.FilterSuperBottomSheetDialog
import com.example.coupangeats.util.GpsControl
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment
import kotlin.collections.ArrayList

@SuppressLint("HandlerLeak")
class HomeFragment() :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home),
    HomeFragmentView {
    private var mAddress = ""
    var mScrollFlag = false
    var mScrollStart = false
    var mScrollFinish = false
    var mScrollValue = 0
    private var countDownTimer: CountDownTimer? = null
    private lateinit var mGpsControl: GpsControl
    private var mUserAddress: UserCheckResponseResult = UserCheckResponseResult(-1, "37.268672042686546", "127.00278199205103", "????????? ????????? ????????? ?????????1??? ????????? 13")
    private var mLat = ""
    private var mLon = ""
    var filterSelected = Array(5) { i -> false }  // ????????? ??????????????? ????????????
    private var whiteColor = "#FFFFFF"
    private var blackColor = "#000000"
    private var mHomeInfoRequest: HomeInfoRequest = HomeInfoRequest()
    private var mRecommSelect = 1
    private lateinit var mDBHelper: CartMenuDatabase
    private lateinit var mDB: SQLiteDatabase
    private val intervalTime = 3000.toLong() // ?????? ???????????? ???????????? ??????????????? (1500 = 1.5???)
    private var mCheetahNum = 0
    private var mSelectDelivery = -1
    private var mSelectMini = -1
    private var mAddressQuestion = false
    private var mGetMainDateFirst =
        "first" // first : firstMain , filter : recommend filter, address : address change : 3
    private var mAddressQuestionHandler = Handler(Looper.getMainLooper())
    private var mAddressQuestionRunnable : Runnable? = null
    private var mAddressQuestionFlag = false
    private var mAddressQuestionHandlerFlag = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mGpsControl = GpsControl(requireContext())

        // ?????????????????? ??????
        mDBHelper = CartMenuDatabase(requireContext(), "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        // ?????? ????????? ?????? ??????
        HomeService(this).tryGetUserCheckAddress(getUserIdx(), "first")

        // addressQuestion
        mAddressQuestion = arguments?.getBoolean("addressQuestion", false) ?: false
        if (mAddressQuestion) setAddressQuestion()

        // no address recyclerView adapter setting
        binding.homeNoAddressRecyclerview.adapter = BaseAddressAdapter(this)
        binding.homeNoAddressRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        // ?????? ??????
        binding.homeSearch.setOnClickListener {
            (activity as MainActivity).setSearchAdvencedFragment()
        }
        // ????????? ?????? ????????????
        binding.homeEventLook.setOnClickListener {
            startActivity(Intent(requireContext(), EventActivity::class.java))
        }

        // ????????? ??????
        binding.homeGpsImg.setOnClickListener {
            if (!loginCheck()) {
                (activity as MainActivity).loginBottomSheetDialogShow()
            } else {
                // ???????????? ?????? ?????? ?????? ????????? ?????? ???????????? ?????????
                (activity as MainActivity).startDeliveryAddressSettingActivityResult()
            }
        }

        // ????????? ??????
        binding.homeGpsAddress.setOnClickListener {
            if (!loginCheck()) {
                (activity as MainActivity).loginBottomSheetDialogShow()
            } else {
                // ???????????? ?????? ?????? ?????? ????????? ?????? ???????????? ?????????
                (activity as MainActivity).startDeliveryAddressSettingActivityResult()
            }
        }

        // ????????? ????????? ??????
        binding.homeAddressArrow.setOnClickListener {
            if (!loginCheck()) {
                (activity as MainActivity).loginBottomSheetDialogShow()
            } else {
                // ???????????? ?????? ?????? ?????? ????????? ?????? ???????????? ?????????
                (activity as MainActivity).startDeliveryAddressSettingActivityResult()
            }
        }

        // ??????
        binding.homeSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchDetailActivity::class.java).apply {
                this.putExtra("lat", mLat)
                this.putExtra("lon", mLon)
            }
            startActivity(intent)
        }

        // ?????????
        binding.homeFilterDeliveryPrice.setOnClickListener {
            val filterSuperBottomSheetDialog =
                FilterSuperBottomSheetDialog(this, 1, mSelectDelivery)
            filterSuperBottomSheetDialog.show(parentFragmentManager, "deliveryPrice")
        }
        // ????????????
        binding.homeFilterMiniOrder.setOnClickListener {
            val filterSuperBottomSheetDialog = FilterSuperBottomSheetDialog(this, 2, mSelectMini)
            filterSuperBottomSheetDialog.show(parentFragmentManager, "deliveryPrice")
        }
        // ?????????
        binding.homeFilterRecommend.setOnClickListener {
            val filterRecommendBottomSheetDialog =
                FilterRecommendBottomSheetDialog(this, mRecommSelect)
            filterRecommendBottomSheetDialog.show(parentFragmentManager, "recommend")
        }
        // ?????? ??????
        binding.homeFilterCheetah.setOnClickListener {
            if (!filterSelected[1]) {
                // ??????
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText.visibility = View.VISIBLE
                binding.homeFilterCheetahTitle.visibility = View.GONE
                binding.homeFilterCheetahBackground2.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg2.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText2.visibility = View.VISIBLE
                binding.homeFilterCheetahTitle2.visibility = View.GONE
                mHomeInfoRequest.cheetah = "Y"
                filterSelected[1] = true
            } else {
                // ??????
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText.visibility = View.GONE
                binding.homeFilterCheetahTitle.visibility = View.VISIBLE
                binding.homeFilterCheetahBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg2.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText2.visibility = View.GONE
                binding.homeFilterCheetahTitle2.visibility = View.VISIBLE
                mHomeInfoRequest.cheetah = null
                filterSelected[1] = false
            }
            // ????????? ??????
            startFilterServerSend()
        }
        // ????????????
        binding.homeFilterCoupon.setOnClickListener {
            if (!filterSelected[4]) {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText.visibility = View.VISIBLE
                binding.homeFilterCouponTitle.visibility = View.GONE
                binding.homeFilterCouponBackground2.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText2.visibility = View.VISIBLE
                binding.homeFilterCouponTitle2.visibility = View.GONE
                // ??????
                mHomeInfoRequest.coupon = "Y"
                filterSelected[4] = true
            } else {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText.visibility = View.GONE
                binding.homeFilterCouponTitle.visibility = View.VISIBLE
                binding.homeFilterCouponBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText2.visibility = View.GONE
                binding.homeFilterCouponTitle2.visibility = View.VISIBLE
                // ?????? ??????
                mHomeInfoRequest.coupon = null
                filterSelected[4] = false
            }
            // ????????? ???????????? ???
            startFilterServerSend()
        }
        // ????????? ??????
        binding.homeFilterClear.setOnClickListener {
            // ????????? ?????????
            resetFilter()
        }

        // ?????????
        binding.homeFilterDeliveryPrice2.setOnClickListener {
            val filterSuperBottomSheetDialog =
                FilterSuperBottomSheetDialog(this, 1, mSelectDelivery)
            filterSuperBottomSheetDialog.show(parentFragmentManager, "deliveryPrice")
        }
        // ????????????
        binding.homeFilterMiniOrder2.setOnClickListener {
            val filterSuperBottomSheetDialog = FilterSuperBottomSheetDialog(this, 2, mSelectMini)
            filterSuperBottomSheetDialog.show(parentFragmentManager, "deliveryPrice")
        }
        // ?????????
        binding.homeFilterRecommend2.setOnClickListener {
            val filterRecommendBottomSheetDialog =
                FilterRecommendBottomSheetDialog(this, mRecommSelect)
            filterRecommendBottomSheetDialog.show(parentFragmentManager, "recommend")
        }
        // ?????? ??????
        binding.homeFilterCheetah2.setOnClickListener {
            if (!filterSelected[1]) {
                // ??????
                binding.homeFilterCheetahBackground2.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg2.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText2.visibility = View.VISIBLE
                binding.homeFilterCheetahTitle2.visibility = View.GONE
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText.visibility = View.VISIBLE
                binding.homeFilterCheetahTitle.visibility = View.GONE
                mHomeInfoRequest.cheetah = "Y"
                filterSelected[1] = true
            } else {
                // ??????
                binding.homeFilterCheetahBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg2.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText2.visibility = View.GONE
                binding.homeFilterCheetahTitle2.visibility = View.VISIBLE
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText.visibility = View.GONE
                binding.homeFilterCheetahTitle.visibility = View.VISIBLE
                mHomeInfoRequest.cheetah = null
                filterSelected[1] = false
            }
            // ????????? ??????
            startFilterServerSend()
        }
        // ????????????
        binding.homeFilterCoupon2.setOnClickListener {
            if (!filterSelected[4]) {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText.visibility = View.VISIBLE
                binding.homeFilterCouponTitle.visibility = View.GONE
                binding.homeFilterCouponBackground2.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText2.visibility = View.VISIBLE
                binding.homeFilterCouponTitle2.visibility = View.GONE
                // ??????
                mHomeInfoRequest.coupon = "Y"
                filterSelected[4] = true
            } else {
                binding.homeFilterCouponBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText2.visibility = View.GONE
                binding.homeFilterCouponTitle2.visibility = View.VISIBLE
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText.visibility = View.GONE
                binding.homeFilterCouponTitle.visibility = View.VISIBLE
                // ?????? ??????
                mHomeInfoRequest.coupon = null
                filterSelected[4] = false
            }
            // ????????? ???????????? ???
            startFilterServerSend()
        }
        // ????????? ??????
        binding.homeFilterClear2.setOnClickListener {
            // ????????? ?????????
            resetFilter()
        }

        // ???????????? ?????? ????????????
        binding.homeDiscountSuperLook.setOnClickListener {
            startSalseSuper()
        }
        // ?????? ???????????????! ????????????
        binding.homeNewSuperLook.setOnClickListener {
            startNewSuper()
        }

        // ????????????
        binding.homeCartBag.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        // ????????? ?????? ??????
        binding.homeScrollUpBtn.setOnClickListener {
            binding.homeScrollView.scrollTo(0, 0)
            binding.homeScrollUpBtn.visibility = View.GONE
        }

        // ????????? ??????
        binding.homeScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            //Log.d("scrolled", "( $scrollX, $scrollY)  ,  ( $oldScrollX , $oldScrollY )")
            if (scrollY > 800) {
                binding.homeScrollUpBtn.visibility = View.VISIBLE
            } else {
                binding.homeScrollUpBtn.visibility = View.GONE
            }
            lastPosUpdate(scrollY)
        }
        binding.homeRecommendRecyclerview.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP)
                setAddressQuestionDown()
            if (!mScrollFinish) {
                if (event.action == MotionEvent.ACTION_UP) {
                    if (mScrollStart) {
                        mScrollStart = false
                        mScrollFlag = false
                    } else {
                        mScrollFlag = false
                    }
                } else if (event.action == MotionEvent.ACTION_DOWN) {
                    // ??????
                    mScrollFlag = true
                    mScrollValue = -1
                }
            }
            false
        }
        binding.homeScrollView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP)
                setAddressQuestionDown()
            if (!mScrollFinish) {
                if (event.action == MotionEvent.ACTION_UP) {
                    if (mScrollStart) {
                        mScrollStart = false
                        mScrollFlag = false
                    } else {
                        mScrollFlag = false
                    }
                } else if (event.action == MotionEvent.ACTION_DOWN) {
                    // ??????
                    mScrollFlag = true
                    mScrollValue = -1
                }
            }
            false
        }
        // ???????????? ?????????
        binding.homeCheetahBannerCancel.setOnClickListener {
            mScrollFinish = true
            binding.homeCheetahBannerParent.visibility = View.GONE
        }

    }

    private fun resetFilter(version: String = "filter") {
        // ????????? ?????????
        mHomeInfoRequest = HomeInfoRequest(
            mUserAddress!!.latitude,
            mUserAddress!!.longitude,
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
        startGetHomeDate(version)
        refreshFilter()
    }

    private fun scrollStart() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.cheetah_start)
        binding.homeCheetahBannerParent.startAnimation(animation)
        binding.homeCheetahBannerParent.visibility = View.GONE
    }

    fun scrollFinish() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.cheetah_finish)
        binding.homeCheetahBannerParent.visibility = View.VISIBLE
        binding.homeCheetahBannerParent.startAnimation(animation)

    }

    // setonTouch
    private fun lastPosUpdate(scrollPos: Int) {
        //Log.d("scrolled", "lastPosUpdate : falg = ${mScrollFlag} , start = ${mScrollStart} , scrollPos = ${scrollPos}")
        if (!mScrollFinish) {
            if (mScrollFlag && !mScrollStart) {
                if (mScrollValue != -1 && mScrollValue != scrollPos) {
                    // ????????? ??????
                    scrollStart()
                    mScrollStart = true
                } else if (mScrollValue == -1) {
                    mScrollValue = scrollPos
                }
            }
            if (countDownTimer != null) {
                countDownTimer!!.cancel()
            }

            countDownTimer = object : CountDownTimer(100, 100) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    if (!mScrollStart) {
                        scrollFinish()
                    }
                }
            }
            countDownTimer!!.start()
        }
    }

    // ?????? ????????? ?????????
    fun cartChange() {
        // ?????? ????????? ????????? ??????
        val num = mDBHelper.checkMenuNum(mDB)
        if (num > 0) {
            // ????????? ??????
            binding.homeCartBag.visibility = View.VISIBLE
            binding.homeCartNum.text = num.toString()
            // ?????? ??????
            val totalPrice = mDBHelper.menuTotalPrice(mDB)
            val totalPricetext = "${priceIntToString(totalPrice)}???"
            binding.homeCartPrice.text = totalPricetext
            binding.homeCheetahBannerParent.visibility = View.GONE
        } else {
            binding.homeCartBag.visibility = View.GONE
            binding.homeCheetahBannerParent.visibility = View.VISIBLE
        }
    }

    private fun toggle() {
        val view =
            if (binding.homeCartBag.visibility == View.GONE) binding.homeNoticeTextParentAbove
            else binding.homeNoticeTextCartAbove

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.check_box_down)

        view.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            view.startAnimation(animation)
            view.visibility = View.GONE
        }, 500)
    }

    // ?????? ???????????? ???????????? ?????????
    fun startFilterServerSend() {
        startGetHomeDate("filter")
        changeClearFilter()
    }

    // ????????? ?????? ????????? ?????? ????????????????????? ??????
    fun changeRecommendFilter(value: String, sendServerString: String, option: Int) {
        if (value == "?????????") {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down)
            binding.homeFilterRecommendBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterRecommendText2.text = value
            binding.homeFilterRecommendText2.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterRecommendImg2.setImageResource(R.drawable.ic_arrow_down)
            filterSelected[0] = false
            mHomeInfoRequest.sort = sendServerString
        } else {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down_white)
            binding.homeFilterRecommendBackground2.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterRecommendText2.text = value
            binding.homeFilterRecommendText2.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterRecommendImg2.setImageResource(R.drawable.ic_arrow_down_white)
            filterSelected[0] = true
            mHomeInfoRequest.sort = sendServerString
        }
        mHomeInfoRequest.sort = sendServerString
        startFilterServerSend()
        mRecommSelect = option
    }

    // ????????? ?????? ????????? ?????? ????????????????????? ??????
    fun changeDeliveryFilter(value: Int, valueString: String, select: Int) {
        if (value != -1) {
            val str = if (valueString == "????????????") "????????????" else "????????? ${valueString}??? ??????"
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterDeliveryPriceText.text = str
            binding.homeFilterDeliveryPriceText.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceTitle.visibility = View.GONE
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down_white)
            binding.homeFilterDeliveryPriceBackground2.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterDeliveryPriceText2.text = str
            binding.homeFilterDeliveryPriceText2.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceTitle2.visibility = View.GONE
            binding.homeFilterDeliveryPriceImg2.setImageResource(R.drawable.ic_arrow_down_white)
            // ????????? ?????????
            mHomeInfoRequest.deliverymin = value
            filterSelected[2] = true
        } else {
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterDeliveryPriceText.visibility = View.GONE
            binding.homeFilterDeliveryPriceTitle.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
            binding.homeFilterDeliveryPriceBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterDeliveryPriceText.visibility = View.GONE
            binding.homeFilterDeliveryPriceTitle.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceImg2.setImageResource(R.drawable.ic_arrow_down)
            mHomeInfoRequest.deliverymin = null
            filterSelected[2] = false
        }
        mSelectDelivery = select
        startFilterServerSend()
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
            binding.homeFilterMiniOrderBackground2.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterMiniOrderText2.text = str
            binding.homeFilterMiniOrderText2.visibility = View.VISIBLE
            binding.homeFilterMiniOrderTitle2.visibility = View.GONE
            binding.homeFilterMiniOrderImg2.setImageResource(R.drawable.ic_arrow_down_white)
            // ???????????? ?????????
            mHomeInfoRequest.ordermin = value
            filterSelected[3] = true
        } else {
            // ??????
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterMiniOrderText.visibility = View.GONE
            binding.homeFilterMiniOrderTitle.visibility = View.VISIBLE
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
            binding.homeFilterMiniOrderBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterMiniOrderText2.visibility = View.GONE
            binding.homeFilterMiniOrderTitle2.visibility = View.VISIBLE
            binding.homeFilterMiniOrderImg2.setImageResource(R.drawable.ic_arrow_down)
            mHomeInfoRequest.ordermin = null
            filterSelected[3] = false
        }
        mSelectMini = select
        startFilterServerSend()
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
            binding.homeFilterClear2.visibility = View.GONE
        } else {
            // ????????? ?????? ??????
            binding.homeFilterClear.visibility = View.VISIBLE
            binding.homeFilterClearNum.text = num.toString()
            binding.homeFilterClear2.visibility = View.VISIBLE
            binding.homeFilterClearNum2.text = num.toString()
        }
    }

    // ?????????
    private fun refreshFilter() {
        mSelectMini = 5
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

        // 2
        // ????????? ?????? ??????
        binding.homeFilterClear2.visibility = View.GONE
        // ?????? ??????
        binding.homeFilterMiniOrderBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterMiniOrderText2.visibility = View.GONE
        binding.homeFilterMiniOrderTitle2.visibility = View.VISIBLE
        binding.homeFilterMiniOrderImg2.setImageResource(R.drawable.ic_arrow_down)
        // ?????????
        binding.homeFilterDeliveryPriceBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterDeliveryPriceText2.visibility = View.GONE
        binding.homeFilterDeliveryPriceTitle2.visibility = View.VISIBLE
        binding.homeFilterDeliveryPriceImg2.setImageResource(R.drawable.ic_arrow_down)
        // ?????????
        binding.homeFilterRecommendBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterRecommendText2.text = "?????????"
        binding.homeFilterRecommendText2.setTextColor(Color.parseColor(blackColor))
        binding.homeFilterRecommendImg2.setImageResource(R.drawable.ic_arrow_down)
        mRecommSelect = 1
        // ????????????
        binding.homeFilterCouponBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCouponText2.visibility = View.GONE
        binding.homeFilterCouponTitle2.visibility = View.VISIBLE
        // ????????????
        binding.homeFilterCheetahBackground2.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCheetahImg2.setImageResource(R.drawable.main_cheetah)
        binding.homeFilterCheetahTitle2.visibility = View.VISIBLE
        binding.homeFilterCheetahText2.visibility = View.GONE
    }

    private fun setAddressQuestion() {
        mAddressQuestionFlag = true
        binding.homeAddressQuestion.visibility = View.VISIBLE
        mAddressQuestionRunnable = Runnable {
            if(mAddressQuestionFlag){
                binding.homeAddressQuestion.visibility = View.GONE
                mAddressQuestionFlag = false
            }
            mAddressQuestionHandlerFlag = false
        }
        mAddressQuestionHandlerFlag = true
        mAddressQuestionHandler.postDelayed(mAddressQuestionRunnable!!, 4000)
    }

    fun setAddressQuestionDown() {
        if (mAddressQuestionFlag) {
            binding.homeAddressQuestion.visibility = View.GONE
            mAddressQuestionFlag = false
        }
    }

    override fun onResume() {
        super.onResume()
        cartChange()
    }

    fun startUserAddressCheckAndGetMainDate(check: Boolean = false) {
        if (check) {
            // ?????? ?????????, addressQuestion
            setAddressQuestion()
            toggle()
            // Log.d("toggle", "?????????")
            HomeService(this).tryGetUserCheckAddress(getUserIdx(), "address")
        }
    }

    fun startUserCheckAddress(){
        HomeService(this).tryGetUserCheckAddress(getUserIdx(), "first")
    }

    fun getUserIdx(): Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    private fun loginCheck(): Boolean {
        return ApplicationClass.sSharedPreferences.getInt("userIdx", -1) != -1
    }

    fun baseAddressResult(baseAddress: String) {
        binding.homeNoAddress.visibility = View.GONE
        binding.homeRealContent.visibility = View.VISIBLE
    }

    private fun gpsCheck() {
        var gpsCheck = false
        if (ApplicationClass.sSharedPreferences.getBoolean("gps", false)) {
            // gps ?????? ??????
            val location = mGpsControl.getLocation()
            if (location != null) {
                mUserAddress = UserCheckResponseResult(
                    0,
                    location.latitude.toString(),
                    location.longitude.toString(),
                    "????????? ??????1.2.3.4?????? 164-7"
                )
                val address = mGpsControl.getCurrentAddress(location.latitude, location.longitude)
                binding.homeGpsAddress.text = address
                mLat = location.latitude.toString()
                mLon = location.longitude.toString()
                gpsCheck = true
            } else {
                mUserAddress = UserCheckResponseResult(
                    0,
                    (37.268672042686546).toString(),
                    (127.00278199205103).toString(),
                    "????????? ????????? ????????? ?????????1??? ????????? 13"
                )
                binding.homeGpsAddress.text = mUserAddress!!.mainAddress
                mLat = (37.268672042686546).toString()
                mLon = (127.00278199205103).toString()
            }
            getMainData("first")
        } else {
            mUserAddress = UserCheckResponseResult(
                0,
                (37.268672042686546).toString(),
                (127.00278199205103).toString(),
                "????????? ????????? ????????? ?????????1??? ????????? 13"
            )
            binding.homeGpsAddress.text = mUserAddress!!.mainAddress
            mLat = (37.268672042686546).toString()
            mLon = (127.00278199205103).toString()
            getMainData("first")
        }
        if (gpsCheck) {
            binding.homeNoAddress.visibility = View.GONE
            binding.homeRealContent.visibility = View.VISIBLE
        } else {
            binding.homeNoAddress.visibility = View.VISIBLE
            binding.homeRealContent.visibility = View.GONE
        }
    }

    fun getMainData(version: String) {
        // ??? ????????? ??????
        val re = HomeInfoRequest(
            mUserAddress!!.latitude,
            mUserAddress!!.longitude,
            "recomm",
            null,
            null,
            null,
            null,
            1,
            10
        )
        mHomeInfoRequest = re
        (activity as MainActivity).changeAddress(mUserAddress!!.latitude, mUserAddress!!.longitude)

        startGetHomeDate(version)
    }

    fun startGetHomeDate(version: String) {
        mGetMainDateFirst = version
        HomeService(this).tryGetHomeData(mHomeInfoRequest)
    }

    override fun onUserCheckAddressSuccess(response: UserCheckResponse, version: String) {
        if (response.code == 1000) {
            if (response.result.addressIdx != 0) {
                // ????????? ????????? ?????? ??????
                mUserAddress = response.result
                binding.homeGpsAddress.text = mUserAddress!!.mainAddress
                mLat = mUserAddress!!.latitude
                mLon = mUserAddress!!.longitude
                if (mAddress != "" && mAddress != mUserAddress!!.mainAddress) {
                    toggle()
                    mAddress = mUserAddress!!.mainAddress
                }
                // ????????????
                HomeService(this).tryGetCheetahCount(mLat, mLon)
                getMainData(version)  // ??? ?????????
            } else {
                // ????????? ????????? ?????? ??????
                gpsCheck()
            }
        } else {
            gpsCheck()
        }
    }

    override fun onUserCheckAddressFailure(message: String) {
        // showCustomToast("?????? ?????? ?????? ??????")
        val img =
            "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMTA1MjdfNTUg%2FMDAxNjIyMTEzMDg3Njc5.J0L7A04dtBVEKOcBVbdbKmJFgHq12BTAAq3fDHFlQoIg.0vN8BoEqOEQjqhU3i-Q7s6MFWbrQ4ElJiJfGWWxoeBQg.JPEG.hs_1472%2Foutput_2445714095.jpg&type=sc960_832"
        val categoryList = ArrayList<StoreCategories>()
        categoryList.add(StoreCategories("?????? ??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_new.png?alt=media&token=4b476b9a-ab86-4577-b114-c9b9fe6ac674"))
        categoryList.add(StoreCategories("1??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_one.png?alt=media&token=8b966c78-266f-48ea-a794-ba9604d83ada"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_korea.png?alt=media&token=ddae262b-93d9-4a24-8350-da29d0d1b740"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_chicken.png?alt=media&token=59b2d3c9-ddd8-4c2e-a2cc-39870c242d32"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_snackbar.png?alt=media&token=4f7fba7d-6c46-4aa4-927a-e118a0390bf8"))
        categoryList.add(StoreCategories("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_pork.png?alt=media&token=01abe6ea-04c6-491d-98ea-6119825d2e9d"))
        categoryList.add(StoreCategories("??????/??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_porkfeet.png?alt=media&token=cc1c1fd1-7ec2-4ba5-a8ca-6643e6d8c8fb"))
        categoryList.add(StoreCategories("???/???", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_steamed.png?alt=media&token=1a7a1214-a8d5-4663-83d5-78f9cf60fc5b"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_girll.png?alt=media&token=d427657a-a9ba-4771-9fc3-c6c1399118bc"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_pizza.jpg?alt=media&token=da1479d3-6a2c-4a48-8e1b-1d00d511541e"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_china.png?alt=media&token=bf4106b2-43cc-493a-8053-b4d514c9a781"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_japan.png?alt=media&token=c98e802b-8c3e-4aad-a010-d67502a8098c"))
        categoryList.add(StoreCategories("???/??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_seafood.png?alt=media&token=557775e1-9fac-4371-a935-aa14de4df839"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_western.png?alt=media&token=3207834b-a36f-46a9-8fbe-17368105f5d0"))
        categoryList.add(StoreCategories("??????/???", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_cha.png?alt=media&token=06c5c630-4581-4864-9b0f-0fdad5fd1bae"))
        categoryList.add(StoreCategories("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_dessert.png?alt=media&token=ecffe007-0159-44e1-b247-c8f747b83d68"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_snack.png?alt=media&token=d784db43-cce7-4e81-b69c-c0f3686fd2cc"))
        categoryList.add(StoreCategories("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_asian.png?alt=media&token=55a56695-d41a-4e22-8c5c-7c0cc9dcd000"))
        categoryList.add(StoreCategories("????????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_sandwich.png?alt=media&token=22427d8a-a491-46e8-8afb-923aa3918a3a"))
        categoryList.add(StoreCategories("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_salad.png?alt=media&token=b8636554-6652-47d2-b25f-be6ae867022a"))
        categoryList.add(StoreCategories("??????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_buger.png?alt=media&token=b3e892a7-776b-4650-a7ac-1ba069a8198c"))
        categoryList.add(StoreCategories("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_mexican.png?alt=media&token=22df2540-3529-4ca3-857e-b42426905e2c"))
        categoryList.add(StoreCategories("?????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_lunchbox.png?alt=media&token=e4d7aff1-6b3f-4bed-93e4-6f7554436292"))
        categoryList.add(StoreCategories("???", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_rice.png?alt=media&token=8efdebc6-7e0c-492d-98ce-2591bfc655f0"))
        categoryList.add(StoreCategories("???????????????", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_franchise.png?alt=media&token=fc05bb7e-0a16-4423-92cd-5dcedb3c16a1"))

        val salseList = ArrayList<OnSaleStores>()
        salseList.add(OnSaleStores(1, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19", "????????????", "4.5(10)", "1.2km", "2,000???"))
        salseList.add(OnSaleStores(2, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dbreowkd.jpg?alt=media&token=d549713a-b673-45d9-9404-3db41b5d4f3b", "????????????", "5.0(42)", "0.2km", "7,000???"))
        salseList.add(OnSaleStores(3, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dbrghl.jpg?alt=media&token=68d5c99e-f96e-41a4-96d6-88d3ba489e70", "???????????? ??????", "4.2(78)", "2.2km", "1,000???"))
        salseList.add(OnSaleStores(4, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/ekfrqkf.jpg?alt=media&token=ee65ead3-0f0c-4036-9605-43459745690f", "????????????", null, "1.5km", "5,000???"))
        salseList.add(OnSaleStores(5, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/tkaruq.jpg?alt=media&token=cc1dc826-43c4-4f0c-9041-24559ccc1a24", "????????? ?????????", "4.0(42)", "1.7km", "3,000???"))

        val newList = ArrayList<NewStores>()
        newList.add(NewStores(1,"https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19", "????????????", "5.0(2)", "1.2km", null, "????????? 2,000???"))
        newList.add(NewStores(2, img, "????????????", null, "0.2km", null, "????????? 1,000???"))
        newList.add(NewStores(3, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dlxkffldk.jpg?alt=media&token=3740442c-6d97-40ba-a486-0f756c4fa0ff", "????????? ?????????", "4.5(7)", "1.5km", null, "????????? 3,000???"))
        newList.add(NewStores(4,"https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dbrghl.jpg?alt=media&token=68d5c99e-f96e-41a4-96d6-88d3ba489e70", "???????????? ??????", null, "0.8km", null, "????????? 2,000???"))
        setNew(newList)
        val recommendList = ArrayList<RecommendStores>()
        recommendList.add(RecommendStores(
            1, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dbreowkd.jpg?alt=media&token=d549713a-b673-45d9-9404-3db41b5d4f3b", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/EJrrnr.jpg?alt=media&token=b05bb795-91d1-4b1c-977d-712286720c4e")
            ,"???????????? ??????", "????????????", "4.5", "0.7km",
            "2,000???", "10~20???", null
        ))
        recommendList.add(RecommendStores(
            2, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/tkaruq.jpg?alt=media&token=cc1dc826-43c4-4f0c-9041-24559ccc1a24")
            ,"????????????", "Y", "2.5", "2.7km",
            "3,000???", "20~30???", "3,000???"
        ))
        recommendList.add(RecommendStores(
            3, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/EJrrnr.jpg?alt=media&token=b05bb795-91d1-4b1c-977d-712286720c4e")
            ,"????????????", "??????", "3.5", "1.7km",
            "3,000???", "20~30???", "3,000???"
        ))
        recommendList.add(RecommendStores(
            4, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dlxkffldk.jpg?alt=media&token=3740442c-6d97-40ba-a486-0f756c4fa0ff", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/EJrrnr.jpg?alt=media&token=b05bb795-91d1-4b1c-977d-712286720c4e", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19")
            ,"????????? ??????", "Y", "3.6", "2.5km",
            "3,000???", "10~20???", "4,000???"
        ))
        recommendList.add(RecommendStores(
            5, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19")
            ,"????????? ??????", "??????", "3.0", "1.4km",
            "3,000???", "30~40???", "2,000???"
        ))
        recommendList.add(RecommendStores(
            2, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/ekfrqkf.jpg?alt=media&token=ee65ead3-0f0c-4036-9605-43459745690f")
            ,"?????? ??????", "????????????", "4.2", "0.9km",
            "3,000???", "10~30???", "1,000???"
        ))


        // ????????? ?????? ??????
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding.homeScrollView.mHeaderParentPosition = binding.homeRecommendSuper.top.toFloat()
            binding.homeScrollView.mHeaderInitPosition =
                binding.homeFilterParent.top.toFloat() ?: 0f
            setStickyScroll()
        }, 200)

        val category = categoryList
        val salse = salseList
        val recommend = recommendList

        setCategory(category)
        setOnSalse(salse)
        binding.homeDiscountSuper.visibility = View.VISIBLE

        setRecommend(recommend)
        binding.homeRecommendRecyclerview.visibility = View.VISIBLE

        // ?????? ????????? ??????
        setEvent(
            arrayListOf(
                Events(1, null, R.drawable.isaac_event),
                Events(2, null, R.drawable.bbq_event)
            )
        )
    }

    override fun onGetHomeDataSuccess(response: HomeInfoResponse) {
        if (response.code == 1000) {
            if (mGetMainDateFirst == "first") {
                binding.homeScrollView.scrollTo(0, 0)
                val events = response.result.events
                val category = response.result.storeCategories
                val salse = response.result.onSaleStores
                val new = response.result.newStores
                val recommend = response.result.recommendStores
                if (events != null) {
                    setEvent(events)
                } else {
                    // ?????? ????????? ??????
                    setEvent(
                        arrayListOf(
                            Events(1, null, R.drawable.isaac_event),
                            Events(2, null, R.drawable.bbq_event)
                        )
                    )
                }
                setCategory(category)
                if (salse != null) {
                    setOnSalse(salse)
                    binding.homeDiscountSuper.visibility = View.VISIBLE
                    binding.homeDiscountLine.visibility = View.VISIBLE
                } else {
                    binding.homeDiscountSuper.visibility = View.GONE
                    binding.homeDiscountLine.visibility = View.GONE
                }
                if (new != null) {
                    setNew(new)
                    binding.homeNewSuper.visibility = View.VISIBLE
                } else {
                    binding.homeNewSuper.visibility = View.GONE
                }
                if (recommend != null) {
                    setRecommend(recommend)
                    binding.homeRecommendRecyclerview.visibility = View.VISIBLE
                    binding.noSuperParent.itemNoSuperParent.visibility = View.GONE
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        binding.homeScrollView.mHeaderParentPosition = binding.homeRecommendSuper.top.toFloat()
                        binding.homeScrollView.mHeaderInitPosition =
                            binding.homeFilterParent.top.toFloat() ?: 0f
                        setStickyScroll()
                    }, 200)
                } else {
                    binding.homeRecommendRecyclerview.visibility = View.GONE
                    binding.noSuperParent.itemNoSuperParent.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        setStickyScroll()
                    }, 200)
                }
                //resetFilter("second")
                refreshFilter()

            } else if (mGetMainDateFirst == "filter") {
                // ?????? ?????? ?????? ??????
                val recommend = response.result.recommendStores
                if (recommend != null) {
                    if (recommend.size > 0) {
                        (binding.homeRecommendRecyclerview.adapter as RecommendAdapter).changeItem(
                            recommend
                        )
                    }
                    // ????????? ?????? ?????? ??? ?????? ??????
                    val position =
                        binding.homeScrollView.mHeaderInitPosition + binding.homeScrollView.mHeaderParentPosition
                    binding.homeScrollView.scrollTo(0, position.toInt())

                    binding.homeRecommendRecyclerview.visibility = View.VISIBLE
                    binding.noSuperParent.itemNoSuperParent.visibility = View.GONE
                } else {
                    binding.homeRecommendRecyclerview.visibility = View.GONE
                    binding.noSuperParent.itemNoSuperParent.visibility = View.VISIBLE
                }
            } else if (mGetMainDateFirst == "address"){
                // address ?????????
                binding.homeScrollView.scrollTo(0, 0)
                val result = response.result
                if (result.newStores != null && result.newStores.size > 0) {
                    if (binding.homeNewSuperRecyclerview.adapter == null) {
                        setNew(result.newStores)
                    }
                    else (binding.homeNewSuperRecyclerview.adapter as NewAdapter).changeItem(result.newStores)
                    binding.homeNewSuper.visibility = View.VISIBLE
                } else {
                    binding.homeNewSuper.visibility = View.GONE
                }
                if (result.onSaleStores != null && result.onSaleStores.size > 0) {
                    if (binding.homeDiscountSuperRecyclerview.adapter == null){
                        setOnSalse(result.onSaleStores)
                    }
                    else (binding.homeDiscountSuperRecyclerview.adapter as SalseAdapter).changeItem(
                        result.onSaleStores
                    )
                    binding.homeDiscountSuper.visibility = View.VISIBLE
                    binding.homeDiscountLine.visibility = View.VISIBLE
                } else {
                    binding.homeDiscountSuper.visibility = View.GONE
                    binding.homeDiscountLine.visibility = View.GONE
                }
                if (result.recommendStores != null && result.recommendStores.size > 0) {
                    if (binding.homeRecommendRecyclerview.adapter == null) {
                        setRecommend(result.recommendStores)
                    }
                    else (binding.homeRecommendRecyclerview.adapter as RecommendAdapter).changeItem(
                        result.recommendStores
                    )
                    binding.homeRecommendRecyclerview.visibility = View.VISIBLE
                    binding.noSuperParent.itemNoSuperParent.visibility = View.GONE
                } else {
                    binding.homeRecommendRecyclerview.visibility = View.GONE
                    binding.noSuperParent.itemNoSuperParent.visibility = View.VISIBLE
                }
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    binding.homeScrollView.mHeaderParentPosition = binding.homeRecommendSuper.top.toFloat()
                    binding.homeScrollView.mHeaderInitPosition =
                        binding.homeFilterParent.top.toFloat() ?: 0f
                }, 200)
            }

        }
    }

    fun setStickyScroll() {
        // ????????? ?????????
        binding.homeScrollView.mHeaderParentPosition = binding.homeRecommendSuper.top.toFloat()
        binding.homeScrollView.run {
            header = binding.homeFilterParent2
            headerShadow = binding.homeFilterShadow
            position = binding.homeFilterParent
            stickyHorizonScrollView = binding.homeStickyScroll
            originHorizonScrollView = binding.homeFilterParent
        }
    }

    override fun onGetHomeDataFailure(message: String) {

        //showCustomToast("??? ????????? ???????????? ??????")
    }

    override fun onGetCheetahCountSuccess(response: CheetahCountResponse) {
        if (response.code == 1000) {
            val cheetahText = "??? ???????????? ?????? ${response.result.count}???!"
            binding.homeCheetahBannerText.text = cheetahText
            mCheetahNum = response.result.count
        }
    }

    override fun onGetCheetahCountFailure(message: String) {

    }

    // ?????? ????????????
    fun startSuper(storeIdx: Int) {
        val intent = Intent(requireContext(), DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }

    // ??????????????? ?????? ??????
    fun startCategorySuper(option: String) {
        val intent = Intent(requireContext(), CategorySuperActivity::class.java).apply {
            this.putExtra("lat", mLat)
            this.putExtra("lon", mLon)
            this.putExtra("categoryName", option)
            this.putExtra("cheetah", mCheetahNum)
        }
        // Log.d("??????", "lat: $mLat ??????: $mLon")
        startActivity(intent)
    }

    // ???????????? ?????? ????????????
    fun startSalseSuper() {
        val intent = Intent(requireContext(), SuperSearchActivity::class.java).apply {
            this.putExtra("lat", mLat)
            this.putExtra("lon", mLon)
            this.putExtra("version", 1)
        }
        startActivity(intent)
    }

    // ?????? ???????????????! ????????????
    fun startNewSuper() {
        val intent = Intent(requireContext(), SuperSearchActivity::class.java).apply {
            this.putExtra("lat", mLat)
            this.putExtra("lon", mLon)
            this.putExtra("version", 2)
        }
        startActivity(intent)
    }

    // ????????? ?????? ??????
    fun startEventItem(eventIdx: Int) {
        val intent = Intent(requireContext(), EventItemActivity::class.java).apply {
            this.putExtra("eventIdx", eventIdx)
        }
        startActivity(intent)
    }

    // ????????? ????????????
    private fun setEvent(eventList: ArrayList<Events>) {
        val size = eventList.size
        binding.homeEventBannerViewpager.adapter = EventAdapter(eventList, this)
        binding.homeEventBannerViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.homeEventBannerViewpager.setCurrentItem(size * 50, false)

        val str = " / $size"
        binding.homeEventPageNumTotal.text = str
        binding.homeEventBannerViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.homeEventPageNum.text = (position % size + 1).toString()
            }
        })

    }

    // ?????? ???????????? ???????????? ?????? ???????????? ????????? ????????? ??????. ??????
    override fun onPause() {
        super.onPause()
        if(mAddressQuestionFlag){
            binding.homeAddressQuestion.visibility = View.GONE
            mAddressQuestionFlag = false
        }
        if(mAddressQuestionHandlerFlag){
            mAddressQuestionHandler.removeCallbacks(mAddressQuestionRunnable!!)
            mAddressQuestionHandlerFlag = false
        }
    }

    fun setCategory(categoryList: ArrayList<StoreCategories>) {
        binding.homeCategoryRecyclerview.adapter = CategoryAdapter(categoryList, this)
        binding.homeCategoryRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setOnSalse(salseList: ArrayList<OnSaleStores>) {
        binding.homeDiscountSuperRecyclerview.adapter = SalseAdapter(salseList, this)
        binding.homeDiscountSuperRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setNew(newList: ArrayList<NewStores>) {
        binding.homeNewSuperRecyclerview.adapter = NewAdapter(newList, this)
        binding.homeNewSuperRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setRecommend(recommendList: ArrayList<RecommendStores>) {
        binding.homeRecommendRecyclerview.adapter = RecommendAdapter(recommendList, this)
        binding.homeRecommendRecyclerview.layoutManager = LinearLayoutManager(requireContext())

    }

    fun priceIntToString(value: Int): String {
        val target = value.toString()
        val size = target.length
        return if (size > 3) {
            val last = target.substring(size - 3 until size)
            val first = target.substring(0..(size - 4))
            "$first,$last"
        } else target
    }
}