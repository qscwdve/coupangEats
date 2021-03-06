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

class ReviewAdapter(var reviewList: ArrayList<Review>, val reviewActivity: ReviewActivity) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    var parentList = Array<LinearLayout?>(reviewList.size) {i -> null}
    class ReviewViewHolder(itemView: View, val reviewActivity: ReviewActivity, val adapter: ReviewAdapter) : RecyclerView.ViewHolder(itemView) {
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
        val reWrite = itemView.findViewById<LinearLayout>(R.id.item_reviews_rewrite_parent)
        val report = itemView.findViewById<TextView>(R.id.item_review_report)
        val modify = itemView.findViewById<LinearLayout>(R.id.item_review_modify)
        val cancel = itemView.findViewById<LinearLayout>(R.id.item_review_cancel)
        val evaluation = itemView.findViewById<LinearLayout>(R.id.item_review_evaluation_parent)
        val cartSpace = itemView.findViewById<LinearLayout>(R.id.item_review_cart_space)

        fun bind(review: Review, position: Int) {
            // ?????? ????????? ????????? ??????
            if(reviewActivity.mCartMenuNum > 0 && position == adapter.reviewList.lastIndex){
                cartSpace.visibility = View.VISIBLE
            } else {
                cartSpace.visibility = View.GONE
            }

            adapter.parentList[position] = reviewParent

            name.text = review.writerName  // ??????
            setStar(review.rating)   // ??????
            date.text = review.writingTimeStamp  // ??????
            // ??????
            if(review.imageUrls == null){
                imgParent.visibility = View.GONE
            } else {
                imgParent.visibility = View.VISIBLE
                setViewpagerSetting(review.imageUrls)
            }
            content.text = review.text  // ?????? ??????
            menu.text = review.orderMenus   // ?????? ??????

            // ???????????? ??? ????????? ??????
            if(review.isWriter == "Y"){
                report.visibility = View.GONE
                cancel.visibility = View.VISIBLE
                reWrite.visibility = View.VISIBLE
                modify.visibility = if(review.isModifiable == null) View.GONE else View.VISIBLE
                reviewParent.setBackgroundColor(Color.parseColor("#F5F6F8"))
                evaluation.visibility = View.GONE

                modify.setOnClickListener {
                    // ?????? ??????
                    reviewActivity.reviewModify(review.reviewIdx)
                }
                cancel.setOnClickListener {
                    // ?????? ??????
                    reviewActivity.reviewDelete(review.reviewIdx, adapterPosition)
                }
            } else {
                report.visibility = View.VISIBLE
                cancel.visibility = View.GONE
                reWrite.visibility = View.GONE
                evaluation.visibility = View.VISIBLE

                reviewParent.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            // ?????? ????????? ?????? & ?????????
            setReviewCountSetting(review.likeCount)
            // ????????? ????????? ?????? ????????? ??????
            when(review.isLiked){
                "YES" -> changeLike(review.likeCount, review.reviewIdx, 1)
                "NO" -> changeDislike(review.likeCount, review.reviewIdx, 1)
                else -> changeNull(review.likeCount, review.reviewIdx, 1)
            }
            likeParent.setOnClickListener {
                if(review.isLiked == "YES"){
                    // Yes -> NULL
                    review.likeCount--
                    changeNull(review.likeCount, review.reviewIdx)
                    review.isLiked = null
                } else {
                    // NO -> YES , null -> YES
                    review.likeCount++
                    changeLike(review.likeCount, review.reviewIdx)
                    review.isLiked = "YES"
                }
            }
            dislikeParent.setOnClickListener {
                if(review.isLiked == "NO"){
                    // NO -> null
                    changeNull(review.likeCount, review.reviewIdx)
                    review.isLiked = null
                } else if(review.isLiked == "YES"){
                    // YES -> NO
                    review.likeCount--
                    changeDislike(review.likeCount, review.reviewIdx)
                    review.isLiked = "NO"
                } else {
                    // null -> NO
                    changeDislike(review.likeCount, review.reviewIdx)
                    review.isLiked = "NO"
                }
            }
        }
        private fun setReviewCountSetting(likeCount: Int){
            if(likeCount <= 0){
                reviewCount.visibility = View.GONE
                reviewCountText.text = "????????? ????????? ?????????????"
            } else {
                reviewCount.visibility = View.VISIBLE
                reviewCount.text = likeCount.toString()
                reviewCountText.text = "????????? ????????? ????????????!"
            }
        }
        fun changeLike(num: Int, reviewIdx: Int, version: Int? = null){
            setReviewCountSetting(num)
            likeParent.setBackgroundResource(R.drawable.dialog_login_basic)
            likeImg.setImageResource(R.drawable.ic_like_blue)
            likeText.setTextColor(Color.parseColor("#00AFFE"))
            dislikeParent.setBackgroundResource(R.drawable.delivery_gps_box)
            dislikeImg.setImageResource(R.drawable.ic_dislike)
            dislikeText.setTextColor(Color.parseColor("#949DA6"))  // baseColors
            // ?????? ??????
            if(version == null) reviewActivity.startReviewHelpLike(reviewIdx)
        }
        fun changeDislike(num: Int, reviewIdx: Int, version: Int? = null){
            setReviewCountSetting(num)
            likeParent.setBackgroundResource(R.drawable.delivery_gps_box)
            likeImg.setImageResource(R.drawable.ic_like)
            likeText.setTextColor(Color.parseColor("#949DA6"))  // baseColors
            dislikeParent.setBackgroundResource(R.drawable.dialog_login_basic)
            dislikeImg.setImageResource(R.drawable.ic_dislike_blue)
            dislikeText.setTextColor(Color.parseColor("#00AFFE"))
            // ?????? ??????
            if(version == null) reviewActivity.startReviewHelpUnlike(reviewIdx)
        }
        private fun changeNull(num: Int, reviewIdx: Int, version: Int? = null){
            setReviewCountSetting(num)
            likeParent.setBackgroundResource(R.drawable.delivery_gps_box)
            likeImg.setImageResource(R.drawable.ic_like)
            likeText.setTextColor(Color.parseColor("#949DA6"))  // baseColors
            dislikeParent.setBackgroundResource(R.drawable.delivery_gps_box)
            dislikeImg.setImageResource(R.drawable.ic_dislike)
            dislikeText.setTextColor(Color.parseColor("#949DA6"))  // baseColors
            // ?????? ??????
            if(version == null) reviewActivity.startReviewHelpDelete(reviewIdx)
        }

        private fun setStar(num: Int) {
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

        private fun setViewpagerSetting(imgUrls: ArrayList<String>) {
            imgViewPager.adapter = ReviewPhotoViewpagerAdapter(imgUrls)
            imgViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            if(imgUrls.size > 1){
                // ViewPager ??? ?????? ?????? ?????????
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
        return ReviewViewHolder(view, reviewActivity, this)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position], position)
    }

    override fun getItemCount(): Int = reviewList.size

    fun deleteItem(position: Int){
        reviewList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getPosition(position: Int) : Int{
        return parentList[position]?.top ?: 0
    }

    fun refresh(array: ArrayList<Review>){
        reviewList = array
        notifyDataSetChanged()
    }
}