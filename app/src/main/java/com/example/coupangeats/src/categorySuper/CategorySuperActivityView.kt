package com.example.coupangeats.src.categorySuper

import com.example.coupangeats.src.categorySuper.model.CategorySuperResponse
import com.example.coupangeats.src.main.search.model.SuperCategoryResponse

interface CategorySuperActivityView {
    fun onGetCategorySuperSuccess(response : CategorySuperResponse)
    fun onGetCategorySuperFailure(message: String)

    fun onGetSuperCategorySuccess(response: SuperCategoryResponse)
    fun onGetSuperCategoryFailure(message: String)
}