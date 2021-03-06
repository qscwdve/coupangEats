package com.example.coupangeats.src.reviewWrite.dialog

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import com.example.coupangeats.R
import com.example.coupangeats.databinding.DialogEditTextBinding
import com.example.coupangeats.src.reviewWrite.ReviewWriteActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReviewWriteOpinionBottomSheet(val version: Int, val opinion: String, val activity: ReviewWriteActivity) : BottomSheetDialogFragment() {
    private lateinit var binding : DialogEditTextBinding
    private lateinit var inputMethodManager : InputMethodManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditTextBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.EditTextDialogStyle)
        inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogEditTextOk.text = "저장"
        binding.dialogEditText.setText(opinion)
        if( version == 1 || version == 2){
            // 메뉴에서 기타의견일 경우
            binding.dialogEditText.hint = "기타의견"
            binding.dialogEditText.maxEms = 80
            val text = "/80"
            binding.dialogEditTextNumTotal.text = text
        }
        else if(version == 3){
            // 배달 불만사항일 경우
            binding.dialogEditText.hint =
                "배달파트너가 정확하게 길을 찾아올 수 있도록 길안내를 작성해주세요.\n\n예) 이수역 2번출구에서 오른쪽 오피스텔입니다. 옆 상가 입구와 헷갈리지 않도록 주의해주세요."
            binding.dialogEditText.maxEms = 300
            val text = "/300"
            binding.dialogEditTextNumTotal.text = text
        }

        // 글자 수 감지
        binding.dialogEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.dialogEditTextNum.text = count.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.dialogEditTextOk.setOnClickListener {
            if(version == 1){

            } else if(version == 2){
                activity.changeDeliveryEtc(binding.dialogEditText.text.toString())
            }
            inputMethodManager.hideSoftInputFromWindow(binding.dialogEditText.windowToken, 0);
            dismiss()
        }
    }

}