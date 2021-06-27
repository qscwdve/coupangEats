package com.example.coupangeats.src.main.order.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.main.order.model.orderMenu

class ReceiptMenuAdapter(val menuList: ArrayList<orderMenu>) : RecyclerView.Adapter<ReceiptMenuAdapter.ReceiptMenuViewHolder>() {
    class ReceiptMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.item_receipt_menu_name)
        val side = itemView.findViewById<TextView>(R.id.item_receipt_menu_side)
        val price = itemView.findViewById<TextView>(R.id.item_receipt_menu_price)

        fun bind(order: orderMenu){
            name.text = order.menuName
            if(order.menuDetail != null && order.menuDetail != ""){
                side.text = order.menuDetail
                side.visibility = View.VISIBLE
            } else {
                side.visibility = View.GONE
            }
            price.text = order.menuPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receipt_menu, parent, false)
        return ReceiptMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptMenuViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size
}