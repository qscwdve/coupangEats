package com.example.coupangeats.src.main.home.adapter

import android.annotation.SuppressLint
import android.util.EventLog
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.main.home.model.HomeInfo.Events

class EventAdapter(val eventList: ArrayList<Events>, val fragment: HomeFragment) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View, val adapter: EventAdapter) : RecyclerView.ViewHolder(itemView) {
        val eventImg = itemView.findViewById<ImageView>(R.id.item_event_img)
        val back = itemView.findViewById<View>(R.id.item_event_click)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: Events, totalNum: Int) {
            Glide.with(adapter.fragment.requireContext()).load(item.url ?: item.urlTemp ?: R.drawable.bbq_event).into(eventImg)

            back.setOnClickListener {
                adapter.fragment.setAddressQuestionDown()  // 배달주소 맞는지 물어보는거 내림
                adapter.fragment.startEventItem(item.eventIdx)
            }

            itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) adapter.fragment.setAddressQuestionDown()  // 배달주소 맞는지 물어보는거 내림
                if(!adapter.fragment.mScrollFinish) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (adapter.fragment.mScrollStart) {
                            adapter.fragment.mScrollStart = false
                            adapter.fragment.mScrollFlag = false
                        } else {
                            adapter.fragment.mScrollFlag = false
                        }
                    } else if (event.action == MotionEvent.ACTION_DOWN) {
                        // 누름
                        adapter.fragment.mScrollFlag = true
                        adapter.fragment.mScrollValue = -1
                    }
                }
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_viewpager, parent, false)
        return EventViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position%(eventList.size)], eventList.size)
    }

    override fun getItemCount(): Int = (eventList.size * 100)
}