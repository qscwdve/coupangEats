package com.example.coupangeats.src.deliveryAddressSetting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityDeliveryAddressSettingBinding
import com.example.coupangeats.src.deliveryAddressSetting.adapter.AddressListAdapter
import com.example.coupangeats.src.deliveryAddressSetting.adapter.SearchAddressListAdapter
import com.example.coupangeats.src.deliveryAddressSetting.adapter.data.SearchAddress
import com.example.coupangeats.src.deliveryAddressSetting.model.BaseAddress
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.SearchAddrListRequest
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.SearchAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponseResult
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import java.util.function.LongFunction

class DeliveryAddressSettingActivity :
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
    private lateinit var imm: InputMethodManager   // 키보드 숨기기

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        showLoadingDialog(this)
        DeliveryAddressSettingService(this).tryGetUserAddressList(getUserIdx())

        binding.deliveryAddressText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // 클릭됨
                if (mSearchTip) {
                    // 검색팁 보여줌
                    binding.deliveryAddressSettingSelectedParent.visibility = View.GONE
                    binding.deliveryAddressSettingSearchParent.visibility = View.VISIBLE
                    mSearchTip = false
                }
                if (!mBackOrFinish) {
                    mBackOrFinish = true
                    binding.deliveryAddressBack.setImageResource(R.drawable.left_arrow)
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
            showCustomToast("엔터키 눌림")
            imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
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
            if (!mBackOrFinish) {
                // 종료
                setResult(RESULT_CANCELED)
                finish()
            } else {
                // 뒤로 가기
                mSearchTip = true
                binding.deliveryAddressBack.setImageResource(R.drawable.cancel)
                binding.deliveryAddressSettingSelectedParent.visibility = View.VISIBLE
                binding.deliveryAddressSettingSearchParent.visibility = View.GONE
                binding.deliveryAddressText.setText("")
                imm.hideSoftInputFromWindow(binding.deliveryAddressText.windowToken, 0)
                binding.deliveryAddressText.clearFocus()
                binding.deliveryAddressTextCancel.visibility = View.INVISIBLE
                mBackOrFinish = false
            }
        }
        binding.deliveryAddressSettingHomeParent.setOnClickListener {
            if (isHome) {
                finishActivitySelectedData(mHomeMainAddress, mHomeaddressIdx)
            } else {
                // 검색하러 가야함
            }
        }
        binding.deliveryAddressSettingBusinessParent.setOnClickListener {
            if (isCompany) {
                finishActivitySelectedData(mCompanyMainAddress, mCompanyaddressIdx)
                Log.d("selected", "company 선택됨")
            } else {
                // 검색하러 가야함
            }
        }
    }

    fun getUserIdx(): Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun setUserAddress(mainAddress: String, addressIdx: Int) {
        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putInt("userAddressIdx", addressIdx)
        edit.putString("userMainAddressIdx", mainAddress)
        edit.apply()
    }

    fun finishActivitySelectedData(mainAddress: String, addressIdx: Int) {
        // 서버통신한 다음에 저거 실행해야 함... selected 고치고 수정!! 이거할차례!
        userMainAddress = mainAddress
        userAddressIdx = addressIdx
        showLoadingDialog(this)
        DeliveryAddressSettingService(this).tryPatchPathUserCheckedAddress(getUserIdx(), addressIdx)
    }

    override fun onGetUserAddressListSuccess(response: UserAddrListResponse) {
        dismissLoadingDialog()
        // 어댑터 생성!!
        val home = response.result.home
        val company = response.result.company
        val addressList = response.result.addressList
        if (home != null) {
            binding.deliveryAddressSettingHomeDetail.text = home.subAddress
            if (response.result.selectedAddressIdx == home.addressIdx) binding.deliveryAddressSettingHomeChecked.visibility =
                View.VISIBLE
            else binding.deliveryAddressSettingHomeChecked.visibility = View.INVISIBLE
            isHome = true
            mHomeMainAddress = response.result.home.mainAddress
            mHomeaddressIdx = response.result.home.addressIdx
        } else {
            binding.deliveryAddressSettingHomeDetail.visibility = View.GONE
            isHome = false
        }
        if (company != null) {
            isCompany = true
            mCompanyMainAddress = response.result.company.mainAddress
            mCompanyaddressIdx = response.result.company.addressIdx
            binding.deliveryAddressBusinessDetail.text = company.subAddress
            if (response.result.selectedAddressIdx == company.addressIdx) binding.deliveryAddressBusinessChecked.visibility =
                View.VISIBLE
            else binding.deliveryAddressBusinessChecked.visibility = View.INVISIBLE
        } else {
            isCompany = false
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
    }

    override fun onGetUserAddressListFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("통신 오류로 배달지 주소 목록을 불러올 수 없습니다.")
    }

    override fun onPathUserCheckedAddressSuccess(response: UserCheckedAddressResponse) {
        dismissLoadingDialog()
        if (response.code == 1000) {
            if (userMainAddress != "" && userAddressIdx != -1) {
                Log.d("selected", "company")
                setUserAddress(userMainAddress, userAddressIdx)
                setResult(RESULT_OK, intent)
                finish()
            }
            setResult(RESULT_CANCELED)
            finish()
        }

    }

    override fun onPathUserCheckedAddressFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("서버에 선택 주소 수정 요청이 실패하였습니다")
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onGetSearchAddrListSuccess(response: SearchAddrListResponse) {
        //dismissLoadingDialog()
        val common = response.results.common
        val jusoList = response.results.juso
        if (common.errorCode == "0" && jusoList.size != 0) {
            // 정상
            binding.deliverySearchError.visibility = View.GONE
            binding.deliveryAddressSettingSearchParent.visibility = View.VISIBLE
            if (common.currentPage == 1) {
                // 처음 시작
                val addressList = ArrayList<SearchAddress>()
                for (juso in jusoList) {
                    var mainAddress = ""
                    if (juso.detBdNmList != null && juso.detBdNmList != "") mainAddress =
                        juso.detBdNmList
                    else if (juso.bdNm != null && juso.bdNm != "") mainAddress = juso.bdNm
                    else mainAddress = juso.jibunAddr!!
                    addressList.add(SearchAddress(mainAddress, juso.roadAddrPart1!!))
                }
                binding.deliveryAddressSettingSearchList.adapter =
                    SearchAddressListAdapter(addressList)
                binding.deliveryAddressSettingSearchList.layoutManager = LinearLayoutManager(this)

            } else {
                // 페이징 처리..
            }
        } else {
            // 오류
            binding.deliverySearchError.visibility = View.VISIBLE
            binding.deliveryAddressSettingSearchParent.visibility = View.GONE
        }
    }

    override fun onGetSearchAddrListFailure(message: String) {
        //dismissLoadingDialog()
    }
}