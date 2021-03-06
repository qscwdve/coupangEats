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
    private lateinit var imm: InputMethodManager   // ????????? ?????????
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

        // ????????? ?????? ??????
        setKeyboardListener()

        mapActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if(result.resultCode == RESULT_OK){
                    val data = result.data
                    // ????????? ?????? ??????
                    if(data != null){
                        val mainAddress = data.getStringExtra("mainAddress") ?: ""
                        val roadAddress = data.getStringExtra("roadAddress") ?: ""
                        val lat = data.getStringExtra("lat") ?: ""
                        val lon = data.getStringExtra("lon") ?: ""
                        changeDetailAddress(SearchAddress(mainAddress, roadAddress), lat, lon)
                    }
                }
            }

        // ???????????? ???????????? gps ????????????
        version = intent.getIntExtra("version", GPS_SELECT)

        if(version == GPS_SELECT){
            // ??????????????? ?????? ?????? ??????
            binding.deliveryAddressManageParent.visibility = View.GONE
            binding.deliveryAddressTextParent.visibility = View.VISIBLE
            binding.deliveryAddressSettingNowGpsFind.visibility = View.VISIBLE
            binding.detailAddressTitle.text = "????????? ?????? ??????"
        } else {
            // ???????????? ??????
            binding.deliveryAddressManageParent.visibility = View.VISIBLE
            binding.deliveryAddressTextParent.visibility = View.GONE
            binding.deliveryAddressSettingNowGpsFind.visibility = View.GONE
            binding.detailAddressTitle.text = "?????? ?????? ??????"
        }

        //??? ???????????? ?????? - DELIVERY_MANAGE
        binding.deliveryAddressManageText.setOnClickListener {
            binding.detailAddressUserList.visibility = View.INVISIBLE
            binding.deliveryAddressManageParent.visibility = View.GONE
            binding.deliveryAddressTextParent.visibility = View.VISIBLE
            binding.deliveryAddressSettingNowGpsFind.visibility = View.VISIBLE
            binding.deliveryAddressBack.setImageResource(R.drawable.ic_left_arrow_black)
            binding.detailAddressTitle.text = "????????? ?????? ??????"
        }

        // edittext ??????
        binding.deliveryAddressText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // ?????????
                if (mSearchTip) {
                    // ????????? ?????????
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


        // ????????? ????????? ?????? ?????? ??????
        binding.deliveryAddressText.setOnEditorActionListener { v, actionId, event ->
            //showCustomToast("????????? ??????")
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            val keyword = binding.deliveryAddressText.text.toString()
            // ?????? API ?????? ??????
            binding.deliveryAddressSettingSearchTip.visibility = View.GONE
            binding.deliveryAddressSettingSearchList.visibility = View.VISIBLE
            //showLoadingDialog(this)
            val searchAddrListRequest = SearchAddrListRequest(1, keyword)
            DeliveryAddressSettingService(this).tryGetSearchAddrList(searchAddrListRequest)
            false
        }

        // editText ?????? ?????? ?????????
        binding.deliveryAddressTextCancel.setOnClickListener {
            binding.deliveryAddressText.setText("")
            binding.deliveryAddressTextCancel.visibility = View.INVISIBLE
        }

        binding.deliveryAddressBack.setOnClickListener {
            // ????????? ????????? ????????? ?????????
            if(mKeyboardStatus) {
                imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
                mKeyboardStatus = false
            }
            mSearchTip = true
            if (!mBackOrFinish) {
                // ??????
                // Log.d("selected", "??????")
                if(binding.detailAddressTitle.text == "????????? ?????? ??????" || (version != GPS_SELECT && (binding.deliveryAddressManageParent.visibility != View.VISIBLE || binding.deliveryAddressSettingNowGpsFind.visibility == View.VISIBLE))){
                    Log.d("selected", "??????????????????")
                    binding.deliveryAddressManageParent.visibility = View.VISIBLE
                    binding.deliveryAddressTextParent.visibility = View.GONE
                    binding.deliveryAddressDetailParent.visibility = View.GONE
                    binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
                    binding.deliveryAddressSettingNowGpsFind.visibility = View.GONE
                    binding.detailAddressUserList.visibility = View.VISIBLE
                    binding.deliveryAddressBack.setImageResource(R.drawable.ic_cancel)
                    binding.detailAddressTitle.text = "?????? ?????? ??????"
                } else {
                    // Log.d("selected", "??????")
                    setResult(RESULT_CANCELED)
                    finish()
                }
            } else if(mDetailAddress){
                // Log.d("selected", "mDetailAddress")
                binding.deliveryAddressDetailParent.visibility = View.GONE
                binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
                binding.detailAddressTitle.text = "????????? ?????? ??????"
                mDetailAddress = false
            } else {
                // ?????? ??????
                // Log.d("selected", "????????????")
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
                // ????????? ???????????? ???
                startDeliveryAddressModify(mHomeaddressIdx)
            } else {
                // ???????????? ?????????
                mCategory = 1
                // ????????? ?????????
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
                // ???????????? ?????????
                mCategory = 2
                // ????????? ?????????
                binding.deliveryAddressSettingSelectedParent.visibility = View.GONE
                binding.deliveryAddressSettingSearchParent.visibility = View.VISIBLE
                mSearchTip = false
            }
        }

        // ?????? ?????? ?????? ??????
        binding.deliveryAddressSettingNowGps.setOnClickListener {
            // ????????? ?????? ???????????? ??????
            val location = gpsCheck()
            val mLat = location?.latitude ?: (-1).toDouble()
            val mLon = location?.longitude ?: (-1).toDouble()
            val intent = Intent(this, MapActivity::class.java).apply {
                // ?????? ?????? ???????????? ???
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
        // ??????????????? ????????? ?????? ???????????? ???... selected ????????? ??????!! ???????????????!
        userMainAddress = mainAddress
        userAddressIdx = addressIdx
        startDeliveryAddressAdd()
    }

    fun changeDetailAddress(searchAddress: SearchAddress, lat: String, lon: String){
        // ????????? ????????? ????????? ?????????
        if(mKeyboardStatus) {
            imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
            mKeyboardStatus = false
        }
        var latitude = lat
        var longiute = lon
        binding.deliveryAddressDetailParent.visibility = View.VISIBLE
        binding.deliveryAddressNotDetailParent.visibility = View.GONE
        binding.detailAddressTitle.text = "????????? ?????? ??????"

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
        // ?????? ?????? ?????? ????????? ????????? ?????? ????????? ????????? ????????? ????????? ??????
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
            binding.detailAddressTitle.text = "?????? ?????? ??????"
            mDetailAddress = false
            mBackOrFinish = false
        }
        mSearchTip = true
    }

    override fun onGetUserAddressListSuccess(response: UserAddrListResponse) {
        dismissLoadingDialog()
        binding.deliveryAddressSettingHomeChecked.visibility = View.INVISIBLE
        binding.deliveryAddressBusinessChecked.visibility = View.INVISIBLE
        // ????????? ??????!!
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

                // ?????? ????????? ????????? ????????? ?????? ????????? ?????????
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
                // ?????? ????????? ????????? ????????? ?????? ????????? ?????????
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
        //ustomToast("?????? ????????? ????????? ?????? ????????? ????????? ??? ????????????.")
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
                setUserAddress("??????????????? ??????????????????", -1)
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
       // showCustomToast("????????? ?????? ?????? ?????? ????????? ?????????????????????")
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
        binding.detailAddressTitle.text = "?????? ?????? ??????"
        mSearchTip = true
        binding.deliveryAddressBusinessDetail.visibility = View.VISIBLE
        binding.deliveryAddressSettingHomeDetail.visibility = View.VISIBLE
        if(addressIdx == mSelectedAddress){
            // ?????? 1????????? ?????????
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
            // ??????
            binding.deliverySearchError.visibility = View.GONE
            binding.deliveryAddressSettingSearchParent.visibility = View.VISIBLE
            if (common.currentPage == 1) {
                // ?????? ??????
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
                // ????????? ??????..
            }
            // ?????? ??? ????????? ?????? ????????? ????????????
            binding.deliveryAddressSettingSearchList.visibility = View.VISIBLE
            binding.deliveryAddressSettingSearchTip.visibility = View.GONE
        } else {
            // ??????
            binding.deliverySearchError.visibility = View.VISIBLE
            binding.deliveryAddressSettingSearchParent.visibility = View.GONE
        }
    }

    override fun onGetSearchAddrListFailure(message: String) {
        //dismissLoadingDialog()
    }

    override fun onPostDeliveryAddressAddSuccess(response: DeliveryAddressResponse) {
        dismissLoadingDialog()
        // ?????? ?????? ?????? ??????...!
        // ?????? ?????? ????????? ???????????? ???????????? ?????? ??????. Home ??? myeats avtivity ????????? ????????????..!
        // ????????? Home??? ????????? ????????????. -> checked ?????? ??????
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
        // ?????? ?????? ?????? ??????..
        dismissLoadingDialog()
       // showCustomToast("?????? ?????? ?????? ??????")
    }

    // ????????? ???????????? ??????
    fun startDeliveryAddressModify(addressIdx: Int) {
        // ????????? ????????? ????????? ?????????
        if(mKeyboardStatus) {
            imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
            mKeyboardStatus = false
        }
        binding.deliveryAddressDetailParent.visibility = View.VISIBLE
        binding.deliveryAddressNotDetailParent.visibility = View.GONE
        binding.detailAddressTitle.text = "????????? ?????? ??????"
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
            // gps ?????? ??????
            val mGpsControl = GpsControl(this)
            val location = mGpsControl.getLocation()
            return location
        }
        return null
    }

    override fun onBackPressed() {
        // ????????? ????????? ????????? ?????????
        if(mKeyboardStatus) {
            imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
            mKeyboardStatus = false
        }
        mSearchTip = true
        if (!mBackOrFinish) {
            // ??????
            // Log.d("selected", "??????")
            if(binding.detailAddressTitle.text == "????????? ?????? ??????" || (version != GPS_SELECT && (binding.deliveryAddressManageParent.visibility != View.VISIBLE || binding.deliveryAddressSettingNowGpsFind.visibility == View.VISIBLE))){
                Log.d("selected", "??????????????????")
                binding.deliveryAddressManageParent.visibility = View.VISIBLE
                binding.deliveryAddressTextParent.visibility = View.GONE
                binding.deliveryAddressDetailParent.visibility = View.GONE
                binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
                binding.deliveryAddressSettingNowGpsFind.visibility = View.GONE
                binding.detailAddressUserList.visibility = View.VISIBLE
                binding.deliveryAddressBack.setImageResource(R.drawable.ic_cancel)
                binding.detailAddressTitle.text = "?????? ?????? ??????"
            } else {
                // Log.d("selected", "??????")
                setResult(RESULT_CANCELED)
                finish()
            }
        } else if(mDetailAddress){
            // Log.d("selected", "mDetailAddress")
            binding.deliveryAddressDetailParent.visibility = View.GONE
            binding.deliveryAddressNotDetailParent.visibility = View.VISIBLE
            binding.detailAddressTitle.text = "????????? ?????? ??????"
            mDetailAddress = false
        } else {
            // ?????? ??????
            // Log.d("selected", "????????????")
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
            // false : ????????? ?????? , true : ????????? ??????
        }
    }
}