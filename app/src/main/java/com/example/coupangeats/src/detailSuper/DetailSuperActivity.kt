package com.example.coupangeats.src.detailSuper

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityDetailSuperBinding
import com.example.coupangeats.src.cart.CartActivity
import com.example.coupangeats.src.detailSuper.adapter.CategoryNameAdapter
import com.example.coupangeats.src.detailSuper.adapter.DetailSuperImgViewPagerAdapter
import com.example.coupangeats.src.detailSuper.adapter.MenuCategoryAdapter
import com.example.coupangeats.src.detailSuper.adapter.SuperPhotoReviewAdapter
import com.example.coupangeats.src.detailSuper.model.*
import com.example.coupangeats.src.menuSelect.MenuSelectActivity
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class DetailSuperActivity : BaseActivity<ActivityDetailSuperBinding>(ActivityDetailSuperBinding::inflate), DetailSuperActivityView {
    private var mSuperIdx = -1
    private var mCouponStatus = true
    private var mCouponPrice = -1
    private var mCouponIdx = -1
    private var textStoreIdx = 1
    private val MENU_SELECT_ACTIVITY = 1234
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSuperIdx = intent.getIntExtra("storeIdx", -1)
        if(mSuperIdx != 36) textStoreIdx = 35
        // 매장 조회 시작
        //DetailSuperService(this).tryGetSuperInfo(mSuperIdx)
        DetailSuperService(this).tryGetSuperInfo(mSuperIdx)  // Test
        // 카트 담긴거 있는지 확인
        cartChange()

        // 쿠폰
        binding.detailSuperCoupon.setOnClickListener {
            // 쿠폰 클릭
            if(mCouponStatus){
                // 서버 통신 일반 매장 35번으로 고정
                DetailSuperService(this).tryPostCouponSave(mSuperIdx , CouponSaveRequest(mCouponIdx, getUserIdx()))
                mCouponStatus = false
                // 쿠폰 사용으로 바꿈
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_select_box)
                val couponText = "√ ${mCouponPrice}원 쿠폰 받기완료"
                binding.detailSuperCouponText.text = couponText
                binding.detailSuperCouponImg.visibility = View.GONE
            }
        }
        // 카트 보기
        binding.detailSuperCartParent.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    fun cartChange() {
        // 카트 담긴거 있는지 확인
        val menuNum = ApplicationClass.sSharedPreferences.getInt("menuNum", 0)
        if(menuNum > 0){
            // 카트가 있음
            binding.detailSuperCartParent.visibility = View.VISIBLE
            binding.detailSuperCartNum.text = menuNum.toString()
            // 전체 가격
            var totalPrice = 0
            for(i in 1..menuNum){
                val menuPrice = "menu${i}Price"
                totalPrice += ApplicationClass.sSharedPreferences.getInt(menuPrice, 0)
            }
            val totalPricetext = "${totalPrice}원"
            binding.detailSuperCartPrice.text = totalPricetext
        } else {
            binding.detailSuperCartParent.visibility = View.GONE
        }
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1) ?: -1

    override fun onGetSuperInfoSuccess(response: SuperResponse) {
        if( response.code == 1000 ){
            setSuperInfo(response.result)
        }
    }

    override fun onGetSuperInfoFailure(message: String) {
        showCustomToast("매장 조회에 실패하였습니다.")
    }

    override fun onPostCouponSaveSuccess(response: CouponSaveResponse) {
        if(response.code == 1000){

        }
    }

    override fun onPostCouponSaveFailure(message: String) {
        showCustomToast("할인 쿠폰 등록에 실패하였습니다.")
    }

    fun startMenuSelect(menuIdx: Int) {
        // 일단 매장 번호 35번
        val intent = Intent(this, MenuSelectActivity::class.java).apply{
            this.putExtra("menuIdx", menuIdx)
            this.putExtra("storeIdx", mSuperIdx)
        }
        startActivityForResult(intent, MENU_SELECT_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MENU_SELECT_ACTIVITY && resultCode == RESULT_OK){
            val menuNum = ApplicationClass.sSharedPreferences.getInt("menuNum", 0)
            if(menuNum > 0){
                // 카트 보기 열어야함
                cartChange()
            }
        }
    }

    // 메뉴 설정
    fun setSuperInfo(result : SuperResponseResult){
        setSuperImgViewPager(result.img)
        binding.detailSuperName.text = result.storeName
        // 리뷰수, 별점
        if(result.rating == null && result.reviewCount == null) binding.detailSuperNameParent.visibility = View.GONE
        else binding.detailSuperNameParent.visibility = View.VISIBLE

        if(result.rating == null){
            binding.detailSuperStar.visibility = View.GONE
            binding.detailSuperRatingNum.visibility = View.GONE
        } else {
            binding.detailSuperStar.visibility = View.VISIBLE
            binding.detailSuperRatingNum.visibility = View.VISIBLE
            binding.detailSuperRatingNum.text = result.rating.toString()
        }

        if(result.reviewCount == null){
            binding.detailSuperReviewCount.visibility = View.GONE
        } else {
            val review = "리뷰 ${result.reviewCount}개  ＞"
            binding.detailSuperReviewCount.visibility = View.VISIBLE
            binding.detailSuperReviewCount.text = review
        }

        // 할인 쿠폰
        if(result.coupon != null ){
            binding.detailSuperCoupon.visibility = View.VISIBLE
            var couponText = ""
            if(result.coupon.hasCoupon == "Y"){
                // 이미 쿠폰을 가져감
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_select_box)
                couponText = "√ ${result.coupon.price}원 쿠폰 받기완료"
                binding.detailSuperCouponImg.visibility = View.GONE
                mCouponStatus = false
            } else {
                // 쿠폰 살아있음
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_box)
                couponText = "${result.coupon.price}원 쿠폰 받기"
                mCouponStatus = true
                mCouponPrice = result.coupon.price
                mCouponIdx = result.coupon.conponIdx
            }
            binding.detailSuperCouponText.text = couponText
        }
        else {
            binding.detailSuperCoupon.visibility = View.GONE
        }
        // 시간 , 치타배달, 배달비, 최소주문
        binding.detailSuperTime.text = result.time
        binding.detailSuperCheetah.visibility = if(result.cheetah == "Y") View.VISIBLE else View.GONE
        val deliveryPrice = if(result.deliveryPrice == 0) "무료 배달" else "${result.deliveryPrice}원"
        binding.detailSuperDeliveryPrice.text = deliveryPrice
        val orderMinPrice = "${result.minPrice}원"
        binding.detailSuperMinorderPrice.text = orderMinPrice

        // 포토 리뷰
        if(result.photoReview != null){
            binding.detailSuperPhotoReviewRecyclerview.visibility = View.VISIBLE
            setPhotoReview(result.photoReview)
        } else {
            binding.detailSuperPhotoReviewRecyclerview.visibility = View.GONE
        }
        // 메뉴 설정
        if(result.menu != null) {
            setMenuCategory(result.menu)
            setMenu((result.menu))
        }

    }

    fun menuFouceItem(position: Int){
        Handler(Looper.getMainLooper()).postDelayed({
            // 해당 항목으로 메뉴 리스트 리사이클러뷰 선택해야 함
            binding.detailSuperMenuRecyclerview.scrollToPosition(position)
        }, 300)

    }

    // 어댑터 설정
    fun setSuperImgViewPager(imgList: ArrayList<String>) {
        if(imgList.size != 0){
            binding.detailSuperImgViewPager.adapter = DetailSuperImgViewPagerAdapter(imgList)
            binding.detailSuperImgViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            // ViewPager 에 따라 숫자 넘기기
            val totalNum = " / ${imgList.size}"
            binding.detailSuperImgNumTotal.text = totalNum
            binding.detailSuperImgViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.detailSuperImgNum.text = (position + 1).toString()
                }
            })
        }
    }

    fun setPhotoReview(reviewList: ArrayList<PhotoReview>) {
        binding.detailSuperPhotoReviewRecyclerview.adapter = SuperPhotoReviewAdapter(reviewList)
        binding.detailSuperPhotoReviewRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    fun setMenuCategory(categoryList: ArrayList<Menu>){
        binding.detailSuperMenuCategoryRecyclerview.adapter = CategoryNameAdapter(categoryList, this)
        binding.detailSuperMenuCategoryRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    fun setMenu(menuCategoryList: ArrayList<Menu>){
        binding.detailSuperMenuRecyclerview.adapter = MenuCategoryAdapter(menuCategoryList, this)
        binding.detailSuperMenuRecyclerview.layoutManager = LinearLayoutManager(this)
    }
}