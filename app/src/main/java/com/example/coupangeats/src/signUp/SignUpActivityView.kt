package com.example.coupangeats.src.signUp

import com.example.coupangeats.src.signUp.model.emailDuplicated.EmailDuplicatedResponse
import com.example.coupangeats.src.signUp.model.phoneCertification.PhoneCertificationResponse
import com.example.coupangeats.src.signUp.model.phoneDuplicated.PhoneDuplicatedResponse
import com.example.coupangeats.src.signUp.model.userSignUp.UserSignUpResponse

interface SignUpActivityView {
    fun onPostSignUpSuccess(response: UserSignUpResponse)
    fun onPostSignUpFailure(message: String)

    fun onGetEmailDuplicatedSuccess(response: EmailDuplicatedResponse)
    fun onGetEmailDuplicatedFailure(message: String)

    fun onGetPhoneDuplicatedSuccess(response: PhoneDuplicatedResponse)
    fun onGetPhoneDuplicatedFailure(message: String)

    fun onPostPhoneCertificationSuccess(response: PhoneCertificationResponse)
    fun onPostPhoneCertificationFailure(message: String)


}