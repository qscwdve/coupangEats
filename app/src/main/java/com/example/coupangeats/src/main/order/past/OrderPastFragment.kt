package com.example.coupangeats.src.main.order.past

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentOrderPastBinding
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.main.order.dialog.ReceiptPastDialog
import com.example.coupangeats.src.main.order.dialog.ReceiptPrepareDialog
import com.example.coupangeats.src.main.order.model.pastOrder
import com.example.coupangeats.src.main.order.model.prepareOrder
import com.example.coupangeats.src.main.order.past.adapter.PastOrderAdapter
import com.example.coupangeats.src.main.order.past.model.OrderPastInfoResponse
import com.example.coupangeats.src.myReview.MyReviewActivity
import com.example.coupangeats.src.reviewWrite.ReviewWriteActivity
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class OrderPastFragment : BaseFragment<FragmentOrderPastBinding>(FragmentOrderPastBinding::bind, R.layout.fragment_order_past), OrderPastFragmentView {
    private var mIsSearch = false
    private var mOrderIdx = -1
    private lateinit var imm: InputMethodManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        // 요청하기
        // OrderPastService(this).tryGetOrderPastInfo(getUserIdx())
        binding.orderPastSearchText.requestFocus()

        binding.orderPastSearchText.setOnClickListener{
            if(!mIsSearch){
                mIsSearch = true
                binding.orderPastTransportMenu.visibility = View.VISIBLE
                binding.orderPastSearchText.hint = "매뉴/매장명 입력"
                imm.showSoftInput(binding.orderPastEditTextParent, 0);
            }
        }

        binding.orderPastEditTextParent.setOnClickListener {
            binding.orderPastSearchText.requestFocus()
            imm.showSoftInput(binding.orderPastSearchText, 0);
            if(!mIsSearch){
                mIsSearch = true
                binding.orderPastTransportMenu.visibility = View.VISIBLE
                binding.orderPastSearchText.hint = "매뉴/매장명 입력"
                imm.showSoftInput(binding.orderPastEditTextParent, 0);
            }
        }

        binding.orderPastTransportMenu.setOnClickListener {
            mIsSearch = false
            binding.orderPastTransportMenu.visibility = View.GONE
            binding.orderPastSearchText.hint = "주문한 메뉴/매장을 찾아보세요"
            imm.hideSoftInputFromWindow(binding.orderPastSearchText.windowToken, 0)
        }

        binding.orderPastSearchDelete.setOnClickListener {
            binding.orderPastSearchText.setText("")
            binding.orderPastSearchDelete.visibility = View.GONE
        }

        binding.orderPastSearchText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               if(binding.orderPastSearchText.text.toString().isNotEmpty()){
                   binding.orderPastSearchDelete.visibility = View.VISIBLE
               } else {
                   binding.orderPastSearchDelete.visibility = View.GONE
               }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        // 요청하기
        OrderPastService(this).tryGetOrderPastInfo(getUserIdx())
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun lookReceipt(order: pastOrder) {
        val receiptDialog = ReceiptPastDialog(order)
        receiptDialog.show(requireFragmentManager(), "receipt")
    }

    fun startDetailSuper(storeIdx: Int){
        val intent = Intent(requireContext(), DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }

    fun startReviewWrite(orderIdx: Int, reviewIdx: Int){
        val intent = Intent(requireContext(), ReviewWriteActivity::class.java).apply {
            this.putExtra("orderIdx", orderIdx)
            this.putExtra("reviewIdx", reviewIdx)
        }
        startActivity(intent)
    }

    fun startMyReview(orderIdx: Int, reviewIdx: Int){
        val intent = Intent(requireContext(), MyReviewActivity::class.java).apply {
            this.putExtra("orderIdx", orderIdx)
            this.putExtra("reviewIdx", reviewIdx)
        }
        startActivity(intent)
    }

    override fun onGetOrderPastInfoSuccess(response: OrderPastInfoResponse) {
        if(response.code == 1000 && response.result != null){
            binding.orderPastNoContent.visibility = View.GONE
            binding.orderPastSearchParent.visibility = View.VISIBLE

            binding.orderPastMenu.adapter = PastOrderAdapter(response.result, this)
            binding.orderPastMenu.layoutManager = LinearLayoutManager(requireContext())

        } else {
            binding.orderPastNoContent.visibility = View.VISIBLE
            binding.orderPastSearchParent.visibility = View.GONE
        }
    }

    override fun onGetOrderPastInfoFailure(message: String) {
        binding.orderPastNoContent.visibility = View.VISIBLE
        binding.orderPastSearchParent.visibility = View.GONE
    }
}