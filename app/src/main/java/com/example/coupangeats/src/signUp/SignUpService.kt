package com.example.coupangeats.src.signUp

import com.example.coupangeats.src.signUp.model.emailDuplicated.EmailDuplicatedResponse
import com.example.coupangeats.src.signUp.model.phoneCertification.PhoneCertificationRequest
import com.example.coupangeats.src.signUp.model.phoneCertification.PhoneCertificationResponse
import com.example.coupangeats.src.signUp.model.phoneDuplicated.PhoneDuplicatedResponse
import com.example.coupangeats.src.signUp.model.userSignUp.UserSignUpRequest
import com.example.coupangeats.src.signUp.model.userSignUp.UserSignUpResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpService(val view: SignUpActivityView) {

    fun tryPostSignUp(userSignUpRequest: UserSignUpRequest) {
        val signUpRetrofitInterface = ApplicationClass.sRetrofit.create(SignUpRetrofitInterface::class.java)
        signUpRetrofitInterface.postSignUp(userSignUpRequest)
            .enqueue(object : Callback<UserSignUpResponse>{
                override fun onResponse(
                    call: Call<UserSignUpResponse>,
                    response: Response<UserSignUpResponse>
                ) {
                    view.onPostSignUpSuccess(response.body() as UserSignUpResponse)
                }

                override fun onFailure(call: Call<UserSignUpResponse>, t: Throwable) {
                    view.onPostSignUpFailure(t.message ?: "통신 오류")
                }
            })
    }

    fun tryGetEmailDuplicated(email: String){
        val signUpRetrofitInterface = ApplicationClass.sRetrofit.create(SignUpRetrofitInterface::class.java)
        signUpRetrofitInterface.getEmailDuplicated(email)
            .enqueue(object : Callback<EmailDuplicatedResponse>{
                override fun onResponse(
                    call: Call<EmailDuplicatedResponse>,
                    response: Response<EmailDuplicatedResponse>
                ) {
                    view.onGetEmailDuplicatedSuccess(response.body() as EmailDuplicatedResponse)
                }

                override fun onFailure(call: Call<EmailDuplicatedResponse>, t: Throwable) {
                    view.onGetEmailDuplicatedFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryGetPhoneDuplicated(phone: String){
        val signUpRetrofitInterface = ApplicationClass.sRetrofit.create(SignUpRetrofitInterface::class.java)
        signUpRetrofitInterface.getPhoneDuplicated(phone)
            .enqueue(object : Callback<PhoneDuplicatedResponse>{
                override fun onResponse(
                    call: Call<PhoneDuplicatedResponse>,
                    response: Response<PhoneDuplicatedResponse>
                ) {
                    view.onGetPhoneDuplicatedSuccess(response.body() as PhoneDuplicatedResponse)
                }

                override fun onFailure(call: Call<PhoneDuplicatedResponse>, t: Throwable) {
                    view.onGetPhoneDuplicatedFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPostPhoneCertification(request: PhoneCertificationRequest) {
        val signUpRetrofitInterface = ApplicationClass.sRetrofit.create(SignUpRetrofitInterface::class.java)
        signUpRetrofitInterface.postPhoneCertification(request)
            .enqueue(object : Callback<PhoneCertificationResponse>{
                override fun onResponse(
                    call: Call<PhoneCertificationResponse>,
                    response: Response<PhoneCertificationResponse>
                ) {
                    view.onPostPhoneCertificationSuccess(response.body() as PhoneCertificationResponse)
                }

                override fun onFailure(call: Call<PhoneCertificationResponse>, t: Throwable) {
                    view.onPostPhoneCertificationFailure(t.message ?: "통신 오류")
                }

            })

    }


}