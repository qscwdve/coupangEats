package com.example.coupangeats.src.cart

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityCartBinding
import com.example.coupangeats.src.cart.adapter.CartMenuInfoAdatper
import com.example.coupangeats.src.cart.model.*
import com.example.coupangeats.src.discount.DiscountActivity
import com.example.coupangeats.util.CartMenuDatabase
import com.example.coupangeats.util.CartOrderRider
import com.example.coupangeats.util.CartSuperOrder
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class CartActivity : BaseActivity<ActivityCartBinding>(ActivityCartBinding::inflate), CartActivityView{
    var mCouponPrice = 0
    var mDeliveryPrice = 0
    var mMenuPrice = 0
    var mTotalPrice = 0
    var mCouponStatus = false
    var mCouponIdx = -1
    var mRiderOrder = -1
    var mCheckSpoon = "N"
    var mSuperOrderString = ""
    private lateinit var mDBHelper: CartMenuDatabase
    private lateinit var mDB: SQLiteDatabase
    private val DISCOUNT_ACTIVITY_NUM = 1234
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 데이터베이스 셋팅
        mDBHelper = CartMenuDatabase(this, "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        binding.cartFill.visibility = View.VISIBLE
        binding.cartEmpty.visibility = View.GONE

        // 서버에서 카트보기 불러오기
        showLoadingDialog(this)
        CartService(this).tryGetCartLook(getUserIdx(), getStoreIdx())
        menuSetting()

        // 매장 이름
        binding.cartStoreName.text = ApplicationClass.sSharedPreferences.getString("storeName", "매장 없음")
        binding.cartBack.setOnClickListener { finish() }

        // 할인쿠폰 보러가기
        binding.cartCouponChange.setOnClickListener { lookCouponList() }
        binding.cartCouponPrice.setOnClickListener { lookCouponList() }

        // 배달 요청사항
        binding.cartRiderOrder.setOnClickListener {
            val cartOrderRider = CartOrderRider(this, mRiderOrder)
            cartOrderRider.show(supportFragmentManager, "riderOrder")
        }
        // 일회용 젓가락 사용 비사용
        binding.cartOneSpoonCheck.setOnClickListener {
            if(mCheckSpoon == "N"){
                binding.cartOneSpoonCheck.setImageResource(R.drawable.ic_add_option_click)
                mCheckSpoon = "Y"
            } else {
                binding.cartOneSpoonCheck.setImageResource(R.drawable.ic_add_option)
                mCheckSpoon = "N"
            }
        }
        // 가게 요청사항
        binding.cartSuperOrder.setOnClickListener {
            mSuperOrderString = binding.cartSuperOrder.text.toString()
            val cartSuperOrder = CartSuperOrder(this)
            cartSuperOrder.show(supportFragmentManager, "superOrder")
        }
        // 요청사항 보여지고 안보여지고
        binding.cartRequestChange.setOnClickListener {
            if(binding.cartRequestParent.visibility == View.VISIBLE)
                binding.cartRequestParent.visibility = View.GONE
            else
                binding.cartRequestParent.visibility = View.VISIBLE
        }
        // 결제하기!!
        binding.cartOk.setOnClickListener {
            // 서버 통신 결제 모듈
            val address = binding.cartDeliveryRoadAddress.text.toString()
            val storeIdx = getStoreIdx()
            val order = getOrderMenu()
            val couponIdx = if(mCouponIdx != -1) mCouponIdx else null
            val orderPrice = mMenuPrice
            val deliveryPrice = mDeliveryPrice
            val discountPrice = mCouponPrice
            val totalPrice = mTotalPrice
            val storeOrder = binding.cartSuperOrder.text.toString()
            val checkEchoProduct = mCheckSpoon
            val deliveryRequests = binding.cartRiderOrder.text.toString()
            val payType = "만나서 현금결제"
            val userIdx = getUserIdx()
            val request = OrderRequest(address, storeIdx, order, couponIdx, orderPrice, deliveryPrice, discountPrice, totalPrice, storeOrder, checkEchoProduct, deliveryRequests, payType, userIdx)
            // 서버 통신하기
            showLoadingDialog(this)
            CartService(this).tryPostOrder(request)
        }
    }

    fun lookCouponList() {
        val intent = Intent(this, DiscountActivity::class.java).apply {
            this.putExtra("storeIdx", getStoreIdx())
            this.putExtra("selectCouponIdx", mCouponIdx)
        }
        startActivityForResult(intent, DISCOUNT_ACTIVITY_NUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == DISCOUNT_ACTIVITY_NUM){

        }
    }
    fun getOrderMenu() : ArrayList<OrderMenu> {
        val array = ArrayList<OrderMenu>()
        val orderMenuList = mDBHelper.menuSelect(mDB)
        for(item in orderMenuList){
            array.add(OrderMenu(item.menuIdx, item.name, item.sub, item.num, item.price))
        }
        return array
    }
    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)
    fun getStoreIdx() : Int = ApplicationClass.sSharedPreferences.getInt("storeIdx", -1)

    fun menuSetting(){
        // 메뉴 세팅

        // 메뉴 불러오기
        val menuList = mDBHelper.menuSelect(mDB)

        // 메뉴 어댑터 설정
        binding.cartMenuRecyclerView.adapter = CartMenuInfoAdatper(menuList, this)
        binding.cartMenuRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    // 각 메뉴 개수 재설정
    fun changeOrderPrice() {
        // 주문 금액
        mMenuPrice = mDBHelper.menuTotalPrice(mDB)
        val menuPrice = "${priceIntToString(mMenuPrice)}원"
        binding.cartMenuPrice.text = menuPrice

        // 총 결제 금액
        mTotalPrice = mMenuPrice + mDeliveryPrice - mCouponPrice
        val superTotalPrice = "${priceIntToString(mTotalPrice)}원"
        binding.cartMenuTotalPrice.text = superTotalPrice
        val total = "$superTotalPrice 결제하기"
        binding.cartOk.text = total
    }

    override fun onGetCartLookSuccess(response: CartMenuLookResponse) {
        dismissLoadingDialog()
        if(response.code == 1000){
            val result = response.result
            // 주소
            binding.cartDeliveryMainAddress.text = result.mainAddress
            binding.cartDeliveryRoadAddress.text = result.address
            // 쿠폰 제어
            couponSetting(result.coupon)

            // 주문 금액
            mMenuPrice = mDBHelper.menuTotalPrice(mDB)
            val menuPrice = "${priceIntToString(mMenuPrice)}원"
            binding.cartMenuPrice.text = menuPrice

            // 배달비
            val totalPrice = "+" + priceIntToString(result.deliveryPrice) + "원"
            mDeliveryPrice = result.deliveryPrice
            binding.cartMenuDelveryPrice.text = totalPrice

            // 총 결제 금액
            mTotalPrice = mMenuPrice + mDeliveryPrice - mCouponPrice
            val superTotalPrice = "${priceIntToString(mTotalPrice)}원"
            binding.cartMenuTotalPrice.text = superTotalPrice
            val total = "$superTotalPrice 결제하기"
            binding.cartOk.text = total
        }
    }

    override fun onGetCartLookFailure(message: String) {
        dismissLoadingDialog()
        //showCustomToast("카트 보기가 실패하였습니다.")
    }

    override fun onPostOrderSuccess(response: OrderResponse) {
        dismissLoadingDialog()
        if(response.code == 1000){
            // 주문내역 다 삭제
            mDBHelper.deleteTotal(mDB)
            finish()
        }
    }

    override fun onPostOrderFailure(message: String) {
        dismissLoadingDialog()
        //showCustomToast("결제하기 실패")
    }

    fun couponSetting(coupon: CartCoupon) {
        if(coupon.redeemStatus == null){
            binding.cartCouponStatus.visibility = View.GONE
        } else {
            if(coupon.redeemStatus == "쿠폰 적용" || coupon.redeemStatus == "쿠폰 자동적용"){
                binding.cartCouponStatus.visibility = View.VISIBLE
                binding.cartCouponStatus.text = if(coupon.redeemStatus == "쿠폰 적용") "쿠폰 적용" else "쿠폰 자동 적용"
                binding.cartCouponStatus.setTextColor(Color.parseColor("#C4263A")) // 빨강
                // 쿠폰 가격 설정
                val couponPrice = "-${coupon.price}"
                binding.cartCouponPrice.text = couponPrice
                binding.cartCouponPrice.setTextColor(Color.parseColor("#000000")) // 검은색
                binding.cartCouponChange.text = "변경"
                mCouponPrice = coupon.price
                mCouponStatus = true
                mCouponIdx = coupon.couponIdx!!
            } else {
                // 쿠폰적용 가능한 쿠폰이 있는 상태
                binding.cartCouponStatus.visibility = View.VISIBLE
                binding.cartCouponStatus.text = "쿠폰 적용 가능"
                binding.cartCouponStatus.setTextColor(Color.parseColor("#949DA6")) // 회색
                mCouponStatus = false
            }
        }
        // 쿠폰 개수
        val couponNum = "${coupon.couponCount}장  ▶"
        binding.cartCouponNum.text = couponNum
    }

    // 배달 요청사항 고르기
    fun changeRiderOrder(index: Int, value: String) {
        binding.cartRiderOrder.text = value
        mRiderOrder = index
        // 7번일 경우 직접 입력하기가 보여야함
    }

    // 가게 요청사항 고르기
    fun changeSuperOrder(value: String){
        binding.cartSuperOrder.text = value
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
    fun cartEmpty(){
        binding.cartFill.visibility = View.GONE
        binding.cartEmpty.visibility = View.VISIBLE
    }
}