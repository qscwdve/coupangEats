package com.example.coupangeats.src.reviewWrite.adapter.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.coupangeats.databinding.DialogEditTextBinding
import com.example.coupangeats.src.reviewWrite.adapter.ReviewWriteMenuAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReviewWriteMenuOpinionBottomSheet(val reviewWriteMenuAdapter: ReviewWriteMenuAdapter, val position: Int ) : BottomSheetDialogFragment() {
    private lateinit var binding : DialogEditTextBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditTextBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogEditTextOk.text = "저장"

        // 메뉴에서 기타의견일 경우
        binding.dialogEditText.hint = "기타의견"
        binding.dialogEditText.maxEms = 80
        val text = "/80"
        binding.dialogEditTextNumTotal.text = text

        // 글자 수 감지
        binding.dialogEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.dialogEditTextNum.text = count.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.dialogEditTextOk.setOnClickListener {
            reviewWriteMenuAdapter.opinionEtcChange(position, binding.dialogEditText.text.toString())
            dismiss()
        }
    }
}