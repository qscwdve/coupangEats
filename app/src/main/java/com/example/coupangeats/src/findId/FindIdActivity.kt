package com.example.coupangeats.src.findId

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.coupangeats.databinding.ActivityFindIdBinding
import com.example.coupangeats.src.findId.model.FindEmailResponse
import com.example.coupangeats.src.findId.model.FindIdRequest
import com.example.coupangeats.src.findId.model.FindIdResponse
import com.softsquared.template.kotlin.config.BaseActivity

class FindIdActivity : BaseActivity<ActivityFindIdBinding>(ActivityFindIdBinding::inflate), FindIdActivityView {
    private lateinit var imm: InputMethodManager
    private var mIsText = -1
    private var userName = ""
    private var userPhone = ""
    private var userEmail = ""
    private var authNum = ""
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.toolbarBack.setOnClickListener { finish() }

        binding.findIdName.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                // 이름쓰기에 포커스가 맞춰질 경우
                mIsText = 1
                binding.findIdNameLine.visibility = View.VISIBLE
            } else {
                binding.findIdNameLine.visibility = View.GONE
            }
        }
        binding.findIdPhone.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                // 휴대폰 번호에 포커스가 맞춰질 경우
                mIsText = 2
                binding.findIdPhoneLine.visibility = View.VISIBLE
            } else {
                binding.findIdPhoneLine.visibility = View.GONE
            }
        }
        binding.findAuthNumText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                // 인증번호 입력에 포커스가 맞춰질 경우
                mIsText = 3
                binding.findIdAuthNumLine.visibility = View.VISIBLE
            } else {
                binding.findIdAuthNumLine.visibility = View.GONE
            }
        }
        // 밖에 선택시 키보드 내리기
        binding.findIdContentParent.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP){
                when(mIsText){
                    1 -> {
                        // 이름일 경우
                        imm.hideSoftInputFromWindow(binding.findIdName.windowToken, 0)
                        binding.findIdName.clearFocus()
                    }
                    2 -> {
                        // 휴대폰 번호
                        imm.hideSoftInputFromWindow(binding.findIdPhone.windowToken, 0)
                        binding.findIdPhone.clearFocus()
                    }
                    3 -> {
                        // 인증번호 입력
                        imm.hideSoftInputFromWindow(binding.findAuthNumText.windowToken, 0)
                        binding.findAuthNumText.clearFocus()
                    }
                }
            }
            false
        }
        // 인증번호 전송
        binding.findIdAuthNumberRequest.setOnClickListener {
            if(binding.findIdName.text.toString().isEmpty()){
                // 이름 에러 메시지 띄움
                binding.findIdError.visibility = View.VISIBLE
                binding.findIdError.text = "이름을 입력해주세요"
            } else if(binding.findIdPhone.text.toString().isEmpty()){
                // 휴대폰 번호 에러 메시지 띄움
                binding.findIdError.visibility = View.VISIBLE
                binding.findIdError.text = "전화번호를 입력해주세요"
            } else {
                // 서버로 요청 가능
                binding.findIdError.visibility = View.GONE
                userName = binding.findIdName.text.toString()
                userPhone = binding.findIdPhone.text.toString()
                FindIdService(this).tryPostFindIdAuthNumber(FindIdRequest(userName, userPhone))
                // 인증번호 입력 부분 띄우기
                binding.findIdAuthNumberRequest.text = "인증번호 재전송"
                binding.findAuthNumTextParent.visibility = View.VISIBLE
                binding.findIdAuthNumError.visibility = View.VISIBLE
                binding.findIdAuthNumOk.visibility = View.VISIBLE
            }
        }
        // 인증번호 확인
        binding.findIdAuthNumOk.setOnClickListener {
            val number = binding.findAuthNumText.text.toString()
            if(number.isEmpty()){
                showCustomToast("인증번호를 입력해주세요")
            } else {
                if(number == authNum){
                    // 인증 성공
                    FindIdService(this).tryGetFindEmail(userPhone)
                }
            }
        }
        // 로그인 하러 가기
        binding.findIdLogin.setOnClickListener { finish() }
        // 비밀번호 찾기
        binding.findIdPasswordFind.setOnClickListener {  }
    }

    override fun onPostFindIdAuthNumberSuccess(response: FindIdResponse) {
        if(response.code == 1000){
            authNum = response.result.authNumber
        } else {
            showCustomToast(response.message ?: "인증번호 전송에 실패하였습니다.")
        }
    }

    override fun onPostFindIdAuthNumberFailure(message: String) {
        showCustomToast("인증번호 전송에 실패하였습니다.")
    }

    override fun onGetFindEmailSuccess(response: FindEmailResponse) {
        if(response.code == 1000){
            binding.findIdMemberPhoneAuthParent.visibility = View.GONE
            binding.findIdPhoneAuthParent.visibility = View.GONE
            binding.findIdUserInfoParent.visibility = View.VISIBLE

            binding.findIdUserNameInfo.text = "${userName} 회원님의 계정 이메일은"
            binding.findIdEmailText.text = response.result.email
        } else {
            showCustomToast(response.message ?: "인증번호 확인에 실패하였습니다.")
        }
    }

    override fun onGetFindEmailFailure(message: String) {
        showCustomToast("인증번호 확인에 실패하였습니다.")
    }
}