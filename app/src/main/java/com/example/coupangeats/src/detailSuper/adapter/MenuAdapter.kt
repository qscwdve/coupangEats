package com.example.coupangeats.src.detailSuper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.detailSuper.ScrollingActivity
import com.example.coupangeats.src.detailSuper.model.MenuList

class MenuAdapter(val menuList: ArrayList<MenuList>, val activity: DetailSuperActivity) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    class MenuViewHolder(itemView: View,val menuAdapter: MenuAdapter) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.item_menu_list_name)
        val price = itemView.findViewById<TextView>(R.id.item_menu_list_price)
        val recommParent = itemView.findViewById<LinearLayout>(R.id.item_menu_list_recomm_parent)
        val introduce = itemView.findViewById<TextView>(R.id.item_menu_list_introduce)
        val manyOrder = itemView.findViewById<TextView>(R.id.item_menu_list_recomm_order)
        val manyReview = itemView.findViewById<TextView>(R.id.item_menu_list_recomm_review)
        val img = itemView.findViewById<ImageView>(R.id.item_menu_list_img)
        val line = itemView.findViewById<View>(R.id.item_menu_list_line)

        fun bind(item: MenuList, position: Int) {
            name.text = item.menuName
            val menuPrice : String = "${item.price}원"
            price.text = menuPrice
            if(item.introduce != null){
                introduce.visibility = View.VISIBLE
                introduce.text = item.introduce
            } else {
                introduce.visibility = View.GONE
            }
            recommParent.visibility = if(item.manyOrder != null || item.manyReview != null) View.VISIBLE else View.GONE
            manyOrder.visibility = if(item.manyOrder != null) View.VISIBLE else View.GONE
            manyReview.visibility = if(item.manyReview != null) View.VISIBLE else View.GONE
            if(item.imageUrl != null) {
                img.visibility = View.VISIBLE
                Glide.with(img).load(item.imageUrl).into(img)
            } else {
                img.visibility = View.GONE
            }
            // 마지막 라인
            line.visibility = if(position == menuAdapter.menuList.size) View.GONE else View.VISIBLE
            // 메뉴 선택!
            itemView.setOnClickListener {
                menuAdapter.activity.startMenuSelect(item.menuIdx)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_super_menu_list, parent, false)
        return MenuViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menuList[position], position)
    }

    override fun getItemCount(): Int = menuList.size
}