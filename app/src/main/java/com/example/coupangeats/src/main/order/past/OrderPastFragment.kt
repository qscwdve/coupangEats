package com.example.coupangeats.src.main.order.past

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentOrderPastBinding
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.order.dialog.ReceiptPastDialog
import com.example.coupangeats.src.main.order.model.PastOrder
import com.example.coupangeats.src.main.order.past.adapter.PastOrderAdapter
import com.example.coupangeats.src.main.order.past.model.OrderPastInfoResponse
import com.example.coupangeats.src.main.order.past.model.PastOrderMenu
import com.example.coupangeats.src.myReview.MyReviewActivity
import com.example.coupangeats.src.reviewWrite.ReviewWriteActivity
import com.example.coupangeats.src.searchDetail.SearchDetailActivity
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class OrderPastFragment(val mainActivity: MainActivity) : BaseFragment<FragmentOrderPastBinding>(
    FragmentOrderPastBinding::bind,
    R.layout.fragment_order_past
), OrderPastFragmentView {
    private var mIsSearch = false
    private var mIsSearchRequest = false
    private var mKeyword = ""
    private lateinit var imm: InputMethodManager
    private var mKeyboardStatus = false
    lateinit var reviewWriteLauncher: ActivityResultLauncher<Intent>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        binding.orderPastSearchText.clearFocus()

        // 리뷰 쓰러가기 result
        reviewWriteLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // refresh 필요
                OrderPastService(this).tryGetOrderPastInfo(getUserIdx())
                //showCustomToast("refresh")
            }
        // 키보드 상태 확인
        setKeyBoardListener()

        // 요청하기
        binding.orderPastSearchText.setText("")
        OrderPastService(this).tryGetOrderPastInfo(getUserIdx())

        binding.orderPastSearchText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (!mIsSearch) {
                    mIsSearch = true
                    binding.orderPastTransportMenu.visibility = View.VISIBLE
                    binding.orderPastSearchText.hint = "메뉴/매장명 입력"
                    imm.showSoftInput(binding.orderPastEditTextParent, 0);
                }
            }
        }

        binding.orderPastEditTextParent.setOnClickListener {
            binding.orderPastSearchText.requestFocus()
            imm.showSoftInput(binding.orderPastSearchText, 0);
        }

        // 검색 아이콘 선택
        binding.orderPastSearchImg.setOnClickListener {
            val keyword = binding.orderPastSearchText.text.toString()
            if (keyword != "") {
                mKeyword = keyword
                // 요청중..
                mIsSearchRequest = true
                OrderPastService(this).tryGetOrderPastInfo(getUserIdx(), mKeyword)
                searchFocusClear()
            }
        }

        // 검은색 밖 화면 터치시 포커스 해제
        binding.orderPastTransportMenu.setOnClickListener {
            searchFocusClear()
        }

        binding.orderPastSearchDelete.setOnClickListener {
            // 엑스 버튼 누름
            if (mIsSearch) {
                binding.orderPastSearchText.setText("")
                binding.orderPastSearchDelete.visibility = View.GONE
            } else {
                // 포커스 밖에 있을 경우
                mKeyword = ""
                binding.orderPastSearchText.setText("")
                binding.orderPastSearchDelete.visibility = View.GONE
                OrderPastService(this).tryGetOrderPastInfo(getUserIdx())
            }
        }

        binding.orderPastSearchText.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // 엔터키가 눌림
                // showCustomToast("엔터키 눌림")
                val keyword = binding.orderPastSearchText.text.toString()
                if (keyword != "") {
                    mKeyword = keyword
                    // 요청중..
                    mIsSearchRequest = true
                    OrderPastService(this).tryGetOrderPastInfo(getUserIdx(), mKeyword)
                    searchFocusClear()
                }
                true
            } else false
        }

        binding.orderPastSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mIsSearch && binding.orderPastSearchText.text.toString() == "") {
                    binding.orderPastSearchText.requestFocus()
                }
                if ((binding.orderPastSearchText.text.toString()
                        .isNotEmpty() && binding.orderPastSearchText.text.toString() != "")
                ) {
                    binding.orderPastSearchDelete.visibility = View.VISIBLE
                } else {
                    binding.orderPastSearchDelete.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 쿠팡이츠 맛집 구경가기
        binding.fragmentOrderPastLook.setOnClickListener {
            val intent = Intent(requireContext(), SearchDetailActivity::class.java).apply {
                this.putExtra("lat", mainActivity.mLat)
                this.putExtra("lon", mainActivity.mLon)
                this.putExtra("keyword", mKeyword)
            }
            startActivity(intent)
        }
    }

    fun searchFocusClear() {
        mIsSearch = false
        binding.orderPastSearchText.setText(mKeyword)
        binding.orderPastSearchText.clearFocus()
        binding.orderPastTransportMenu.visibility = View.GONE
        binding.orderPastSearchText.hint = "주문한 메뉴/매장을 찾아보세요"
        imm.hideSoftInputFromWindow(binding.orderPastSearchText.windowToken, 0)

    }

    fun getUserIdx(): Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun lookReceipt(order: PastOrder) {
        val receiptDialog = ReceiptPastDialog(order)
        receiptDialog.show(parentFragmentManager, "receipt")
    }

    fun startDetailSuper(storeIdx: Int) {
        val intent = Intent(requireContext(), DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }

    fun startReviewWrite(orderIdx: Int, reviewIdx: Int) {

        reviewWriteLauncher.launch(
            Intent(requireContext(), ReviewWriteActivity::class.java).apply {
            this.putExtra("orderIdx", orderIdx)
            this.putExtra("reviewIdx", reviewIdx)
        })
    }

    fun startMyReview(orderIdx: Int, reviewIdx: Int) {
        reviewWriteLauncher.launch( Intent(requireContext(), MyReviewActivity::class.java).apply {
            this.putExtra("orderIdx", orderIdx)
            this.putExtra("reviewIdx", reviewIdx)
        })
    }

    override fun onGetOrderPastInfoSuccess(response: OrderPastInfoResponse) {
        if (response.code == 1000) {
            binding.orderPastMenu.visibility = View.VISIBLE
            if (mIsSearchRequest) {
                mIsSearchRequest = false
                if (response.result != null) {
                    binding.orderPastNoContent.visibility = View.GONE
                    binding.orderPastSearchParent.visibility = View.VISIBLE
                    binding.orderPastNotSearch.visibility = View.GONE

                    binding.orderPastMenu.adapter =
                        PastOrderAdapter(response.result, this, mKeyword)
                    binding.orderPastMenu.layoutManager = LinearLayoutManager(requireContext())
                } else {
                    // 맛집 결과 없음
                    binding.orderPastNotSearch.visibility = View.VISIBLE
                    binding.orderPastSearchParent.visibility = View.VISIBLE
                    binding.orderPastNoContent.visibility = View.GONE
                    binding.orderPastMenu.visibility = View.GONE
                }
            } else if (response.result != null) {
                binding.orderPastNoContent.visibility = View.GONE
                binding.orderPastSearchParent.visibility = View.VISIBLE
                binding.orderPastNotSearch.visibility = View.GONE

                binding.orderPastMenu.adapter = PastOrderAdapter(response.result, this)
                binding.orderPastMenu.layoutManager = LinearLayoutManager(requireContext())
            } else {
                binding.orderPastNoContent.visibility = View.VISIBLE
                binding.orderPastSearchParent.visibility = View.GONE
                binding.orderPastNotSearch.visibility = View.GONE
            }
        } else {
            binding.orderPastNoContent.visibility = View.VISIBLE
            binding.orderPastSearchParent.visibility = View.GONE
            binding.orderPastNotSearch.visibility = View.GONE
        }
    }

    override fun onGetOrderPastInfoFailure(message: String) {
        val array = ArrayList<PastOrder>()
        val orderMenus = ArrayList<PastOrderMenu>()
        orderMenus.add(PastOrderMenu(2, "마라탕", null, "3,000원", "Y"))
        array.add(
            PastOrder(
                1,
                1,
                "마라탕 탕화쿵푸",
                null,
                "2020-09-02 12:22",
                "배달완료",
                orderMenus,
                "37,000원",
                "3,000원",
                "3,000원",
                "37,000원",
                "만나서 결제",
                null,
                null
            )
        )

        binding.orderPastMenu.adapter = PastOrderAdapter(array, this)
        binding.orderPastMenu.layoutManager = LinearLayoutManager(requireContext())

        binding.orderPastNoContent.visibility = View.GONE
        binding.orderPastSearchParent.visibility = View.VISIBLE
    }

    private fun setKeyBoardListener() {
        // 키보드 상태 확인
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {

            var navigationBarHeight = 0
            var resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                navigationBarHeight = resources.getDimensionPixelSize(resourceId)
            }

            var statusBarHeight = 0
            resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }

            val rect = Rect()
            mainActivity.window.decorView.getWindowVisibleDisplayFrame(rect)

            val keyboardHeight =
                binding.root.rootView.height - (statusBarHeight + navigationBarHeight + rect.height())

            if (keyboardHeight > 0) {
                // 키보드가 올라간 상태
                mKeyboardStatus = true
            } else {
                // 키보드가 내려간 상태
                if (mKeyboardStatus && binding.orderPastTransportMenu.visibility == View.VISIBLE) {
                    // 올라갔다가 내려갔을 경우
                    mIsSearch = false
                    binding.orderPastSearchText.setText(mKeyword)
                    binding.orderPastSearchText.clearFocus()
                    binding.orderPastTransportMenu.visibility = View.GONE
                    binding.orderPastSearchText.hint = "주문한 메뉴/매장을 찾아보세요"
                }
                mKeyboardStatus = false
            }
        }
    }
}