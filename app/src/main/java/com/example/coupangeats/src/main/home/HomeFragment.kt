package com.example.coupangeats.src.main.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentHomeBinding
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.home.adapter.*
import com.example.coupangeats.src.main.home.model.HomeInfo.*
import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponse
import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponseResult
import com.example.coupangeats.util.GpsControl
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home), HomeFragmentView {
    private var mLoginCheck = false
    private lateinit var mGpsControl : GpsControl
    private var mUserAddress : UserCheckResponseResult? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mGpsControl = GpsControl(requireContext())

        showLoadingDialog(requireContext())
        HomeService(this).tryGetUserCheckAddress(getUserIdx())
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
                gpsCheck = true
            } else {
                mUserAddress = UserCheckResponseResult(0, (37.5724714912).toString(), (126.9911925560).toString(),"종로구 종로1.2.3.4가동 164-7")
                binding.homeGpsAddress.text = mUserAddress!!.mainAddress
            }
        } else {
            mUserAddress = UserCheckResponseResult(0, (37.5724714912).toString(), (126.9911925560).toString(),"종로구 종로1.2.3.4가동 164-7")
            binding.homeGpsAddress.text = mUserAddress!!.mainAddress
        }
        if(gpsCheck){
            getMainData()
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
                getMainData()
            } else {
                // 유저가 선택한 주소 없음
                gpsCheck()
            }
        }
    }

    // event 숫자 바꾸기
    fun changeEventNum(text: String) {
        binding.homeEventPageNum.text = text
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
                Log.d("new", "있음  ${new}")
                binding.homeNewSuper.visibility = View.VISIBLE
            } else {
                binding.homeNewSuper.visibility = View.GONE
            }
        }
    }

    override fun onGetHomeDataFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("홈 데이터 불러오기 실패")
    }

    // 어댑터 설정하기
    fun setEvent(eventList: ArrayList<Events>){
        binding.homeEventBannerViewpager.adapter = EventAdapter(eventList, this)
        binding.homeEventBannerViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    fun setCategory(categoryList: ArrayList<StoreCategories>){
        binding.homeCategoryRecyclerview.adapter = CategoryAdapter(categoryList)
        binding.homeCategoryRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setOnSalse(salseList: ArrayList<OnSaleStores>){
        binding.homeDiscountSuperRecyclerview.adapter = SalseAdapter(salseList)
        binding.homeDiscountSuperRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setNew(newList: ArrayList<NewStores>) {
        binding.homeNewSuperRecyclerview.adapter = NewAdapter(newList)
        binding.homeNewSuperRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }
}