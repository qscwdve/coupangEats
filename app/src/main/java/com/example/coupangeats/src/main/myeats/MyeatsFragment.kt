package com.example.coupangeats.src.main.myeats

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentMyeatsBinding
import com.example.coupangeats.src.setting.SettingActivity
import com.softsquared.template.kotlin.config.BaseFragment

class MyeatsFragment : BaseFragment<FragmentMyeatsBinding>(FragmentMyeatsBinding::bind, R.layout.fragment_myeats) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myeatsSetting.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))

        }
        binding.myeatsAddressManage.setOnClickListener {  }
        binding.myeatsAgree.setOnClickListener {  }
        binding.myeatsDiscount.setOnClickListener {  }
        binding.myeatsEvent.setOnClickListener {  }
        binding.myeatsFavorites.setOnClickListener {  }
        binding.myeatsNotice.setOnClickListener {  }
        binding.myeatsPatner.setOnClickListener {  }
        binding.myeatsPay.setOnClickListener {  }
        binding.myeatsQuestion.setOnClickListener {  }
        binding.myeatsTell.setOnClickListener {  }
    }
}