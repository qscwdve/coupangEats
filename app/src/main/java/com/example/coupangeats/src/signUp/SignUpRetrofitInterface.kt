package com.example.coupangeats.src.signUp

import com.example.coupangeats.src.signUp.model.emailDuplicated.EmailDuplicatedResponse
import com.example.coupangeats.src.signUp.model.phoneCertification.PhoneCertificationRequest
import com.example.coupangeats.src.signUp.model.phoneCertification.PhoneCertificationResponse
import com.example.coupangeats.src.signUp.model.phoneDuplicated.PhoneDuplicatedResponse
import com.example.coupangeats.src.signUp.model.userSignUp.UserSignUpRequest
import com.example.coupangeats.src.signUp.model.userSignUp.UserSignUpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SignUpRetrofitInterface {
    @POST("/users")
    fun postSignUp(@Body params: UserSignUpRequest): Call<UserSignUpResponse>

    @GET("/users/email/check")
    fun getEmailDuplicated(@Query("email") email: String): Call<EmailDuplicatedResponse>

    @GET("/users/phone/check")
    fun getPhoneDuplicated(@Query("phone") phone: String): Call<PhoneDuplicatedResponse>

    @POST("/users/auth/phone")
    fun postPhoneCertification(@Body params: PhoneCertificationRequest) : Call<PhoneCertificationResponse>


}