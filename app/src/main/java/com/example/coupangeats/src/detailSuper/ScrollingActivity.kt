package com.example.coupangeats.src.detailSuper.detailSuperFragment

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityScrollingBinding
import com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter.CategoryNameAdapterTest
import com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter.ImageAdapterTest
import com.example.coupangeats.src.detailSuper.detailSuperFragment.model.CouponSaveResponse
import com.example.coupangeats.src.detailSuper.detailSuperFragment.model.PhotoReview
import com.example.coupangeats.src.detailSuper.detailSuperFragment.model.SuperResponse
import com.example.coupangeats.src.menuSelect.MenuSelectActivity
import com.google.android.material.appbar.AppBarLayout
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class ScrollingActivity : BaseActivity<ActivityScrollingBinding>(ActivityScrollingBinding::inflate),
    DetailSuperActivityView {
    private var mSuperIdx = -1
    private var mCouponStatus = true
    private var mCouponPrice = -1
    private var mCouponIdx = -1
    private var textStoreIdx = 1
    private val MENU_SELECT_ACTIVITY = 1234
    private val mSuperName = "동대문 엽기떡볶이"
    private enum class CollapsingToolbarLayoutState {
        EXPANDED, COLLAPSED, INTERNEDIATE
    }
    private var mCollapsingToolbarState : CollapsingToolbarLayoutState? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSuperIdx = intent.getIntExtra("storeIdx", -1)
        if(mSuperIdx != 36) textStoreIdx = 35
        // 매장 조회 시작
        //DetailSuperService(this).tryGetSuperInfo(mSuperIdx)
        //DetailSuperService(this).tryGetSuperInfo(mSuperIdx)  // Test
        // 카트 담긴거 있는지 확인
        //cartChange()
        val menuList = ArrayList<String>()
        menuList.add("버거")
        menuList.add("탕수육")
        menuList.add("치킨")
        menuList.add("사이드메뉴")
        menuList.add("음료수")
        setMenuCategory(menuList)
        val menuItemList = ArrayList<Int>()
        menuItemList.add(R.drawable.category_asian)
        menuItemList.add(R.drawable.category_buger)
        menuItemList.add(R.drawable.category_asian)
        menuItemList.add(R.drawable.category_buger)
        menuItemList.add(R.drawable.category_asian)
        menuItemList.add(R.drawable.category_buger)
        menuItemList.add(R.drawable.category_asian)
        menuItemList.add(R.drawable.category_buger)
        menuItemList.add(R.drawable.category_asian)
        binding.detailSuperMenuRecyclerview.adapter = ImageAdapterTest(menuItemList)
        binding.detailSuperMenuRecyclerview.layoutManager = LinearLayoutManager(this)
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
    }

    fun setMenuCategory(categoryList: ArrayList<String>){
        binding.detailSuperMenuCategoryRecyclerview.adapter = CategoryNameAdapterTest(categoryList)
        binding.detailSuperMenuCategoryRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1) ?: -1

    override fun onGetSuperInfoSuccess(response: SuperResponse) {
        if( response.code == 1000 ){

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
                //cartChange()
            }
        }
    }

    fun setPhotoReview(reviewList: ArrayList<PhotoReview>) {
        //binding.detailSuperPhotoReviewRecyclerview.adapter = SuperPhotoReviewAdapter(reviewList)
        //binding.detailSuperPhotoReviewRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

}