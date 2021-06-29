package com.example.coupangeats.src.main.order.prepare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentOrderPrepareBinding
import com.example.coupangeats.src.deliveryStatus.DeliveryStatusActivity
import com.example.coupangeats.src.main.order.OrderFragment
import com.example.coupangeats.src.main.order.dialog.ReceiptPrepareDialog
import com.example.coupangeats.src.main.order.model.prepareOrder
import com.example.coupangeats.src.main.order.prepare.adapter.PrepareOrderAdapter
import com.example.coupangeats.src.main.order.prepare.model.OrderPrepareInfoResponse
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment
import okhttp3.internal.notify

class OrderPrepareFragment : BaseFragment<FragmentOrderPrepareBinding>(FragmentOrderPrepareBinding::bind, R.layout.fragment_order_prepare), OrderPrepareFragmentView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.orderPreparePastLook.setOnClickListener {
            //(requireParentFragment() as OrderFragment).changeTab()
        }

    }

    override fun onResume() {
        super.onResume()
        OrderPrepareService(this).tryGetOrderPrepareInfo(getUserIdx())
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    override fun onGetOrderPrepareInfoSuccess(response: OrderPrepareInfoResponse) {
        if(response.code == 1000 && response.result != null){
            binding.orderPrepareNot.visibility = View.GONE
            binding.orderPrepareRecyclerView.visibility = View.VISIBLE

            binding.orderPrepareRecyclerView.adapter = PrepareOrderAdapter(response.result, this)
            binding.orderPrepareRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        } else {
            binding.orderPrepareNot.visibility = View.VISIBLE
            binding.orderPrepareRecyclerView.visibility = View.GONE
        }
    }

    override fun onGetOrderPrepareInfoFailure(message: String) {
        binding.orderPrepareNot.visibility = View.VISIBLE
        binding.orderPrepareRecyclerView.visibility = View.GONE
    }

    fun lookReceipt(order: prepareOrder) {
        val receiptDialog = ReceiptPrepareDialog(order)
        receiptDialog.show(requireFragmentManager(), "receipt")
    }

    fun startDeliveryStatus(){
        startActivity(Intent(requireContext(), DeliveryStatusActivity::class.java))
    }
}