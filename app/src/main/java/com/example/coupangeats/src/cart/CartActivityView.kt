package com.example.coupangeats.src.cart

import com.example.coupangeats.src.cart.model.CartMenuLookResponse
import com.example.coupangeats.src.cart.model.OrderResponse

interface CartActivityView {
    fun onGetCartLookSuccess(response: CartMenuLookResponse)
    fun onGetCartLookFailure(message: String)

    fun onPostOrderSuccess(response: OrderResponse)
    fun onPostOrderFailure(message: String)

}