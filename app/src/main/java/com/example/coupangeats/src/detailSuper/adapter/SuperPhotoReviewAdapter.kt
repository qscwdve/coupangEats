package com.example.coupangeats.src.detailSuper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.detailSuper.model.PhotoReview

class SuperPhotoReviewAdapter(val reviewList: ArrayList<PhotoReview>, val detailSuperActivity: DetailSuperActivity) : RecyclerView.Adapter<SuperPhotoReviewAdapter.SuperPhotoReviewViewHolder>() {
    class SuperPhotoReviewViewHolder(itemView: View, val superPhotoReviewAdapter: SuperPhotoReviewAdapter) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById<ImageView>(R.id.item_super_photo_review_img)
        val content = itemView.findViewById<TextView>(R.id.item_super_photo_review_content)
        val star1 = itemView.findViewById<ImageView>(R.id.item_super_photo_review_star1)
        val star2 = itemView.findViewById<ImageView>(R.id.item_super_photo_review_star2)
        val star3 = itemView.findViewById<ImageView>(R.id.item_super_photo_review_star3)
        val star4 = itemView.findViewById<ImageView>(R.id.item_super_photo_review_star4)
        val star5 = itemView.findViewById<ImageView>(R.id.item_super_photo_review_star5)
        val thelook = itemView.findViewById<RelativeLayout>(R.id.item_super_photo_review_theLook)
        val parent = itemView.findViewById<LinearLayout>(R.id.item_super_photo_review_parent)

        fun bind(item: PhotoReview?, position: Int){
            if(item == null){
                parent.visibility = View.GONE
                thelook.visibility = View.VISIBLE
            } else {
                parent.visibility = View.VISIBLE
                thelook.visibility = View.GONE
                Glide.with(img).load(item.img).into(img)
                content.text = item.content
                setStar(item.rating.toInt())
            }
            itemView.setOnClickListener {
                // 포토 리뷰 선택!!
                if(item != null){
                    superPhotoReviewAdapter.detailSuperActivity.startReviewPosition(item.reviewIdx)
                }
            }
            thelook.setOnClickListener { superPhotoReviewAdapter.detailSuperActivity.startReviewPosition() }
        }

        fun setStar(num: Int) {
            star1.setImageResource(R.drawable.ic_star_no)
            star2.setImageResource(R.drawable.ic_star_no)
            star3.setImageResource(R.drawable.ic_star_no)
            star4.setImageResource(R.drawable.ic_star_no)
            star5.setImageResource(R.drawable.ic_star_no)
            if(num >= 1) star1.setImageResource(R.drawable.ic_star)
            if(num >= 2) star2.setImageResource(R.drawable.ic_star)
            if(num >= 3) star3.setImageResource(R.drawable.ic_star)
            if(num >= 4) star4.setImageResource(R.drawable.ic_star)
            if(num >= 5) star5.setImageResource(R.drawable.ic_star)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperPhotoReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_super_photo_review, parent, false)
        return SuperPhotoReviewViewHolder(view,this)
    }

    override fun onBindViewHolder(holder: SuperPhotoReviewViewHolder, position: Int) {
        if(reviewList.size <= position){
            holder.bind(null, position)
        } else {
            holder.bind(reviewList[position], position)
        }
    }

    override fun getItemCount(): Int = reviewList.size + 1
}