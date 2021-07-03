package com.example.coupangeats.src.detailSuper

import com.example.coupangeats.src.detailSuper.model.*
import com.example.coupangeats.src.favorites.model.FavoritesSuperDeleteRequest
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailSuperService(val view: DetailSuperActivityView) {

    fun tryGetSuperInfo(storesIdx: Int){
        val detailSuperRetrofitInterface = ApplicationClass.sRetrofit.create(
            DetailSuperRetrofitInterface::class.java)
        detailSuperRetrofitInterface.getSuperInfo(storesIdx)
            .enqueue(object : Callback<SuperResponse> {
                override fun onResponse(
                    call: Call<SuperResponse>,
                    response: Response<SuperResponse>
                ) {
                    view.onGetSuperInfoSuccess(response.body() as SuperResponse)
                }

                override fun onFailure(call: Call<SuperResponse>, t: Throwable) {
                    view.onGetSuperInfoFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPostCouponSave(storesIdx: Int, request: CouponSaveRequest){
        val detailSuperRetrofitInterface = ApplicationClass.sRetrofit.create(
            DetailSuperRetrofitInterface::class.java)
        detailSuperRetrofitInterface.postCouponSave(storesIdx, request)
            .enqueue(object : Callback<CouponSaveResponse>{
                override fun onResponse(
                    call: Call<CouponSaveResponse>,
                    response: Response<CouponSaveResponse>
                ) {
                    view.onPostCouponSaveSuccess(response.body() as CouponSaveResponse)
                }

                override fun onFailure(call: Call<CouponSaveResponse>, t: Throwable) {
                    view.onPostCouponSaveFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPostBookMarkAdd(request: BookMarkAddRequest){
        val detailSuperRetrofitInterface = ApplicationClass.sRetrofit.create(
            DetailSuperRetrofitInterface::class.java)
        detailSuperRetrofitInterface.postBookMarkAdd(request)
            .enqueue(object : Callback<BookMarkAddResponse>{
                override fun onResponse(
                    call: Call<BookMarkAddResponse>,
                    response: Response<BookMarkAddResponse>
                ) {
                    view.onPostBookMarkAddSuccess(response.body() as BookMarkAddResponse)
                }

                override fun onFailure(call: Call<BookMarkAddResponse>, t: Throwable) {
                    view.onPostBookMarkAddFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPostFavoritesSuperDelete(userIdx: Int, request: FavoritesSuperDeleteRequest){
        val detailSuperRetrofitInterface = ApplicationClass.sRetrofit.create(
            DetailSuperRetrofitInterface::class.java)
        detailSuperRetrofitInterface.postFavoritesSuperDelete(userIdx, request)
            .enqueue(object : Callback<BaseResponse>{
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    view.onPostFavoritesSuperDeleteSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onPostFavoritesSuperDeleteFailure(t.message ?: "통신 오류")
                }

            })
    }
}