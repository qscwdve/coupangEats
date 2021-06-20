package com.example.coupangeats.src.detailSuper.detailSuperFragment

import com.example.coupangeats.src.detailSuper.detailSuperFragment.model.CouponSaveResponse
import com.example.coupangeats.src.detailSuper.detailSuperFragment.model.SuperResponse

interface DetailSuperActivityView {
    fun onGetSuperInfoSuccess(response: SuperResponse)
    fun onGetSuperInfoFailure(message: String)

    fun onPostCouponSaveSuccess(response: CouponSaveResponse)
    fun onPostCouponSaveFailure(message: String)
}