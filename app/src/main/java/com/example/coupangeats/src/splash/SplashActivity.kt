package com.example.coupangeats.src.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Base64
import android.util.Log
import com.example.coupangeats.databinding.ActivitySplashBinding
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.splash.model.AutoLoginResponse
import com.kakao.sdk.common.util.Utility
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate), SplashActivityView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //getAppKeyHash()
        SplashService(this).tryGetAutoLogin(getUserIdx())
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
        var keyHash = Utility.getKeyHash(this)
        Log.d("keyHash", "key : $keyHash")
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

    private fun getAppKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something: String = String(Base64.encode(md.digest(), 0))
                Log.e("Hash key", something)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("name not found", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }
}