package com.example.coupangeats.src.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
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
import com.example.coupangeats.src.superSearch.SuperSearchActivity
import com.example.coupangeats.util.CartMenuDatabase
import com.example.coupangeats.util.FilterRecommendBottomSheetDialog
import com.example.coupangeats.util.FilterSuperBottomSheetDialog
import com.example.coupangeats.util.GpsControl
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class HomeFragment() :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home), HomeFragmentView {
    private var mAddress = ""
    var mScrollFlag = false
    var mScrollStart = false
    var mScrollValue = 0
    private var countDownTimer: CountDownTimer? = null
    private lateinit var mGpsControl: GpsControl
    private var mUserAddress: UserCheckResponseResult? = null
    private var mLat = ""
    private var mLon = ""
    private var version = 1  // 버전 1이면 그냥 홈 데이터 버전 2면 주변 맛집 필터
    var filterSelected = Array(5) { i -> false }  // 필터를 선택했는지 안했는데
    private var whiteColor = "#FFFFFF"
    private var blackColor = "#000000"
    private var mHomeInfoRequest: HomeInfoRequest = HomeInfoRequest()
    private var mRecommSelect = 1
    private lateinit var mDBHelper: CartMenuDatabase
    private lateinit var mDB: SQLiteDatabase
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mGpsControl = GpsControl(requireContext())

        // 데이터베이스 셋팅
        mDBHelper = CartMenuDatabase(requireContext(), "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        // no address recyclerView adapter setting
        binding.homeNoAddressRecyclerview.adapter = BaseAddressAdapter(this)
        binding.homeNoAddressRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        // 검색 기능
        binding.homeSearch.setOnClickListener {
            (activity as MainActivity).setSearchAdvencedFragment()
        }

        // 이벤트 전체 보러가기
        binding.homeEventLook.setOnClickListener {
            startActivity( Intent(requireContext(), EventActivity::class.java))
        }

        // 주소지 클릭
        binding.homeGpsAddress.setOnClickListener {
            if (!loginCheck()) {
                (activity as MainActivity).loginBottomSheetDialogShow()
            } else {
                // 로그인이 되어 있는 경우 배달지 주소 설정으로 넘어감
                (activity as MainActivity).startDeliveryAddressSettingActivityResult()
            }
        }
        // 배달비
        binding.homeFilterDeliveryPrice.setOnClickListener {
            val filterSuperBottomSheetDialog = FilterSuperBottomSheetDialog(this, 1)
            filterSuperBottomSheetDialog.show(requireFragmentManager(), "deliveryPrice")
        }
        // 최소주문
        binding.homeFilterMiniOrder.setOnClickListener {
            val filterSuperBottomSheetDialog = FilterSuperBottomSheetDialog(this, 2)
            filterSuperBottomSheetDialog.show(requireFragmentManager(), "deliveryPrice")
        }
        // 추천순
        binding.homeFilterRecommend.setOnClickListener {
            val filterRecommendBottomSheetDialog =
                FilterRecommendBottomSheetDialog(this, mRecommSelect)
            filterRecommendBottomSheetDialog.show(requireFragmentManager(), "recommend")
        }
        // 치타 배달
        binding.homeFilterCheetah.setOnClickListener {
            if (!filterSelected[1]) {
                // 선택
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText.setTextColor(Color.parseColor(whiteColor))
                mHomeInfoRequest.cheetah = "Y"
                filterSelected[1] = true
            } else {
                // 취소
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText.setTextColor(Color.parseColor(blackColor))
                mHomeInfoRequest.cheetah = null
                filterSelected[1] = false
            }
            // 서버와 통신
            startFilterServerSend()
        }
        // 할인쿠폰
        binding.homeFilterCoupon.setOnClickListener {
            if (!filterSelected[4]) {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText.setTextColor(Color.parseColor(whiteColor))
                // 선택
                mHomeInfoRequest.coupon = "Y"
                filterSelected[4] = true
            } else {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText.setTextColor(Color.parseColor(blackColor))
                // 선택 취소
                mHomeInfoRequest.coupon = null
                filterSelected[4] = false
            }
            // 서버와 통신해야 함
            startFilterServerSend()
        }
        // 초기화 누름
        binding.homeFilterClear.setOnClickListener {
            // 초기화 시키기
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
            HomeService(this).tryGetHomeData(mHomeInfoRequest)
            refreshFilter()
        }
        // 할인중인 맛집 보러가기
        binding.homeDiscountSuperLook.setOnClickListener {
            startSalseSuper()
        }
        // 새로 들어왔어요! 보러가기
        binding.homeNewSuperLook.setOnClickListener {
            startNewSuper()
        }

        // 카트보기
        binding.homeCartBag.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        // 스크롤 감지
        binding.homeScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            //Log.d("scrolled", "( $scrollX, $scrollY)  ,  ( $oldScrollX , $oldScrollY )")
            lastPosUpdate(scrollY)
        }
        binding.homeRecommendRecyclerview.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP){
                if(mScrollStart){
                    mScrollStart  = false
                    mScrollFlag = false
                } else {
                    mScrollFlag = false
                }
            } else if(event.action == MotionEvent.ACTION_DOWN){
                // 누름
                mScrollFlag = true
                mScrollValue = -1
            }
            false
        }
        binding.homeScrollView.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP){
                if(mScrollStart){
                    mScrollStart  = false
                    mScrollFlag = false
                } else {
                    mScrollFlag = false
                }
            } else if(event.action == MotionEvent.ACTION_DOWN){
                // 누름
                mScrollFlag = true
                mScrollValue = -1
            }
            false
        }
        // 치타배달 내리기
        binding.homeCheetahBannerCancel.setOnClickListener {
            //scrollStart()
            //binding.homeCheetahBannerParent.visibility = View.GONE
        }

    }
    fun scrollStart(){
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 300
        transition.addTarget(binding.homeCheetahBannerParent)
        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.homeCheetahBannerParent.visibility = View.GONE

        TransitionManager.beginDelayedTransition(binding.root, transition)

    }
    fun scrollFinish(){
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 300
        transition.addTarget(binding.homeCheetahBannerParent)
        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.homeCheetahBannerParent.visibility = View.VISIBLE

        TransitionManager.beginDelayedTransition(binding.root, transition)
    }
    // setonTouch
    private fun lastPosUpdate(scrollPos: Int){
        //Log.d("scrolled", "lastPosUpdate : falg = ${mScrollFlag} , start = ${mScrollStart} , scrollPos = ${scrollPos}")
        if(mScrollFlag && !mScrollStart){
            if(mScrollValue != -1 && mScrollValue != scrollPos){
                // 스크롤 시작
                scrollStart()
                mScrollStart = true
            } else if(mScrollValue == -1){
                mScrollValue = scrollPos
            }
        }
        if(countDownTimer != null){
            countDownTimer!!.cancel()
        }

        countDownTimer = object: CountDownTimer(100, 100) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if(!mScrollStart){ scrollFinish()}
            }
        }
        countDownTimer!!.start()
    }

    // 카트 보는거 체인지
    fun cartChange() {
        // 카트 담긴거 있는지 확인
        val num = mDBHelper.checkMenuNum(mDB)
        if(num > 0){
            // 카트가 있음
            binding.homeCartBag.visibility = View.VISIBLE
            binding.homeCartNum.text = num.toString()
            // 전체 가격
            val totalPrice = mDBHelper.menuTotalPrice(mDB)
            val totalPricetext = "${priceIntToString(totalPrice)}원"
            binding.homeCartPrice.text = totalPricetext
            binding.homeCheetahBannerParent.visibility = View.GONE
        } else {
            binding.homeCartBag.visibility = View.GONE
            binding.homeCheetahBannerParent.visibility = View.VISIBLE
        }
    }

    private fun toggle() {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(binding.homeNoticeText)
        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.homeNoticeText.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            TransitionManager.beginDelayedTransition(binding.root, transition)
            binding.homeNoticeText.visibility = View.GONE
        }, 1500)
    }

    // 필터 활용해서 서버한테 보내기
    fun startFilterServerSend() {
        HomeService(this).tryGetHomeData(mHomeInfoRequest)
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
            mHomeInfoRequest.sort = sendServerString
        } else {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down_white)
            filterSelected[0] = true
            mHomeInfoRequest.sort = sendServerString
        }
        mHomeInfoRequest.sort = sendServerString
        startFilterServerSend()
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
            mHomeInfoRequest.deliverymin = value
            filterSelected[2] = true
        } else {
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterDeliveryPriceText.text = "배달비"
            binding.homeFilterDeliveryPriceText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
            mHomeInfoRequest.deliverymin = null
            filterSelected[2] = false
        }

        startFilterServerSend()
    }

    // 최소 주문 바꾸는 함수 다이얼로그에서 호출
    fun changeOrderMinFilter(value: Int, valueString: String) {
        if (value != -1) {
            val str = "최소주문 ${valueString}원 이하"
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterMiniOrderText.text = str
            binding.homeFilterMiniOrderText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down_white)
            // 최소주문 바꾸기
            mHomeInfoRequest.ordermin = value
            filterSelected[3] = true
        } else {
            // 전체
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterMiniOrderText.text = "최소주문"
            binding.homeFilterMiniOrderText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
            mHomeInfoRequest.ordermin = null
            filterSelected[3] = false
        }
        startFilterServerSend()
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

    override fun onResume() {
        super.onResume()
        cartChange()
        HomeService(this).tryGetUserCheckAddress(getUserIdx())
    }

    fun getUserIdx(): Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun changeGpsInfo() {
        // 현재 주소에 변화가 생김
        binding.homeGpsAddress.text =
            ApplicationClass.sSharedPreferences.getString("userMainAddressIdx", "주소 실패..")
    }

    private fun loginCheck(): Boolean {
        return ApplicationClass.sSharedPreferences.getInt("userIdx", -1) != -1
    }

    fun baseAddressResult(baseAddress: String) {
        binding.homeNoAddress.visibility = View.GONE
        binding.homeRealContent.visibility = View.VISIBLE
    }

    fun gpsCheck() {
        var gpsCheck = false
        if (ApplicationClass.sSharedPreferences.getBoolean("gps", false)) {
            // gps 사용 가능
            val location = mGpsControl.getLocation()
            if (location != null) {
                mUserAddress = UserCheckResponseResult(
                    0,
                    location.latitude.toString(),
                    location.longitude.toString(),
                    "종로구 종로1.2.3.4가동 164-7"
                )
                val address = mGpsControl.getCurrentAddress(location.latitude, location.longitude)
                binding.homeGpsAddress.text = address
                mLat = location.latitude.toString()
                mLon = location.longitude.toString()
                gpsCheck = true
            } else {
                mUserAddress = UserCheckResponseResult(
                    0,
                    (37.5724714912).toString(),
                    (126.9911925560).toString(),
                    "종로구 종로1.2.3.4가동 164-7"
                )
                binding.homeGpsAddress.text = mUserAddress!!.mainAddress
                mLat = (37.5724714912).toString()
                mLon = (126.9911925560).toString()
            }
            getMainData()
        } else {
            mUserAddress = UserCheckResponseResult(
                0,
                (37.5724714912).toString(),
                (126.9911925560).toString(),
                "종로구 종로1.2.3.4가동 164-7"
            )
            binding.homeGpsAddress.text = mUserAddress!!.mainAddress
            mLat = (37.5724714912).toString()
            mLon = (126.9911925560).toString()
            getMainData()
        }
        if (gpsCheck) {
            binding.homeNoAddress.visibility = View.GONE
            binding.homeRealContent.visibility = View.VISIBLE
        } else {
            binding.homeNoAddress.visibility = View.VISIBLE
            binding.homeRealContent.visibility = View.GONE
        }
    }

    fun getMainData() {
        // 홈 데이터 얻음
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
        HomeService(this).tryGetHomeData(re)
    }

    override fun onUserCheckAddressSuccess(response: UserCheckResponse) {
        if (response.code == 1000) {
            if (response.result.addressIdx != 0) {
                // 유저가 선택한 주소 있음
                mUserAddress = response.result
                binding.homeGpsAddress.text = mUserAddress!!.mainAddress
                mLat = mUserAddress!!.latitude
                mLon = mUserAddress!!.longitude
                if(mAddress != "" && mAddress != mUserAddress!!.mainAddress){
                    toggle()
                    mAddress = mUserAddress!!.mainAddress
                }
                // 치타배달
                HomeService(this).tryGetCheetahCount(mLat, mLon)
                getMainData()  // 홈 데이터
            } else {
                // 유저가 선택한 주소 없음
                gpsCheck()
            }
        } else {
            gpsCheck()
        }
    }

    override fun onUserCheckAddressFailure(message: String) {
        // showCustomToast("유저 선택 주소 실패")
        val img = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMTA1MjdfNTUg%2FMDAxNjIyMTEzMDg3Njc5.J0L7A04dtBVEKOcBVbdbKmJFgHq12BTAAq3fDHFlQoIg.0vN8BoEqOEQjqhU3i-Q7s6MFWbrQ4ElJiJfGWWxoeBQg.JPEG.hs_1472%2Foutput_2445714095.jpg&type=sc960_832"
        val categoryList = ArrayList<StoreCategories>()
        categoryList.add(StoreCategories("햄버거", img))
        categoryList.add(StoreCategories("햄버거", img))
        categoryList.add(StoreCategories("돈까스", img))
        categoryList.add(StoreCategories("정식", img))
        categoryList.add(StoreCategories("불고기", img))
        categoryList.add(StoreCategories("분식", img))
        val salseList = ArrayList<OnSaleStores>()
        salseList.add(OnSaleStores(1, img, "분식점", null, "1.2km", "2,000원"))
        salseList.add(OnSaleStores(2, img, "분식점", null, "1.2km", "7,000원"))
        salseList.add(OnSaleStores(3, img, "분식점", null, "1.2km", "1,000원"))
        salseList.add(OnSaleStores(4, img, "분식점", null, "1.0km", "5,000원"))
        salseList.add(OnSaleStores(5, img, "분식점", null, "1.5km", "3,000원"))
        val recommendList = ArrayList<RecommendStores>()
        recommendList.add(RecommendStores(1, arrayListOf(img), "분식점", "Y","4.5", "1.7km", "2,000원", "10~20분", null))
        recommendList.add(RecommendStores(2, arrayListOf(img), "분식점", "Y","3.5", "1.5km", "2,000원", "10~20분", null))
        recommendList.add(RecommendStores(3, arrayListOf(img), "분식점", "N","1.5", "1km", "2,000원", "10~20분", null))
        recommendList.add(RecommendStores(4, arrayListOf(img), "분식점", "Y","2.5", "1km", "2,000원", "10~20분", null))

        val category = categoryList
        val salse = salseList
        val recommend = recommendList

        setCategory(category)
        setOnSalse(salse)
        binding.homeDiscountSuper.visibility = View.VISIBLE

        setRecommend(recommend)
        binding.homeRecommendRecyclerview.visibility = View.VISIBLE

        val eventList = ArrayList<Events>()
        eventList.add(Events(1, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/images%2Fznvkdzero.JPG?alt=media&token=9b8d4dc2-7d1a-492e-b114-c41ed1f12d53"))
        eventList.add(Events(2,"https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/images%2Fenfpwnfm.JPG?alt=media&token=1d513824-440b-4260-ac1e-5832660988e4"))
        eventList.add(Events(3, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/images%2Fdsaaddsa.JPG?alt=media&token=9a2769bb-285e-47d8-974e-00c5dcd0f726"))

        setEvent(eventList)
    }

    override fun onGetHomeDataSuccess(response: HomeInfoResponse) {
        if (response.code == 1000) {
            if (version == 1) {
                val events = response.result.events
                val category = response.result.storeCategories
                val salse = response.result.onSaleStores
                val new = response.result.newStores
                val recommend = response.result.recommendStores
                setEvent(events)
                setCategory(category)
                if (salse != null) {
                    setOnSalse(salse)
                    binding.homeDiscountSuper.visibility = View.VISIBLE
                } else {
                    binding.homeDiscountSuper.visibility = View.GONE
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
                } else {
                    binding.homeRecommendRecyclerview.visibility = View.GONE
                }
            } else {
                // 주변 매장 맛집 필터
                val recommend = response.result.recommendStores
                if (recommend != null) {
                    setRecommend(recommend)
                    binding.homeRecommendRecyclerview.visibility = View.VISIBLE
                } else {
                    binding.homeRecommendRecyclerview.visibility = View.GONE
                }
            }

        }
    }

    override fun onGetHomeDataFailure(message: String) {

        //showCustomToast("홈 데이터 불러오기 실패")
    }

    override fun onGetCheetahCountSuccess(response: CheetahCountResponse) {
        if(response.code == 1000){
            val cheetahText = "내 도착하는 맛집 ${response.result.count}개!"
            binding.homeCheetahBannerText.text = cheetahText
        }
    }

    override fun onGetCheetahCountFailure(message: String) {

    }

    // 매장 조회하기
    fun startSuper(storeIdx: Int) {
        val intent = Intent(requireContext(), DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }

    // 카테고리별 가게 조회
    fun startCategorySuper(option: String) {
        val intent = Intent(requireContext(), CategorySuperActivity::class.java).apply {
            this.putExtra("lat", mLat)
            this.putExtra("lon", mLon)
            this.putExtra("categoryName", option)
        }
        startActivity(intent)
    }

    // 할인중인 맛집 보러가기
    fun startSalseSuper() {
        val intent = Intent(requireContext(), SuperSearchActivity::class.java).apply {
            this.putExtra("lat", mLat)
            this.putExtra("lon", mLon)
            this.putExtra("version", 1)
        }
        startActivity(intent)
    }

    // 새로 들어왔어요! 보러가기
    fun startNewSuper() {
        val intent = Intent(requireContext(), SuperSearchActivity::class.java).apply {
            this.putExtra("lat", mLat)
            this.putExtra("lon", mLon)
            this.putExtra("version", 2)
        }
        startActivity(intent)
    }

    // 이벤트 상세 보기
    fun startEventItem(eventIdx: Int){
        val intent = Intent(requireContext(), EventItemActivity::class.java).apply {
            this.putExtra("eventIdx", eventIdx)
        }
        startActivity(intent)
    }

    // 어댑터 설정하기
    fun setEvent(eventList: ArrayList<Events>) {
        binding.homeEventBannerViewpager.adapter = EventAdapter(eventList, this)
        binding.homeEventBannerViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val str = " / ${eventList.size}"
        binding.homeEventPageNumTotal.text = str
        binding.homeEventBannerViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.homeEventPageNum.text = (position + 1).toString()
            }
        })
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

    fun setRecommend(recommendList: ArrayList<RecommendStores>) {
        binding.homeRecommendRecyclerview.adapter = RecommendAdapter(recommendList, this)
        binding.homeRecommendRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    fun priceIntToString(value: Int) : String {
        val target = value.toString()
        val size = target.length
        return if(size > 3){
            val last = target.substring(size - 3 until size)
            val first = target.substring(0..(size - 4))
            "$first,$last"
        } else target
    }
}