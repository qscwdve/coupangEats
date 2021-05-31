package com.example.coupangeats.src.detailSuper

import com.example.coupangeats.src.detailSuper.model.CouponSaveResponse
import com.example.coupangeats.src.detailSuper.model.SuperResponse

interface DetailSuperActivityView {
    fun onGetSuperInfoSuccess(response: SuperResponse)
    fun onGetSuperInfoFailure(message: String)

    fun onPostCouponSaveSuccess(response: CouponSaveResponse)
    fun onPostCouponSaveFailure(message: String)
}