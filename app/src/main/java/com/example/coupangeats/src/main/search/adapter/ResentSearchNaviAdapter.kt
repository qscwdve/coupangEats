package com.example.coupangeats.src.main.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.main.search.SearchFragment
import com.example.coupangeats.src.searchDetail.model.ResentSearchDate

class ResentSearchNaviAdapter(var resentList: ArrayList<ResentSearchDate>, val searchFragment: SearchFragment): RecyclerView.Adapter<ResentSearchNaviAdapter.ResentSearchNaviViewHolder>() {
    class ResentSearchNaviViewHolder(itemView: View, val adapter: ResentSearchNaviAdapter): RecyclerView.ViewHolder(itemView){
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
                adapter.searchFragment.startResentSearch(resentDate.text, resentDate.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResentSearchNaviViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resent_search, parent, false)
        return ResentSearchNaviViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ResentSearchNaviViewHolder, position: Int) {
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
        searchFragment.deleteResentSearchItem(id)
    }
}