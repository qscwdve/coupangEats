package com.example.coupangeats.src.categorySuper

import com.example.coupangeats.src.categorySuper.model.CategorySuperResponse
import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CategorySuperActivityRetrofitInterface {
    @GET("/stores/category")
    fun getCategorySuper(@Query("lat") lat: String, @Query("lon") lon: String, @Query("category") category: String,
                         @Query("sort") sort: String, @Query("cheetah") cheetah: String?,
                         @Query("coupon") coupon: String?, @Query("ordermin") ordermin: Int?,
                         @Query("deliverymin") deliverymin: Int?, @Query("cursor") cursor: Int = 1,
                         @Query("numOfRows") numOfRows: Int = 10) : Call<CategorySuperResponse>

    @GET("/stores/categories")
    fun getSuperCategory() : Call<SuperCategoryResponse>
}