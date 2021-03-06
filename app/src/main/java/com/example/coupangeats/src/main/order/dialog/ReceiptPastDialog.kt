package com.example.coupangeats.src.main.order.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.databinding.DialogReceiptBinding
import com.example.coupangeats.src.main.order.adapter.ReceiptMenuAdapter
import com.example.coupangeats.src.main.order.model.OrderMenu
import com.example.coupangeats.src.main.order.model.PastOrder

class ReceiptPastDialog (val menuData: PastOrder) : DialogFragment()  {
    private lateinit var binding : DialogReceiptBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogReceiptBinding.inflate(layoutInflater)
        // 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogReceiptBack.setOnClickListener { dismiss() }

        binding.dialogReceiptStoreName.text = menuData.storeName
        binding.dialogReceiptDate.text = menuData.orderDate
        val orderMenuList = ArrayList<OrderMenu>()
        val pastList = menuData.orderMenus
        for(index in pastList.indices){
            orderMenuList.add(OrderMenu(pastList[index].count, pastList[index].menuName, pastList[index].menuDetail, pastList[index].menuPrice))
        }
        binding.dialogReceiptMenuRecyclerView.adapter = ReceiptMenuAdapter(orderMenuList)
        binding.dialogReceiptMenuRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val orderPrice = "${menuData.orderPrice}원"
        val deliveryPrice = "+${menuData.deliveryPrice}원"
        val discountPrice = if(menuData.discountPrice == "0") "${menuData.discountPrice}원" else "-${menuData.discountPrice}원"
        val totalPrice = "${menuData.totalPrice}원"
        binding.dialogReceiptOrderPrice.text = orderPrice
        binding.dialogReceiptDeliveryPrice.text = deliveryPrice
        binding.dialogReceiptDiscountPrice.text = discountPrice
        binding.dialogReceiptTotalPrice.text = totalPrice

        binding.dialogReceiptPayType.text = menuData.payType
    }


}