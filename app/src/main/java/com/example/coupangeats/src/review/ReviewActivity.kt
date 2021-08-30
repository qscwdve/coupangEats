package com.example.coupangeats.src.review

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityReviewBinding
import com.example.coupangeats.src.cart.CartActivity
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.review.adapter.ReviewAdapter
import com.example.coupangeats.src.review.dialog.ReviewFilterBottomSheetDialog
import com.example.coupangeats.src.review.model.*
import com.example.coupangeats.src.reviewWrite.ReviewWriteActivity
import com.example.coupangeats.util.CartMenuDatabase
import com.google.android.material.appbar.AppBarLayout
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.config.BaseResponse
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator
import java.lang.Math.abs

class ReviewActivity : BaseActivity<ActivityReviewBinding>(ActivityReviewBinding::inflate), ReviewActivityView {
    var mStoreIdx = -1
    var mReviewIdx = -1
    var mPhotoReview = false
    var mSort = "new"
    var mSortNum = 1
    var mDeleteItem = -1
    var mType : String? = null
    private var mCartMenuNum = 0
    private lateinit var mDBHelper: CartMenuDatabase
    private lateinit var mDB: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition( R.anim.horizon_start_enter, R.anim.horizon_start_exit)

        // 데이터베이스 셋팅
        mDBHelper = CartMenuDatabase(this, "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        // 리뷰 불러오기
        mStoreIdx = intent.getIntExtra("storeIdx", -1)
        mReviewIdx = intent.getIntExtra("reviewIdx", -1)
        Log.d("scrolled", "reviewIdx : ${mReviewIdx}")
        ReviewService(this).tryGetReviewInfo(mStoreIdx, null, null)

        binding.toolbarBack.setOnClickListener { finish() }

        // 포토리뷰
        binding.reviewFilterPhoto.setOnClickListener {
            if(mPhotoReview){
                // true -> false
                binding.reviewFilterPhotoCheck2.setBackgroundResource(R.drawable.check_box_off)
                binding.reviewFilterPhotoCheckImg2.setImageResource(R.drawable.ic_check_gray)
                binding.reviewFilterPhotoCheck.setBackgroundResource(R.drawable.check_box_off)
                binding.reviewFilterPhotoCheckImg.setImageResource(R.drawable.ic_check_gray)
            } else {
                // false -> true
                binding.reviewFilterPhotoCheck.setBackgroundResource(R.drawable.check_box_on)
                binding.reviewFilterPhotoCheckImg.setImageResource(R.drawable.ic_check_white)
                binding.reviewFilterPhotoCheck2.setBackgroundResource(R.drawable.check_box_on)
                binding.reviewFilterPhotoCheckImg2.setImageResource(R.drawable.ic_check_white)
            }
            mPhotoReview = !mPhotoReview
            startReviewInfo()
        }
        // 필터
        binding.reviewFilterTextParent.setOnClickListener {
            val reviewFilterRecommendBottomSheetDialog = ReviewFilterBottomSheetDialog(this, mSortNum)
            reviewFilterRecommendBottomSheetDialog.show(supportFragmentManager, "filter")
        }

        // 필터2
        binding.reviewFilterPhoto2.setOnClickListener {
            if(mPhotoReview){
                // true -> false
                binding.reviewFilterPhotoCheck.setBackgroundResource(R.drawable.check_box_off)
                binding.reviewFilterPhotoCheckImg.setImageResource(R.drawable.ic_check_gray)
                binding.reviewFilterPhotoCheck2.setBackgroundResource(R.drawable.check_box_off)
                binding.reviewFilterPhotoCheckImg2.setImageResource(R.drawable.ic_check_gray)
            } else {
                // false -> true
                binding.reviewFilterPhotoCheck2.setBackgroundResource(R.drawable.check_box_on)
                binding.reviewFilterPhotoCheckImg2.setImageResource(R.drawable.ic_check_white)
                binding.reviewFilterPhotoCheck.setBackgroundResource(R.drawable.check_box_on)
                binding.reviewFilterPhotoCheckImg.setImageResource(R.drawable.ic_check_white)
            }
            mPhotoReview = !mPhotoReview
            startReviewInfo()
        }

        binding.reviewFilterTextParent2.setOnClickListener {
            val reviewFilterRecommendBottomSheetDialog = ReviewFilterBottomSheetDialog(this, mSortNum)
            reviewFilterRecommendBottomSheetDialog.show(supportFragmentManager, "filter")
        }

        //스티키 스크롤 설정
        binding.reviewStickyScrollView.run {
            header = binding.reviewFilterParent2
            position = binding.reviewFilterParent
        }

        binding.reviewStickyScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY > 700){
                binding.reviewScrollUpBtn.visibility = View.VISIBLE
            } else {
                binding.reviewScrollUpBtn.visibility = View.GONE
            }
        }

        binding.reviewScrollUpBtn.setOnClickListener {
            val position = binding.reviewStickyScrollView.mHeaderParentPosition + binding.reviewStickyScrollView.mHeaderInitPosition
            binding.reviewStickyScrollView.scrollTo(0, position.toInt())
            binding.reviewScrollUpBtn.visibility = View.GONE
        }

        //카트 보기
        binding.reviewCartParent.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }
    // 카트 보는거 체인지
    fun cartChange() {
        // 카트 담긴거 있는지 확인
        val num = mDBHelper.checkMenuNum(mDB)
        if(num > 0){
            // 카트가 있음
            binding.reviewCartParent.visibility = View.VISIBLE
            binding.reviewCartNum.text = num.toString()
            // 전체 가격
            val totalPrice = mDBHelper.menuTotalPrice(mDB)
            val totalPricetext = "${priceIntToString(totalPrice)}원"
            binding.reviewCartPrice.text = totalPricetext
            // 알림 오픈
            if(mCartMenuNum != num && mCartMenuNum != 0){  }
            mCartMenuNum = num
        } else {
            binding.reviewCartParent.visibility = View.GONE
        }
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

    override fun onResume() {
        super.onResume()
        cartChange()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition( R.anim.horiaon_exit, R.anim.horizon_enter)
    }

    private fun startReviewInfo() {
        val type = if(mPhotoReview) "photo" else null
        ReviewService(this).tryGetReviewInfo(mStoreIdx, type, mSort)
    }

    fun changeRecommendFilter(sort: String, text: String, sortNum: Int){
        mSort = sort
        mSortNum = sortNum
        val filterText = "$text ∨"
        binding.reviewFilterText.text = filterText
        val type = if(mPhotoReview) "photo" else null
        ReviewService(this).tryGetReviewInfo(mStoreIdx, type, mSort)
    }

    override fun onGetReviewInfoSuccess(response: ReviewInfoResponse) {
        if(response.code == 1000){
            binding.toolbarSuperName.text = response.result.title
            binding.reviewRating.text = response.result.totalRating.toString()  // 별점 점수
            binding.reviewNum.text = response.result.reviewCount   // 리뷰 수
            binding.reviewStarRating.rating = response.result.totalRating.toFloat()  // 별점
            binding.reviewStarRating.isClickable = false

            val position = binding.reviewStickyScrollView.mHeaderInitPosition + binding.reviewStickyScrollView.mHeaderParentPosition
            binding.reviewStickyScrollView.scrollTo(0, position.toInt())

            if(response.result.reviews != null){
                val reviews = response.result.reviews
                if(mReviewIdx != -1){
                    var positionIndex = -2
                    for(index in reviews.indices){
                        if(reviews[index].reviewIdx == mReviewIdx){
                            positionIndex = index
                            break
                        }
                    }

                    binding.reviewRecyclerview.adapter = ReviewAdapter(reviews, this)
                    binding.reviewRecyclerview.layoutManager = LinearLayoutManager(this)

                    mReviewIdx = -1

                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        val diff = binding.reviewFilterParent.top - binding.reviewRecyclerview.top
                        val value = (binding.reviewRecyclerview.adapter as ReviewAdapter).getPosition(positionIndex) + binding.reviewRecyclerview.top + diff
                        binding.reviewStickyScrollView.scrollTo(0, value)
                    }, 100)
                } else {
                    if(binding.reviewRecyclerview.adapter == null) {
                        binding.reviewRecyclerview.adapter = ReviewAdapter(reviews, this)
                        binding.reviewRecyclerview.layoutManager = LinearLayoutManager(this)
                    } else {
                        (binding.reviewRecyclerview.adapter as ReviewAdapter).refresh(reviews)
                    }
                }

            }
        }
    }

    override fun onGetReviewInfoFailure(message: String) {
        // 리뷰 불러오기 실패 서버 통신 실패
        val reviewList = ArrayList<Review>()
        val imgArray = ArrayList<String>()

        imgArray.add("https://dbscthumb-phinf.pstatic.net/2765_000_49/20181011231341812_P8JFIGYEJ.jpg/5524436.jpg?type=m250&wm=N")
        imgArray.add("https://t1.daumcdn.net/cfile/tistory/22084E4B593DF4D714")
        imgArray.add("https://dbscthumb-phinf.pstatic.net/2765_000_39/20181007210844284_LW82GFYCC.jpg/247063.jpg?type=m4500_4500_fst&wm=N")

        reviewList.add(Review(1, null,"임**", 4, "오늘", null, "맛있어요 최고!!ㅎㅎ", "달콤세트", 2, null))
        reviewList.add(Review(2, null,"김**", 3, "오늘", imgArray, "너무 비려서 맛이 없어요 다음부터는 조금 덜 비리게 해주셨으면 좋겠어요 ㅜㅜ\n 배달기사 아저씨가 친절했어요", "달콤세트 + 매콤세트", 0, null))
        reviewList.add(Review(3, null,"양**", 5, "어제", imgArray, "맛있어요 최고!!ㅎㅎ", "달콤세트", 0, "NO"))
        reviewList.add(Review(4, null,"임**", 5, "어제", null, "맛있어요 최고!!ㅎㅎ", "달콤세트", 3, null))
        reviewList.add(Review(5, null,"김**", 1, "6/21", null, "망고가 너무 셔요", "과일 빙수", 1, "YES"))
        reviewList.add(Review(6, null, "이**", 2, "6/20", imgArray, "음식이 다 식어서 와서 너무 눅눅했어요. 배달기사 아저씨가 문을 너무 세게 두들겨서 깜짝 놀랐네요 ㅠㅠ", "매콤세트", 2, null))

        binding.reviewRecyclerview.adapter = ReviewAdapter(reviewList, this)
        binding.reviewRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.reviewRecyclerview.itemAnimator = FadeInDownAnimator(OvershootInterpolator(2F))
        //binding.FragmentMemoRecyclerView.itemAnimator = SlideInUpAnimator(OvershootInterpolator(2f))
        //binding.FragmentMemoRecyclerView.itemAnimator = SlideInDownAnimator(OvershootInterpolator(2F))
        //binding.FragmentMemoRecyclerView.itemAnimator = FadeInAnimator(OvershootInterpolator(2F))

    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun reviewDelete(reviewIdx: Int, position: Int) {
        ReviewService(this).tryPatchReviewDelete(getUserIdx(), reviewIdx)
        mDeleteItem = position
    }

    fun reviewModify(reviewIdx: Int){
        val intent = Intent(this, ReviewWriteActivity::class.java).apply {
            this.putExtra("reviewIdx", reviewIdx)
            this.putExtra("orderIdx", -1)
        }
        startActivity(intent)
    }

    override fun onPatchReviewDeleteSuccess(response: ReviewDeleteResponse) {
        if(response.code == 1000){
            // 리뷰 삭제 완료
            if(mDeleteItem != -1){
                ( binding.reviewRecyclerview.adapter as ReviewAdapter ).deleteItem(mDeleteItem)
            }
            mDeleteItem = -1
        }
    }

    override fun onPatchReviewDeleteFailure(message: String) {

    }

    // 리뷰 도움 돼요 안돼요 관련

    fun startReviewHelpLike(reviewIdx: Int) {
        val request = ReviewHelpRequest(getUserIdx(), reviewIdx)
        ReviewService(this).tryPostReviewHelpLike(request)
    }

    fun startReviewHelpUnlike(reviewIdx: Int){
        val request = ReviewHelpRequest(getUserIdx(), reviewIdx)
        ReviewService(this).tryPostReviewHelpUnlike(request)
    }

    fun startReviewHelpDelete(reviewIdx: Int){
        ReviewService(this).tryPatchReviewHelpDelete(getUserIdx(), reviewIdx)
    }

    override fun onPostReviewHelpLikeSuccess(response: ReviewHelpResponse) {
        if(response.code == 1000){ }
    }

    override fun onPostReviewHelpLikeFailure(message: String) {

    }

    override fun onPostReviewHelpUnlikeSuccess(response: ReviewHelpResponse) {
        if(response.code == 1000){ }
    }

    override fun onPostReviewHelpUnlikeFailure(message: String) {

    }

    override fun onPatchReviewHelpDeleteSuccess(response: BaseResponse) {
        if(response.code == 1000){ }
    }

    override fun onPatchReviewHelpDeleteFailure(message: String) {

    }
}