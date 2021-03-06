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
    private var mSuperName = "????????? ???????????????"
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
    private val intervalTime = 2000.toLong() // ?????? ???????????? ???????????? ??????????????? (1500 = 1.5???)
    lateinit var menuSelectActivityLauncher : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition( R.anim.horizon_start_enter, R.anim.horizon_start_exit)

        menuSelectActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK){
                cartChange()
            }
        }
        // ?????????????????? ??????
        mDBHelper = CartMenuDatabase(this, "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        binding.toolbarBack.setOnClickListener {
            finish()
        }

        mSuperIdx = intent!!.getIntExtra("storeIdx", -1)
        if(mSuperIdx != 36) textStoreIdx = 35
        // ?????? ?????? ??????
        //DetailSuperService(this).tryGetSuperInfo(mSuperIdx)
        DetailSuperService(this).tryGetSuperInfo(mSuperIdx)  // Test

        // ?????? ?????? ??????
        binding.detailSuperReviewCount.setOnClickListener {
            startReviewPosition()
        }
        // ??????, ????????? ????????????
        binding.detailSuperInfo.setOnClickListener {
            val intent = Intent(this, SuperInfoActivity::class.java).apply {
                this.putExtra("storeIdx", mSuperIdx)
            }
            startActivity(intent)
        }
        // ?????????
        binding.detailSuperDeliveryPriceDetail.setOnClickListener {
            showDialogDeliveryDetail(
                binding.detailSuperDeliveryPrice.text.toString(),
                binding.detailSuperMinorderPrice.text.toString() + "~"
            )
        }
        // ??????
        binding.detailSuperCoupon.setOnClickListener {
            // ?????? ??????
            if(mCouponStatus){
                // ?????? ?????? ?????? ?????? 35????????? ??????
                DetailSuperService(this).tryPostCouponSave(mSuperIdx , CouponSaveRequest(mCouponIdx, getUserIdx()))
                mCouponStatus = false
                // ?????? ???????????? ??????
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_select_box)
                val couponText = "${priceIntToString(mCouponPrice)}??? ?????? ????????????"
                binding.detailSuperCouponText.setTextColor(Color.parseColor("#949DA6"))
                binding.detailSuperCouponText.text = couponText
                binding.detailSuperCouponImg.setImageResource(R.drawable.ic_check_dark_gray)
            }
        }

        // ???????????? ??????
        binding.toolbarHeart.setOnClickListener {
            if(mFavorites == 0){
                // ??????????????? ?????????
                if(mCollapsingToolbarState == CollapsingToolbarLayoutState.COLLAPSED){
                    // red
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_red_fill)
                } else {
                    binding.toolbarHeart.setImageResource(R.drawable.ic_heart_white_fill)
                }
                mFavorites = 1
                DetailSuperService(this).tryPostBookMarkAdd(BookMarkAddRequest(getUserIdx(), mSuperIdx))
            } else {
                // ???????????? ??????
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

        // ?????? ????????????
        // ????????????
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
                // ????????? ????????? ??? ?????? ??????
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
                //Log.d("vertical", "?????? ????????? ?????? ??????")
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

        //?????? ??????
        binding.detailSuperCartParent.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // ?????? ?????????
        binding.detailSuperMenuRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastPosition = (recyclerView.adapter!!.itemCount - 1)
                val itemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()

                if(lastVisibleItemPosition == lastPosition){
                    // ?????????
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

    // ?????? ????????????
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

    // ?????? ????????? ?????????
    fun cartChange() {
        // ?????? ????????? ????????? ??????
        val num = mDBHelper.checkMenuNum(mDB)
        if(num > 0){
            // ????????? ??????
            binding.detailSuperCartParent.visibility = View.VISIBLE
            binding.detailSuperCartNum.text = num.toString()
            // ?????? ??????
            val totalPrice = mDBHelper.menuTotalPrice(mDB)
            val totalPricetext = "${priceIntToString(totalPrice)}???"
            binding.detailSuperCartPrice.text = totalPricetext
            // ?????? ??????
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
        // status bar ?????? ?????? ??????
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
            // ?????? ?????? ??????
            val edit = ApplicationClass.sSharedPreferences.edit()
            edit.putString("storeName", response.result.storeName).apply()
            mSuperName = response.result.storeName
        } else {
            // ?????? ?????? ??????
            notServerDumyData()
        }
    }

    override fun onGetSuperInfoFailure(message: String) {
        //showCustomToast("?????? ????????? ?????????????????????.")
        notServerDumyData()
    }

    override fun onPostCouponSaveSuccess(response: CouponSaveResponse) {
        if(response.code == 1000){

        }
    }

    override fun onPostCouponSaveFailure(message: String) {
        //showCustomToast("?????? ?????? ????????? ?????????????????????.")
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
        // ?????? ???????????? ????????? ??????
        // ?????? ??????
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

        photoReviewArray.add(PhotoReview(1, imgArray[1], "????????????!", 5.0))
        photoReviewArray.add(PhotoReview(1, imgArray[0], "?????? ?????? ???????????? ??????????????? ???????????? ????????? ????????????", 3.0))
        photoReviewArray.add(PhotoReview(1, imgArray[2], "????????????!", 4.0))

        menu1.add(MenuList(1, "?????? ?????????", imgArray[0], 5000, null, null, "Y"))
        menu1.add(MenuList(2, "??????", imgArray[1], 2000, null, null, null))
        menu1.add(MenuList(3, "?????? ??????", null, 3000, null, "Y", null))

        menu2.add(MenuList(4, "????????????", null, 1000, "????????? ????????? ?????? ??? ?????? ?????? ??????", "Y", "Y"))
        menu2.add(MenuList(5, "?????????", imgArray[2], 1500, "???????????? ?????????", null, null))
        menu2.add(MenuList(6, "????????? ??????", null, 3000, null, null, null))

        menu3.add(MenuList(7, "?????????", null, 1500, null, null, null))
        menu3.add(MenuList(7, "??????", null, 1500, null, null, null))
        menu3.add(MenuList(7, "??????", null, 1500, null, null, null))

        menuList.add(Menu("?????? ??????", null, menu1))
        menuList.add(Menu("????????? ??????", null, menu2))
        menuList.add(Menu("?????????", null, menu3))
        menuList.add(Menu("??????1", null, menu1))
        menuList.add(Menu("??????2", null, menu2))
        menuList.add(Menu("??????3", null, menu1))

        setPhotoReview(photoReviewArray)
        setSuperImgViewPager(imgArray)
        // ?????? ??????
        setMenuCategory(menuList)
        setMenu((menuList))

        statusBarHeightControl()
    }

    fun startMenuSelect(menuIdx: Int) {
        // ?????? ?????? ?????? 35???
        val intent = Intent(this, MenuSelectActivity::class.java).apply{
            this.putExtra("menuIdx", menuIdx)
            this.putExtra("storeIdx", mSuperIdx)
            this.putExtra("storeName", mSuperName)
        }
        menuSelectActivityLauncher.launch(intent)
    }


    // ?????? ??????
    private fun setSuperInfo(result : SuperResponseResult){
        setSuperImgViewPager(result.img)
        binding.detailSuperName.text = result.storeName
        // ?????????, ??????
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

        // ?????? ??????
        if(result.coupon != null ){
            binding.detailSuperCoupon.visibility = View.VISIBLE
            var couponText = ""
            if(result.coupon.hasCoupon == "Y"){
                // ?????? ????????? ?????????
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_select_box)
                couponText = "${priceIntToString(result.coupon.price)}??? ?????? ????????????"
                binding.detailSuperCouponText.setTextColor(Color.parseColor("#949DA6"))
                binding.detailSuperCouponImg.visibility = View.VISIBLE
                binding.detailSuperCouponImg.setImageResource(R.drawable.ic_check_dark_gray)
                mCouponStatus = false
            } else {
                // ?????? ????????????
                binding.detailSuperCoupon.setBackgroundResource(R.drawable.detail_super_coupon_box)
                couponText = "${priceIntToString(result.coupon.price)}??? ?????? ??????"
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
        // ?????? , ????????????, ?????????, ????????????
        binding.detailSuperTime.text = result.time
        binding.detailSuperCheetah.visibility = if(result.cheetah == "Y") View.VISIBLE else View.GONE
        val deliveryPrice = if(result.deliveryPrice == 0) "?????? ??????" else "${priceIntToString(result.deliveryPrice)}???"
        binding.detailSuperDeliveryPrice.text = deliveryPrice
        val orderMinPrice = "${priceIntToString(result.minPrice)}???"
        binding.detailSuperMinorderPrice.text = orderMinPrice

        // ?????? ??????
        if(result.photoReview != null){
            binding.detailSuperPhotoReviewRecyclerview.visibility = View.VISIBLE
            setPhotoReview(result.photoReview)
        } else {
            binding.detailSuperPhotoReviewRecyclerview.visibility = View.GONE
        }
        // ?????? ??????
        if(result.menu != null) {
            setMenuCategory(result.menu)
            setMenu((result.menu))
        }

        statusBarHeightControl()
    }

    fun menuFouceItem(position: Int){
        Handler(Looper.getMainLooper()).postDelayed({
            // ?????? ???????????? ?????? ????????? ?????????????????? ???????????? ???
            //binding.detailSuperMenuRecyclerview.smoothScrollToPosition(position)
            binding.detailSuperMenuRecyclerview.scrollToPosition(position)
        }, 300)

    }

    // ????????? ??????
    private fun setSuperImgViewPager(imgList: ArrayList<String>) {
        mSize = imgList.size
        if( mSize != 0){
            binding.detailSuperImgViewPager.adapter = DetailSuperImgViewPagerAdapter(imgList, this)
            binding.detailSuperImgViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            // ViewPager ??? ?????? ?????? ?????????
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
                            // ???????????? ???
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
        myHandler.removeMessages(0) // ?????? ????????? ???????????? 1???, 2???, 3??? ... n??? ?????? ?????? ?????????
        myHandler.sendEmptyMessageDelayed(0, intervalTime) // intervalTime ?????? ???????????? ???????????? ???????????? ???
    }

    private fun autoScrollStop(){
        myHandler.removeMessages(0) // ???????????? ????????????
    }

    private inner class MyHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0) {
                val count = binding.detailSuperImgViewPager.currentItem + 1
                binding.detailSuperImgViewPager.setCurrentItem(count, true) // ?????? ???????????? ??????
                autoScrollStart() // ???????????? ?????? ????????? ??????.
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

    // ?????? ???????????? ???????????? ?????? ???????????? ????????? ????????? ??????. ??????
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

        // ?????? ????????? ?????? ?????? ????????????????????? ????????? ???????????????
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
