package com.example.coupangeats.src.review

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityReviewBinding
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.review.adapter.ReviewAdapter
import com.example.coupangeats.src.review.dialog.ReviewFilterBottomSheetDialog
import com.example.coupangeats.src.review.model.*
import com.example.coupangeats.src.reviewWrite.ReviewWriteActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 리뷰 불러오기
        mStoreIdx = intent.getIntExtra("storeIdx", -1)
        mReviewIdx = intent.getIntExtra("reviewIdx", -1)
        ReviewService(this).tryGetReviewInfo(mStoreIdx, null, null)

        binding.toolbarBack.setOnClickListener { finish() }

        // 포토리뷰
        binding.reviewFilterPhoto.setOnClickListener {
            if(mPhotoReview){
                // true -> false
                binding.reviewFilterPhotoImg.setImageResource(R.drawable.ic_add_option)
            } else {
                // false -> true
                binding.reviewFilterPhotoImg.setImageResource(R.drawable.ic_add_option_click)
            }
            mPhotoReview = !mPhotoReview
            startReviewInfo()
        }
        // 필터
        binding.reviewFilterText.setOnClickListener {
            val reviewFilterRecommendBottomSheetDialog = ReviewFilterBottomSheetDialog(this, mSortNum)
            reviewFilterRecommendBottomSheetDialog.show(supportFragmentManager, "filter")
        }

        //스티키 스크롤 설정
        binding.reviewStickyScrollView.run {
            header = binding.reviewFilterParent
            stickListener = { _ ->
                binding.reviewFilterParent.elevation = 10F
            }
            freeListener = { _ ->
                binding.reviewFilterParent.elevation = 0F
            }
        }

    }

    fun setStar(num: Double) {
        binding.reviewStar1.setImageResource(R.drawable.ic_star_no)
        binding.reviewStar2.setImageResource(R.drawable.ic_star_no)
        binding.reviewStar3.setImageResource(R.drawable.ic_star_no)
        binding.reviewStar4.setImageResource(R.drawable.ic_star_no)
        binding.reviewStar5.setImageResource(R.drawable.ic_star_no)
        if (num >= 0.0) binding.reviewStar1.setImageResource(R.drawable.ic_star)
        if (num >= 1.0) binding.reviewStar2.setImageResource(R.drawable.ic_star)
        if (num >= 2.0) binding.reviewStar3.setImageResource(R.drawable.ic_star)
        if (num >= 3.0) binding.reviewStar4.setImageResource(R.drawable.ic_star)
        if (num >= 4.0) binding.reviewStar5.setImageResource(R.drawable.ic_star)
    }

    fun startReviewInfo() {
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
            setStar(response.result.totalRating)  // 별점

            if(response.result.reviews != null){
                val reviews = response.result.reviews

                binding.reviewRecyclerview.adapter = ReviewAdapter(reviews, this)
                binding.reviewRecyclerview.layoutManager = LinearLayoutManager(this)

                //binding.reviewParent.smoothScrollTo(0, binding.reviewFilterParent.top)
                //binding.reviewParent.scrollToPosition(binding.reviewFilterParent)
            }
        }
    }
    // 리사이클러뷰에서 선택한 포지션으로 스크롤 이동
    /*fun moveScrollToPosition(position: Int){
        binding.reviewRecyclerview.scrollToPosition(position)
    }

    fun StickyScrollView.scrollToPosition(view: View) {
        val y = computeDistanceToView(view)
        //Log.d("scrolled", " scrollToPosition : $y")
        this.scrollTo(0, y)
    }

    internal fun StickyScrollView.computeDistanceToView(view: View): Int {
        return abs(calculateRectOnScreen(this).top - (this.scrollY + calculateRectOnScreen(view).top))
    }

    internal fun calculateRectOnScreen(view: View): Rect {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return Rect(
            location[0],
            location[1],
            location[0] + view.measuredWidth,
            location[1] + view.measuredHeight
        )
    }
*/
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