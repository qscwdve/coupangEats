package com.example.coupangeats.src.detailSuper.detailSuperFragment

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityDetailSuperBinding
import com.example.coupangeats.src.cart.CartActivity
import com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter.CategoryNameAdapter
import com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter.DetailSuperImgViewPagerAdapter
import com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter.MenuCategoryAdapter
import com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter.SuperPhotoReviewAdapter
import com.example.coupangeats.src.detailSuper.detailSuperFragment.model.*
import com.example.coupangeats.src.menuSelect.MenuSelectActivity
import com.example.coupangeats.util.CartMenuDatabase
import com.google.android.material.appbar.AppBarLayout
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class DetailSuperActivity : BaseActivity<ActivityDetailSuperBinding>(ActivityDetailSuperBinding::inflate)
    ,DetailSuperActivityView {
    private var mSuperIdx = -1
    private var mCouponStatus = true
    private var mCouponPrice = -1
    private var mCouponIdx = -1
    private var textStoreIdx = 1
    private val MENU_SELECT_ACTIVITY = 1234
    private var mSuperName = "동대문 엽기떡볶이"
    private enum class CollapsingToolbarLayoutState {
        EXPANDED, COLLAPSED, INTERNEDIATE
    }
    private var mCollapsingToolbarState : CollapsingToolbarLayoutState? = null
    private lateinit var mDBHelper: CartMenuDatabase
    private lateinit var mDB: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 데이터베이스 셋팅
        mDBHelper = CartMenuDatabase(this, "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        mSuperIdx = intent!!.getIntExtra("storeIdx", -1)
        if(mSuperIdx != 36) textStoreIdx = 35
        // 매장 조회 시작
        //DetailSuperService(this).tryGetSuperInfo(mSuperIdx)
        DetailSuperService(this).tryGetSuperInfo(mSuperIdx)  // Test

        // 쿠폰
        binding.detailSuperCoupon.setOnClickListener {
            // 쿠폰 클릭
            if(mCouponStatus){
                // 서버 통신 일반 매장 35번으로 고정
                DetailSuperService(this).tryPostCouponSave(mSuperIdx , CouponSaveRequest(mCouponIdx, getUserIdx()))
                mCouponStatus = false
                // 쿠폰 사용으로 바꿈
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_select_box)
                val couponText = "√ ${priceIntToString(mCouponPrice)}원 쿠폰 받기완료"
                binding.detailSuperCouponText.text = couponText
                binding.detailSuperCouponImg.visibility = View.GONE
            }
        }

        // 메뉴 스크롤링
        // 스크롤링
        setSupportActionBar(binding.toolbar)
        val stateListAnimator = StateListAnimator()
        stateListAnimator.addState(intArrayOf(), ObjectAnimator.ofFloat(binding.appBar, "elevation", 0F))
        binding.appBar.stateListAnimator = stateListAnimator
        binding.appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener{
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if(verticalOffset == 0){
                    if(mCollapsingToolbarState != CollapsingToolbarLayoutState.EXPANDED){
                        binding.toolbar.setBackgroundColor(Color.parseColor("#00000000"))
                        binding.toolbarSuperName.text = ""
                        binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_white)
                        binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white)
                        binding.toolbarShare.setImageResource(R.drawable.ic_share_white)
                        mCollapsingToolbarState = CollapsingToolbarLayoutState.EXPANDED
                    }
                }
                else if(Math.abs(verticalOffset) >= appBarLayout!!.totalScrollRange){
                    // 액션바 스크롤 맨 위로 올림
                    binding.toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    binding.toolbarSuperName.text = mSuperName
                    binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_black)
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_red)
                    binding.toolbarShare.setImageResource(R.drawable.ic_share_black)
                    mCollapsingToolbarState = CollapsingToolbarLayoutState.COLLAPSED
                }
                else if(Math.abs(verticalOffset) < appBarLayout.totalScrollRange){
                    //Log.d("vertical", "아직 액션바 안에 있음")
                    binding.toolbarSuperName.text = ""
                    binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_white)
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white)
                    binding.toolbarShare.setImageResource(R.drawable.ic_share_white)
                    binding.toolbar.setBackgroundColor(Color.parseColor("#00000000"))
                    mCollapsingToolbarState = CollapsingToolbarLayoutState.INTERNEDIATE
                }
            }

        })

        //카트 보기
        binding.detailSuperCartParent.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }


    // 카트 보는거 체인지
    fun cartChange() {
        // 카트 담긴거 있는지 확인
        val num = mDBHelper.checkMenuNum(mDB)
        if(num > 0){
            // 카트가 있음
            binding.detailSuperCartParent.visibility = View.VISIBLE
            binding.detailSuperCartNum.text = num.toString()
            // 전체 가격
            val totalPrice = mDBHelper.menuTotalPrice(mDB)
            val totalPricetext = "${priceIntToString(totalPrice)}원"
            binding.detailSuperCartPrice.text = totalPricetext
        } else {
            binding.detailSuperCartParent.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        cartChange()
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1) ?: -1

    override fun onGetSuperInfoSuccess(response: SuperResponse) {
        if( response.code == 1000 ){
            setSuperInfo(response.result)
            // 매장 이름 넣기
            val edit = ApplicationClass.sSharedPreferences.edit()
            edit.putString("storeName", response.result.storeName).apply()
            mSuperName = response.result.storeName
        } else {
            // 미리 설정해둔 기본값 넣기
            // 포토 리뷰
            binding.detailSuperPhotoReviewRecyclerview.visibility = View.VISIBLE
            val photoReviewArray = ArrayList<PhotoReview>()
            val menuList = ArrayList<Menu>()
            val imgArray = ArrayList<String>()
            val menu1 = ArrayList<MenuList>()
            val menu2 = ArrayList<MenuList>()
            val menu3 = ArrayList<MenuList>()

            imgArray.add("https://www.google.com/url?sa=i&url=http%3A%2F%2Fitempage3.auction.co.kr%2FDetailView.aspx%3Fitemno%3DB421243618&psig=AOvVaw0sOfmpR-IkXKjnv6g_-gGd&ust=1624268751857000&source=images&cd=vfe&ved=0CAoQjRxqFwoTCPDsnuH2pfECFQAAAAAdAAAAABAE")
            imgArray.add("https://www.google.com/imgres?imgurl=https%3A%2F%2Fmedia.istockphoto.com%2Fphotos%2Fspicy-rice-cakes-picture-id1152570620%3Fk%3D6%26m%3D1152570620%26s%3D612x612%26w%3D0%26h%3Dcw_3cPUysYYTteUa-EmTaGePtA0OmUNKX5KqEseu91s%3D&imgrefurl=https%3A%2F%2Fwww.istockphoto.com%2Fkr%2F%25EC%259D%25B4%25EB%25AF%25B8%25EC%25A7%2580%2F%25EB%2596%25A1%25EB%25B3%25B6%25EC%259D%25B4&tbnid=9WGQmpoWaoiUZM&vet=12ahUKEwiE2fmT9qXxAhUL4GEKHXsFDE8QMygMegUIARD8AQ..i&docid=FrjgyoG-vPvKNM&w=612&h=408&q=%EB%96%A1%EB%B3%B6%EC%9D%B4&ved=2ahUKEwiE2fmT9qXxAhUL4GEKHXsFDE8QMygMegUIARD8AQ")
            imgArray.add("https://t1.daumcdn.net/cfile/tistory/22084E4B593DF4D714")
            imgArray.add("https://www.google.com/url?sa=i&url=https%3A%2F%2Fmagazine.hankyung.com%2Fbusiness%2Farticle%2F201911262453b&psig=AOvVaw00SmD3N-nqsENEPdkm-O6S&ust=1624269228028000&source=images&cd=vfe&ved=0CAoQjRxqFwoTCLDOgsX4pfECFQAAAAAdAAAAABAD") // 소떡소떡
            imgArray.add("https://www.google.com/url?sa=i&url=https%3A%2F%2Fblog.pulmuone.com%2F5303&psig=AOvVaw1OInRDRT2NA0_UtiZ1-Zxl&ust=1624269335175000&source=images&cd=vfe&ved=0CAoQjRxqFwoTCLDwq_n4pfECFQAAAAAdAAAAABAe") // 계란찜

            photoReviewArray.add(PhotoReview(1, imgArray[0], "맛있어요!", 5.0))
            photoReviewArray.add(PhotoReview(1, imgArray[1], "간이 조금 싱거워서 아쉬웠지만 비린내는 안나서 좋았어요", 3.0))
            photoReviewArray.add(PhotoReview(1, imgArray[2], "맛있어요!", 4.0))

            menu1.add(MenuList(1, "매운 떡볶이", imgArray[1], 5000, null, null, "Y"))
            menu1.add(MenuList(2, "순대", imgArray[0], 2000, null, null, null))
            menu1.add(MenuList(3, "모둠 튀김", null, 3000, null, "Y", null))

            menu2.add(MenuList(4, "소떡소떡", imgArray[3], 1000, "누구나 맛있게 즐길 수 있는 인기 메뉴", "Y", "Y"))
            menu2.add(MenuList(5, "계란찜", imgArray[4], 1500, "영양만점 계란찜", null, null))
            menu2.add(MenuList(6, "왕새우 튀김", null, 3000, null, null, null))

            menu3.add(MenuList(7, "사이다", null, 1500, null, null, null))
            menu3.add(MenuList(7, "콜라", null, 1500, null, null, null))
            menu3.add(MenuList(7, "환타", null, 1500, null, null, null))

            menuList.add(Menu("메인 메뉴", null, menu1))
            menuList.add(Menu("사이드 메뉴", null, menu2))
            menuList.add(Menu("음료수", null, menu3))
            menuList.add(Menu("메뉴1", null, menu1))
            menuList.add(Menu("메뉴2", null, menu2))
            menuList.add(Menu("메뉴3", null, menu1))

            setPhotoReview(photoReviewArray)
            setSuperImgViewPager(imgArray)
            // 메뉴 설정
            setMenuCategory(menuList)
            setMenu((menuList))

        }
    }

    override fun onGetSuperInfoFailure(message: String) {
        //showCustomToast("매장 조회에 실패하였습니다.")
    }

    override fun onPostCouponSaveSuccess(response: CouponSaveResponse) {
        if(response.code == 1000){

        }
    }

    override fun onPostCouponSaveFailure(message: String) {
        //showCustomToast("할인 쿠폰 등록에 실패하였습니다.")
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
        if(result.rating == null && result.reviewCount == null) {
            binding.detailSuperRatingReviewParent.visibility = View.GONE
        }
        else binding.detailSuperRatingReviewParent.visibility = View.VISIBLE

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
                couponText = "√ ${priceIntToString(result.coupon.price)}원 쿠폰 받기완료"
                binding.detailSuperCouponImg.visibility = View.GONE
                mCouponStatus = false
            } else {
                // 쿠폰 살아있음
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_box)
                couponText = "${priceIntToString(result.coupon.price)}원 쿠폰 받기"
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
        val deliveryPrice = if(result.deliveryPrice == 0) "무료 배달" else "${priceIntToString(result.deliveryPrice)}원"
        binding.detailSuperDeliveryPrice.text = deliveryPrice
        val orderMinPrice = "${priceIntToString(result.minPrice)}원"
        binding.detailSuperDeliveryPrice.text = orderMinPrice

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

    fun setMenu(menuCategoryList: ArrayList<Menu>) {
        binding.detailSuperMenuRecyclerview.adapter = MenuCategoryAdapter(menuCategoryList, this)
        binding.detailSuperMenuRecyclerview.layoutManager = LinearLayoutManager(this)
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

}
