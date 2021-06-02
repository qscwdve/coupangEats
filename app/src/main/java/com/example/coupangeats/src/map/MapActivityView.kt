package com.example.coupangeats.src.map

import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.DeliveryAddressResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse

interface MapActivityView {
    fun onPathUserCheckedAddressSuccess(response: UserCheckedAddressResponse)
    fun onPathUserCheckedAddressFailure(message: String)

    fun onPostDeliveryAddressAddSuccess(response: DeliveryAddressResponse)
    fun onPostDeliveryAddressAddFailure(message: String)
}