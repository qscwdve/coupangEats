package com.example.coupangeats.src.main

import com.example.coupangeats.src.main.KaKao.KaKaoLoginResponse

interface MainActivityView {
    fun onPostKaKaoLoginSuccess(response: KaKaoLoginResponse)
    fun onPostKaKaoLoginFailure(message: String)
}