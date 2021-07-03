package com.example.coupangeats.src.searchDetail.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.searchDetail.SearchDetailActivity
import com.example.coupangeats.src.searchDetail.model.ResentSearchDate

@RequiresApi(Build.VERSION_CODES.O)
class ResentSearchAdapter(var resentList: ArrayList<ResentSearchDate>, val activity: SearchDetailActivity): RecyclerView.Adapter<ResentSearchAdapter.ResentSearchViewHolder>() {
    class ResentSearchViewHolder(itemView: View, val adapter: ResentSearchAdapter): RecyclerView.ViewHolder(itemView){
        val text = itemView.findViewById<TextView>(R.id.item_resent_search_text)
        val date = itemView.findViewById<TextView>(R.id.item_resent_search_date)
        val cancel = itemView.findViewById<ImageView>(R.id.item_resent_search_cancel)


        fun bind(resentDate: ResentSearchDate, position: Int) {
            text.text = resentDate.text
            date.text = resentDate.date

            cancel.setOnClickListener {
                // 삭제
                adapter.itemDelete(resentDate, resentDate.id)
            }
            itemView.setOnClickListener {
                // 선택
                adapter.activity.startResentSearch(resentDate.text, resentDate.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResentSearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resent_search, parent, false)
        return ResentSearchViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ResentSearchViewHolder, position: Int) {
        holder.bind(resentList[position], position)
    }

    override fun getItemCount(): Int = resentList.size
    // id, text , date

    fun refresh(itemList: ArrayList<ResentSearchDate>){
        resentList = itemList
        notifyDataSetChanged()
    }

    fun itemDelete(item: ResentSearchDate, id: Int){
        val position = resentList.indexOf(item)
        resentList.removeAt(position)
        notifyItemRemoved(position)
        activity.deleteResentSearchItem(id)
    }
}