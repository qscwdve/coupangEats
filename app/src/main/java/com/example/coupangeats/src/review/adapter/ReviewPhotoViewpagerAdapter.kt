package com.example.coupangeats.src.review.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R

class ReviewPhotoViewpagerAdapter(val urls: ArrayList<String>) : RecyclerView.Adapter<ReviewPhotoViewpagerAdapter.ReviewPhotoViewpagerViewHolder>() {
    class ReviewPhotoViewpagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById<ImageView>(R.id.item_review_viewpager_imgs)

        fun bind(url: String){
            Glide.with(img).load(url).into(img)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewPhotoViewpagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_viewpager_img, parent, false)
        return ReviewPhotoViewpagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewPhotoViewpagerViewHolder, position: Int) {
        holder.bind(urls[position])
    }

    override fun getItemCount(): Int = urls.size
}