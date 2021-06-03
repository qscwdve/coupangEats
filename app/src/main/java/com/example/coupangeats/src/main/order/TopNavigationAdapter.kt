package com.example.coupangeats.src.main.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TopNavigationAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> OrderPastFragment()
            1 -> OrderPrepareFragment()
            else -> OrderPastFragment()
        }
    }

}