package com.example.coupangeats.src.main.search.category

import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponse

interface CategorySearchFragmentView {
    fun onGetSuperCategorySuccess(response: SuperCategoryResponse)
    fun onGetSuperCategoryFailure(message: String)
}