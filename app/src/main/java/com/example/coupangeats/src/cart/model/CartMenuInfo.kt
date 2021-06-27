package com.example.coupangeats.src.cart.model

data class CartMenuInfo(
    val id: Int?, val name:String, var num:Int, var price:Int, val sub: String?, val menuIdx: Int
)
