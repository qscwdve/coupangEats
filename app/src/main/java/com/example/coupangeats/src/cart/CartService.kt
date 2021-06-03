package com.example.coupangeats.src.cart

import com.example.coupangeats.src.cart.model.CartMenuLookResponse
import com.example.coupangeats.src.cart.model.OrderRequest
import com.example.coupangeats.src.cart.model.OrderResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartService(val view: CartActivityView) {

    fun tryGetCartLook(userIdx: Int, storeIdx: Int){
        val cartActivityRetrofitInterface = ApplicationClass.sRetrofit.create(CartActivityRetrofitInterface::class.java)
        cartActivityRetrofitInterface.getCartLook(userIdx, storeIdx)
            .enqueue(object : Callback<CartMenuLookResponse>{
                override fun onResponse(
                    call: Call<CartMenuLookResponse>,
                    response: Response<CartMenuLookResponse>
                ) {
                    view.onGetCartLookSuccess(response.body() as CartMenuLookResponse)
                }

                override fun onFailure(call: Call<CartMenuLookResponse>, t: Throwable) {
                    view.onGetCartLookFailure(t.message ?: "통신 오류")
                }

            })
    }

    fun tryPostOrder(request: OrderRequest){
        val  cartActivityRetrofitInterface = ApplicationClass.sRetrofit.create(CartActivityRetrofitInterface::class.java)
        cartActivityRetrofitInterface.postOrder(request)
            .enqueue(object : Callback<OrderResponse>{
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    view.onPostOrderSuccess(response.body() as OrderResponse)
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    view.onPostOrderFailure(t.message ?: "통신 오류")
                }

            })
    }
}