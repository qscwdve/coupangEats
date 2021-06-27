package com.example.coupangeats.src.reviewWrite.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.reviewWrite.ReviewWriteActivity
import com.example.coupangeats.src.reviewWrite.adapter.dialog.ReviewWriteMenuOpinionBottomSheet
import com.example.coupangeats.src.reviewWrite.adapter.model.ReviewWriteMenu
import org.w3c.dom.Text

class ReviewWriteMenuAdapter(val menuList: ArrayList<ReviewWriteMenu>, val activity: ReviewWriteActivity) : RecyclerView.Adapter<ReviewWriteMenuAdapter.ReviewWriteMenuViewHolder>() {
    class ReviewWriteMenuViewHolder(itemView: View, val reviewWriteMenuAdapter: ReviewWriteMenuAdapter) : RecyclerView.ViewHolder(itemView){
        val menuName = itemView.findViewById<TextView>(R.id.item_review_write_menu_name)
        val sideName = itemView.findViewById<TextView>(R.id.item_review_write_side_menu_name)
        val like = itemView.findViewById<LinearLayout>(R.id.item_review_write_like)
        val likeImg = itemView.findViewById<ImageView>(R.id.item_review_write_like_img)
        val dislike = itemView.findViewById<LinearLayout>(R.id.item_review_write_dislike)
        val dislikeImg = itemView.findViewById<ImageView>(R.id.item_review_write_dislike_img)
        val opinion = itemView.findViewById<RelativeLayout>(R.id.item_review_write_menu_opinion)
        val opinionSmallAmount = itemView.findViewById<LinearLayout>(R.id.item_review_write_small_amount)
        val opinionVerySalt = itemView.findViewById<LinearLayout>(R.id.item_review_write_very_salt)
        val opinionVeryFresh = itemView.findViewById<LinearLayout>(R.id.item_review_write_very_fresh)
        val opinionCold = itemView.findViewById<LinearLayout>(R.id.item_review_write_cold)
        val opinionExpensive = itemView.findViewById<LinearLayout>(R.id.item_review_write_expensive)
        val etc = itemView.findViewById<LinearLayout>(R.id.item_review_write_etc)
        val etcText = itemView.findViewById<TextView>(R.id.item_review_write_etc_text)
        val etcChange = itemView.findViewById<TextView>(R.id.item_review_write_stc_change)

        fun bind(item: ReviewWriteMenu, position: Int){
            menuName.text = item.menuName   // 메뉴 이름
            sideName.text = item.menuSideName  // 메뉴 사이드 이름

            if(item.menuGood == null) setLikeAndDisLikeNot(position)
            else if(item.menuGood == true) setLike(position)
            else setDisLike(position)

            if(item.menuEtc != null || item.menuEtc == ""){
                etcText.text = item.menuEtc
                etcText.setTextColor(Color.parseColor("#949DA6"))  // 회색
                etcChange.visibility = View.VISIBLE
            } else {
                etcText.text = "기타의견"
                etcText.setTextColor(Color.parseColor("#00AFFE"))  // 파란색
                etcChange.visibility = View.GONE
            }

            like.setOnClickListener { setLike(position) }
            dislike.setOnClickListener { setDisLike(position) }

            opinionSmallAmount.setOnClickListener { opinionChange(opinionSmallAmount, position, 0) }
            opinionVerySalt.setOnClickListener { opinionChange(opinionVerySalt, position, 1) }
            opinionVeryFresh.setOnClickListener { opinionChange(opinionVeryFresh, position, 2) }
            opinionCold.setOnClickListener { opinionChange(opinionCold, position, 3) }
            opinionExpensive.setOnClickListener { opinionChange(opinionExpensive, position, 4) }

            etc.setOnClickListener {
                val reviewWriteMenuOpinionBottomSheet = ReviewWriteMenuOpinionBottomSheet(reviewWriteMenuAdapter, position)
                reviewWriteMenuOpinionBottomSheet.show(reviewWriteMenuAdapter.activity.supportFragmentManager, "etcOpinion")
            }

            etcChange.setOnClickListener {
                val reviewWriteMenuOpinionBottomSheet = ReviewWriteMenuOpinionBottomSheet(reviewWriteMenuAdapter, position)
                reviewWriteMenuOpinionBottomSheet.show(reviewWriteMenuAdapter.activity.supportFragmentManager, "etcOpinion")
            }
        }
        fun setLike(position: Int) {
            like.setBackgroundResource(R.drawable.review_like_box_check)
            likeImg.setImageResource(R.drawable.ic_review_like_check)
            opinion.visibility = View.GONE
            dislike.setBackgroundResource(R.drawable.review_like_box)
            dislikeImg.setImageResource(R.drawable.ic_review_dislike)
            reviewWriteMenuAdapter.menuList[position].menuGood = true
        }
        fun setDisLike(position: Int) {
            dislike.setBackgroundResource(R.drawable.review_like_box_check)
            dislikeImg.setImageResource(R.drawable.ic_review_dislike_check)
            opinion.visibility = View.VISIBLE
            like.setBackgroundResource(R.drawable.review_like_box)
            likeImg.setImageResource(R.drawable.ic_review_like)
            reviewWriteMenuAdapter.menuList[position].menuGood = false
            reviewWriteMenuAdapter.menuList[position].menuGood = false
        }
        fun setLikeAndDisLikeNot(position: Int){
            like.setBackgroundResource(R.drawable.review_like_box)
            likeImg.setImageResource(R.drawable.ic_review_like)
            opinion.visibility = View.GONE
            dislike.setBackgroundResource(R.drawable.review_like_box)
            dislikeImg.setImageResource(R.drawable.ic_review_dislike)
            reviewWriteMenuAdapter.menuList[position].menuGood = null
        }

        fun opinionChange(parent: LinearLayout, position: Int, menuPosition: Int){
            if(reviewWriteMenuAdapter.menuList[position].menuOpinion[menuPosition]){
                parent.setBackgroundResource(R.drawable.login_box)
                reviewWriteMenuAdapter.menuList[position].menuOpinion[menuPosition] = false
            } else {
                parent.setBackgroundResource(R.drawable.detail_address_category_box)
                reviewWriteMenuAdapter.menuList[position].menuOpinion[menuPosition] = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewWriteMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_write_menu, parent, false)
        return ReviewWriteMenuViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ReviewWriteMenuViewHolder, position: Int) {
        holder.bind(menuList[position], position)
    }

    override fun getItemCount(): Int  = menuList.size

    fun opinionEtcChange(position: Int, content: String) {
        menuList[position].menuEtc = content
        notifyItemChanged(position)
    }

    // 데이터 가져가게 함
}