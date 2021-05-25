package com.example.coupangeats.src.main.home

import android.os.Bundle
import android.view.View
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentHomeBinding
import com.example.coupangeats.src.login.model.UserLoginRequest
import com.example.coupangeats.src.login.model.UserLoginResponse
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.util.LoginBottomSheetDialog
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home), HomeFragmentView {
    private var mLoginCheck = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 자동 로그인 체크
        updateLogin()

        // 검색 기능
        binding.homeSearch.setOnClickListener {
            (activity as MainActivity).setSearchAdvencedFragment()
        }
        // 주소지 클릭
        binding.homeGpsAddress.setOnClickListener {
            if(!mLoginCheck) {
                val loginBottomSheetDialog : LoginBottomSheetDialog = LoginBottomSheetDialog()
                loginBottomSheetDialog.show(activity!!.supportFragmentManager, "Login")
            }
        }
    }

    // 자동 로그인
    fun updateLogin() {
        val shared = ApplicationClass.sSharedPreferences
        // jwt 초기화
        val edit = shared.edit()
        edit.putString(ApplicationClass.X_ACCESS_TOKEN, null).apply()
        val userId = shared.getInt("userIdx", -1)
        if(userId != -1) {
            val email = shared.getString("email", "") ?: ""
            val password = shared.getString("password", "") ?: ""
            if(email != "" && password != ""){
                // 자동 로그인 할 수 있음 -> 서버에 로그인 시도
                showCustomToast("자동 로그인 시도")
                val userLoginRequest = UserLoginRequest(email, password)
                HomeService(this).tryPostLogin(userLoginRequest)
            } else {
                edit.putInt("userIdx", -1)
                edit.apply()
            }
        }
    }

    override fun onPostLoginSuccess(response: UserLoginResponse) {
        val shared = ApplicationClass.sSharedPreferences
        val edit = shared.edit()
        mLoginCheck = if(response.code == 1000 && response.userLoginResponseResult != null){
            val result = response.userLoginResponseResult
            // 자동 로그인 성공
            if(result.jwt != null) {
                edit.putString(ApplicationClass.X_ACCESS_TOKEN, result.jwt)
                true
            } else {
                edit.putInt("userIdx", -1)
                false
            }
        } else {
            edit.putInt("userIdx", -1)
            false
        }
        edit.apply()
    }

    override fun onPostLoginFailure(message: String) {
        val shared = ApplicationClass.sSharedPreferences
        val edit = shared.edit()
        edit.putInt("userIdx", -1)
        mLoginCheck = false
        edit.apply()
    }
}