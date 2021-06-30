package com.example.coupangeats.src.favorites

import com.example.coupangeats.src.favorites.model.FavoritesInfoResponse
import com.example.coupangeats.src.favorites.model.FavoritesSuperDeleteRequest
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesService(val view: FavoritesActivityView) {

    fun tryGetFavoritesInfoSort(userIdx: Int, sort: String){
        val favoritesRetrofitInterface = ApplicationClass.sRetrofit.create(FavoritesRetrofitInterface::class.java)
        favoritesRetrofitInterface.getFavoritesInfoSort(userIdx, sort)
            .enqueue(object : Callback<FavoritesInfoResponse>{
                override fun onResponse(
                    call: Call<FavoritesInfoResponse>,
                    response: Response<FavoritesInfoResponse>
                ) {
                    view.onGetFavoritesInfoSortSuccess(response.body() as FavoritesInfoResponse)
                }

                override fun onFailure(call: Call<FavoritesInfoResponse>, t: Throwable) {
                    view.onGetFavoritesInfoSortFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPostFavoritesSuperDelete(userIdx: Int, request: FavoritesSuperDeleteRequest){
        val favoritesRetrofitInterface = ApplicationClass.sRetrofit.create(FavoritesRetrofitInterface::class.java)
        favoritesRetrofitInterface.postFavoritesSuperDelete(userIdx, request)
            .enqueue(object : Callback<BaseResponse>{
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    view.onPostFavoritesSuperDeleteSuccess(response.body() as BaseResponse)
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    view.onGetFavoritesInfoSortFailure(t.message ?: "통신 오류")
                }
            })
    }
}