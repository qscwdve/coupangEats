package com.example.coupangeats.src.deliveryAddressSetting

import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.DeliveryAddressResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.SearchAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponseResult
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse

interface DeliveryAddressSettingActivityView {
    fun onGetUserAddressListSuccess(response: UserAddrListResponse)
    fun onGetUserAddressListFailure(message: String)

    fun onPathUserCheckedAddressSuccess(response: UserCheckedAddressResponse)
    fun onPathUserCheckedAddressFailure(message: String)

    fun onGetSearchAddrListSuccess(response: SearchAddrListResponse)
    fun onGetSearchAddrListFailure(message: String)

    fun onPostDeliveryAddressAddSuccess(response: DeliveryAddressResponse)
    fun onPostDeliveryAddressAddFailure(message: String)
}