package com.example.coupangeats.util

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.coupangeats.R
import com.example.coupangeats.databinding.DialogEditTextBinding
import com.example.coupangeats.src.cart.CartActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CartOrderRiderEdit(val activity: CartActivity, val value: String): BottomSheetDialogFragment()  {
    private lateinit var binding : DialogEditTextBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditTextBinding.inflate(layoutInflater)
        //dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(value != "상세 요청사항을 입력해주세요"){
            binding.dialogEditText.setText(value)
        }
        binding.dialogEditText.hint = "예, \"공동 현관문 비밀번호는 1234입니다.\""
        binding.dialogEditText.setText(activity.mDeliveryOrderString)
        // 글자 수 감지
        binding.dialogEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.dialogEditTextNum.text = count.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        // 확인 누르기
        binding.dialogEditTextOk.setOnClickListener {
            activity.changeOrderRiderEdit(binding.dialogEditText.text.toString())
            dismiss()
        }
    }
}