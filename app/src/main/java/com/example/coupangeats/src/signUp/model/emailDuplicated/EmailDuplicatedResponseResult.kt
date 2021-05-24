package com.example.coupangeats.src.signUp.model.emailDuplicated

import com.google.gson.annotations.SerializedName

data class EmailDuplicatedResponseResult(
    @SerializedName("isDuplicated") val isDuplicated: Boolean
)
