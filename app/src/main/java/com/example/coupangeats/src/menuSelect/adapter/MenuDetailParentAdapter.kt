package com.example.coupangeats.src.menuSelect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextClock
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.menuSelect.MenuSelectActivity
import com.example.coupangeats.src.menuSelect.model.MenuOption

class MenuDetailParentAdapter(val menuOptionList: ArrayList<MenuOption>, val activity: MenuSelectActivity) : RecyclerView.Adapter<MenuDetailParentAdapter.MenuDetailParentViewHolder>() {
    data class necessaryCheckData(var necessary: Boolean = false, var check: Boolean = false)
    val necessaryCheckList = Array(menuOptionList.size){i -> necessaryCheckData()}
    class MenuDetailParentViewHolder(itemView: View, val activity: MenuSelectActivity, val adapter: MenuDetailParentAdapter) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.item_menu_detail_parent_name)
        val necessary = itemView.findViewById<TextView>(R.id.item_menu_detail_parent_nessary)
        val recyclerView =
            itemView.findViewById<RecyclerView>(R.id.item_menu_detail_parent_recyclerView)

        fun bind(item: MenuOption, position: Int) {
            name.text = item.optionCategoryName
            if(item.requiredChoiceFlag == "Y"){
                adapter.necessaryCheckList[position].necessary = true
                necessary.visibility = View.VISIBLE
                if(item.option != null){
                    recyclerView.adapter = MenuDetailAdapter(item.option, 1, 1, position, activity, adapter)
                    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
                }
            } else {
                adapter.necessaryCheckList[position].necessary = false
                necessary.visibility = View.GONE
                if(item.option != null){
                    recyclerView.adapter = MenuDetailAdapter(item.option, 2, item.numberOfChoices, position, activity, adapter)
                    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuDetailParentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_detail_parent, parent, false)
        return MenuDetailParentViewHolder(view, activity, this)
    }

    override fun onBindViewHolder(holder: MenuDetailParentViewHolder, position: Int) {
        holder.bind(menuOptionList[position], position)
    }

    override fun getItemCount(): Int = menuOptionList.size

    fun checkNecessary() : Boolean{
        for(index in necessaryCheckList.indices){
            if(necessaryCheckList[index].necessary && (!necessaryCheckList[index].check)){
                return false
            }
        }

        return true
    }
}