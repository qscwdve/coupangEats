package com.example.coupangeats.src.main.home

import com.example.coupangeats.src.main.home.model.HomeInfo.HomeInfoResponse
import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponse

interface HomeFragmentView {
    fun onUserCheckAddressSuccess(response: UserCheckResponse)
    fun onUserCheckAddressFailure(message: String)

    fun onGetHomeDataSuccess(response: HomeInfoResponse)
    fun onGetHomeDataFailure(message: String)
}