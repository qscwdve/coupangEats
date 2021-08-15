package com.example.coupangeats.src.main.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.search.SearchFragment
import com.example.coupangeats.src.main.search.model.SuperCategoryResponseResult

class SearchCategoryAdapter(val categoryResponseResultDataList : ArrayList<SuperCategoryResponseResult>, val fragment: SearchFragment) : RecyclerView.Adapter<SearchCategoryAdapter.SearchCategoryViewHolder>() {
    class SearchCategoryViewHolder(val itemView: View, val adapter: SearchCategoryAdapter) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.search_category_img)
        val text = itemView.findViewById<TextView>(R.id.search_category_text)
        val new = itemView.findViewById<TextView>(R.id.item_search_category_new)
        val parent = itemView.findViewById<RelativeLayout>(R.id.item_search_category_parent)
        fun bind(item: SuperCategoryResponseResult, index: Int) {

            if(item.name == "신규 맛집"){
                new.visibility = View.VISIBLE
            } else {
                new.visibility = View.GONE
            }

            parent.setOnClickListener {
                // 카테고리 화면 선택으로 넘김
                adapter.fragment.startCategorySuper(item.name)
            }
            Glide.with(img).load(item.img).circleCrop().into(img)
            text.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_category, parent, false)
        return SearchCategoryViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: SearchCategoryViewHolder, position: Int) {
        holder.bind(categoryResponseResultDataList[position], position)
    }

    override fun getItemCount(): Int = categoryResponseResultDataList.size
}