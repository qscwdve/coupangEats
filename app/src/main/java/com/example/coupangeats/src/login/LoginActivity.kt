package com.example.coupangeats.src.login

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityLoginBinding
import com.example.coupangeats.src.login.model.UserLoginRequest
import com.example.coupangeats.src.login.model.UserLoginResponse
import com.softsquared.template.kotlin.config.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate),LoginActivityView {
    private var mPasswordLook = false
    private var mEmailDeleteCheck = false
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
        binding.loginEmailText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count != 0 && !mEmailDeleteCheck){
                    binding.loginEmailCancel.visibility = View.VISIBLE
                    mEmailDeleteCheck = true
                }
                if(count == 0 && mEmailDeleteCheck){
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
            val userLoginRequest = UserLoginRequest(email, password)
            showLoadingDialog(this)
            LoginService(this).tryPostLogin(userLoginRequest)
        }
    }

    override fun onPostLoginSuccess(response: UserLoginResponse) {
        // 로그인 성공
        dismissLoadingDialog()
        if(response.code == 1000){
            showCustomToast("로그인 성공 ${response.userLoginResponseResult.jwt}")
        } else {
            showCustomToast("로그인 실패 ${response.message ?: "사유없음"}")
        }
    }

    override fun onPostLoginFailure(message: String) {
        // 로그인 실패
        dismissLoadingDialog()
        showCustomToast("로그인 실패")
    }
}