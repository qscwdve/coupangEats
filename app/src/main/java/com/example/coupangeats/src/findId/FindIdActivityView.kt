package com.example.coupangeats.src.findId

import com.example.coupangeats.src.findId.model.FindIdResponse
import com.example.coupangeats.src.findId.model.FindEmailResponse

interface FindIdActivityView {
    fun onPostFindIdAuthNumberSuccess(response: FindIdResponse)
    fun onPostFindIdAuthNumberFailure(message: String)

    fun onGetFindEmailSuccess(response: FindEmailResponse)
    fun onGetFindEmailFailure(message: String)
}