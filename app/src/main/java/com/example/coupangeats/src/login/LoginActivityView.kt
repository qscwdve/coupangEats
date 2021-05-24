package com.example.coupangeats.src.login

import com.example.coupangeats.src.login.model.UserLoginResponse

interface LoginActivityView {
    fun onPostLoginSuccess(response: UserLoginResponse)
    fun onPostLoginFailure(message: String)
}