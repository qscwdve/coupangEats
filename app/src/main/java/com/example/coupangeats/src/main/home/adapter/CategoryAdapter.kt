package com.example.coupangeats.src.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.main.home.model.HomeInfo.StoreCategories

class CategoryAdapter(val categoryList: ArrayList<StoreCategories>, val fragment: HomeFragment): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    class CategoryViewHolder(itemView: View, val categoryAdapter: CategoryAdapter) : RecyclerView.ViewHolder(itemView) {
        val categoryImg = itemView.findViewById<ImageView>(R.id.item_home_category_img)
        val categoryName = itemView.findViewById<TextView>(R.id.item_home_category_name)

        fun bind(item: StoreCategories){

            Glide.with(categoryImg).load(item.url).circleCrop().into(categoryImg)
            categoryName.text = item.categoryName

            itemView.setOnClickListener {
                // 카테고리로 넘어감
                categoryAdapter.fragment.startCategorySuper(item.categoryName)
            }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_category, parent, false)
        return CategoryViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int = categoryList.size
}