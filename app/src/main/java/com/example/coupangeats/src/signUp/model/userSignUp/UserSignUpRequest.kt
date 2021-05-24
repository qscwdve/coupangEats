package com.example.coupangeats.src.signUp.model.userSignUp

import com.google.gson.annotations.SerializedName

data class UserSignUpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("phone") val phone: String
)
