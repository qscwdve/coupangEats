package com.example.coupangeats.src.cart.model

import com.google.gson.annotations.SerializedName

data class OrderResponseResult(
    @SerializedName("createdIdx") val createIdx: Int
)
