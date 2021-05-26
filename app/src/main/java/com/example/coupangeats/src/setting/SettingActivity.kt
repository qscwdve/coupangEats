package com.example.coupangeats.src.setting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.example.coupangeats.databinding.ActivitySettingBinding
import com.example.coupangeats.databinding.DialogLoginCheckBinding
import com.example.coupangeats.databinding.DialogLogoutBinding
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import kotlin.math.log

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.settingBackImg.setOnClickListener {
            finish()
        }
        binding.settingLogout.setOnClickListener {
            // 로그아웃

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
        }
        logoutBinding.dialogLogoutNo.setOnClickListener {
            // 로그아웃 취소
            alertDialog.dismiss()
        }
    }
    fun loginFailure(){
        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putString(ApplicationClass.X_ACCESS_TOKEN, null)
        edit.putInt("userIdx", -1)
        edit.apply()
    }
}