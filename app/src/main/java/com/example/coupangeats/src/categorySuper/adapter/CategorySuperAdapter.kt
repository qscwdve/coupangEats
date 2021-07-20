package com.example.coupangeats.src.categorySuper.adapter

import android.graphics.Color
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.categorySuper.CategorySuperActivity
import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponseResult

class CategorySuperAdapter(val categoryList: ArrayList<SuperCategoryResponseResult>, val activity: CategorySuperActivity, val option: String): RecyclerView.Adapter<CategorySuperAdapter.CategorySuperViewHolder>() {
    var categoryCheck = option
    var textView : TextView? = null
    var imageView: ImageView? = null
    class CategorySuperViewHolder(itemView: View, val categorySuperAdapter: CategorySuperAdapter) : RecyclerView.ViewHolder(itemView) {
        val categoryImg = itemView.findViewById<ImageView>(R.id.item_category_super_category_img)
        val back = itemView.findViewById<ImageView>(R.id.item_category_super_category_img_back)
        val categoryName = itemView.findViewById<TextView>(R.id.item_category_super_category_name)
        val line = itemView.findViewById<View>(R.id.item_category_super_category_line)

        fun bind(item: SuperCategoryResponseResult, position: Int){

            Glide.with(categoryImg).load(item.img).circleCrop().into(categoryImg)
            categoryName.text = item.name

            if(categorySuperAdapter.categoryCheck == item.name){
                line.visibility = View.VISIBLE
                categoryName.setTextColor(Color.parseColor("#00AFFE"))
                back.visibility = View.VISIBLE
                Glide.with(back).load(R.drawable.rectangle_blue_box).circleCrop().into(back)
            } else {
                line.visibility = View.INVISIBLE
                categoryName.setTextColor(Color.parseColor("#000000"))
                back.visibility = View.INVISIBLE
            }

            itemView.setOnClickListener {
                // 선택되면 바뀌어야 한다.
                if(categorySuperAdapter.categoryCheck != item.name){
                    categorySuperAdapter.categoryCheck = item.name
                    categorySuperAdapter.changeCategory(item.name)
                }
            }
            categorySuperAdapter.textView = categoryName
            categorySuperAdapter.imageView = back
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySuperViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_super_category, parent, false)
        return CategorySuperViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: CategorySuperViewHolder, position: Int) {
        holder.bind(categoryList[position], position)
    }

    override fun getItemCount(): Int = categoryList.size

    fun changeCategory(value: String){
        notifyDataSetChanged()
        activity.categoryChange(value, 1)
    }

    fun changeCategoryOnly(value: String){
        categoryCheck = value
        notifyDataSetChanged()
    }

    fun getHeight(): Int{
        //return textView!!.top
        return imageView!!.bottom
    }

}