package com.example.coupangeats.src.menuSelect

import com.example.coupangeats.src.menuSelect.model.MenuDetailResponse

interface MenuSelectActivityView {
    fun onGetMenuDetailSuccess(response: MenuDetailResponse)
    fun onGetMenuDetailFailure(message: String)
}