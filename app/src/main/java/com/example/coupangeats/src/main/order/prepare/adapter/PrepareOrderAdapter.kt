package com.example.coupangeats.src.main.order.prepare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.order.adapter.OrderMenuAdapter
import com.example.coupangeats.src.main.order.model.prepareOrder
import com.example.coupangeats.src.main.order.prepare.OrderPrepareFragment

class PrepareOrderAdapter(val prepareOrderList: ArrayList<prepareOrder>, val fragment: OrderPrepareFragment) : RecyclerView.Adapter<PrepareOrderAdapter.PrepareOrderViewHolder>() {
    class PrepareOrderViewHolder(itemView: View, val fragment: OrderPrepareFragment): RecyclerView.ViewHolder(itemView){
        val storeName = itemView.findViewById<TextView>(R.id.item_prepare_name)
        val date = itemView.findViewById<TextView>(R.id.item_prepare_date)
        val status = itemView.findViewById<TextView>(R.id.item_prepare_status)
        val img = itemView.findViewById<ImageView>(R.id.item_prepare_img)
        val menuRecyclerView = itemView.findViewById<RecyclerView>(R.id.item_prepare_menu_recycler_view)
        val totalPrice = itemView.findViewById<TextView>(R.id.item_prepare_price)
        val receipt = itemView.findViewById<TextView>(R.id.item_prepare_receipt)
        val statusLook = itemView.findViewById<TextView>(R.id.item_prepare_look_status)
        val superParent = itemView.findViewById<LinearLayout>(R.id.item_prepare_order_super_parent)

        fun bind(order: prepareOrder) {
            storeName.text = order.storeName
            date.text = order.orderDate
            status.text = order.status
            if(order.imageUrl != null) {
                Glide.with(img).load(order.imageUrl).into(img)
                img.visibility = View.VISIBLE
            } else img.visibility = View.GONE
            totalPrice.text = order.totalPrice
            menuRecyclerView.adapter = OrderMenuAdapter(order.OrderMenus)
            menuRecyclerView.layoutManager = LinearLayoutManager(itemView.context)

            receipt.setOnClickListener {
                // ????????? ??????
                fragment.lookReceipt(order)
            }
            statusLook.setOnClickListener {
                // ?????? ?????? ??????
                fragment.startDeliveryStatus()
            }
            superParent.setOnClickListener {
                // ?????? ?????? ??????
                fragment.startDetailSuper(order.storeIdx)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrepareOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prepare_order, parent, false)
        return PrepareOrderViewHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: PrepareOrderViewHolder, position: Int) {
        holder.bind(prepareOrderList[position])
    }

    override fun getItemCount(): Int = prepareOrderList.size
}