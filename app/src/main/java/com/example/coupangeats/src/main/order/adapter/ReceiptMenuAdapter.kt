package com.example.coupangeats.src.main.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.main.order.model.OrderMenu

class ReceiptMenuAdapter(val menuList: ArrayList<OrderMenu>) : RecyclerView.Adapter<ReceiptMenuAdapter.ReceiptMenuViewHolder>() {
    class ReceiptMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.item_receipt_menu_name)
        val side = itemView.findViewById<TextView>(R.id.item_receipt_menu_side)
        val price = itemView.findViewById<TextView>(R.id.item_receipt_menu_price)

        fun bind(order: OrderMenu){
            // 메뉴 이름
            val menuName = if(order.count > 1) "${order.menuName} X${order.count}" else order.menuName
            name.text = menuName

            // 사이드 메뉴 추가
            if(order.menuDetail != null && order.menuDetail != ""){
                side.text = order.menuDetail
                side.visibility = View.VISIBLE
            } else {
                side.visibility = View.GONE
            }

            // 가격
            val menuPrice = order.menuPrice.replace(",", "").toInt()
            val menuPriceText = "${priceIntToString(menuPrice * order.count)}원"
            price.text = menuPriceText
        }

        private fun priceIntToString(value: Int) : String {
            val target = value.toString()
            val size = target.length
            return if(size > 3){
                val last = target.substring(size - 3 until size)
                val first = target.substring(0..(size - 4))
                "$first,$last"
            } else target
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