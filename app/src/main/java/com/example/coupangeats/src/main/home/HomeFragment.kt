package com.example.coupangeats.src.main.home

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentHomeBinding
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.util.LoginBottomSheetDialog
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {
    private var mLoginCheck = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginCheck()

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

    // 로그인 여부 체크
    fun loginCheck() {
        val shared = ApplicationClass.sSharedPreferences
        val userId = shared.getString("userId", "")
        if(userId != "") mLoginCheck = true
    }
}