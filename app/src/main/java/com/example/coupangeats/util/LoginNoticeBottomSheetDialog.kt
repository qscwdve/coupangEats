package com.example.coupangeats.util

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.coupangeats.databinding.DialogLoginNoticeBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.transition.Slide;
import com.example.coupangeats.R

class LoginNoticeBottomSheetDialog(private val notice: Int) : BottomSheetDialogFragment() {
    private lateinit var binding : DialogLoginNoticeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoginNoticeBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        binding.noticeText.setText(notice)
    }

    override fun onStart() {
        super.onStart()
        val window: Window? = dialog!!.window
        val windowParams: WindowManager.LayoutParams = window!!.attributes
        windowParams.dimAmount = 0.0f
        window!!.attributes = windowParams
    }
}