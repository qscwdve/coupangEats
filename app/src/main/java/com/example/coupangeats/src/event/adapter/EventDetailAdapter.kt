package com.example.coupangeats.src.event.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.event.EventActivity
import com.example.coupangeats.src.event.model.EventDetail

class EventDetailAdapter(val eventList: ArrayList<EventDetail>, val activity: EventActivity): RecyclerView.Adapter<EventDetailAdapter.EventDetailViewHolder>() {
    class EventDetailViewHolder(itemView: View, val adapter: EventDetailAdapter) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById<ImageView>(R.id.item_event_detail_img)
        val endDate = itemView.findViewById<TextView>(R.id.item_event_detail_endDate)
        val back = itemView.findViewById<View>(R.id.item_event_detail_back)
        fun bind(event: EventDetail){
            Glide.with(img).load(event.bannerUrl ?: event.bannerTemp).into(img)
            if(event.endDate == null){
                endDate.visibility = View.GONE
            } else {
                endDate.visibility = View.VISIBLE
                endDate.text = event.endDate
            }

            back.setOnClickListener {
                // 이벤트 보러가야함
                adapter.activity.startEventItem(event.eventIdx)
            }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_detail, parent, false)
        return EventDetailViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: EventDetailViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size
}