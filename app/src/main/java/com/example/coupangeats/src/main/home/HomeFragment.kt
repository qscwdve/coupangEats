package com.example.coupangeats.src.main.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.databinding.DialogFilterSuperBinding
import com.example.coupangeats.databinding.FragmentHomeBinding
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.home.adapter.*
import com.example.coupangeats.src.main.home.model.HomeInfo.*
import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponse
import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponseResult
import com.example.coupangeats.src.superSearch.SuperSearchActivity
import com.example.coupangeats.util.FilterRecommendBottomSheetDialog
import com.example.coupangeats.util.FilterSuperBottomSheetDialog
import com.example.coupangeats.util.GpsControl
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home), HomeFragmentView {
    private var mLoginCheck = false
    private lateinit var mGpsControl : GpsControl
    private var mUserAddress : UserCheckResponseResult? = null
    private var mLat = ""
    private var mLon = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mGpsControl = GpsControl(requireContext())

        // no address recyclerView adapter setting
        binding.homeNoAddressRecyclerview.adapter = BaseAddressAdapter(this)
        binding.homeNoAddressRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        //val dividerItemDecoration = DividerItemDecoration(binding.homeCategoryRecyclerview.context, LinearLayoutManager(requireContext()).orientation);
        //binding.homeNoAddressRecyclerview.addItemDecoration(dividerItemDecoration);

        // 검색 기능
        binding.homeSearch.setOnClickListener {
            (activity as MainActivity).setSearchAdvencedFragment()
        }
        // 주소지 클릭
        binding.homeGpsAddress.setOnClickListener {
            if(!loginCheck()) {
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
            val filterRecommendBottomSheetDialog = FilterRecommendBottomSheetDialog(this, 1)
            filterRecommendBottomSheetDialog.show(requireFragmentManager(), "recommend")
        }
        // 할인쿠폰
        binding.homeFilterCoupon.setOnClickListener {
            binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
        }
        // 할인중인 맛집 보러가기
        binding.homeDiscountSuperLook.setOnClickListener {
            startSalseSuper()
        }
        // 새로 들어왔어요! 보러가기
        binding.homeNewSuperLook.setOnClickListener {
            startNewSuper()
        }
    }

    override fun onResume() {
        super.onResume()
        showLoadingDialog(requireContext())
        HomeService(this).tryGetUserCheckAddress(getUserIdx())
    }
    fun getUserIdx(): Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun changeGpsInfo(){
        // 현재 주소에 변화가 생김
        binding.homeGpsAddress.text = ApplicationClass.sSharedPreferences.getString("userMainAddressIdx", "주소 실패..")
        // 팝업창 필요..!
    }

    private fun loginCheck() : Boolean {
        return ApplicationClass.sSharedPreferences.getInt("userIdx", -1) != -1
    }

    fun baseAddressResult(baseAddress : String) {
        binding.homeNoAddress.visibility = View.GONE
        binding.homeRealContent.visibility = View.VISIBLE
    }

    fun gpsCheck() {
        var gpsCheck = false
        if(ApplicationClass.sSharedPreferences.getBoolean("gps", false)){
            // gps 사용 가능
            val location = mGpsControl.getLocation()
            if(location != null){
                mUserAddress = UserCheckResponseResult(0, location.latitude.toString(), location.longitude.toString(), "종로구 종로1.2.3.4가동 164-7")
                val address = mGpsControl.getCurrentAddress(location.latitude, location.longitude)
                binding.homeGpsAddress.text = address
                mLat = location.latitude.toString()
                mLon = location.longitude.toString()
                gpsCheck = true
            } else {
                mUserAddress = UserCheckResponseResult(0, (37.5724714912).toString(), (126.9911925560).toString(),"종로구 종로1.2.3.4가동 164-7")
                binding.homeGpsAddress.text = mUserAddress!!.mainAddress
                mLat = (37.5724714912).toString()
                mLon = (126.9911925560).toString()
            }
            getMainData()
        } else {
            mUserAddress = UserCheckResponseResult(0, (37.5724714912).toString(), (126.9911925560).toString(),"종로구 종로1.2.3.4가동 164-7")
            binding.homeGpsAddress.text = mUserAddress!!.mainAddress
            mLat = (37.5724714912).toString()
            mLon = (126.9911925560).toString()
            getMainData()
        }
        if(gpsCheck){
            binding.homeNoAddress.visibility = View.GONE
            binding.homeRealContent.visibility = View.VISIBLE
        } else {
            binding.homeNoAddress.visibility = View.VISIBLE
            binding.homeRealContent.visibility = View.GONE
        }
    }

    fun getMainData(){
        // 홈 데이터 얻음
        val re = HomeInfoRequest(mUserAddress!!.latitude, mUserAddress!!.longitude, "recomm", null, null, null, null, 1, 10)
        showLoadingDialog(requireContext())
        HomeService(this).tryGetHomeData(re)
    }

    override fun onUserCheckAddressSuccess(response: UserCheckResponse) {
        dismissLoadingDialog()
        if(response.code == 1000){
            if(response.result.addressIdx != 0){
                // 유저가 선택한 주소 있음
                mUserAddress = response.result
                binding.homeGpsAddress.text = mUserAddress!!.mainAddress
                mLat = mUserAddress!!.latitude
                mLon = mUserAddress!!.longitude
                getMainData()
            } else {
                // 유저가 선택한 주소 없음
                gpsCheck()
            }
        } else {
            gpsCheck()
        }
    }

    override fun onUserCheckAddressFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("유저 선택 주소 실패")
    }

    override fun onGetHomeDataSuccess(response: HomeInfoResponse) {
        dismissLoadingDialog()
        if(response.code == 1000){
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
            if(new != null){
                setNew(new)
                binding.homeNewSuper.visibility = View.VISIBLE
            } else {
                binding.homeNewSuper.visibility = View.GONE
            }
            if(recommend != null){
                setRecommend(recommend)
            }
        }
    }

    override fun onGetHomeDataFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("홈 데이터 불러오기 실패")
    }
    
    // 매장 조회하기
    fun startSuper(storeIdx: Int){
        val intent = Intent(requireContext(), DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
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
    // 어댑터 설정하기
    fun setEvent(eventList: ArrayList<Events>){
        binding.homeEventBannerViewpager.adapter = EventAdapter(eventList, this)
        binding.homeEventBannerViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val str = " / ${eventList.size}"
        binding.homeEventPageNumTotal.text = str
        binding.homeEventBannerViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.homeEventPageNum.text = (position + 1).toString()
            }
        })
    }

    fun setCategory(categoryList: ArrayList<StoreCategories>){
        binding.homeCategoryRecyclerview.adapter = CategoryAdapter(categoryList)
        binding.homeCategoryRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setOnSalse(salseList: ArrayList<OnSaleStores>){
        binding.homeDiscountSuperRecyclerview.adapter = SalseAdapter(salseList, this)
        binding.homeDiscountSuperRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setNew(newList: ArrayList<NewStores>) {
        binding.homeNewSuperRecyclerview.adapter = NewAdapter(newList, this)
        binding.homeNewSuperRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setRecommend(recommendList: ArrayList<RecommendStores>) {
        binding.homeRecommendRecyclerview.adapter = RecommendAdapter(recommendList, this)
        binding.homeRecommendRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }
}