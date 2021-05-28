package com.example.coupangeats.src.deliveryAddressSetting.detail

import com.example.coupangeats.src.deliveryAddressSetting.detail.model.DeliveryAddressDeleteResponse
import com.example.coupangeats.src.deliveryAddressSetting.detail.model.DeliveryAddressDetailResponse
import com.example.coupangeats.src.deliveryAddressSetting.detail.model.DeliveryAddressModifyResponse
import com.example.coupangeats.src.deliveryAddressSetting.detail.model.HomeOrCompanyCheckResponse

interface DetailAddressFragmentView {
    fun onGetHomeOrCompanyCheckedSuccess(response: HomeOrCompanyCheckResponse)
    fun onGetHomeOrCompanyCheckedFailure(message: String)

    fun onDeliveryAddressDetailLookSuccess(response: DeliveryAddressDetailResponse)
    fun onDeliveryAddressDetailLookFailure(message: String)

    fun onDeliveryAddressDeleteSuccess(response: DeliveryAddressDeleteResponse)
    fun onDeliveryAddressDeleteFailure(message: String)

    fun onDeliveryAddressModifySuccess(response: DeliveryAddressModifyResponse)
    fun onDeliveryAddressModifyFailure(message: String)
}