package com.example.coupangeats.src.main.order.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.coupangeats.src.main.order.past.OrderPastFragment
import com.example.coupangeats.src.main.order.prepare.OrderPrepareFragment

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