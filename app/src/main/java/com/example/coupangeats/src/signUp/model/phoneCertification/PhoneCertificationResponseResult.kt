package com.example.coupangeats.src.signUp.model.phoneCertification

import com.google.gson.annotations.SerializedName

data class PhoneCertificationResponseResult(
    @SerializedName("phone") val phone: String,
    @SerializedName("authNumber") val authNumber: String
)
