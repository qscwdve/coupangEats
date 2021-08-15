package com.example.coupangeats.src.main.search

import com.example.coupangeats.src.main.search.model.SuperCategoryResponse

interface CategorySearchFragmentView {
    fun onGetSuperCategorySuccess(response: SuperCategoryResponse)
    fun onGetSuperCategoryFailure(message: String)
}