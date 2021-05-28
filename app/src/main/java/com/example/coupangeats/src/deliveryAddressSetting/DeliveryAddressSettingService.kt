package com.example.coupangeats.src.deliveryAddressSetting

import com.example.coupangeats.src.deliveryAddressSetting.model.DeliveryAddressAddRequest
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.DeliveryAddressResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.SearchAddrListRequest
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.SearchAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserAddrListResponseResult
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryAddressSettingService(val view: DeliveryAddressSettingActivityView) {

    fun tryGetUserAddressList(userIdx: Int) {
        val deliveryAddressSettingRetrofitInterface = ApplicationClass.sRetrofit.create(DeliveryAddressSettingRetrofitInterface::class.java)
        deliveryAddressSettingRetrofitInterface.getUserAddressList(userIdx)
            .enqueue(object : Callback<UserAddrListResponse>{
                override fun onResponse(
                    call: Call<UserAddrListResponse>,
                    response: Response<UserAddrListResponse>
                ) {
                    view.onGetUserAddressListSuccess(response = response.body() as UserAddrListResponse)
                }

                override fun onFailure(call: Call<UserAddrListResponse>, t: Throwable) {
                    view.onGetUserAddressListFailure(t.message ?: "통신 오류")
                }
            })
    }

    fun tryPatchPathUserCheckedAddress(userIdx: Int, addressIdx: Int){
        val deliveryAddressSettingRetrofitInterface = ApplicationClass.sRetrofit.create(DeliveryAddressSettingRetrofitInterface::class.java)
        deliveryAddressSettingRetrofitInterface.pathUserCheckedAddress(userIdx, addressIdx)
            .enqueue(object : Callback<UserCheckedAddressResponse>{
                override fun onResponse(
                    call: Call<UserCheckedAddressResponse>,
                    response: Response<UserCheckedAddressResponse>
                ) {
                    view.onPathUserCheckedAddressSuccess(response = response.body() as UserCheckedAddressResponse)
                }

                override fun onFailure(call: Call<UserCheckedAddressResponse>, t: Throwable) {
                    view.onPathUserCheckedAddressFailure(t.message ?: "통신 오류")
                }

            })
    }

    // 검색 API
    fun tryGetSearchAddrList(request: SearchAddrListRequest){
        val deliveryAddressSettingRetrofitInterface = ApplicationClass.searchRetrofit.create(DeliveryAddressSettingRetrofitInterface::class.java)
        deliveryAddressSettingRetrofitInterface.getSearchAddrList(ApplicationClass.SEARCH_API_KEY, request.currentPage, request.countPerPage, request.keyword)
            .enqueue(object : Callback<SearchAddrListResponse>{
                override fun onResponse(
                    call: Call<SearchAddrListResponse>,
                    response: Response<SearchAddrListResponse>
                ) {
                    view.onGetSearchAddrListSuccess(response = response.body() as SearchAddrListResponse)
                }

                override fun onFailure(call: Call<SearchAddrListResponse>, t: Throwable) {
                    view.onGetSearchAddrListFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPostDeliveryAddressAdd(request: DeliveryAddressAddRequest){
        val deliveryAddressSettingRetrofitInterface = ApplicationClass.sRetrofit.create(DeliveryAddressSettingRetrofitInterface::class.java)
        deliveryAddressSettingRetrofitInterface.postDeliveryAddressAdd(request)
            .enqueue(object : Callback<DeliveryAddressResponse>{
                override fun onResponse(
                    call: Call<DeliveryAddressResponse>,
                    response: Response<DeliveryAddressResponse>
                ) {
                    view.onPostDeliveryAddressAddSuccess(response.body() as DeliveryAddressResponse)
                }
                override fun onFailure(call: Call<DeliveryAddressResponse>, t: Throwable) {
                    view.onPostDeliveryAddressAddFailure(t.message ?: "통신 오류")
                }
            })
    }
}