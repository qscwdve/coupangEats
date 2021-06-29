package com.example.coupangeats.src.main.order

import android.os.Bundle
import android.view.View
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentOrderBinding
import com.example.coupangeats.src.main.order.adapter.TopNavigationAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.softsquared.template.kotlin.config.BaseFragment

class OrderFragment : BaseFragment<FragmentOrderBinding>(FragmentOrderBinding::bind, R.layout.fragment_order) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabElement = arrayListOf("과거 주문 내역", "준비중")
        binding.topNavigationViewPager2.adapter = TopNavigationAdapter(requireActivity())
        TabLayoutMediator(binding.orderTabLayout, binding.topNavigationViewPager2) { tab, position ->
            tab.text = tabElement[position]
        }.attach()
    }

    /*fun changeTab(){
        val tab = binding.orderTabLayout.getTabAt(0)
        tab!!.select()
    }*/
}