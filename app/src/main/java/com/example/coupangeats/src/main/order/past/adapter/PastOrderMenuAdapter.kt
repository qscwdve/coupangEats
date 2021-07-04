package com.example.coupangeats.src.main.order.past.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
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

class PastOrderMenuAdapter(val orderMenuList: ArrayList<PastOrderMenu>, val keyword: String? = null) : RecyclerView.Adapter<PastOrderMenuAdapter.PastOrderMenuViewHolder>(){
    class PastOrderMenuViewHolder(itemView: View, val adapter: PastOrderMenuAdapter) : RecyclerView.ViewHolder(itemView){
        val num = itemView.findViewById<TextView>(R.id.item_order_menu_num)
        val name = itemView.findViewById<TextView>(R.id.item_order_menu_name)
        val side = itemView.findViewById<TextView>(R.id.item_order_menu_side)
        val like = itemView.findViewById<ImageView>(R.id.item_order_menu_like)

        fun bind(menu: PastOrderMenu) {
            num.text = menu.count.toString()
            name.text = setTextBackgroundColor(menu.menuName)
            if(menu.menuDetail == null || menu.menuDetail == ""){
                side.visibility = View.GONE
            } else {
                side.visibility = View.VISIBLE
                side.text = setTextBackgroundColor(menu.menuDetail)
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

        fun setTextBackgroundColor(item: String): SpannableStringBuilder {
            val key = adapter.keyword
            val ssb = SpannableStringBuilder(item)
            val mapping = ArrayList<PastOrderAdapter.StringIndex>()
            if(key != null){
                var str = item
                while(str.length >= key.length){
                    if(item.contains(key)){
                        // 문자열이 포함되는 것이 있음
                        val index = item.indexOf(key)
                        val finish = index + key.length
                        mapping.add(PastOrderAdapter.StringIndex(index, finish))
                        if(str.length <= finish + 1) break
                        str = str.substring(finish + 1)
                    } else {
                        break
                    }
                }
                if(mapping.isNotEmpty()){
                    for(index in mapping.indices){
                        ssb.setSpan(BackgroundColorSpan(Color.parseColor("#F5FDA0")), mapping[index].start, mapping[index].end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    }
                }
            }
            return ssb
        }
    }
    override fun onBindViewHolder(holder: PastOrderMenuViewHolder, position: Int) {
        holder.bind(orderMenuList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastOrderMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_menu, parent, false)
        return PastOrderMenuViewHolder(view, this)
    }

    override fun getItemCount(): Int = orderMenuList.size
}