package com.example.coupangeats.src.SuperInfo

import com.example.coupangeats.src.SuperInfo.model.SuperInfoResponse

interface SuperInfoActivityView {
    fun onGetSuperInfoSuccess(response: SuperInfoResponse)
    fun onGetSuperInfoFailure(message: String)
}