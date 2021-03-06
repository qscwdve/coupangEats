package com.example.coupangeats.src.detailSuper

import com.example.coupangeats.src.detailSuper.model.BookMarkAddResponse
import com.example.coupangeats.src.detailSuper.model.CouponSaveResponse
import com.example.coupangeats.src.detailSuper.model.SuperResponse
import com.softsquared.template.kotlin.config.BaseResponse


interface DetailSuperActivityView {
    fun onGetSuperInfoSuccess(response: SuperResponse)
    fun onGetSuperInfoFailure(message: String)

    fun onPostCouponSaveSuccess(response: CouponSaveResponse)
    fun onPostCouponSaveFailure(message: String)

    fun onPostBookMarkAddSuccess(response: BookMarkAddResponse)
    fun onPostBookMarkAddFailure(message: String)

    fun onPostFavoritesSuperDeleteSuccess(response: BaseResponse)
    fun onPostFavoritesSuperDeleteFailure(message: String)
}