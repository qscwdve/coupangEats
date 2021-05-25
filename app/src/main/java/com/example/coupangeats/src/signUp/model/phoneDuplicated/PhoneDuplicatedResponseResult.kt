package com.example.coupangeats.src.signUp.model.phoneDuplicated

import com.google.gson.annotations.SerializedName

data class PhoneDuplicatedResponseResult(
    @SerializedName("isDuplicated") val isDuplicated : Boolean,
    @SerializedName("duplicatedEmail") val duplicatedEmail : String?
)
