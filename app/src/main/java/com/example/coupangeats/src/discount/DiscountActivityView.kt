package com.example.coupangeats.src.discount

import com.example.coupangeats.src.discount.model.ApplyCouponResponse
import com.example.coupangeats.src.discount.model.CouponInfoResponse

interface DiscountActivityView {
    fun onGetCouponInfoSuccess(response: CouponInfoResponse)
    fun onGetCouponInfoFailure(message: String)

    fun onPostApplyCouponSuccess(response: ApplyCouponResponse)
    fun onPostApplyCouponFailure(message: String)

    fun onGetMyEatsDiscountSuccess(response: CouponInfoResponse)
    fun onGetMyEatsDiscountFailure(message: String)
}