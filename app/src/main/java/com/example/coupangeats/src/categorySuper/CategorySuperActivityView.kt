package com.example.coupangeats.src.categorySuper

import com.example.coupangeats.src.categorySuper.model.CategorySuperResponse
import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponse
import com.example.coupangeats.src.splash.model.AutoLoginResponse

interface CategorySuperActivityView {
    fun onGetCategorySuperSuccess(response : CategorySuperResponse)
    fun onGetCategorySuperFailure(message: String)

    fun onGetSuperCategorySuccess(response: SuperCategoryResponse)
    fun onGetSuperCategoryFailure(message: String)
}