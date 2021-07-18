package com.example.coupangeats.src.myReview

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityMyReviewBinding
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.myReview.model.MyReviewResponse
import com.example.coupangeats.src.review.adapter.ReviewPhotoViewpagerAdapter
import com.example.coupangeats.src.review.model.ReviewDeleteResponse
import com.example.coupangeats.src.reviewWrite.ReviewWriteActivity
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class MyReviewActivity : BaseActivity<ActivityMyReviewBinding>(ActivityMyReviewBinding::inflate), MyReviewActivityView {
    var mOrderIdx = -1
    var mReviewIdx = -1
    var mStoreIdx = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mOrderIdx = intent.getIntExtra("orderIdx", -1)
        mReviewIdx = intent.getIntExtra("reviewIdx", -1)

        // 뒤로가기
        binding.myReviewBack.setOnClickListener { finish() }

        // 수정하러가기
        binding.myReviewModify.setOnClickListener {
            val intent = Intent(this, ReviewWriteActivity::class.java).apply {
                this.putExtra("orderIdx", mOrderIdx)
                this.putExtra("reviewIdx", mReviewIdx)
            }
            startActivity(intent)
        }

        // 삭제하기
        binding.myReviewCancel.setOnClickListener {
            MyReviewService(this).tryPatchReviewDelete(getUserIdx(), mReviewIdx)
        }

        // 가게 보러가기
        binding.myReviewSuper.setOnClickListener {
            val intent = Intent(this, DetailSuperActivity::class.java).apply {
                this.putExtra("storeIdx", mStoreIdx)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 내가 작성한 리뷰 불러오기
        MyReviewService(this).tryGetMyReviewInfo(getUserIdx(), mReviewIdx)
    }

    fun getUserIdx(): Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    override fun onGetMyReviewInfoSuccess(response: MyReviewResponse) {
        if(response.code == 1000){
            val reviewInfo = response.result

            mStoreIdx = reviewInfo.storeIdx

            binding.myReviewSuperName.text = reviewInfo.storeName  // 매장이름
            setStar(reviewInfo.rating)  // 별점
            binding.myReviewDate.text = reviewInfo.writingTimeStamp  // 리뷰 작성한 일로부터 지난 시간

            // 이미지 뷰페이저 설정
            if(reviewInfo.imgList == null){
                binding.myReviewImgParent.visibility = View.GONE
            } else {
                binding.myReviewImgParent.visibility = View.VISIBLE
                setViewpagerSetting(reviewInfo.imgList)
            }

            binding.myReviewContent.text = reviewInfo.content  // 리뷰 내용
            binding.myReviewMenu.text = reviewInfo.orderMenus
            binding.myReviewLikeCount.text = reviewInfo.likeCount.toString()  // 도움이 돼요 개수

            if(reviewInfo.remainingReviewTime == 0){
                // 수정 불가능
                binding.myReviewModifyDate.visibility = View.GONE
            } else {
                // 수정 가능
                binding.myReviewModifyDate.visibility = View.VISIBLE
                val modifyText = "리뷰 수정기간이 ${reviewInfo.remainingReviewTime}일 남았습니다"
                binding.myReviewModifyDate.text = modifyText
            }
        }
    }

    override fun onGetMyReviewInfoFailure(message: String) {
        showCustomToast("내가 작성한 리뷰를 불러올 수 없습니다.")
    }

    override fun onPatchReviewDeleteSuccess(response: ReviewDeleteResponse) {
        if(response.code == 1000){
            // 삭제 성공
            finish()
        }
    }

    override fun onPatchReviewDeleteFailure(message: String) {

    }

    fun setStar(num: Int) {
        binding.myReviewStar1.setImageResource(R.drawable.ic_star_no)
        binding.myReviewStar2.setImageResource(R.drawable.ic_star_no)
        binding.myReviewStar3.setImageResource(R.drawable.ic_star_no)
        binding.myReviewStar4.setImageResource(R.drawable.ic_star_no)
        binding.myReviewStar5.setImageResource(R.drawable.ic_star_no)
        if (num >= 1) binding.myReviewStar1.setImageResource(R.drawable.ic_star)
        if (num >= 2) binding.myReviewStar2.setImageResource(R.drawable.ic_star)
        if (num >= 3) binding.myReviewStar3.setImageResource(R.drawable.ic_star)
        if (num >= 4) binding.myReviewStar4.setImageResource(R.drawable.ic_star)
        if (num >= 5) binding.myReviewStar5.setImageResource(R.drawable.ic_star)
    }

    fun setViewpagerSetting(imgUrls: ArrayList<String>) {
        binding.myReviewViewPager.adapter = ReviewPhotoViewpagerAdapter(imgUrls)
        binding.myReviewViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        if(imgUrls.size > 1){
            // ViewPager 에 따라 숫자 넘기기
            val totalNum = " / ${imgUrls.size}"
            binding.myReviewViewpagerTotal.text = totalNum
            binding.myReviewViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.myReviewViewpagerImgNum.text = (position + 1).toString()
                }
            })
            binding.myReviewViewpagerNumParent.visibility = View.VISIBLE
        } else {
            binding.myReviewViewpagerNumParent.visibility = View.GONE
        }

    }
}