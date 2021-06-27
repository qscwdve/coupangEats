package com.example.coupangeats.src.detailSuper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.detailSuper.model.Menu

class MenuCategoryAdapter(val menuCategoryList: ArrayList<Menu>, val activity: DetailSuperActivity) : RecyclerView.Adapter<MenuCategoryAdapter.MenuCategoryViewHolder>() {
    class MenuCategoryViewHolder(itemView: View, val menuCategoryAdapter: MenuCategoryAdapter): RecyclerView.ViewHolder(itemView) {
        val categoryName = itemView.findViewById<TextView>(R.id.item_detail_menu_category_name)
        val categoryIntroduce = itemView.findViewById<TextView>(R.id.item_detail_menu_category_introduce)
        val menuRecyclerView = itemView.findViewById<RecyclerView>(R.id.item_detail_menuList_recyclerView)
        val view = itemView.findViewById<View>(R.id.item_detail_menu_category_view)

        fun bind(item: Menu, position: Int) {
            categoryName.text = item.categoryName
            if(item.categoryIntroduce != null){
                categoryIntroduce.visibility = View.VISIBLE
                categoryIntroduce.text = item.categoryIntroduce
            } else {
                categoryIntroduce.visibility = View.GONE
            }
            if(position + 1== menuRecyclerView.size) view.visibility = View.GONE
            else view.visibility = View.VISIBLE
            // 리사이클러뷰 생성
            menuRecyclerView.adapter = MenuAdapter(item.menuList, menuCategoryAdapter.activity)
            menuRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_super_menu_parent, parent, false)
        return MenuCategoryViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: MenuCategoryViewHolder, position: Int) {
        holder.bind(menuCategoryList[position], position)
    }

    override fun getItemCount(): Int = menuCategoryList.size

    fun setFocus(position: Int){

    }
}