package com.example.coupangeats.src.main.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentHomeBinding
import com.example.coupangeats.src.login.model.UserLoginRequest
import com.example.coupangeats.src.login.model.UserLoginResponse
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.home.adapter.BaseAddressAdapter
import com.example.coupangeats.util.GpsControl
import com.example.coupangeats.util.LoginBottomSheetDialog
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home), HomeFragmentView {
    private var mLoginCheck = false
    private lateinit var mGpsControl : GpsControl
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
                val loginBottomSheetDialog : LoginBottomSheetDialog = LoginBottomSheetDialog()
                loginBottomSheetDialog.show(activity!!.supportFragmentManager, "Login")
            } else {
                // 로그인이 되어 있는 경우 배달지 주소 설정으로 넘어감

            }
        }
    }

    override fun onResume() {
        super.onResume()
        var gpsCheck = false
        // gps 사용 가능 여부 체크
        if(ApplicationClass.sSharedPreferences.getBoolean("gps", false)){
            // gps 사용 가능
            val location = mGpsControl.getLocation()
            if(location != null){
                val address = mGpsControl.getCurrentAddress(location.latitude, location.longitude)
                binding.homeGpsAddress.text = address
                gpsCheck = true
            }
        }
        if(gpsCheck){
            binding.homeNoAddress.visibility = View.GONE
            binding.homeRealContent.visibility = View.VISIBLE
        } else {
            binding.homeNoAddress.visibility = View.VISIBLE
            binding.homeRealContent.visibility = View.GONE
        }
    }

    fun loginCheck() : Boolean {
        return false
        //return ApplicationClass.sSharedPreferences.getInt("userIdx", -1) != -1
    }

    /*// 자동 로그인
    private fun updateLogin() {
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
                edit.putString(ApplicationClass.X_ACCESS_TOKEN, null)
                edit.apply()
            }
        }
    }

    fun loginSuccess(jwt: String, userIdx: Int, email: String = "", password: String = ""){
        val edit = ApplicationClass.sSharedPreferences.edit()
        if(email != "" && password != ""){
            edit.putString("email", email)
            edit.putString("password", password)
        }
        edit.putString(ApplicationClass.X_ACCESS_TOKEN, jwt)
        edit.putInt("userIdx", userIdx)
        edit.apply()
    }
    fun loginFailure(){
        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putString(ApplicationClass.X_ACCESS_TOKEN, null)
        edit.putInt("userIdx", -1)
        edit.apply()
    }*/

    fun baseAddressResult(baseAddress : String) {
        binding.homeNoAddress.visibility = View.GONE
        binding.homeRealContent.visibility = View.VISIBLE
    }


}