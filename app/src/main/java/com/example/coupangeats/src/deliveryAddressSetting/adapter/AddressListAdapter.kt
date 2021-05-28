package com.example.coupangeats.src.deliveryAddressSetting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.deliveryAddressSetting.DeliveryAddressSettingActivity
import com.example.coupangeats.src.deliveryAddressSetting.model.BaseAddress

class AddressListAdapter(val addressList: ArrayList<BaseAddress>, val selectedIdx: Int, val deliveryAddressSettingActivity: DeliveryAddressSettingActivity) : RecyclerView.Adapter<AddressListAdapter.AddressViewHolder>(){
    class AddressViewHolder(itemView: View,val deliveryAddressSettingActivity: DeliveryAddressSettingActivity) : RecyclerView.ViewHolder(itemView){
        val mainAddress = itemView.findViewById<TextView>(R.id.item_delivery_address_setting_title)
        val subAddress = itemView.findViewById<TextView>(R.id.item_delivery_address_setting_detail)
        val check = itemView.findViewById<ImageView>(R.id.item_delivery_address_setting_checked)

        fun bind(item: BaseAddress, selectedIdx: Int){
            mainAddress.text = item.mainAddress
            subAddress.text = item.subAddress
            if(item.addressIdx == selectedIdx) check.visibility = View.VISIBLE
            else check.visibility = View.INVISIBLE

            itemView.setOnClickListener {
                // 주소 선택 -> 배달지 주소 선택됨
                if(deliveryAddressSettingActivity.version == deliveryAddressSettingActivity.GPS_SELECT)
                    deliveryAddressSettingActivity.finishActivitySelectedData(item.mainAddress, item.addressIdx)
                else {
                    // 배달지 수정
                    deliveryAddressSettingActivity.startDeliveryAddressModify(item.addressIdx)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(deliveryAddressSettingActivity).inflate(R.layout.item_delivery_address_setting, parent, false)
        return AddressViewHolder(view, deliveryAddressSettingActivity)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addressList[position], selectedIdx)
    }

    override fun getItemCount(): Int = addressList.size
}