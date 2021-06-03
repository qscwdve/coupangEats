package com.example.coupangeats.src.categorySuper

import com.example.coupangeats.src.categorySuper.model.CategorySuperResponse
import com.example.coupangeats.src.main.search.category.CategorySearchFragmentRetrofitInterface
import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Appendable

class CategorySuperService(val view: CategorySuperActivityView) {

    fun tryGetCategorySuper(lat:String, lon:String, category: String, sort:String, cheetah:String?, coupon: String?,
                            ordermin: Int?, delivery: Int?, cursor: Int=1, numOfRows: Int=10){
        val categorySuperActivityRetrofitInterface = ApplicationClass.sRetrofit.create(CategorySuperActivityRetrofitInterface::class.java)
        categorySuperActivityRetrofitInterface.getCategorySuper(lat, lon, category, sort, cheetah, coupon, ordermin, delivery, cursor, numOfRows)
            .enqueue(object : Callback<CategorySuperResponse>{
                override fun onResponse(
                    call: Call<CategorySuperResponse>,
                    response: Response<CategorySuperResponse>
                ) {
                    view.onGetCategorySuperSuccess(response = response.body() as CategorySuperResponse)
                }

                override fun onFailure(call: Call<CategorySuperResponse>, t: Throwable) {
                    view.onGetCategorySuperFailure(t.message ?: "통신 오류")
               }
            })
    }

    fun tryGetSuperCategory() {
        val categorySuperActivityRetrofitInterface = ApplicationClass.sRetrofit.create(CategorySuperActivityRetrofitInterface::class.java)
        categorySuperActivityRetrofitInterface.getSuperCategory()
            .enqueue(object : Callback<SuperCategoryResponse>{
                override fun onResponse(
                    call: Call<SuperCategoryResponse>,
                    response: Response<SuperCategoryResponse>
                ) {
                    view.onGetSuperCategorySuccess(response.body() as SuperCategoryResponse)
                }

                override fun onFailure(call: Call<SuperCategoryResponse>, t: Throwable) {
                    view.onGetSuperCategoryFailure(t.message ?: "통신 오류")
                }

            })
    }

}