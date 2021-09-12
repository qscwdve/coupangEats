package com.example.coupangeats.src.cart.adapter.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.coupangeats.databinding.DialogCartMenuManyBinding
import com.example.coupangeats.src.cart.CartActivity
import com.example.coupangeats.src.cart.adapter.CartMenuInfoAdatper

class CartMenuNumManyDialog(val cartMenuInfoAdatper: CartMenuInfoAdatper, val num: Int) : DialogFragment() {
    private lateinit var binding : DialogCartMenuManyBinding
    private var mNum = num
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCartMenuManyBinding.inflate(layoutInflater)
        // 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogCartMenuManyNumText.text = mNum.toString()
        binding.dialogCartMenuManyNumCancel.setOnClickListener { dismiss() }
        binding.dialogCartMenuManyNumPlus.setOnClickListener {
            if(mNum != 100){
                mNum++
                binding.dialogCartMenuManyNumText.text = mNum.toString()
            }
        }
        binding.dialogCartMenuManyNumMinus.setOnClickListener {
            if(mNum > 1){
                mNum--
                binding.dialogCartMenuManyNumText.text = mNum.toString()
            }
        }
        binding.dialogCartMenuManyNumOk.setOnClickListener {
            cartMenuInfoAdatper.changeNumRefresh(mNum)
            dismiss()
        }
    }
}