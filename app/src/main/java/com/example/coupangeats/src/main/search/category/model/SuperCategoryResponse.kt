package com.example.coupangeats.src.main.search.category.model

import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class SuperCategoryResponse(
    @SerializedName("result") val result : ArrayList<SuperCategoryResponseResult>
): BaseResponse()
