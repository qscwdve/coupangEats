package com.example.coupangeats.src.cart.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R

class MenuNumAdapter(val numList: ArrayList<Int>, val selectNum: Int, val cartMenuInfoAdatper: CartMenuInfoAdatper, val mPosition: Int) : RecyclerView.Adapter<MenuNumAdapter.MenuNumViewHolder>() {
    class MenuNumViewHolder(itemView: View, val menuNumAdapter: MenuNumAdapter) : RecyclerView.ViewHolder(itemView){
        val numText = itemView.findViewById<TextView>(R.id.item_menu_num_text)
        val plus = itemView.findViewById<TextView>(R.id.item_menu_num_plus)
        val parent = itemView.findViewById<LinearLayout>(R.id.item_menu_num_parent)

        fun bind(num: Int, position: Int) {
            numText.text = num.toString()
            plus.visibility = if(num == 10) View.VISIBLE else View.GONE
            if(menuNumAdapter.selectNum == num){
                // 선택된 숫자
                parent.setBackgroundColor(Color.parseColor("#AEE0F8"))
                numText.setTextColor(Color.parseColor("#00AFFE"))
            } else {
                parent.setBackgroundColor(Color.parseColor("#FFFFFF"))
                numText.setTextColor(Color.parseColor("#000000"))
            }
            itemView.setOnClickListener {
                if(num == 10){
                    menuNumAdapter.cartMenuInfoAdatper.showDialogMenuManyNum(menuNumAdapter.mPosition, num)
                }
                else menuNumAdapter.cartMenuInfoAdatper.changeNumRefresh(num)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuNumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_num, parent, false)
        return MenuNumViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: MenuNumViewHolder, position: Int) {
        holder.bind(numList[position], position)
    }

    override fun getItemCount(): Int = numList.size

}