package com.example.coupangeats.src.main.home.adapter

import android.util.EventLog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.home.model.HomeInfo.Events

class EventAdapter(val eventList: ArrayList<Events>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImg = itemView.findViewById<ImageView>(R.id.item_event_img)
        val eventPageNum = itemView.findViewById<TextView>(R.id.item_event_page_num)

        fun bind(item: Events, totalNum: Int) {
            Glide.with(eventImg).load(item.url).into(eventImg)
            val text = "${(item.eventIdx)} / $totalNum"
            eventPageNum.text = text

            itemView.setOnClickListener {
                // 이벤트 클릭시
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_viewpager, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position], eventList.size)
    }

    override fun getItemCount(): Int = eventList.size
}