package com.example.coupangeats.src.deliveryAddressSetting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.deliveryAddressSetting.DeliveryAddressSettingActivity
import com.example.coupangeats.src.deliveryAddressSetting.adapter.data.SearchAddress
import kotlin.contracts.contract

class SearchAddressListAdapter(val addressList: ArrayList<SearchAddress>, val activity: DeliveryAddressSettingActivity) : RecyclerView.Adapter<SearchAddressListAdapter.SearchAddressViewHolder>() {
    private var nowCountPage = 1
    class SearchAddressViewHolder(itemView: View, val activity: DeliveryAddressSettingActivity) : RecyclerView.ViewHolder(itemView){
        val mainAddress = itemView.findViewById<TextView>(R.id.item_delivery_address_setting_search_list_main_address)
        val subAddress = itemView.findViewById<TextView>(R.id.item_search_list_sub_address)

        fun bind(item: SearchAddress, position: Int){
            mainAddress.text = item.mainAddress
            subAddress.text = item.subAddress

            itemView.setOnClickListener {
                // 주소 선택
                 activity.changeDetailAddress(item, "", "")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_delivery_address_setting_search_list, parent, false)
        return SearchAddressViewHolder(view, activity)
    }

    override fun onBindViewHolder(holder: SearchAddressViewHolder, position: Int) {
        holder.bind(addressList[position], position)
    }

    override fun getItemCount(): Int = addressList.size
}