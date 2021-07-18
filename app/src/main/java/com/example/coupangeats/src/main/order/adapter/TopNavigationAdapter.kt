package com.example.coupangeats.src.main.order.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.MainActivityView
import com.example.coupangeats.src.main.order.OrderFragment
import com.example.coupangeats.src.main.order.past.OrderPastFragment
import com.example.coupangeats.src.main.order.prepare.OrderPrepareFragment

class TopNavigationAdapter (fragmentActivity: FragmentActivity,
                            val mainActivity: MainActivity,
                            val fragment: OrderFragment) : FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> OrderPastFragment(mainActivity)
            1 -> OrderPrepareFragment(fragment)
            else -> OrderPastFragment(mainActivity)
        }
    }

}