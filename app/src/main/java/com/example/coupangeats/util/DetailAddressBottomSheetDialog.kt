package com.example.coupangeats.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.coupangeats.databinding.DialogDetailAddressBinding
import com.example.coupangeats.src.deliveryAddressSetting.DeliveryAddressSettingActivity
import com.example.coupangeats.src.deliveryAddressSetting.detail.DetailAddressFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailAddressBottomSheetDialog(val detailAddressFragment: DetailAddressFragment) : BottomSheetDialogFragment() {
    private lateinit var binding : DialogDetailAddressBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogDetailAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogDetailAddressIgnore.setOnClickListener {
            dismiss()
            detailAddressFragment.checkHomeOrCompany()
        }

        binding.dialogDetailAddressInput.setOnClickListener {
            dismiss()
        }
    }
}