package com.example.coupangeats.src.deliveryAddressSetting.detail

import com.example.coupangeats.src.deliveryAddressSetting.detail.model.*
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAddressService(val view: DetailAddressFragmentView) {

    fun tryGetHomeOrCompanyChecked(userIdx: Int, type: String){
        val detailAddressFragmentRetrofitInterface =
            ApplicationClass.sRetrofit.create(DetailAddressFragmentRetrofitInterface::class.java)
        detailAddressFragmentRetrofitInterface.getHomeOrCompanyChecked(userIdx, type)
            .enqueue(object : Callback<HomeOrCompanyCheckResponse>{
                override fun onResponse(
                    call: Call<HomeOrCompanyCheckResponse>,
                    response: Response<HomeOrCompanyCheckResponse>
                ) {
                    view.onGetHomeOrCompanyCheckedSuccess(response.body() as HomeOrCompanyCheckResponse)
                }

                override fun onFailure(call: Call<HomeOrCompanyCheckResponse>, t: Throwable) {
                    view.onGetHomeOrCompanyCheckedFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryDeliveryAddressDetailLook(userIdx: Int, addressIdx: Int){
        val detailAddressFragmentRetrofitInterface =
            ApplicationClass.sRetrofit.create(DetailAddressFragmentRetrofitInterface::class.java)
        detailAddressFragmentRetrofitInterface.getDeliveryAddressDetailLook(userIdx, addressIdx)
            .enqueue(object : Callback<DeliveryAddressDetailResponse>{
                override fun onResponse(
                    call: Call<DeliveryAddressDetailResponse>,
                    response: Response<DeliveryAddressDetailResponse>
                ) {
                    view.onDeliveryAddressDetailLookSuccess(response.body() as DeliveryAddressDetailResponse)
                }

                override fun onFailure(call: Call<DeliveryAddressDetailResponse>, t: Throwable) {
                    view.onDeliveryAddressDetailLookFailure(t.message ?: "통신 오류")
                }
            })

    }

    fun tryDeliveryAddressDelete(userIdx: Int, addressIdx: Int){
        val detailAddressFragmentRetrofitInterface =
            ApplicationClass.sRetrofit.create(DetailAddressFragmentRetrofitInterface::class.java)
        detailAddressFragmentRetrofitInterface.pathDeliveryAddressDelete(userIdx, addressIdx)
            .enqueue(object : Callback<DeliveryAddressDeleteResponse>{
                override fun onResponse(
                    call: Call<DeliveryAddressDeleteResponse>,
                    response: Response<DeliveryAddressDeleteResponse>
                ) {
                    view.onDeliveryAddressDeleteSuccess(response.body() as DeliveryAddressDeleteResponse)
                }

                override fun onFailure(call: Call<DeliveryAddressDeleteResponse>, t: Throwable) {
                    view.onDeliveryAddressDeleteFailure(t.message ?: "통신 오류")
                }
            })
    }

    fun tryDeliveryAddressModify(userIdx: Int, addressIdx: Int, request: DeliveryAddressModifyRequest){
        val detailAddressFragmentRetrofitInterface =
            ApplicationClass.sRetrofit.create(DetailAddressFragmentRetrofitInterface::class.java)
        detailAddressFragmentRetrofitInterface.pathDeliveryAddressModify(userIdx, addressIdx, request)
            .enqueue(object : Callback<DeliveryAddressModifyResponse>{
                override fun onResponse(
                    call: Call<DeliveryAddressModifyResponse>,
                    response: Response<DeliveryAddressModifyResponse>
                ) {
                    view.onDeliveryAddressModifySuccess(response.body() as DeliveryAddressModifyResponse)
                }

                override fun onFailure(call: Call<DeliveryAddressModifyResponse>, t: Throwable) {
                    view.onDeliveryAddressModifyFailure(t.message ?: "통신 오류")
                }

            })
    }
}