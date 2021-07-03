package com.example.coupangeats.src.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.example.coupangeats.databinding.ActivitySplashBinding
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.splash.model.AutoLoginResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate), SplashActivityView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SplashService(this).tryGetAutoLogin(getUserIdx())
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)

    }

    override fun onGetAutoLoginSuccess(response: AutoLoginResponse) {
        if(response.code == 1000 && response.result.isLogin != null){

        } else {
            // 자동 로그인 실패
            loginFailure()
        }
    }

    override fun onGetAutoLoginFailure(message: String) {

    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun loginFailure() {
        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putInt("userIdx", -1)
        edit.apply()
    }

    // android device id 확인
    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }
}