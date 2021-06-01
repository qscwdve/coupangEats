package com.example.coupangeats.src.menuSelect.model

import com.google.gson.annotations.SerializedName

data class Option(
    @SerializedName("optionName") val optionName: String,
    @SerializedName("extraPrice") val extraPrive: Int
)
