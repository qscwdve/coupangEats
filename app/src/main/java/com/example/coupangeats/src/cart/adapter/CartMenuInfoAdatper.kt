package com.example.coupangeats.src.cart.adapter

import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.cart.CartActivity
import com.example.coupangeats.src.cart.model.CartMenuInfo
import com.example.coupangeats.util.CartMenuDatabase

class CartMenuInfoAdatper(var menuList: ArrayList<CartMenuInfo>, val activity: CartActivity) : RecyclerView.Adapter<CartMenuInfoAdatper.CartMenuInfoViewHolder>() {

    private var mDBHelper = CartMenuDatabase(activity, "Menu.db", null, 1)
    private var mDB: SQLiteDatabase = mDBHelper.writableDatabase

    class CartMenuInfoViewHolder(itemView: View, val cartMenuInfoAdatper: CartMenuInfoAdatper): RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.item_cart_menu_name)
        val cancel = itemView.findViewById<ImageView>(R.id.item_cart_menu_cancel)
        val sub = itemView.findViewById<TextView>(R.id.item_cart_menu_sub)
        val price = itemView.findViewById<TextView>(R.id.item_cart_menu_price)
        val num = itemView.findViewById<TextView>(R.id.item_cart_menu_num)

        fun bind(item: CartMenuInfo){
            name.text = item.name
            num.text = item.num.toString()

            if(item.sub == "" || item.sub == null){
                sub.visibility = View.GONE
            } else {
                sub.visibility = View.VISIBLE
                sub.text = item.sub
            }

            val priceText = item.price.toString() + "원"
            price.text = priceText

            cancel.setOnClickListener {
                // 메뉴 취소하기를 누르면 메뉴 선택이 취소된다.
                cartMenuInfoAdatper.deleteMenu(item.id!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartMenuInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_menu, parent, false)
        return CartMenuInfoViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: CartMenuInfoViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size

    // 메뉴 삭제
    fun deleteMenu(id: Int){
        mDBHelper.deleteId(mDB, id)
        menuList = mDBHelper.menuSelect(mDB)
        notifyDataSetChanged()
    }
}