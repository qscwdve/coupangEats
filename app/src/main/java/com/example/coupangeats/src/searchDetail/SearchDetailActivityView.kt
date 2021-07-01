package com.example.coupangeats.src.searchDetail

import com.example.coupangeats.src.main.home.model.userCheckAddress.UserCheckResponse
import com.example.coupangeats.src.searchDetail.model.SearchDetailResponse

interface SearchDetailActivityView {
    fun onGetSearchSuperSuccess(response: SearchDetailResponse)
    fun onGetSearchSuperFailure(message: String)

}