package com.example.coupangeats.src.reviewWrite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.reviewWrite.ReviewWriteActivity
import com.example.coupangeats.src.reviewWrite.model.PhotoData

class ReviewWritePhotoAdapter(var imgList: ArrayList<String>, val activity: ReviewWriteActivity): RecyclerView.Adapter<ReviewWritePhotoAdapter.ReviewWritePhotoViewHolder>() {
    class ReviewWritePhotoViewHolder(itemView: View, val reviewWritePhotoAdapter: ReviewWritePhotoAdapter) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById<ImageView>(R.id.item_review_write_photo_img)
        val delete = itemView.findViewById<ImageView>(R.id.item_review_write_photo_delete)

        fun bind(photoData: String, position: Int) {
            Glide.with(img).load(photoData).into(img)

            delete.setOnClickListener {
                // 사진 삭제
                reviewWritePhotoAdapter.deletePhoto(position)
            }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewWritePhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_write_photo, parent, false)
        return ReviewWritePhotoViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ReviewWritePhotoViewHolder, position: Int) {
        holder.bind(imgList[position], position)
    }

    override fun getItemCount(): Int = imgList.size

    fun deletePhoto(position: Int) {
        imgList.removeAt(position)
        notifyDataSetChanged()
    }

    fun addPhoto(photoData: String) {
        imgList.add(photoData)
        notifyDataSetChanged()
    }

    fun getPhotoList() : ArrayList<String>? {
        if(imgList.size == 0) return null
        val photoList = ArrayList<String>()
        for(index in imgList.indices){
            photoList.add(imgList[index])
        }
        return photoList
    }

    fun changeItemList(imgUrl: ArrayList<String>) {
        imgList = imgUrl
        notifyDataSetChanged()
    }
}