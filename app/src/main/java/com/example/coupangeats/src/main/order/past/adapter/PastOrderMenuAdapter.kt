package com.example.coupangeats.src.main.order.past.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.main.order.adapter.OrderMenuAdapter
import com.example.coupangeats.src.main.order.model.orderMenu
import com.example.coupangeats.src.main.order.past.model.PastOrderMenu

class PastOrderMenuAdapter(val orderMenuList: ArrayList<PastOrderMenu>) : RecyclerView.Adapter<PastOrderMenuAdapter.PastOrderMenuViewHolder>(){
    class PastOrderMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val num = itemView.findViewById<TextView>(R.id.item_order_menu_num)
        val name = itemView.findViewById<TextView>(R.id.item_order_menu_name)
        val side = itemView.findViewById<TextView>(R.id.item_order_menu_side)
        val like = itemView.findViewById<ImageView>(R.id.item_order_menu_like)

        fun bind(menu: PastOrderMenu) {
            num.text = menu.count.toString()
            name.text = menu.menuName
            if(menu.menuDetail == null || menu.menuDetail == ""){
                side.visibility = View.GONE
            } else {
                side.visibility = View.VISIBLE
                side.text = menu.menuDetail
            }
            when(menu.menuLiked){
                "GOOD" -> {
                    like.visibility = View.VISIBLE
                    like.setImageResource(R.drawable.ic_menu_like)
                }
                "BAD" -> {
                    like.visibility = View.VISIBLE
                    like.setImageResource(R.drawable.ic_menu_dislike)
                }
                else -> like.visibility = View.GONE
            }
        }
    }
    override fun onBindViewHolder(holder: PastOrderMenuViewHolder, position: Int) {
        holder.bind(orderMenuList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastOrderMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_menu, parent, false)
        return PastOrderMenuViewHolder(view)
    }

    override fun getItemCount(): Int = orderMenuList.size
}