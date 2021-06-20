package com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.detailSuper.detailSuperFragment.DetailSuperActivity
import com.example.coupangeats.src.detailSuper.detailSuperFragment.model.MenuList

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
        val parent = itemView.findViewById<LinearLayout>(R.id.item_detail_super_menu_list_parent)

        fun bind(item: MenuList, position: Int) {
            name.text = item.menuName
            val menuPrice : String = "${priceIntToString(item.price)}원"
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
            parent.setOnClickListener {
                menuAdapter.activity.startMenuSelect(item.menuIdx)
            }

        }
        fun priceIntToString(value: Int) : String {
            val target = value.toString()
            val size = target.length
            return if(size > 3){
                val last = target.substring(size - 3 until size)
                val first = target.substring(0..(size - 4))
                "$first,$last"
            } else target
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