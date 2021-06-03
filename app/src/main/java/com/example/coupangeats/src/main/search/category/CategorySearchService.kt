package com.example.coupangeats.src.main.search.category

import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategorySearchService(val viewCategory: CategorySearchFragmentView) {

    fun tryGetSuperCategory() {
        val searchFragmentRetrofitInterface = ApplicationClass.sRetrofit.create(
            CategorySearchFragmentRetrofitInterface::class.java)
        searchFragmentRetrofitInterface.getSuperCategory()
            .enqueue(object : Callback<SuperCategoryResponse>{
                override fun onResponse(
                    call: Call<SuperCategoryResponse>,
                    response: Response<SuperCategoryResponse>
                ) {
                    viewCategory.onGetSuperCategorySuccess(response.body() as SuperCategoryResponse)
                }

                override fun onFailure(call: Call<SuperCategoryResponse>, t: Throwable) {
                    viewCategory.onGetSuperCategoryFailure(t.message ?: "통신 오류")
                }

            })
    }


}