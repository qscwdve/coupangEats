package com.example.coupangeats.src.superSearch

import com.example.coupangeats.src.superSearch.model.DiscountSuperResponse
import com.example.coupangeats.src.superSearch.model.NewSuperResponse

interface SuperSearchActivityView {
    fun onGetNewSuperSuccess(response: NewSuperResponse)
    fun onGetNewSuperFailure(message: String)

    fun onGetDiscountSuperSuccess(response: DiscountSuperResponse)
    fun onGetDiscountSuperFailure(message: String)
}