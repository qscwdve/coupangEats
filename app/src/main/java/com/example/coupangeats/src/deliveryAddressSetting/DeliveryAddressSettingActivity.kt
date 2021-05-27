package com.example.coupangeats.src.deliveryAddressSetting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.databinding.ActivityDeliveryAddressSettingBinding
import com.example.coupangeats.src.deliveryAddressSetting.adapter.AddressListAdapter
import com.example.coupangeats.src.deliveryAddressSetting.model.BaseAddress
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponseResult
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class DeliveryAddressSettingActivity : BaseActivity<ActivityDeliveryAddressSettingBinding>(ActivityDeliveryAddressSettingBinding::inflate) , DeliveryAddressSettingActivityView{
    private var userMainAddress = ""
    private var userAddressIdx = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showLoadingDialog(this)
        DeliveryAddressSettingService(this).tryGetUserAddressList(getUserIdx())

        binding.deliveryAddressSettingHomeParent.setOnClickListener {  }
        binding.deliveryAddressSettingBusinessParent.setOnClickListener {  }

        binding.deliveryAddressBack.setOnClickListener {
            finish()
        }
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun finishActivitySelectedData(mainAddress: String, addressIdx: Int){
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
        if( home != null){
            binding.deliveryAddressSettingHomeDetail.text = home.subAddress
            if(response.result.selectedAddressIdx == home.addressIdx) binding.deliveryAddressSettingHomeChecked.visibility = View.VISIBLE
            else binding.deliveryAddressSettingHomeChecked.visibility = View.INVISIBLE
        } else {
            binding.deliveryAddressSettingHomeDetail.visibility = View.GONE
        }
        Log.d("address", "company : ${company}")
        if(company != null){
            binding.deliveryAddressBusinessDetail.text = company.subAddress
            if(response.result.selectedAddressIdx == company.addressIdx) binding.deliveryAddressBusinessChecked.visibility = View.VISIBLE
            else binding.deliveryAddressBusinessChecked.visibility = View.INVISIBLE
        } else {
            binding.deliveryAddressBusinessDetail.visibility = View.GONE
        }
        if( addressList != null){
            binding.deliveryAddressSettingListRecyclerView.adapter = AddressListAdapter(addressList as ArrayList<BaseAddress>, response.result.selectedAddressIdx, this)
            binding.deliveryAddressSettingListRecyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onGetUserAddressListFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("통신 오류로 배달지 주소 목록을 불러올 수 없습니다.")
    }

    override fun onPathUserCheckedAddressSuccess(response: UserCheckedAddressResponse) {
        dismissLoadingDialog()
        if(response.code == 1000){
            if(userMainAddress != "" && userAddressIdx != -1){
                val edit = ApplicationClass.sSharedPreferences.edit()
                edit.putInt("userAddressIdx", userAddressIdx)
                edit.putString("userMainAddressIdx", userMainAddress)
                edit.apply()
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
}