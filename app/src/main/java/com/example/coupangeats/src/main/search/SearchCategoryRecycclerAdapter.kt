package com.example.coupangeats.src.main.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponseResult

class SearchCategoryRecycclerAdapter(val categoryResponseResultDataList : ArrayList<SuperCategoryResponseResult>) : RecyclerView.Adapter<SearchCategoryRecycclerAdapter.SearchCategoryViewHolder>() {
    class SearchCategoryViewHolder(val itemView: View, val parent_context: Context) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.search_category_img)
        val text = itemView.findViewById<TextView>(R.id.search_category_text)

        fun bind(item: SuperCategoryResponseResult, index: Int) {
            itemView.setOnClickListener {
                // 카테고리 화면 선택으로 넘김
                // Intent(parent_context, )
            }
            Glide.with(img).load(item.img).into(img)
            text.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_category, parent, false)
        return SearchCategoryViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: SearchCategoryViewHolder, position: Int) {
        holder.bind(categoryResponseResultDataList[position], position)
    }

    override fun getItemCount(): Int = categoryResponseResultDataList.size
}