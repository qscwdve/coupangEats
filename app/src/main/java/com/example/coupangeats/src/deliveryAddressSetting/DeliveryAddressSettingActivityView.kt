package com.example.coupangeats.src.deliveryAddressSetting

import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponseResult
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse

interface DeliveryAddressSettingActivityView {
    fun onGetUserAddressListSuccess(response: UserAddrListResponse)
    fun onGetUserAddressListFailure(message: String)

    fun onPathUserCheckedAddressSuccess(response: UserCheckedAddressResponse)
    fun onPathUserCheckedAddressFailure(message: String)
}