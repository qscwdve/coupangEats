package com.example.coupangeats.src.map

import com.example.coupangeats.src.deliveryAddressSetting.DeliveryAddressSettingRetrofitInterface
import com.example.coupangeats.src.deliveryAddressSetting.model.DeliveryAddressAddRequest
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.DeliveryAddressResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapService(val view: MapActivityView) {

    fun tryPatchPathUserCheckedAddress(userIdx: Int, addressIdx: Int){
        val deliveryAddressSettingRetrofitInterface = ApplicationClass.sRetrofit.create(
            DeliveryAddressSettingRetrofitInterface::class.java)
        deliveryAddressSettingRetrofitInterface.pathUserCheckedAddress(userIdx, addressIdx)
            .enqueue(object : Callback<UserCheckedAddressResponse> {
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

    fun tryPostDeliveryAddressAdd(request: DeliveryAddressAddRequest){
        val mapActivityRetrofitInterface = ApplicationClass.sRetrofit.create(MapActivityRetrofitInterface::class.java)
        mapActivityRetrofitInterface.postDeliveryAddressAdd(request)
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