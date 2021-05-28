package com.example.coupangeats.src.main.myeats

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentMyeatsBinding
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.myeats.model.UserInfoResponse
import com.example.coupangeats.src.setting.SettingActivity
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class MyeatsFragment : BaseFragment<FragmentMyeatsBinding>(FragmentMyeatsBinding::bind, R.layout.fragment_myeats), MyeatsFragmentView {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyeatsService(this).tryGetUserInfo(getUserIdx())

        binding.myeatsSetting.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))

        }
        binding.myeatsAddressManage.setOnClickListener {
            (activity as MainActivity).startDeliveryAddressSettingActivityResult(2)
        }
        binding.myeatsAgree.setOnClickListener {  }
        binding.myeatsDiscount.setOnClickListener {  }
        binding.myeatsEvent.setOnClickListener {  }
        binding.myeatsFavorites.setOnClickListener {  }
        binding.myeatsNotice.setOnClickListener {  }
        binding.myeatsPatner.setOnClickListener {  }
        binding.myeatsPay.setOnClickListener {  }
        binding.myeatsQuestion.setOnClickListener {  }
        binding.myeatsTell.setOnClickListener {  }
    }

    override fun onResume() {
        super.onResume()
        if(!loginCheck()){
            (activity as MainActivity).setHomeFragment()
        }
    }
    fun loginCheck() : Boolean = ApplicationClass.sSharedPreferences.getInt("userIdx", -1) != -1

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun loginFailure(){
        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putString(ApplicationClass.X_ACCESS_TOKEN, null)
        edit.putInt("userIdx", -1)
        edit.apply()
    }

    override fun onGetUserInfoSuccess(response: UserInfoResponse) {
        if(response.code == 1000) {
            // 성공
            binding.myeatsName.text = response.result!!.userName
            binding.myeatsPhone.text = response.result.phone
        } else {
            // 로그인 다시 해야한다.
            loginFailure()
            showCustomToast("로그인 인증이 올바르지 않습니다.")
            (activity as MainActivity).setHomeFragment()
        }
    }

    override fun onGetUserInfoFailure(message: String) {

    }
}