package com.example.coupangeats.src.main.home.adapter

import android.annotation.SuppressLint
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
import com.example.coupangeats.src.main.home.model.HomeInfo.StoreCategories

class CategoryAdapter(val categoryList: ArrayList<StoreCategories>, val fragment: HomeFragment): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View, val categoryAdapter: CategoryAdapter) : RecyclerView.ViewHolder(itemView) {
        val categoryImg = itemView.findViewById<ImageView>(R.id.item_home_category_img)
        val categoryName = itemView.findViewById<TextView>(R.id.item_home_category_name)
        val new = itemView.findViewById<TextView>(R.id.item_home_category_new)
        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: StoreCategories){

            Glide.with(categoryImg).load(item.url).circleCrop().into(categoryImg)
            categoryName.text = item.categoryName

            if(item.categoryName == "신규 맛집"){
                new.visibility = View.VISIBLE
            } else {
                new.visibility = View.GONE
            }

            itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) categoryAdapter.fragment.setAddressQuestionDown()  // 배달주소 맞는지 물어보는거 내림
                if(!categoryAdapter.fragment.mScrollFinish) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (categoryAdapter.fragment.mScrollStart) {
                            categoryAdapter.fragment.mScrollStart = false
                            categoryAdapter.fragment.mScrollFlag = false
                        } else {
                            categoryAdapter.fragment.mScrollFlag = false
                        }
                    } else if (event.action == MotionEvent.ACTION_DOWN) {
                        // 누름
                        categoryAdapter.fragment.mScrollFlag = true
                        categoryAdapter.fragment.mScrollValue = -1
                    }
                }
                false
            }
            itemView.setOnClickListener {
                categoryAdapter.fragment.setAddressQuestionDown()  // 배달주소 맞는지 물어보는거 내림
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