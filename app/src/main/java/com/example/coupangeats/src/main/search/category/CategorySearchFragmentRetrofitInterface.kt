package com.example.coupangeats.src.main.search.category

import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponse
import retrofit2.Call
import retrofit2.http.GET

interface CategorySearchFragmentRetrofitInterface {
    @GET("/stores/categories")
    fun getSuperCategory() : Call<SuperCategoryResponse>
}