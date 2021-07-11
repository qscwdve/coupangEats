package com.example.coupangeats.src.detailSuper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R

class DetailSuperImgViewPagerAdapter(val imgList: ArrayList<String>) : RecyclerView.Adapter<DetailSuperImgViewPagerAdapter.DetailSuperImgViewpagerViewHoler>() {
    class DetailSuperImgViewpagerViewHoler(itemView: View) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById<ImageView>(R.id.item_detail_super_viewpager_img)

        fun bind(item: String, position: Int){
            Glide.with(img).load(item).into(img)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailSuperImgViewpagerViewHoler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_super_viewpager, parent, false)
        return DetailSuperImgViewpagerViewHoler(view)
    }

    override fun onBindViewHolder(holder: DetailSuperImgViewpagerViewHoler, position: Int) {
        holder.bind(imgList[position%(imgList.size)], position%(imgList.size))
    }

    override fun getItemCount(): Int = (imgList.size * 100)
}