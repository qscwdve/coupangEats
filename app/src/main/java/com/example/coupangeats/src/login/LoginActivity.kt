package com.example.coupangeats.src.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityLoginBinding
import com.example.coupangeats.databinding.DialogLoginCheckBinding
import com.example.coupangeats.src.login.model.UserLoginRequest
import com.example.coupangeats.src.login.model.UserLoginResponse
import com.example.coupangeats.src.signUp.SignUpActivity
import com.example.coupangeats.util.LoginBottomSheetDialog
import com.example.coupangeats.util.LoginNoticeBottomSheetDialog
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity


class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate),LoginActivityView {
    private var mPasswordLook = false
    private var mEmailDeleteCheck = false
    private var mLoginCheck = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 박스 색깔 바꾸기
        binding.loginEmailText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.loginEmailParent.setBackgroundResource(R.drawable.login_box_checked)
            } else {
                binding.loginEmailParent.setBackgroundResource(R.drawable.login_box)
            }
        }
        // 비밀번호 보기 / 숨기기
        binding.loginPasswordText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.loginPasswordParent.setBackgroundResource(R.drawable.login_box_checked)
            } else {
                binding.loginPasswordParent.setBackgroundResource(R.drawable.login_box)
            }
        }

        binding.loginPasswordLook.setOnClickListener {
            if(!mPasswordLook){
                binding.loginPasswordText.inputType = InputType.TYPE_CLASS_TEXT
                binding.loginPasswordLook.setImageResource(R.drawable.login_password_look)
                binding.loginPasswordText.setSelection(binding.loginPasswordText.text.toString().length)
            } else {
                binding.loginPasswordText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.loginPasswordLook.setImageResource(R.drawable.login_password_hide)
                binding.loginPasswordText.setSelection(binding.loginPasswordText.text.toString().length)
            }
            mPasswordLook = !mPasswordLook
        }

        // 아이디 delete
        binding.loginEmailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count != 0 && !mEmailDeleteCheck) {
                    binding.loginEmailCancel.visibility = View.VISIBLE
                    mEmailDeleteCheck = true
                }
                if (count == 0 && mEmailDeleteCheck) {
                    binding.loginEmailCancel.visibility = View.INVISIBLE
                    mEmailDeleteCheck = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // email delete
        binding.loginEmailCancel.setOnClickListener {
            binding.loginEmailText.setText("")
            binding.loginEmailCancel.visibility = View.INVISIBLE
        }

        // 뒤로가기 누름
        binding.basicLoginBack.setOnClickListener { finish() }

        // 로그인 하기
        binding.loginSend.setOnClickListener {
            // 이메일, 비밀번호 검증 필요
            val email = binding.loginEmailText.text.toString()
            val password = binding.loginPasswordText.text.toString()

            var error = -1
            if(email == "") error = 1
            else if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) error = 2
            else if(password == "") error = 3

            if(error != -1){
                val notice = when(error){
                    1 -> R.string.login_notice_need_id
                    2 -> R.string.login_notice_email_format
                    3 -> R.string.login_notice_password_need
                    else -> R.string.login_notice_error
                }
                toggle(notice)
            } else {
                val userLoginRequest = UserLoginRequest(email, password)
                showLoadingDialog(this)
                LoginService(this).tryPostLogin(userLoginRequest)
            }
        }

        // 회원가입 하러 가기
        binding.loginToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    override fun onPostLoginSuccess(response: UserLoginResponse) {
        dismissLoadingDialog()
        val shared = ApplicationClass.sSharedPreferences
        val edit = shared.edit()
        if(response.code == 1000 && response.userLoginResponseResult != null){
            val result = response.userLoginResponseResult
            if(result.jwt != null) {
                edit.putString(ApplicationClass.X_ACCESS_TOKEN, result.jwt)
                mLoginCheck = true
                finish()
            }
            else {
                edit.putInt("userIdx", -1)
                mLoginCheck = false
                showDialogLoginCheck()
            }
        } else {
            showDialogLoginCheck(response.message ?: "")
            edit.putInt("userIdx", -1)
            mLoginCheck = false
        }
        edit.apply()
    }

    override fun onPostLoginFailure(message: String) {
        dismissLoadingDialog()
        val shared = ApplicationClass.sSharedPreferences
        val edit = shared.edit()
        edit.putInt("userIdx", -1)
        mLoginCheck = false
        edit.apply()
        showDialogLoginCheck("로그인 실패 error message : $message")
    }
    private fun toggle(notice : Int) {

        binding.noticeText.setText(notice)

        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(binding.noticeText)
        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.noticeText.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            TransitionManager.beginDelayedTransition(binding.root, transition)
            binding.noticeText.visibility = View.GONE
        }, 1500)
    }

    private fun showDialogLoginCheck(notice : String = "") {
        val loginCheckBinding = DialogLoginCheckBinding.inflate(layoutInflater)
        if(notice != "") loginCheckBinding.dialogLoginCheckText.text = notice
        val builder = AlertDialog.Builder(this)
        builder.setView(loginCheckBinding.root)
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

        loginCheckBinding.dialogLoginCheckOkBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}