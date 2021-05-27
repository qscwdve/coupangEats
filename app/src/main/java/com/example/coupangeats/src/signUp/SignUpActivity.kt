package com.example.coupangeats.src.signUp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivitySignupBinding
import com.example.coupangeats.src.login.LoginActivity
import com.example.coupangeats.src.signUp.model.emailDuplicated.EmailDuplicatedResponse
import com.example.coupangeats.src.signUp.model.phoneDuplicated.PhoneDuplicatedResponse
import com.example.coupangeats.src.signUp.model.userSignUp.UserSignUpRequest
import com.example.coupangeats.src.signUp.model.userSignUp.UserSignUpResponse
import com.softsquared.template.kotlin.config.BaseActivity
import java.util.regex.Pattern

class SignUpActivity : BaseActivity<ActivitySignupBinding>(ActivitySignupBinding::inflate), SignUpActivityView {
    private val mSignUpChecked = arrayListOf(false, false, false, false)
    private val mSignUpAgreeChecked = arrayListOf(false, false, false, false, false)
    private var mSignUpAgreeAllChecked = false
    private var mSignUpAgreeCheckedNum = 0
    private var mEmailDuplicatedCheck = false
    private var mNowEditText = 0
    private var mPasswordLookImg = true  // 눈 뜸
    private val mErrorColor = "#C4263A"
    private val mOkColor = "#4472C4"
    private val mPasswordCheckedTextColor = "#35763F"
    private lateinit var imm: InputMethodManager

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // 이메일 검증
        val patternEmail = Patterns.EMAIL_ADDRESS
        binding.signUpEmailText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                if(mSignUpChecked[0] && mEmailDuplicatedCheck){
                    binding.signUpEmailLine.setBackgroundColor(Color.parseColor(mOkColor))
                } else {
                    if(binding.signUpEmailLine.visibility != View.VISIBLE) binding.signUpEmailLine.setBackgroundColor(Color.parseColor(mOkColor))
                    else binding.signUpEmailLine.setBackgroundColor(Color.parseColor(mErrorColor))
                }
                binding.signUpEmailLine.visibility = View.VISIBLE
                binding.signUpEmailChecked.visibility = View.INVISIBLE
                mNowEditText = 1
            }
            if(!hasFocus){
                if(binding.signUpPasswordErrorParent.visibility != View.VISIBLE && binding.signUpPasswordInfo.visibility != View.VISIBLE){
                    binding.signUpPasswordLine.visibility = View.INVISIBLE
                }
                if(checkedStr(binding.signUpEmailText.text.toString(), patternEmail)){
                    // 통과
                    binding.signUpEmailLine.visibility = View.INVISIBLE
                    binding.signUpEmailError.visibility = View.GONE
                    binding.signUpEmailChecked.visibility = View.VISIBLE
                    mSignUpChecked[0] = true
                    binding.signUpEmailDuplicateLogin.visibility = View.GONE
                    binding.signUpEmailDuplicatePasswordFind.visibility = View.GONE
                    // 이메일 중복 여부 체크를 위한 서버 통신
                    SignUpService(this).tryGetEmailDuplicated(binding.signUpEmailText.text.toString())
                } else {
                    // 불통
                    binding.signUpEmailLine.setBackgroundColor(Color.parseColor(mErrorColor))
                    binding.signUpEmailError.visibility = View.VISIBLE
                    binding.signUpEmailChecked.visibility = View.INVISIBLE
                    if(binding.signUpEmailText.text.toString() == "") binding.signUpEmailError.setText(R.string.Sign_up_email_error2)
                    else binding.signUpEmailError.setText(R.string.Sign_up_email_error1)
                    mSignUpChecked[0] = false
                    binding.signUpEmailDuplicateLogin.visibility = View.GONE
                    binding.signUpEmailDuplicatePasswordFind.visibility = View.GONE
                }
                mNowEditText = 0
            }

        }

        // 이메일 중복 시 로그인 하러 가기
        binding.signUpEmailDuplicateLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 비밀번호 검증
        binding.signUpPasswordText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                if(binding.signUpPasswordLine.visibility != View.VISIBLE){
                    binding.signUpPasswordLine.setBackgroundColor(Color.parseColor(mOkColor))
                }
                passwordChangeLook()
                binding.signUpPasswordLine.visibility = View.VISIBLE
                if(binding.signUpPasswordInfo.visibility == View.GONE && binding.signUpPasswordErrorParent.visibility == View.GONE){
                    binding.signUpPasswordErrorParent.visibility = View.VISIBLE
                }
                mNowEditText = 2
            } else {
                mNowEditText = 0
                if(mSignUpChecked[1]) {
                    binding.signUpPasswordLine.visibility = View.INVISIBLE
                    binding.signUpPasswordLookImg.setImageResource(R.drawable.ic_sign_up_checked)
                    binding.signUpPasswordTextLook.visibility = View.GONE
                }else if(binding.signUpPasswordText.text.toString() == ""){
                    binding.signUpPasswordLine.visibility = View.INVISIBLE
                }
            }
        }
        binding.signUpPasswordText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = binding.signUpPasswordText.text.toString()
                var passwordCheckedNum = 0
                if(mPasswordLookImg){
                    if(binding.signUpPasswordTextLook.visibility == View.VISIBLE) binding.signUpPasswordTextLook.visibility = View.GONE
                } else {
                    // 눈 감을 때 보여줘야함
                    if(mSignUpChecked[1]) binding.signUpPasswordTextLook.visibility = View.GONE
                    else if(binding.signUpPasswordTextLook.visibility != View.VISIBLE) binding.signUpPasswordTextLook.visibility = View.VISIBLE
                    binding.signUpPasswordTextLook.text = binding.signUpPasswordText.text
                }
                if(password == ""){
                    binding.signUpPasswordError1Img.setImageResource(R.drawable.sign_up_password_error)
                    binding.signUpPasswordError1Text.setTextColor(Color.parseColor(mErrorColor))
                    binding.signUpPasswordError2Img.setImageResource(R.drawable.sign_up_password_error)
                    binding.signUpPasswordError2Text.setTextColor(Color.parseColor(mErrorColor))
                    binding.signUpPasswordError3Img.setImageResource(R.drawable.sign_up_password_error)
                    binding.signUpPasswordError3Text.setTextColor(Color.parseColor(mErrorColor))
                    binding.signUpPasswordTextLook.visibility = View.GONE
                }
                else {
                    if(checkedPasswordError1(password)) {
                        passwordCheckedNum++
                        binding.signUpPasswordError1Img.setImageResource(R.drawable.ic_sign_up_password_checked)
                        binding.signUpPasswordError1Text.setTextColor(Color.parseColor(mPasswordCheckedTextColor))
                    }
                    else {
                        binding.signUpPasswordError1Img.setImageResource(R.drawable.sign_up_password_error)
                        binding.signUpPasswordError1Text.setTextColor(Color.parseColor(mErrorColor))
                    }
                    if(checkedPasswordError2(password)) {
                        passwordCheckedNum++
                        binding.signUpPasswordError2Img.setImageResource(R.drawable.ic_sign_up_password_checked)
                        binding.signUpPasswordError2Text.setTextColor(Color.parseColor(mPasswordCheckedTextColor))
                    }
                    else {
                        binding.signUpPasswordError2Img.setImageResource(R.drawable.sign_up_password_error)
                        binding.signUpPasswordError2Text.setTextColor(Color.parseColor(mErrorColor))
                    }
                    if(checkedPasswordError3(password)) {
                        passwordCheckedNum++
                        binding.signUpPasswordError3Img.setImageResource(R.drawable.ic_sign_up_password_checked)
                        binding.signUpPasswordError3Text.setTextColor(Color.parseColor(mPasswordCheckedTextColor))
                    }
                    else {
                        binding.signUpPasswordError3Img.setImageResource(R.drawable.sign_up_password_error)
                        binding.signUpPasswordError3Text.setTextColor(Color.parseColor(mErrorColor))
                    }
                }
                if(passwordCheckedNum == 3){
                    binding.signUpPasswordLine.setBackgroundColor(Color.parseColor(mOkColor))
                    binding.signUpPasswordErrorParent.visibility = View.GONE
                    binding.signUpPasswordInfo.visibility = View.VISIBLE
                    binding.signUpPasswordLookImg.setImageResource(R.drawable.ic_sign_up_checked)
                    mSignUpChecked[1] = true
                } else {
                    binding.signUpPasswordLine.setBackgroundColor(Color.parseColor(mErrorColor))
                    binding.signUpPasswordErrorParent.visibility = View.VISIBLE
                    binding.signUpPasswordInfo.visibility = View.GONE
                    passwordChangeLook()
                    mSignUpChecked[1] = false
                }

            }
            override fun afterTextChanged(s: Editable?) {}
        })
        // 비밀번호 숨기기 옵션
        binding.signUpPasswordLookImg.setOnClickListener {
            mPasswordLookImg = !mPasswordLookImg

            if(!mSignUpChecked[1]){
                passwordChangeLook()
                if(mPasswordLookImg){
                    if(binding.signUpPasswordText.text.toString() != "" && binding.signUpPasswordTextLook.visibility == View.VISIBLE){
                        binding.signUpPasswordTextLook.visibility = View.GONE
                    }
                } else {
                    // 숫자 보여짐
                    if(binding.signUpPasswordText.text.toString() != "" && binding.signUpPasswordTextLook.visibility == View.GONE){
                        binding.signUpPasswordTextLook.visibility = View.VISIBLE
                        binding.signUpPasswordTextLook.text = binding.signUpPasswordText.text
                    }
                }
            }
        }
        // 이름 검증 -> 한글, 영어 소문자, 대문자 허용 1글자 이상 허용
        val patternName = Pattern.compile("^[a-zA-Z가-힣]*$")
        binding.signUpNameText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                if(mSignUpChecked[2]) binding.signUpNameLine.setBackgroundColor(
                    Color.parseColor(
                        mOkColor
                    )
                )
                else {
                    if(binding.signUpNameLine.visibility != View.VISIBLE)
                        binding.signUpNameLine.setBackgroundColor(Color.parseColor(mOkColor))
                    else
                        binding.signUpNameLine.setBackgroundColor(Color.parseColor(mErrorColor))
                }
                binding.signUpNameLine.visibility = View.VISIBLE
                binding.signUpNameChecked.visibility = View.INVISIBLE
                mNowEditText = 3
            } else {
                // 포커스 벗어남
                if(checkedStr(binding.signUpNameText.text.toString(), patternName) && binding.signUpNameText.text.toString().length > 1){
                    // 검증 설공
                    binding.signUpNameLine.visibility = View.INVISIBLE
                    binding.signUpNameChecked.visibility = View.VISIBLE
                    binding.signUpNameError.visibility = View.GONE
                    mSignUpChecked[2] = true
                } else {
                    // 검증 실패
                    binding.signUpNameError.visibility = View.VISIBLE
                    binding.signUpNameLine.setBackgroundColor(Color.parseColor(mErrorColor))
                    mSignUpChecked[2] = false
                }
                mNowEditText = 0
            }
        }

        // 핸드폰 번호 검증
        val patternPhone = Pattern.compile("^01(?:0|1|[6-9])[0-9]{4}[0-9]{4}\$")
        binding.signUpPhoneText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                if(mSignUpChecked[3]) binding.signUpPhoneLine.setBackgroundColor(
                    Color.parseColor(
                        mOkColor
                    )
                )
                else {
                    if(binding.signUpPhoneLine.visibility != View.VISIBLE) binding.signUpPhoneLine.setBackgroundColor(
                        Color.parseColor(
                            mOkColor
                        )
                    )
                    else binding.signUpPhoneLine.setBackgroundColor(Color.parseColor(mErrorColor))
                }
                binding.signUpPhoneLine.visibility = View.VISIBLE
                binding.signUpPhoneChecked.visibility = View.INVISIBLE
                mNowEditText = 4
            } else {
                // 포커스 벗어남
                val phone = binding.signUpPhoneText.text.toString()
                if(checkedStr(phone, patternPhone)){
                    // 검증 성공
                    binding.signUpPhoneDuplicateCertification.visibility = View.GONE
                    binding.signUpPhoneLine.visibility = View.INVISIBLE
                    binding.signUpPhoneChecked.visibility = View.VISIBLE
                    binding.signUpPhoneError.visibility = View.GONE
                    // 핸드폰 번호 겹치는 거 확인 -> 서버 통신
                    SignUpService(this).tryGetPhoneDuplicated(phone)
                // mSignUpChecked[3] = true
                } else {
                    // 검증 실패
                    binding.signUpPhoneDuplicateCertification.visibility = View.GONE
                    binding.signUpPhoneError.visibility = View.VISIBLE
                    binding.signUpPhoneLine.setBackgroundColor(Color.parseColor(mErrorColor))
                    if (phone == "") binding.signUpPhoneError.setText(R.string.Sign_up_phone_error2)
                    else binding.signUpPhoneError.setText(R.string.Sign_up_phone_error1)
                    mSignUpChecked[3] = false
                }
                mNowEditText = 0
            }
        }

        // agree 체크
        binding.signUpAgreeAll.setOnClickListener {
            if(mSignUpAgreeAllChecked){
                // 전체 취소
                agreeAllChange(R.drawable.ic_agree_not_checked, false)
                mSignUpAgreeCheckedNum = 0
                mSignUpAgreeAllChecked = false
                binding.signUpAgreeAllImg.setImageResource(R.drawable.ic_agree_not_checked)
            } else {
                // 전체 동의
                agreeAllChange(R.drawable.ic_agree_checked, true)
                mSignUpAgreeCheckedNum = 5
                mSignUpAgreeAllChecked = true
                binding.signUpAgreeAllImg.setImageResource(R.drawable.ic_agree_checked)
            }
        }
        binding.signUpAgreeOne.setOnClickListener { agreeChange(0) }
        binding.signUpAgreeTwo.setOnClickListener { agreeChange(1) }
        binding.signUpAgreeThree.setOnClickListener { agreeChange(2) }
        binding.signUpAgreeFour.setOnClickListener { agreeChange(3) }
        binding.signUpAgreeFive.setOnClickListener { agreeChange(4) }

        // 서버로 회원가입 정보 보냄
        binding.signUpSend.setOnClickListener {
            val email = binding.signUpEmailText.text.toString()
            val password = binding.signUpPasswordText.text.toString()
            val userName = binding.signUpNameText.text.toString()
            val phone = binding.signUpPhoneText.text.toString()
            val userSignUpRequest = UserSignUpRequest(email, password, userName, phone)
            Log.d("signup info", "password : $password")
            showLoadingDialog(this)
            SignUpService(this).tryPostSignUp(userSignUpRequest)
            if(checkSignUpReady()){

            }
        }

        // editText 외부 선택 시 focus clear 밑 키보드 숨기기
        binding.signUpParent.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP){
                Log.d("name", "click")
                when(mNowEditText){
                    1 -> {
                        // email
                        imm.hideSoftInputFromWindow(binding.signUpEmailText.windowToken, 0)
                        binding.signUpEmailText.clearFocus()
                    }
                    2 -> {
                        // password
                        imm.hideSoftInputFromWindow(binding.signUpPasswordText.windowToken, 0)
                        binding.signUpPasswordText.clearFocus()
                    }
                    3 -> {
                        // name
                        imm.hideSoftInputFromWindow(binding.signUpNameText.windowToken, 0)
                        binding.signUpNameText.clearFocus()
                    }
                    4 -> {
                        // phone
                        imm.hideSoftInputFromWindow(binding.signUpPhoneText.windowToken, 0)
                        binding.signUpPhoneText.clearFocus()
                    }
                }
            }
            true
        }

        // 뒤로 가기
        binding.signUpBack.setOnClickListener { finish() }

    }

    fun agreeAllChange(resource : Int, agree : Boolean) {
        binding.signUpAgreeOneImg.setImageResource(resource)
        binding.signUpAgreeTwoImg.setImageResource(resource)
        binding.signUpAgreeThreeImg.setImageResource(resource)
        binding.signUpAgreeFourImg.setImageResource(resource)
        binding.signUpAgreeFiveImg.setImageResource(resource)
        for(i in 0..4) mSignUpAgreeChecked[i] = agree
    }

    fun agreeChange(index: Int){
        mSignUpAgreeChecked[index] = !mSignUpAgreeChecked[index]
        if(mSignUpAgreeChecked[index]) {
            // 미동의 -> 동의
            when (index) {
                0 -> { binding.signUpAgreeOneImg.setImageResource(R.drawable.ic_agree_checked) }
                1 -> { binding.signUpAgreeTwoImg.setImageResource(R.drawable.ic_agree_checked) }
                2 -> { binding.signUpAgreeThreeImg.setImageResource(R.drawable.ic_agree_checked) }
                3 -> { binding.signUpAgreeFourImg.setImageResource(R.drawable.ic_agree_checked) }
                4 -> { binding.signUpAgreeFiveImg.setImageResource(R.drawable.ic_agree_checked) }
            }
            mSignUpAgreeCheckedNum++
        } else {
            // 동의 -> 미동의
            when (index) {
                0 -> { binding.signUpAgreeOneImg.setImageResource(R.drawable.ic_agree_not_checked) }
                1 -> { binding.signUpAgreeTwoImg.setImageResource(R.drawable.ic_agree_not_checked) }
                2 -> { binding.signUpAgreeThreeImg.setImageResource(R.drawable.ic_agree_not_checked) }
                3 -> { binding.signUpAgreeFourImg.setImageResource(R.drawable.ic_agree_not_checked) }
                4 -> { binding.signUpAgreeFiveImg.setImageResource(R.drawable.ic_agree_not_checked) }
            }
            mSignUpAgreeCheckedNum--
        }
        if(mSignUpAgreeCheckedNum == 5){
            binding.signUpAgreeError.visibility = View.GONE
            binding.signUpAgreeAllImg.setImageResource(R.drawable.ic_agree_checked)
            mSignUpAgreeAllChecked = true
            binding.signUpAgreeError.visibility = View.GONE
        }
        if(mSignUpAgreeCheckedNum < 5){
            binding.signUpAgreeAllImg.setImageResource(R.drawable.ic_agree_not_checked)
            mSignUpAgreeAllChecked = false
        }
    }

    private fun checkedStr(str: String, ps: Pattern) : Boolean = ps.matcher(str).matches()

    fun checkedPasswordError1(target: String): Boolean {
        // 영문/숫자/특수문자 2가지 이상 조합(8~20자)
        val regexEnglishAndNum = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$"
        val regexEnglishAndSpecial = "^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,20}$"
        val regexSpecialAndNum = "^(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,20}$"
        val pattern1 = Pattern.compile(regexEnglishAndNum, Pattern.CASE_INSENSITIVE)
        val pattern2 = Pattern.compile(regexEnglishAndSpecial, Pattern.CASE_INSENSITIVE)
        val pattern3 = Pattern.compile(regexSpecialAndNum, Pattern.CASE_INSENSITIVE)

        val result1 = pattern1.matcher(target).find()
        val result2 = pattern2.matcher(target).find()
        val result3 = pattern3.matcher(target).find()

        return result1 || result2 || result3
    }

    fun checkedPasswordError2(target: String): Boolean {
        return !(Pattern.compile("(\\w)\\1\\1").matcher(target).find())
    }
    fun checkedPasswordError3(target: String): Boolean {
        return !(mSignUpChecked[0] && target == binding.signUpEmailText.text.toString())
    }

    fun passwordChangeLook(){
        if(mPasswordLookImg)
            binding.signUpPasswordLookImg.setImageResource(R.drawable.ic_sign_up_password_hide)
        else
            binding.signUpPasswordLookImg.setImageResource(R.drawable.ic_sign_up_password_look)
    }
    override fun onPostSignUpSuccess(response: UserSignUpResponse) {
        // 회원가입 성공
        dismissLoadingDialog()
        if(response.code == 1000){
            showCustomToast("회원가입 성공 -> userIdx : ${response.userSignUpResponseResult!!.userIdx}")
            finish()
        } else {
            showCustomToast("회원가입 실패")
        }
    }

    override fun onPostSignUpFailure(message: String) {
        // 회원가입 실패
        dismissLoadingDialog()
        showCustomToast("회원가입 실패 error message : $message")
    }

    override fun onGetEmailDuplicatedSuccess(response: EmailDuplicatedResponse) {
        // 이메일 중복 여부 판단 성공
        if(response.code == 1000){
            showCustomToast("이메일 중복 여부 판단 성공 ${response.result.isDuplicated}")
            Log.d("network check value", "isDuplicated 값 확인 : ${response.result.isDuplicated}")
            mEmailDuplicatedCheck = response.result.isDuplicated != "Y"
            if(!mEmailDuplicatedCheck){
                // 이메일 중복
                binding.signUpEmailDuplicateLogin.visibility = View.VISIBLE
                binding.signUpEmailDuplicatePasswordFind.visibility = View.VISIBLE
                binding.signUpEmailError.setText(R.string.sign_up_email_duplicate)
                binding.signUpEmailError.visibility = View.VISIBLE
                binding.signUpEmailLine.visibility = View.VISIBLE
                binding.signUpEmailLine.setBackgroundColor(Color.parseColor(mErrorColor))
                binding.signUpEmailChecked.visibility = View.INVISIBLE
                mEmailDuplicatedCheck = false
            } else mEmailDuplicatedCheck = true
        }
    }

    override fun onGetEmailDuplicatedFailure(message: String) {
        // 이메일 중복 여부 판단 실패
        showCustomToast("EmailDuplicated 통신 오류")
    }

    override fun onGetPhoneDuplicatedSuccess(response: PhoneDuplicatedResponse) {
        // 핸드폰 번호 중복 여부 판단 성공
        if(response.code == 1000){
            showCustomToast("핸드폰 중복 여부 판단 성공")
            Log.d("duplicated", "phone : ${response.result.isDuplicated}")
            if(response.result.isDuplicated == "Y"){
                // 중복됨
                mSignUpChecked[3] = false
                binding.signUpPhoneChecked.visibility = View.INVISIBLE
                binding.signUpPhoneError.visibility = View.VISIBLE
                binding.signUpPhoneLine.visibility = View.VISIBLE
                binding.signUpPhoneLine.setBackgroundColor(Color.parseColor(mErrorColor))
                val phoneDuplicated = response.result.duplicatedEmail + " 아이디(이메일)로 가입된 휴대혼 번호입니다."
                binding.signUpPhoneError.text = phoneDuplicated
                binding.signUpPhoneDuplicateCertification.visibility = View.VISIBLE
            } else {
                mSignUpChecked[3] = true
            }
        } else {
            showCustomToast(response.message ?: "핸드폰 중복 여부 판단 실패")
        }
    }

    override fun onGetPhoneDuplicatedFailure(message: String) {
        // 핸드폰 번호 중복 여부 판단 실패
        showCustomToast("PhoneDuplicated 통신 오류")
    }

    fun checkSignUpReady(): Boolean {
        var check : Int = 0
        var targetError = -1
        if(!mSignUpAgreeAllChecked){
            check += 1
            binding.signUpAgreeError.visibility = View.VISIBLE
        }
        if(!mSignUpChecked[0]){
            if(targetError == -1) targetError = 0
            if(binding.signUpEmailError.visibility == View.GONE){
                // 이메일 오류 표시
                binding.signUpEmailLine.setBackgroundColor(Color.parseColor(mErrorColor))
                binding.signUpEmailError.visibility = View.VISIBLE
                binding.signUpEmailChecked.visibility = View.INVISIBLE
                binding.signUpEmailLine.visibility = View.VISIBLE
                if(binding.signUpEmailText.text.toString() == "") binding.signUpEmailError.setText(R.string.Sign_up_email_error2)
                else binding.signUpEmailError.setText(R.string.Sign_up_email_error1)
                mSignUpChecked[0] = false
                binding.signUpEmailDuplicateLogin.visibility = View.GONE
                binding.signUpEmailDuplicatePasswordFind.visibility = View.GONE
            }
        } else check += 1
        if(!mSignUpChecked[1]){
            if(targetError == -1) targetError = 1
            if(binding.signUpPasswordInfo.visibility == View.GONE && binding.signUpPasswordErrorParent.visibility == View.GONE){
                // 비밀번호 오류 표시
                binding.signUpPasswordError1Img.setImageResource(R.drawable.sign_up_password_error)
                binding.signUpPasswordError1Text.setTextColor(Color.parseColor(mErrorColor))
                binding.signUpPasswordError2Img.setImageResource(R.drawable.sign_up_password_error)
                binding.signUpPasswordError2Text.setTextColor(Color.parseColor(mErrorColor))
                binding.signUpPasswordError3Img.setImageResource(R.drawable.sign_up_password_error)
                binding.signUpPasswordError3Text.setTextColor(Color.parseColor(mErrorColor))
                binding.signUpPasswordTextLook.visibility = View.GONE
                binding.signUpPasswordErrorParent.visibility = View.VISIBLE
                binding.signUpPasswordLine.visibility = View.VISIBLE
                binding.signUpPasswordLine.setBackgroundColor(Color.parseColor(mErrorColor))
                Log.d("passworderror", "오류 맞음!!")
            }
        } else check += 1
        if(!mSignUpChecked[2]){
            if(targetError == -1) targetError = 2
            if(binding.signUpNameError.visibility == View.GONE){
                // 이름 오류 표시
                binding.signUpNameError.visibility = View.VISIBLE
                binding.signUpNameLine.visibility = View.VISIBLE
                binding.signUpNameLine.setBackgroundColor(Color.parseColor(mErrorColor))
                mSignUpChecked[2] = false
            }
        } else check += 1
        if(!mSignUpChecked[3]){
            if(targetError == -1) targetError = 3
            if(binding.signUpPhoneError.visibility == View.GONE){
                // 핸드폰 오류
                binding.signUpPhoneDuplicateCertification.visibility = View.GONE
                binding.signUpPhoneError.visibility = View.VISIBLE
                binding.signUpPhoneLine.visibility = View.VISIBLE
                binding.signUpPhoneLine.setBackgroundColor(Color.parseColor(mErrorColor))
                binding.signUpPhoneError.setText(R.string.Sign_up_phone_error2)
            }
        } else check += 1


        if(targetError != -1) {
            when (targetError) {
                1 -> binding.signUpEmailText.requestFocus()
                2 -> binding.signUpPasswordText.requestFocus()
                3 -> binding.signUpNameText.requestFocus()
                4 -> binding.signUpPhoneText.requestFocus()
            }
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
        return check == 5
    }
}