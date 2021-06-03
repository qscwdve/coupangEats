package com.example.coupangeats.src.splash

import com.example.coupangeats.src.splash.model.AutoLoginResponse

interface SplashActivityView {
    fun onGetAutoLoginSuccess(response: AutoLoginResponse)
    fun onGetAutoLoginFailure(message: String)
}