package com.example.coupangeats.src.main.myeats

import com.example.coupangeats.src.main.myeats.model.UserInfoResponse

interface MyeatsFragmentView {
    fun onGetUserInfoSuccess(response: UserInfoResponse)
    fun onGetUserInfoFailure(message: String)
}