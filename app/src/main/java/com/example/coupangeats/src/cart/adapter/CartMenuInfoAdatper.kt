package com.example.coupangeats.src.cart.adapter

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.databinding.DialogCartChangeBinding
import com.example.coupangeats.databinding.DialogLogoutBinding
import com.example.coupangeats.databinding.DialogMenuNumBinding
import com.example.coupangeats.src.cart.CartActivity
import com.example.coupangeats.src.cart.adapter.dialog.CartMenuNumManyDialog
import com.example.coupangeats.src.cart.model.CartMenuInfo
import com.example.coupangeats.util.CartMenuDatabase

class CartMenuInfoAdatper(var menuList: ArrayList<CartMenuInfo>, val activity: CartActivity) : RecyclerView.Adapter<CartMenuInfoAdatper.CartMenuInfoViewHolder>() {
    private var mDBHelper = CartMenuDatabase(activity, "Menu.db", null, 1)
    private var mDB: SQLiteDatabase = mDBHelper.writableDatabase
    private var mSelectNumPosition = -1
    private var mAlertDialog : AlertDialog? = null

    class CartMenuInfoViewHolder(itemView: View, val cartMenuInfoAdatper: CartMenuInfoAdatper): RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.item_cart_menu_name)
        val cancel = itemView.findViewById<ImageView>(R.id.item_cart_menu_cancel)
        val sub = itemView.findViewById<TextView>(R.id.item_cart_menu_sub)
        val price = itemView.findViewById<TextView>(R.id.item_cart_menu_price)
        val num = itemView.findViewById<TextView>(R.id.item_cart_menu_num)
        val numParent = itemView.findViewById<LinearLayout>(R.id.item_cart_menu_num_parent)

        fun bind(item: CartMenuInfo){
            name.text = item.name
            num.text = item.num.toString()

            if(item.sub == "" || item.sub == null){
                sub.visibility = View.GONE
            } else {
                sub.visibility = View.VISIBLE
                sub.text = item.sub
            }

            val priceText = priceIntToString(item.price * item.num) + "원"
            price.text = priceText

            cancel.setOnClickListener {
                // 메뉴 취소하기를 누르면 메뉴 선택이 취소된다.
                showDialogMenuDelete(item.id!!)
            }

            numParent.setOnClickListener {
                if(num.text.toString().toInt() >= 10){
                    // 수량 선택 dialog를 띄워야한다.
                    cartMenuInfoAdatper.showDialogMenuManyNum(adapterPosition, num.text.toString().toInt())
                }
                else showDialogMenuNum(adapterPosition)
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

        private fun showDialogMenuDelete(id: Int){
            val cartChangeBinding = DialogCartChangeBinding.inflate(cartMenuInfoAdatper.activity.layoutInflater)
            val builder = AlertDialog.Builder(cartMenuInfoAdatper.activity)
            builder.setView(cartChangeBinding.root)
            builder.setCancelable(true)

            cartChangeBinding.dialogCartChangeTitle.text = "메뉴를 삭제하시겠습니까?"
            cartChangeBinding.dialogCartChangeReplace.text = "예"
            cartChangeBinding.dialogCartChangeNo.text = "아니요"
            val alertDialog = builder.create()
            val window = alertDialog.window
            if(window != null){
                val params = window.attributes
                params.width = WindowManager.LayoutParams.MATCH_PARENT
                params.height = WindowManager.LayoutParams.WRAP_CONTENT
                alertDialog.window!!.attributes = params
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            alertDialog.show()

            cartChangeBinding.dialogCartChangeReplace.setOnClickListener {
                // 삭제
                alertDialog.dismiss()
                cartMenuInfoAdatper.deleteMenu(id)
            }
            cartChangeBinding.dialogCartChangeNo.setOnClickListener { alertDialog.dismiss() }
        }

        private fun showDialogMenuNum(position: Int) {
            val menuNumBinding = DialogMenuNumBinding.inflate(cartMenuInfoAdatper.activity.layoutInflater)
            menuNumBinding.dialogMenuNumList.adapter = MenuNumAdapter(arrayListOf(1,2,3,4,5,6,7,8,9,10), cartMenuInfoAdatper.menuList[position].num, cartMenuInfoAdatper, position)
            menuNumBinding.dialogMenuNumList.layoutManager = LinearLayoutManager(cartMenuInfoAdatper.activity)

            val builder = AlertDialog.Builder(cartMenuInfoAdatper.activity)
            builder.setView(menuNumBinding.root)
            builder.setCancelable(true)

            val alertDialog = builder.create()
            cartMenuInfoAdatper.mAlertDialog = alertDialog
            cartMenuInfoAdatper.mSelectNumPosition = position
            alertDialog.show()

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
        if(menuList.size == 0){
            activity.cartEmpty()
        }
        notifyDataSetChanged()
        activity.changeOrderPrice()
    }

    fun changeNumRefresh(num: Int){
        if(mAlertDialog != null) {
            mAlertDialog!!.dismiss()
            mAlertDialog = null
        }
        mDBHelper.menuIdModify(mDB, menuList[mSelectNumPosition].id!!, num)
        menuList[mSelectNumPosition].num = num

        notifyDataSetChanged()
        activity.changeOrderPrice()
    }

    fun showDialogMenuManyNum(position: Int, num: Int){
        if(mAlertDialog != null) {
            mAlertDialog!!.dismiss()
            mAlertDialog = null
        }
        val cartMenuNumManyDialog = CartMenuNumManyDialog(this, num)
        cartMenuNumManyDialog.show(activity.supportFragmentManager, "manyNum")
        mAlertDialog = null
        mSelectNumPosition = position
    }
}