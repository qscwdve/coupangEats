package com.example.coupangeats.src.main.order.prepare.model

import com.example.coupangeats.src.main.order.model.prepareOrder
import com.google.gson.annotations.SerializedName
import com.softsquared.template.kotlin.config.BaseResponse

data class OrderPrepareInfoResponse(
    @SerializedName("result") val result: ArrayList<prepareOrder>?
): BaseResponse()
