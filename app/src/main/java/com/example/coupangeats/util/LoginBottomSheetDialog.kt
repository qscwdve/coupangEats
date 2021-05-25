package com.example.coupangeats.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import com.example.coupangeats.databinding.DialogLoginBinding
import com.example.coupangeats.src.login.LoginActivity
import com.example.coupangeats.src.signUp.SignUpActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding : DialogLoginBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogLoginBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogLoginSignUpParent.setOnClickListener {
            // 회원가입
            startActivity(Intent(activity, SignUpActivity::class.java))
        }
        binding.dialogLoginBasic.setOnClickListener {
            // 쿠팡 아이디로 로그인
            startActivity(Intent(activity, LoginActivity::class.java))
        }
        binding.dialogLoginSocial.setOnClickListener {
            // 쿠팡 앱으로 로그인

        }
    }

}