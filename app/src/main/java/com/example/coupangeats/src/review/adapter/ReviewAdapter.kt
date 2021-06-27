package com.example.coupangeats.src.review.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.src.review.ReviewActivity
import com.example.coupangeats.src.review.model.Review
import org.w3c.dom.Text

class ReviewAdapter(val reviewList: ArrayList<Review>, val reviewActivity: ReviewActivity) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    class ReviewViewHolder(itemView: View, val reviewActivity: ReviewActivity) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.item_review_name)
        val star1 = itemView.findViewById<ImageView>(R.id.item_review_star1)
        val star2 = itemView.findViewById<ImageView>(R.id.item_review_star2)
        val star3 = itemView.findViewById<ImageView>(R.id.item_review_star3)
        val star4 = itemView.findViewById<ImageView>(R.id.item_review_star4)
        val star5 = itemView.findViewById<ImageView>(R.id.item_review_star5)
        val date = itemView.findViewById<TextView>(R.id.item_review_date)
        val imgParent = itemView.findViewById<RelativeLayout>(R.id.item_review_img_parent)
        val imgViewPager = itemView.findViewById<ViewPager2>(R.id.item_review_viewpager)
        val imgViewPagerNumParent = itemView.findViewById<LinearLayout>(R.id.item_review_viewpager_num_parent)
        val imgNum = itemView.findViewById<TextView>(R.id.item_review_viewpager_img_num)
        val imgTotal = itemView.findViewById<TextView>(R.id.item_review_viewpager_total)
        val content = itemView.findViewById<TextView>(R.id.item_review_text)
        val menu = itemView.findViewById<TextView>(R.id.item_review_menu)
        val reviewCount = itemView.findViewById<TextView>(R.id.item_review_like_count)
        val reviewCountText = itemView.findViewById<TextView>(R.id.item_review_like_count_text)
        val likeParent = itemView.findViewById<LinearLayout>(R.id.item_review_like_parent)
        val likeImg = itemView.findViewById<ImageView>(R.id.item_review_like_img)
        val likeText = itemView.findViewById<TextView>(R.id.item_review_like_text)
        val dislikeParent = itemView.findViewById<LinearLayout>(R.id.item_review_dislike_parent)
        val dislikeImg = itemView.findViewById<ImageView>(R.id.item_review_dislike_img)
        val dislikeText = itemView.findViewById<TextView>(R.id.item_review_dislike_text)
        val reviewParent = itemView.findViewById<LinearLayout>(R.id.item_review_parent)
        val reWrite = itemView.findViewById<LinearLayout>(R.id.item_review_rewrite)
        val report = itemView.findViewById<TextView>(R.id.item_review_report)
        val modify = itemView.findViewById<LinearLayout>(R.id.item_review_modify)
        val cancel = itemView.findViewById<LinearLayout>(R.id.item_review_cancel)
        val evaluation = itemView.findViewById<LinearLayout>(R.id.item_review_evaluation_parent)

        fun bind(review: Review) {
            name.text = review.writerName  // 이름
            setStar(review.rating)   // 별점
            date.text = review.writingTimeStamp  // 날짜
            // 사진
            if(review.imageUrls == null){
                imgParent.visibility = View.GONE
            } else {
                imgParent.visibility = View.VISIBLE
                setViewpagerSetting(review.imageUrls)
            }
            content.text = review.text  // 리뷰 내용
            menu.text = review.orderMenus   // 주문 메뉴

            // 사용자가 쓴 리뷰일 경우
            if(review.isWriter == "Y"){
                report.visibility = View.GONE
                reWrite.visibility = View.VISIBLE
                reviewParent.setBackgroundColor(Color.parseColor("#F5F6F8"))
                evaluation.visibility = View.GONE

                modify.setOnClickListener {
                    // 리뷰 수정
                }
                cancel.setOnClickListener {
                    // 리뷰 삭제
                }
            } else {
                report.visibility = View.VISIBLE
                reWrite.visibility = View.GONE
                evaluation.visibility = View.VISIBLE

                reviewParent.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            // 리뷰 도움이 돼요 & 안돼요
            setReviewCountSetting(review.likeCount)
            // 리뷰에 도움이 되요 안되요 박스
            when(review.isLiked){
                "YES" -> changeLike(review.likeCount)
                "NO" -> changeDislike(review.likeCount)
                else -> changeNull(review.likeCount)
            }
            likeParent.setOnClickListener {
                if(review.isLiked == "YES"){
                    // Yes -> NULL
                    review.likeCount--
                    changeNull(review.likeCount)
                    review.isLiked = null
                } else {
                    // NO -> YES , null -> YES
                    review.likeCount++
                    changeLike(review.likeCount)
                    review.isLiked = "YES"
                }
            }
            dislikeParent.setOnClickListener {
                if(review.isLiked == "NO"){
                    // NO -> null
                    changeNull(review.likeCount)
                    review.isLiked = null
                } else if(review.isLiked == "YES"){
                    // YES -> NO
                    review.likeCount--
                    changeDislike(review.likeCount)
                    review.isLiked = "NO"
                } else {
                    // null -> NO
                    changeDislike(review.likeCount)
                    review.isLiked = "NO"
                }
            }
            if(reviewActivity.mReviewIdx == review.reviewIdx){
                Log.d("scrolled", "reviewIdx : ${review.reviewIdx}")
                //reviewActivity.moveScrollToPosition(adapterPosition)
                reviewActivity.mReviewIdx = -1
            }
        }
        fun setReviewCountSetting(likeCount: Int){
            if(likeCount <= 0){
                reviewCount.visibility = View.GONE
                reviewCountText.text = "리뷰가 도움이 되었나요?"
            } else {
                reviewCount.visibility = View.VISIBLE
                reviewCount.text = likeCount.toString()
                reviewCountText.text = "명에게 도움이 되었어요!"
            }
        }
        fun changeLike(num: Int){
            setReviewCountSetting(num)
            likeParent.setBackgroundResource(R.drawable.dialog_login_basic)
            likeImg.setImageResource(R.drawable.ic_like_blue)
            likeText.setTextColor(Color.parseColor("#00AFFE"))
            dislikeParent.setBackgroundResource(R.drawable.delivery_gps_box)
            dislikeImg.setImageResource(R.drawable.ic_dislike)
            dislikeText.setTextColor(Color.parseColor("#949DA6"))  // baseColors
        }
        fun changeDislike(num: Int){
            setReviewCountSetting(num)
            likeParent.setBackgroundResource(R.drawable.delivery_gps_box)
            likeImg.setImageResource(R.drawable.ic_like)
            likeText.setTextColor(Color.parseColor("#949DA6"))  // baseColors
            dislikeParent.setBackgroundResource(R.drawable.dialog_login_basic)
            dislikeImg.setImageResource(R.drawable.ic_dislike_blue)
            dislikeText.setTextColor(Color.parseColor("#00AFFE"))

        }
        fun changeNull(num: Int){
            setReviewCountSetting(num)
            likeParent.setBackgroundResource(R.drawable.delivery_gps_box)
            likeImg.setImageResource(R.drawable.ic_like)
            likeText.setTextColor(Color.parseColor("#949DA6"))  // baseColors
            dislikeParent.setBackgroundResource(R.drawable.delivery_gps_box)
            dislikeImg.setImageResource(R.drawable.ic_dislike)
            dislikeText.setTextColor(Color.parseColor("#949DA6"))  // baseColors

        }

        fun setStar(num: Int) {
            star1.setImageResource(R.drawable.ic_star_no)
            star2.setImageResource(R.drawable.ic_star_no)
            star3.setImageResource(R.drawable.ic_star_no)
            star4.setImageResource(R.drawable.ic_star_no)
            star5.setImageResource(R.drawable.ic_star_no)
            if (num >= 1) star1.setImageResource(R.drawable.ic_star)
            if (num >= 2) star2.setImageResource(R.drawable.ic_star)
            if (num >= 3) star3.setImageResource(R.drawable.ic_star)
            if (num >= 4) star4.setImageResource(R.drawable.ic_star)
            if (num >= 5) star5.setImageResource(R.drawable.ic_star)
        }

        fun setViewpagerSetting(imgUrls: ArrayList<String>) {
            imgViewPager.adapter = ReviewPhotoViewpagerAdapter(imgUrls)
            imgViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            if(imgUrls.size > 1){
                // ViewPager 에 따라 숫자 넘기기
                val totalNum = " / ${imgUrls.size}"
                imgTotal.text = totalNum
                imgViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        imgNum.text = (position + 1).toString()
                    }
                })
                imgViewPagerNumParent.visibility = View.VISIBLE
            } else {
                imgViewPagerNumParent.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view, reviewActivity)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int = reviewList.size
}