package com.example.coupangeats.src.deliveryAddressSetting

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.location.GpsSatellite
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityDeliveryAddressSettingBinding
import com.example.coupangeats.src.deliveryAddressSetting.adapter.AddressListAdapter
import com.example.coupangeats.src.deliveryAddressSetting.adapter.SearchAddressListAdapter
import com.example.coupangeats.src.deliveryAddressSetting.adapter.data.SearchAddress
import com.example.coupangeats.src.deliveryAddressSetting.detail.DetailAddressFragment
import com.example.coupangeats.src.deliveryAddressSetting.model.*
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.DeliveryAddressResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.SearchAddrListRequest
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.SearchAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchXY.SearchXYRequest
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchXY.SearchXYResult
import com.example.coupangeats.src.map.MapActivity
import com.example.coupangeats.util.GpsControl
import com.naver.maps.map.NaverMap
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class DeliveryAddressSettingActivity() :
    BaseActivity<ActivityDeliveryAddressSettingBinding>(ActivityDeliveryAddressSettingBinding::inflate),
    DeliveryAddressSettingActivityView {
    private var userMainAddress = ""
    private var userAddressIdx = -1
    private var mHomeMainAddress = ""
    private var mHomeaddressIdx = -1
    private var mCompanyMainAddress = ""
    private var mCompanyaddressIdx = -1
    private var isHome = false
    private var isCompany = false
    private var mBackOrFinish = false
    private var mSearchTip = true
    private var mDetailAddress = false
    private var mCategory = -1
    private lateinit var imm: InputMethodManager   // 키보드 숨기기
    val GPS_SELECT = 1
    var version = GPS_SELECT
    var mSelectedAddress = -1
    var mKeyboardStatus = false
    lateinit var mapActivityLauncher : ActivityResultLauncher<Intent>
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        showLoadingDialog(this)
        DeliveryAddressSettingService(this).tryGetUserAddressList(getUserIdx())

        // 키보드 상태 확인
        setKeyboardListener()

        mapActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if(result.resultCode == RESULT_OK){
                    val data = result.data
                    // 배달지 주소 추가
                    if(data != null){
                        val mainAddress = data.getStringExtra("mainAddress") ?: ""
                        val roadAddress = data.getStringExtra("roadAddress") ?: ""
                        val lat = data.getStringExtra("lat") ?: ""
                        val lon = data.getStringExtra("lon") ?: ""
                        changeDetailAddress(SearchAddress(mainAddress, roadAddress), lat, lon)
                    }
                }
            }

        // 배달주소 관리인지 gps 선택인지
        version = intent.getIntExtra("version", GPS_SELECT)

        if(version == GPS_SELECT){
            // 홈화면에서 바로 주소 선택
            binding.deliveryAddressManageParent.visibility = View.GONE
            binding.deliveryAddressTextParent.visibility = View.VISIBLE
            binding.deliveryAddressSettingNowGpsFind.visibility = View.VISIBLE
            binding.detailAddressTitle.text = "배달지 주소 설정"
        } else {
            // 배달주소 관리
            binding.deliveryAddressManageParent.visibility = View.VISIBLE
            binding.deliveryAddressTextParent.visibility = View.GONE
            binding.deliveryAddressSettingNowGpsFind.visibility = View.GONE
            binding.detailAddressTitle.text = "배달 주소 관리"
        }

        //새 배달주소 추가 - DELIVERY_MANAGE
        binding.deliveryAddressManageText.setOnClickListener {
            binding.detailAddressUserList.visibility = View.INVISIBLE
            binding.deliveryAddressManageParent.visibility = View.GONE
            binding.deliveryAddressTextParent.visibility = View.VISIBLE
            binding.deliveryAddressSettingNowGpsFind.visibility = View.VISIBLE
            binding.deliveryAddressBack.setImageResource(R.drawable.ic_left_arrow_black)
            binding.detailAddressTitle.text = "배달지 주소 설정"
        }

        // edittext 설정
        binding.deliveryAddressText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // 클릭됨
                if (mSearchTip) {
                    // 검색팁 보여줌
                    binding.deliveryAddressSettingSelectedParent.visibility = View.GONE
                    binding.deliveryAddressSettingSearchParent.visibility = View.VISIBLE
                    binding.deliveryAddressSettingSearchList.visibility = View.GONE
                    binding.deliveryAddressSettingSearchTip.visibility = View.VISIBLE
                    mSearchTip = false
                }
                if (!mBackOrFinish) {
                    mBackOrFinish = true
                    binding.deliveryAddressBack.setImageResource(R.drawable.ic_left_arrow_black)
                }
            }
        }
        binding.deliveryAddressText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.deliveryAddressText.text.toString()
                        .isEmpty()
                ) binding.deliveryAddressTextCancel.visibility = View.INVISIBLE
                else binding.deliveryAddressTextCancel.visibility = View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        // 키보드 상에서 완료 버튼 누름
        binding.deliveryAddressText.setOnEditorActionListener { v, actionId, event ->
            //showCustomToast("엔터키 눌림")
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            val keyword = binding.deliveryAddressText.text.toString()
            // 검색 API 서버 호출
            binding.deliveryAddressSettingSearchTip.visibility = View.GONE
            binding.deliveryAddressSettingSearchList.visibility = View.VISIBLE
            //showLoadingDialog(this)
            val searchAddrListRequest = SearchAddrListRequest(1, keyword)
            DeliveryAddressSettingService(this).tryGetSearchAddrList(searchAddrListRequest)
            false
        }

        // editText 전체 취소 리스너
        binding.deliveryAddressTextCancel.setOnClickListener {
            binding.deliveryAddressText.setText("")
            binding.deliveryAddressTextCancel.visibility = View.INVISIBLE
        }

        binding.deliveryAddressBack.setOnClickListener {
            // 키보드 올라가 있으면 내리기
            if(mKeyboardStatus) {
                imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
                mKeyboardStatus = false
            }
            mSearchTip = true
            if (!mBackOrFinish) {
                // 종료
                // Log.d("selected", "종료")
                if(binding.detailAddressTitle.text == "배달지 상세 정보" || (version != GPS_SELECT && (binding.deliveryAddressManageParent.visibility != View.VISIBLE || binding.deliveryAddressSettingNowGpsFind.visibility == View.VISIBLE))){
                    Log.d("selected", "종료안되야함")
                    binding.deliveryAddressManageParent.visibility = View.VISIBLE
                    binding.deliveryAddressTextParent.visibility = View.GONE
                    binding.deliveryAddressDetailParent.visibility = View.GONE
                    binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
                    binding.deliveryAddressSettingNowGpsFind.visibility = View.GONE
                    binding.detailAddressUserList.visibility = View.VISIBLE
                    binding.deliveryAddressBack.setImageResource(R.drawable.ic_cancel)
                    binding.detailAddressTitle.text = "배달 주소 관리"
                } else {
                    // Log.d("selected", "종료")
                    setResult(RESULT_CANCELED)
                    finish()
                }
            } else if(mDetailAddress){
                // Log.d("selected", "mDetailAddress")
                binding.deliveryAddressDetailParent.visibility = View.GONE
                binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
                binding.detailAddressTitle.text = "배달지 주소 설정"
                mDetailAddress = false
            } else {
                // 뒤로 가기
                // Log.d("selected", "뒤로가기")
                if(version == GPS_SELECT) binding.deliveryAddressBack.setImageResource(R.drawable.ic_cancel)
                binding.deliveryAddressSettingSelectedParent.visibility = View.VISIBLE
                binding.deliveryAddressSettingSearchParent.visibility = View.GONE
                binding.deliveryAddressText.setText("")
                binding.deliveryAddressText.clearFocus()
                binding.deliveryAddressTextCancel.visibility = View.INVISIBLE
                mBackOrFinish = false
            }
        }
        binding.deliveryAddressSettingHomeParent.setOnClickListener {
            if (isHome && version == 1) {
                finishActivitySelectedData(mHomeMainAddress, mHomeaddressIdx)
            } else if(version != 1 && isHome){
                // 배달지 수정하러 감
                startDeliveryAddressModify(mHomeaddressIdx)
            } else {
                // 검색하러 가야함
                mCategory = 1
                // 검색팁 보여줌
                binding.deliveryAddressSettingSelectedParent.visibility = View.GONE
                binding.deliveryAddressSettingSearchParent.visibility = View.VISIBLE
                mSearchTip = false
            }

        }
        binding.deliveryAddressSettingBusinessParent.setOnClickListener {
            if (isCompany && version == 1) {
                finishActivitySelectedData(mCompanyMainAddress, mCompanyaddressIdx)
            } else if(isCompany && version != 1){
                startDeliveryAddressModify(mCompanyaddressIdx)
            } else {
                // 검색하러 가야함
                mCategory = 2
                // 검색팁 보여줌
                binding.deliveryAddressSettingSelectedParent.visibility = View.GONE
                binding.deliveryAddressSettingSearchParent.visibility = View.VISIBLE
                mSearchTip = false
            }
        }

        // 현재 위치 지도 부름
        binding.deliveryAddressSettingNowGps.setOnClickListener {
            // 지도가 있는 액티비티 부름
            val location = gpsCheck()
            val mLat = location?.latitude ?: (-1).toDouble()
            val mLon = location?.longitude ?: (-1).toDouble()
            val intent = Intent(this, MapActivity::class.java).apply {
                // 현재 위치 가져와야 함
                this.putExtra("lat", mLat.toString())
                this.putExtra("lon", mLon.toString())
                this.putExtra("detailAddressVersion", version)
                this.putExtra("nowGPS", true)
            }
            mapActivityLauncher.launch(intent)
        }
    }

    fun getUserIdx(): Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    private fun setUserAddress(mainAddress: String, addressIdx: Int) {
        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putInt("userAddressIdx", addressIdx)
        edit.putString("userMainAddressIdx", mainAddress)
        edit.apply()
    }

    fun finishActivitySelectedData(mainAddress: String, addressIdx: Int) {
        // 서버통신한 다음에 저거 실행해야 함... selected 고치고 수정!! 이거할차례!
        userMainAddress = mainAddress
        userAddressIdx = addressIdx
        startDeliveryAddressAdd()
    }

    fun changeDetailAddress(searchAddress: SearchAddress, lat: String, lon: String){
        // 키보드 올라가 있으면 내리기
        if(mKeyboardStatus) {
            imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
            mKeyboardStatus = false
        }
        var latitude = lat
        var longiute = lon
        binding.deliveryAddressDetailParent.visibility = View.VISIBLE
        binding.deliveryAddressNotDetailParent.visibility = View.GONE
        binding.detailAddressTitle.text = "배달지 상세 정보"

        if(lat == "" || lon == ""){
            val address = GpsControl(this).getLocationToXY(searchAddress.subAddress)
            if(address != null){
                latitude = address.latitude.toString()
                longiute = address.longitude.toString()
            }
            // Log.d("addressSetting", "subAdress : ${searchAddress.subAddress}, main : ${searchAddress.mainAddress}")
        }

        if(latitude != "" && longiute != ""){
            supportFragmentManager.beginTransaction()
                .replace(R.id.delivery_address_detail_parent, DetailAddressFragment().apply {
                    arguments = Bundle().apply {
                        putString("mainAddress", searchAddress.mainAddress)
                        putString("roadAddress", searchAddress.subAddress)
                        putString("lat", latitude)
                        putString("lon", longiute)
                        putInt("category", mCategory)
                    }
                }, )
                .commitAllowingStateLoss()
            mDetailAddress = true
        }


    }

    fun deliveryCheckedAndFinish(){
        binding.deliveryAddressSettingHomeDetail.visibility = View.VISIBLE
        binding.deliveryAddressBusinessDetail.visibility = View.VISIBLE
        showLoadingDialog(this)
        DeliveryAddressSettingService(this).tryPatchPathUserCheckedAddress(getUserIdx(), userAddressIdx)
    }

    fun startDeliveryAdd(request: DeliveryAddressAddRequest){
        // 배달 추가 시작 이것도 나중에 배달 추가를 위해서 구분할 필요가 있음
        userMainAddress = request.address
        if(version == GPS_SELECT){
            showLoadingDialog(this)
            DeliveryAddressSettingService(this).tryPostDeliveryAddressAdd(request)
        } else {
            DeliveryAddressSettingService(this).tryPostDeliveryAddressAdd(request)
            binding.deliveryAddressDetailParent.visibility = View.GONE
            binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
            binding.deliveryAddressSettingNowGpsFind.visibility = View.GONE
            binding.detailAddressUserList.visibility = View.VISIBLE
            binding.deliveryAddressSettingSearchParent.visibility = View.GONE
            binding.deliveryAddressSettingSelectedParent.visibility = View.VISIBLE
            binding.deliveryAddressTextParent.visibility = View.GONE
            binding.deliveryAddressManageParent.visibility = View.VISIBLE
            binding.deliveryAddressBack.setImageResource(R.drawable.ic_cancel)
            binding.detailAddressTitle.text = "배달 주소 관리"
            mDetailAddress = false
            mBackOrFinish = false
        }
        mSearchTip = true
    }

    override fun onGetUserAddressListSuccess(response: UserAddrListResponse) {
        dismissLoadingDialog()
        binding.deliveryAddressSettingHomeChecked.visibility = View.INVISIBLE
        binding.deliveryAddressBusinessChecked.visibility = View.INVISIBLE
        // 어댑터 생성!!
        if(response.code == 1000){
            val home = response.result.home
            val company = response.result.company
            val addressList = response.result.addressList
            if (home != null) {
                binding.deliveryAddressSettingHomeDetail.visibility = View.VISIBLE
                binding.deliveryAddressSettingHomeDetail.text = home.subAddress
                if (response.result.selectedAddressIdx == home.addressIdx) binding.deliveryAddressSettingHomeChecked.visibility =
                    View.VISIBLE
                else binding.deliveryAddressSettingHomeChecked.visibility = View.INVISIBLE
                isHome = true
                if(version != GPS_SELECT){
                    binding.deliveryAddressSettingHomeChecked.visibility = View.INVISIBLE
                }
                mHomeMainAddress = response.result.home.mainAddress
                mHomeaddressIdx = response.result.home.addressIdx

                // 현재 유저의 주소가 집일때 선택 이미지 띄우기
                if(home.addressIdx == response.result.selectedAddressIdx){
                    binding.deliveryAddressSettingHomeChecked.visibility = View.VISIBLE
                }
            } else {
                binding.deliveryAddressSettingHomeDetail.visibility = View.GONE
                binding.deliveryAddressSettingHomeChecked.visibility = View.INVISIBLE
                isHome = false
            }
            if (company != null) {
                isCompany = true
                binding.deliveryAddressBusinessDetail.visibility = View.VISIBLE
                binding.deliveryAddressBusinessImg.visibility = View.VISIBLE
                mCompanyMainAddress = response.result.company.mainAddress
                mCompanyaddressIdx = response.result.company.addressIdx
                binding.deliveryAddressBusinessDetail.text = company.subAddress
                if (response.result.selectedAddressIdx == company.addressIdx) binding.deliveryAddressBusinessChecked.visibility =
                    View.VISIBLE
                else binding.deliveryAddressBusinessChecked.visibility = View.INVISIBLE

                if(version != GPS_SELECT){
                    binding.deliveryAddressBusinessChecked.visibility = View.INVISIBLE
                }
                // 현재 유저의 주소가 집일때 선택 이미지 띄우기
                if(company.addressIdx == response.result.selectedAddressIdx){
                    binding.deliveryAddressBusinessChecked.visibility = View.VISIBLE
                }
            } else {
                isCompany = false
                binding.deliveryAddressBusinessDetail.visibility = View.INVISIBLE
                binding.deliveryAddressBusinessDetail.visibility = View.GONE
            }
            if (addressList != null) {
                binding.deliveryAddressSettingListRecyclerView.adapter = AddressListAdapter(
                    addressList as ArrayList<BaseAddress>,
                    response.result.selectedAddressIdx,
                    this
                )
                binding.deliveryAddressSettingListRecyclerView.layoutManager = LinearLayoutManager(this)
            }
            mSelectedAddress = response.result.selectedAddressIdx

        }
    }

    override fun onGetUserAddressListFailure(message: String) {
        dismissLoadingDialog()
        //ustomToast("통신 오류로 배달지 주소 목록을 불러올 수 없습니다.")
    }

    override fun onPathUserCheckedAddressSuccess(response: UserCheckedAddressResponse) {
        dismissLoadingDialog()
        if (response.code == 1000) {
            val intent = Intent().apply {
                this.putExtra("check", true)
            }
            if (userMainAddress != "" && userAddressIdx != -1) {
                Log.d("selected", "company")
                setUserAddress(userMainAddress, userAddressIdx)
                setResult(RESULT_OK, intent)
                finish()
            }
            else if(mSelectedAddress == -1){
                setUserAddress("배달주소를 선택해주세요", -1)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                setResult(RESULT_CANCELED, intent)
                finish()
            }
        }
    }

    override fun onPathUserCheckedAddressFailure(message: String) {
        dismissLoadingDialog()
       // showCustomToast("서버에 선택 주소 수정 요청이 실패하였습니다")
        setResult(RESULT_CANCELED)
        finish()
    }

    fun backClick(addressIdx: Int?) {
        binding.deliveryAddressManageParent.visibility = View.VISIBLE
        binding.deliveryAddressTextParent.visibility = View.GONE
        binding.deliveryAddressDetailParent.visibility = View.GONE
        binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
        binding.deliveryAddressSettingNowGpsFind.visibility = View.GONE
        binding.detailAddressUserList.visibility = View.VISIBLE
        binding.deliveryAddressBack.setImageResource(R.drawable.ic_cancel)
        binding.detailAddressTitle.text = "배달 주소 관리"
        mSearchTip = true
        binding.deliveryAddressBusinessDetail.visibility = View.VISIBLE
        binding.deliveryAddressSettingHomeDetail.visibility = View.VISIBLE
        if(addressIdx == mSelectedAddress){
            // 선택 1번으로 해야함
            DeliveryAddressSettingService(this).tryPatchPathUserCheckedAddress(getUserIdx(), 1)
            mSelectedAddress = -1
        }
        showLoadingDialog(this)
        DeliveryAddressSettingService(this).tryGetUserAddressList(getUserIdx())
    }

    override fun onGetSearchAddrListSuccess(response: SearchAddrListResponse) {
        //dismissLoadingDialog()
        val common = response.results.common
        val jusoList = response.results.juso
        if (common.errorCode == "0" && jusoList.isNotEmpty()) {
            // 정상
            binding.deliverySearchError.visibility = View.GONE
            binding.deliveryAddressSettingSearchParent.visibility = View.VISIBLE
            if (common.currentPage == 1) {
                // 처음 시작
                val addressList = ArrayList<SearchAddress>()
                for (juso in jusoList) {
                    val mainAddress =
                        if (juso.detBdNmList != null && juso.detBdNmList != "") juso.detBdNmList
                        else if (juso.bdNm != null && juso.bdNm != "") juso.bdNm
                        else juso.jibunAddr!!
                    addressList.add(SearchAddress(mainAddress, juso.roadAddrPart1!!, juso))
                }
                binding.deliveryAddressSettingSearchList.adapter =
                    SearchAddressListAdapter(addressList, this)
                binding.deliveryAddressSettingSearchList.layoutManager = LinearLayoutManager(this)

            } else {
                // 페이징 처리..
            }
            // 검색 팁 없애고 주소 리스트 보여주기
            binding.deliveryAddressSettingSearchList.visibility = View.VISIBLE
            binding.deliveryAddressSettingSearchTip.visibility = View.GONE
        } else {
            // 오류
            binding.deliverySearchError.visibility = View.VISIBLE
            binding.deliveryAddressSettingSearchParent.visibility = View.GONE
        }
    }

    override fun onGetSearchAddrListFailure(message: String) {
        //dismissLoadingDialog()
    }

    override fun onPostDeliveryAddressAddSuccess(response: DeliveryAddressResponse) {
        dismissLoadingDialog()
        // 배달 주소 추가 완료...!
        // 배달 주소 추가를 완료하면 두가지의 길이 있다. Home 과 myeats avtivity 변수로 받아본다..!
        // 일단은 Home의 경우만 실행한다. -> checked 바로 수행
        if(response.code == 1000){
            if(version == GPS_SELECT){
                userAddressIdx = response.result.addressIdx
                setUserAddress(userMainAddress, userAddressIdx)
                deliveryCheckedAndFinish()
            } else {
                showLoadingDialog(this)
                DeliveryAddressSettingService(this).tryGetUserAddressList(getUserIdx())
            }
        }
    }

    private fun startDeliveryAddressAdd() {
        showLoadingDialog(this)
        DeliveryAddressSettingService(this).tryPatchPathUserCheckedAddress(getUserIdx(), userAddressIdx)
    }

    override fun onPostDeliveryAddressAddFailure(message: String) {
        // 배달 주소 추가 실패..
        dismissLoadingDialog()
       // showCustomToast("배달 주소 추가 실패")
    }

    // 배달지 수정하러 가기
    fun startDeliveryAddressModify(addressIdx: Int) {
        // 키보드 올라가 있으면 내리기
        if(mKeyboardStatus) {
            imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
            mKeyboardStatus = false
        }
        binding.deliveryAddressDetailParent.visibility = View.VISIBLE
        binding.deliveryAddressNotDetailParent.visibility = View.GONE
        binding.detailAddressTitle.text = "배달지 상세 정보"
        binding.deliveryAddressBack.setImageResource(R.drawable.ic_left_arrow_black)

        supportFragmentManager.beginTransaction()
            .replace(R.id.delivery_address_detail_parent, DetailAddressFragment().apply {
                arguments = Bundle().apply {
                    putInt("version", 3)
                    putInt("addressIdx", addressIdx)
                }
            }, "DeliveryAddressModify")
            .commitAllowingStateLoss()
        mDetailAddress = true
        mBackOrFinish = false
    }

    private fun gpsCheck() : Location? {
        if(ApplicationClass.sSharedPreferences.getBoolean("gps", false)){
            // gps 사용 가능
            val mGpsControl = GpsControl(this)
            val location = mGpsControl.getLocation()
            return location
        }
        return null
    }

    override fun onBackPressed() {
        // 키보드 올라가 있으면 내리기
        if(mKeyboardStatus) {
            imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
            mKeyboardStatus = false
        }
        mSearchTip = true
        if (!mBackOrFinish) {
            // 종료
            // Log.d("selected", "종료")
            if(binding.detailAddressTitle.text == "배달지 상세 정보" || (version != GPS_SELECT && (binding.deliveryAddressManageParent.visibility != View.VISIBLE || binding.deliveryAddressSettingNowGpsFind.visibility == View.VISIBLE))){
                Log.d("selected", "종료안되야함")
                binding.deliveryAddressManageParent.visibility = View.VISIBLE
                binding.deliveryAddressTextParent.visibility = View.GONE
                binding.deliveryAddressDetailParent.visibility = View.GONE
                binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
                binding.deliveryAddressSettingNowGpsFind.visibility = View.GONE
                binding.detailAddressUserList.visibility = View.VISIBLE
                binding.deliveryAddressBack.setImageResource(R.drawable.ic_cancel)
                binding.detailAddressTitle.text = "배달 주소 관리"
            } else {
                // Log.d("selected", "종료")
                setResult(RESULT_CANCELED)
                finish()
            }
        } else if(mDetailAddress){
            // Log.d("selected", "mDetailAddress")
            binding.deliveryAddressDetailParent.visibility = View.GONE
            binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
            binding.detailAddressTitle.text = "배달지 주소 설정"
            mDetailAddress = false
        } else {
            // 뒤로 가기
            // Log.d("selected", "뒤로가기")
            if(version == GPS_SELECT) binding.deliveryAddressBack.setImageResource(R.drawable.ic_cancel)
            binding.deliveryAddressSettingSelectedParent.visibility = View.VISIBLE
            binding.deliveryAddressSettingSearchParent.visibility = View.GONE
            binding.deliveryAddressText.setText("")
            binding.deliveryAddressText.clearFocus()
            binding.deliveryAddressTextCancel.visibility = View.INVISIBLE
            mBackOrFinish = false
        }
    }

    private fun setKeyboardListener(){
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {

            var navigationBarHeight = 0
            var resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                navigationBarHeight = resources.getDimensionPixelSize(resourceId)
            }

            var statusBarHeight = 0
            resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }

            val rect = Rect()
            window.decorView.getWindowVisibleDisplayFrame(rect)

            val keyboardHeight = binding.root.rootView.height - (statusBarHeight + navigationBarHeight + rect.height())

            mKeyboardStatus = keyboardHeight > 0
            // false : 내려간 상태 , true : 올라간 상태
        }
    }
}