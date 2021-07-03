package com.example.coupangeats.src.setting

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.example.coupangeats.databinding.ActivitySettingBinding
import com.example.coupangeats.databinding.DialogLoginCheckBinding
import com.example.coupangeats.databinding.DialogLogoutBinding
import com.example.coupangeats.util.CartMenuDatabase
import com.kakao.sdk.user.UserApiClient
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import kotlin.math.log

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    private lateinit var mDBHelper: CartMenuDatabase
    private lateinit var mDB: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 데이터베이스 셋팅
        mDBHelper = CartMenuDatabase(this, "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        binding.settingBackImg.setOnClickListener {
            finish()
        }
        binding.settingLogout.setOnClickListener {
            // 로그아웃
            showDialogLogoutCheck()
        }
        binding.settingSignOut.setOnClickListener {
            // 카카오 탈퇴
            if(isKaKao()){
                // 연결 끊기
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        Log.e("Login", "연결 끊기 실패", error)
                    } else {
                        Log.i("Login", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                        val edit = ApplicationClass.sSharedPreferences.edit()
                        edit.putString(ApplicationClass.X_ACCESS_TOKEN, null)
                        edit.putInt("userIdx", -1)
                        edit.putBoolean("kakao", false)
                        edit.apply()
                    }
                }
            }

        }
    }

    private fun showDialogLogoutCheck() {
        val logoutBinding = DialogLogoutBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(logoutBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()
        // 너비 꽉차게
        val window = alertDialog.window
        if(window != null){
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            alertDialog.window!!.attributes = params
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        alertDialog.show()

        logoutBinding.dialogLogoutYes.setOnClickListener {
            // 로그아웃
            loginFailure()
            alertDialog.dismiss()
            mDBHelper.deleteTotal(mDB)
        }
        logoutBinding.dialogLogoutNo.setOnClickListener {
            // 로그아웃 취소
            alertDialog.dismiss()
        }
    }
    fun loginFailure(){
        if(isKaKao()){
            // 로그아웃
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.d("Login", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    showCustomToast("카카오 로그아웃에 실패였습니다. sdk에서 토큰은 삭제됨")
                }
                else {
                    Log.d("Login", "로그아웃 성공. SDK에서 토큰 삭제됨")
                    val edit = ApplicationClass.sSharedPreferences.edit()
                    edit.putString(ApplicationClass.X_ACCESS_TOKEN, null)
                    edit.putInt("userIdx", -1)
                    edit.putBoolean("kakao", false)
                    edit.apply()
                    finish()
                }
            }
        } else {
            val edit = ApplicationClass.sSharedPreferences.edit()
            edit.putString(ApplicationClass.X_ACCESS_TOKEN, null)
            edit.putInt("userIdx", -1)
            edit.apply()
            finish()
        }
    }

    fun isKaKao() : Boolean{
        return ApplicationClass.sSharedPreferences.getBoolean("kakao", false)
    }
}