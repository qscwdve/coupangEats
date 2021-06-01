package com.example.coupangeats.src.menuSelect.model

import com.google.gson.annotations.SerializedName

data class MenuOption(
    @SerializedName("optionCategoryName") val optionCategoryName: String,
    @SerializedName("requiredChoiceFlag") val requiredChoiceFlag: String,
    @SerializedName("numberOfChoices") val numberOfChoices: Int,
    @SerializedName("options") val option: ArrayList<Option>?
)
