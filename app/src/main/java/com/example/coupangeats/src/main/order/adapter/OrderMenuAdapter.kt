package com.example.coupangeats.src.main.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.main.order.model.orderMenu

class OrderMenuAdapter(val orderMenuList: ArrayList<orderMenu>) : RecyclerView.Adapter<OrderMenuAdapter.OrderMenuViewHolder>(){
    class OrderMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val num = itemView.findViewById<TextView>(R.id.item_order_menu_num)
        val name = itemView.findViewById<TextView>(R.id.item_order_menu_name)
        val side = itemView.findViewById<TextView>(R.id.item_order_menu_side)
        val like = itemView.findViewById<ImageView>(R.id.item_order_menu_like)

        fun bind(menu: orderMenu) {
            num.text = menu.count.toString()
            name.text = menu.menuName
            if(menu.menuDetail == null && menu.menuDetail != ""){
                side.visibility = View.GONE
            } else {
                side.visibility = View.VISIBLE
                side.text = menu.menuDetail
            }
            like.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_menu, parent, false)
        return OrderMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderMenuViewHolder, position: Int) {
        holder.bind(orderMenuList[position])
    }

    override fun getItemCount(): Int = orderMenuList.size
}