package com.example.coupangeats.src.detailSuper

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityDetailSuperBinding
import com.example.coupangeats.databinding.DialogDeliveryDetailBinding
import com.example.coupangeats.src.SuperInfo.SuperInfoActivity
import com.example.coupangeats.src.cart.CartActivity
import com.example.coupangeats.src.detailSuper.adapter.CategoryNameAdapter
import com.example.coupangeats.src.detailSuper.adapter.DetailSuperImgViewPagerAdapter
import com.example.coupangeats.src.detailSuper.adapter.MenuCategoryAdapter
import com.example.coupangeats.src.detailSuper.adapter.SuperPhotoReviewAdapter
import com.example.coupangeats.src.detailSuper.model.*
import com.example.coupangeats.src.favorites.model.FavoritesSuperDeleteRequest
import com.example.coupangeats.src.lookImage.LookImageActivity
import com.example.coupangeats.src.menuSelect.MenuSelectActivity
import com.example.coupangeats.src.review.ReviewActivity
import com.example.coupangeats.util.CartMenuDatabase
import com.google.android.material.appbar.AppBarLayout
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.config.BaseResponse
import kotlin.math.abs

@SuppressLint("HandlerLeak")
class DetailSuperActivity : BaseActivity<ActivityDetailSuperBinding>(ActivityDetailSuperBinding::inflate)
    , DetailSuperActivityView {
    private var mIsServer = true
    private var mSuperIdx = -1
    private var mCouponStatus = true
    private var mCouponPrice = -1
    private var mCouponIdx = -1
    private var textStoreIdx = 1
    private var mSuperName = "동대문 엽기떡볶이"
    private var mCartMenuNum = 0
    private var mFavorites = 0
    private var mMenuPosition = 0
    private enum class CollapsingToolbarLayoutState {
        EXPANDED, COLLAPSED, INTERNEDIATE
    }
    private var mCollapsingToolbarState : CollapsingToolbarLayoutState? = null
    private lateinit var mDBHelper: CartMenuDatabase
    private lateinit var mDB: SQLiteDatabase
    private var myHandler = MyHandler()
    private var mSize = 0
    private val intervalTime = 2000.toLong() // 몇초 간격으로 페이지를 넘길것인지 (1500 = 1.5초)
    lateinit var menuSelectActivityLauncher : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition( R.anim.horizon_start_enter, R.anim.horizon_start_exit)

        menuSelectActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK){
                cartChange()
            }
        }
        // 데이터베이스 셋팅
        mDBHelper = CartMenuDatabase(this, "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        binding.toolbarBack.setOnClickListener {
            finish()
        }

        mSuperIdx = intent!!.getIntExtra("storeIdx", -1)
        if(mSuperIdx != 36) textStoreIdx = 35
        // 매장 조회 시작
        //DetailSuperService(this).tryGetSuperInfo(mSuperIdx)
        DetailSuperService(this).tryGetSuperInfo(mSuperIdx)  // Test

        // 리뷰 보러 가기
        binding.detailSuperReviewCount.setOnClickListener {
            startReviewPosition()
        }
        // 매장, 원산지 정보보기
        binding.detailSuperInfo.setOnClickListener {
            val intent = Intent(this, SuperInfoActivity::class.java).apply {
                this.putExtra("storeIdx", mSuperIdx)
            }
            startActivity(intent)
        }
        // 자세히
        binding.detailSuperDeliveryPriceDetail.setOnClickListener {
            showDialogDeliveryDetail(
                binding.detailSuperDeliveryPrice.text.toString(),
                binding.detailSuperMinorderPrice.text.toString() + "~"
            )
        }
        // 쿠폰
        binding.detailSuperCoupon.setOnClickListener {
            // 쿠폰 클릭
            if(mCouponStatus){
                // 서버 통신 일반 매장 35번으로 고정
                DetailSuperService(this).tryPostCouponSave(mSuperIdx , CouponSaveRequest(mCouponIdx, getUserIdx()))
                mCouponStatus = false
                // 쿠폰 사용으로 바꿈
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_select_box)
                val couponText = "${priceIntToString(mCouponPrice)}원 쿠폰 받기완료"
                binding.detailSuperCouponText.setTextColor(Color.parseColor("#949DA6"))
                binding.detailSuperCouponText.text = couponText
                binding.detailSuperCouponImg.setImageResource(R.drawable.ic_check_dark_gray)
            }
        }

        // 즐겨찾기 추가
        binding.toolbarHeart.setOnClickListener {
            if(mFavorites == 0){
                // 즐겨찾기에 추가함
                if(mCollapsingToolbarState == CollapsingToolbarLayoutState.COLLAPSED){
                    // red
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_red_fill)
                } else {
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white_fill)
                }
                mFavorites = 1
                DetailSuperService(this).tryPostBookMarkAdd(BookMarkAddRequest(getUserIdx(), mSuperIdx))
            } else {
                // 즐겨찾기 빼기
                if(mCollapsingToolbarState == CollapsingToolbarLayoutState.COLLAPSED){
                    // red
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_red)
                } else {
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white)
                }
                mFavorites = 0
                val storeList = ArrayList<Int>()
                storeList.add(mSuperIdx)
                DetailSuperService(this).tryPostFavoritesSuperDelete(getUserIdx(), FavoritesSuperDeleteRequest(storeList))
            }
        }

        // 메뉴 스크롤링
        // 스크롤링
        setSupportActionBar(binding.toolbar)
        val stateListAnimator = StateListAnimator()
        stateListAnimator.addState(intArrayOf(), ObjectAnimator.ofFloat(binding.appBar, "elevation", 0F))
        binding.appBar.stateListAnimator = stateListAnimator
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if(verticalOffset == 0){
                if(mCollapsingToolbarState != CollapsingToolbarLayoutState.EXPANDED){
                    binding.toolbar.setBackgroundColor(Color.parseColor("#00000000"))
                    binding.toolbarSuperName.text = ""
                    binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_white)
                    if(mFavorites == 0)
                        binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white)
                    else
                        binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white_fill)
                    binding.toolbarShare.setImageResource(R.drawable.ic_share_white)
                    mCollapsingToolbarState = CollapsingToolbarLayoutState.EXPANDED
                }
            } else if(Math.abs(verticalOffset) >= appBarLayout!!.totalScrollRange){
                // 액션바 스크롤 맨 위로 올림
                binding.toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.toolbarSuperName.text = mSuperName
                binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_black)
                if(mFavorites == 0)
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_red)
                else
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_red_fill)
                binding.toolbarShare.setImageResource(R.drawable.ic_share_black)
                mCollapsingToolbarState = CollapsingToolbarLayoutState.COLLAPSED
            } else if(abs(verticalOffset) < appBarLayout.totalScrollRange){
                //Log.d("vertical", "아직 액션바 안에 있음")
                if(mCollapsingToolbarState != CollapsingToolbarLayoutState.INTERNEDIATE){
                    binding.toolbarSuperName.text = ""
                    binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_white)
                    if(mFavorites == 0)
                        binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white)
                    else
                        binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white_fill)
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

        // 메뉴 스크롤
        binding.detailSuperMenuRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastPosition = (recyclerView.adapter!!.itemCount - 1)
                val itemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()

                if(lastVisibleItemPosition == lastPosition){
                    // 마지막
                    if(mMenuPosition != lastPosition){
                        (binding.detailSuperMenuCategoryRecyclerview.adapter as CategoryNameAdapter).changeHighlightCheck(lastPosition)
                        binding.detailSuperMenuCategoryRecyclerview.smoothScrollToPosition(lastPosition)
                        mMenuPosition = lastPosition
                    }
                } else if(itemPosition != mMenuPosition){
                    mMenuPosition = itemPosition
                    (binding.detailSuperMenuCategoryRecyclerview.adapter as CategoryNameAdapter).changeHighlightCheck(mMenuPosition)
                    binding.detailSuperMenuCategoryRecyclerview.smoothScrollToPosition(itemPosition)
                }
            }

        })
    }

    // 리뷰 보러가기
    fun startReviewPosition(reviewIdx : Int = -1){
        val intent = Intent(this, ReviewActivity::class.java).apply {
            this.putExtra("storeIdx", mSuperIdx)
            this.putExtra("reviewIdx", reviewIdx)
        }
        startActivity(intent)
    }

    private fun showDialogDeliveryDetail(delivery:String, minOrder: String) {
        val deliveryDetailBinding = DialogDeliveryDetailBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(deliveryDetailBinding.root)
        builder.setCancelable(true)

        deliveryDetailBinding.deliveryDetailMinOrder.text = minOrder
        deliveryDetailBinding.deliveryDetailDeliveryPrice.text = delivery
        val alertDialog = builder.create()
        val window = alertDialog.window
        if(window != null){
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            alertDialog.window!!.attributes = params
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog.show()

        deliveryDetailBinding.deliveryDetailCheck.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition( R.anim.horiaon_exit, R.anim.horizon_enter)
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
            // 알림 오픈
            if(mCartMenuNum != num && mCartMenuNum != 0){  }
            mCartMenuNum = num
            (binding.detailSuperMenuRecyclerview.adapter as MenuCategoryAdapter?)?.changeLastFlag(true)
        } else {
            binding.detailSuperCartParent.visibility = View.GONE
            (binding.detailSuperMenuRecyclerview.adapter as MenuCategoryAdapter?)?.changeLastFlag(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if(mSize > 1) autoScrollStart()
        cartChange()
    }

    override fun onDestroy() {
        if(!mIsServer){
            mDBHelper.deleteTotal(mDB)
        }
        super.onDestroy()
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1) ?: -1

    private fun statusBarHeightControl(){
        // status bar 높이 차이 해결
        val vto: ViewTreeObserver = binding.toolbarItem.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.toolbarItem.viewTreeObserver.removeOnGlobalLayoutListener(this)
                binding.appBar.layoutParams.height = binding.toolbarItem.measuredHeight

            }
        })
    }

    override fun onGetSuperInfoSuccess(response: SuperResponse) {
        if( response.code == 1000 ){
            setSuperInfo(response.result)
            if(response.result.isBookmarked == "Y"){
                mFavorites = 1
                binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white_fill)
            } else {
                binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white)
            }
            // 매장 이름 넣기
            val edit = ApplicationClass.sSharedPreferences.edit()
            edit.putString("storeName", response.result.storeName).apply()
            mSuperName = response.result.storeName
        } else {
            // 매장 조회 실패
            notServerDumyData()
        }
    }

    override fun onGetSuperInfoFailure(message: String) {
        //showCustomToast("매장 조회에 실패하였습니다.")
        notServerDumyData()
    }

    override fun onPostCouponSaveSuccess(response: CouponSaveResponse) {
        if(response.code == 1000){

        }
    }

    override fun onPostCouponSaveFailure(message: String) {
        //showCustomToast("할인 쿠폰 등록에 실패하였습니다.")
    }

    override fun onPostBookMarkAddSuccess(response: BookMarkAddResponse) {

    }

    override fun onPostBookMarkAddFailure(message: String) {

    }

    override fun onPostFavoritesSuperDeleteSuccess(response: BaseResponse) {
        if(response.code == 1000){

        }
    }

    override fun onPostFavoritesSuperDeleteFailure(message: String) {

    }

    fun notServerDumyData() {
        // 미리 설정해둔 기본값 넣기
        // 포토 리뷰
        mIsServer = false
        binding.detailSuperPhotoReviewRecyclerview.visibility = View.VISIBLE
        val photoReviewArray = ArrayList<PhotoReview>()
        val menuList = ArrayList<Menu>()
        val imgArray = ArrayList<String>()
        val menu1 = ArrayList<MenuList>()
        val menu2 = ArrayList<MenuList>()
        val menu3 = ArrayList<MenuList>()

        imgArray.add("https://dbscthumb-phinf.pstatic.net/2765_000_49/20181011231341812_P8JFIGYEJ.jpg/5524436.jpg?type=m250&wm=N")
        imgArray.add("https://t1.daumcdn.net/cfile/tistory/22084E4B593DF4D714")
        imgArray.add("https://dbscthumb-phinf.pstatic.net/2765_000_39/20181007210844284_LW82GFYCC.jpg/247063.jpg?type=m4500_4500_fst&wm=N")

        photoReviewArray.add(PhotoReview(1, imgArray[1], "맛있어요!", 5.0))
        photoReviewArray.add(PhotoReview(1, imgArray[0], "간이 조금 싱거워서 아쉬웠지만 비린내는 안나서 좋았어요", 3.0))
        photoReviewArray.add(PhotoReview(1, imgArray[2], "맛있어요!", 4.0))

        menu1.add(MenuList(1, "매운 떡볶이", imgArray[0], 5000, null, null, "Y"))
        menu1.add(MenuList(2, "순대", imgArray[1], 2000, null, null, null))
        menu1.add(MenuList(3, "모둠 튀김", null, 3000, null, "Y", null))

        menu2.add(MenuList(4, "소떡소떡", null, 1000, "누구나 맛있게 즐길 수 있는 인기 메뉴", "Y", "Y"))
        menu2.add(MenuList(5, "계란찜", imgArray[2], 1500, "영양만점 계란찜", null, null))
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

        statusBarHeightControl()
    }

    fun startMenuSelect(menuIdx: Int) {
        // 일단 매장 번호 35번
        val intent = Intent(this, MenuSelectActivity::class.java).apply{
            this.putExtra("menuIdx", menuIdx)
            this.putExtra("storeIdx", mSuperIdx)
            this.putExtra("storeName", mSuperName)
        }
        menuSelectActivityLauncher.launch(intent)
    }


    // 메뉴 설정
    private fun setSuperInfo(result : SuperResponseResult){
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
            val count = " ${result.reviewCount}"
            binding.detailSuperReviewCount.visibility = View.VISIBLE
            binding.detailSuperReviewCountText.text = count
        }

        // 할인 쿠폰
        if(result.coupon != null ){
            binding.detailSuperCoupon.visibility = View.VISIBLE
            var couponText = ""
            if(result.coupon.hasCoupon == "Y"){
                // 이미 쿠폰을 가져감
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_select_box)
                couponText = "${priceIntToString(result.coupon.price)}원 쿠폰 받기완료"
                binding.detailSuperCouponText.setTextColor(Color.parseColor("#949DA6"))
                binding.detailSuperCouponImg.visibility = View.VISIBLE
                binding.detailSuperCouponImg.setImageResource(R.drawable.ic_check_dark_gray)
                mCouponStatus = false
            } else {
                // 쿠폰 살아있음
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_box)
                couponText = "${priceIntToString(result.coupon.price)}원 쿠폰 받기"
                binding.detailSuperCouponText.setTextColor(Color.parseColor("#00AFFE"))
                binding.detailSuperCouponImg.setImageResource(R.drawable.ic_download)
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

        statusBarHeightControl()
    }

    fun menuFouceItem(position: Int){
        Handler(Looper.getMainLooper()).postDelayed({
            // 해당 항목으로 메뉴 리스트 리사이클러뷰 선택해야 함
            //binding.detailSuperMenuRecyclerview.smoothScrollToPosition(position)
            binding.detailSuperMenuRecyclerview.scrollToPosition(position)
        }, 300)

    }

    // 어댑터 설정
    private fun setSuperImgViewPager(imgList: ArrayList<String>) {
        mSize = imgList.size
        if( mSize != 0){
            binding.detailSuperImgViewPager.adapter = DetailSuperImgViewPagerAdapter(imgList, this)
            binding.detailSuperImgViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            // ViewPager 에 따라 숫자 넘기기
            val totalNum = " / ${imgList.size}"
            binding.detailSuperImgNumTotal.text = totalNum
            binding.detailSuperImgViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.detailSuperImgNum.text = (position%mSize + 1).toString()
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if(mSize > 1){
                        when(state){
                            // 멈춰있을 때
                            ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart()
                            ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                            ViewPager2.SCROLL_STATE_SETTLING -> { }
                        }
                    }

                }
            })

        }
    }

    private fun autoScrollStart() {
        myHandler.removeMessages(0) // 이거 안하면 핸들러가 1개, 2개, 3개 ... n개 만큼 계속 늘어남
        myHandler.sendEmptyMessageDelayed(0, intervalTime) // intervalTime 만큼 반복해서 핸들러를 실행하게 함
    }

    private fun autoScrollStop(){
        myHandler.removeMessages(0) // 핸들러를 중지시킴
    }

    private inner class MyHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0) {
                val count = binding.detailSuperImgViewPager.currentItem + 1
                binding.detailSuperImgViewPager.setCurrentItem(count, true) // 다음 페이지로 이동
                autoScrollStart() // 스크롤을 계속 이어서 한다.
            }
        }
    }

    fun startLookImageActivity(num: Int, imgString: String){
        val intent = Intent(this, LookImageActivity::class.java).apply {
            putExtra("imgList", imgString)
            putExtra("position", num)
        }
        Log.d("imgList", "${imgString}")
        startActivity(intent)
    }

    // 다른 페이지로 떠나있는 동안 스크롤이 동작할 필요는 없음. 정지
    override fun onPause() {
        super.onPause()
        if(mSize > 1) autoScrollStop()
    }

    private fun setPhotoReview(reviewList: ArrayList<PhotoReview>) {
        binding.detailSuperPhotoReviewRecyclerview.adapter = SuperPhotoReviewAdapter(reviewList, this)
        binding.detailSuperPhotoReviewRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setMenuCategory(categoryList: ArrayList<Menu>){
        binding.detailSuperMenuCategoryRecyclerview.adapter = CategoryNameAdapter(categoryList, this)
        binding.detailSuperMenuCategoryRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setMenu(menuCategoryList: ArrayList<Menu>) {
        binding.detailSuperMenuRecyclerview.adapter = MenuCategoryAdapter(menuCategoryList, this)
        binding.detailSuperMenuRecyclerview.layoutManager = LinearLayoutManager(this)

        // 카트 보기에 따라 메뉴 리사이클러뷰의 크기를 조절해야함
        cartChange()
    }

    private fun priceIntToString(value: Int) : String {
        val target = value.toString()
        val size = target.length
        return if(size > 3){
            val last = target.substring(size - 3 until size)
            val first = target.substring(0..(size - 4))
            "$first,$last"
        } else target
    }

}
