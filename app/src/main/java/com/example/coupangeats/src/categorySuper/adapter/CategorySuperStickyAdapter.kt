package com.example.coupangeats.src.categorySuper.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.categorySuper.CategorySuperActivity
import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponseResult

class CategorySuperStickyAdapter(val categoryList: ArrayList<SuperCategoryResponseResult>, val activity: CategorySuperActivity, val option: String): RecyclerView.Adapter<CategorySuperStickyAdapter.CategorySuperStickyViewHolder>() {
    var categoryCheck = option
    class CategorySuperStickyViewHolder(itemView: View , val categorySuperAdapter: CategorySuperStickyAdapter) : RecyclerView.ViewHolder(itemView){
        val categoryName = itemView.findViewById<TextView>(R.id.item_category_super_category_sticky_name)
        val line = itemView.findViewById<View>(R.id.item_category_super_category_sticky_line)

        fun bind(item: SuperCategoryResponseResult, position: Int){

            categoryName.text = item.name

            if(categorySuperAdapter.categoryCheck == item.name){
                line.visibility = View.VISIBLE
                categoryName.setTextColor(Color.parseColor("#00AFFE"))
            } else {
                line.visibility = View.INVISIBLE
                categoryName.setTextColor(Color.parseColor("#000000"))
            }

            itemView.setOnClickListener {
                // 선택되면 바뀌어야 한다.
                if(categorySuperAdapter.categoryCheck != item.name){
                    categorySuperAdapter.categoryCheck = item.name
                    categorySuperAdapter.changeCategory(item.name)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategorySuperStickyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_super_category_sticky, parent, false)
        return CategorySuperStickyViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: CategorySuperStickyViewHolder, position: Int) {
        holder.bind(categoryList[position], position)
    }

    override fun getItemCount(): Int = categoryList.size

    fun changeCategory(value: String){
        notifyDataSetChanged()
        activity.categoryChange(value, 2)
    }

    fun changeCategoryOnly(value: String){
        categoryCheck = value
        notifyDataSetChanged()
    }
}