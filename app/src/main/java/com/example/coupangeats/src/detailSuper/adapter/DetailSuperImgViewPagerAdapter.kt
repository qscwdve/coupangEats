package com.example.coupangeats.src.detailSuper.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.menuSelect.MenuSelectActivity

class DetailSuperImgViewPagerAdapter(val imgList: ArrayList<String>, val activity: Activity, val version : Int = 1) : RecyclerView.Adapter<DetailSuperImgViewPagerAdapter.DetailSuperImgViewpagerViewHoler>() {
    class DetailSuperImgViewpagerViewHoler(itemView: View, val adapter: DetailSuperImgViewPagerAdapter) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById<ImageView>(R.id.item_detail_super_viewpager_img)
        val parent = itemView.findViewById<ConstraintLayout>(R.id.item_detail_super_viewpager_parent)

        fun bind(item: String, position: Int){
            Glide.with(img).load(item).into(img)

            parent.setOnClickListener {
                adapter.startLookImgActivity(position + 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailSuperImgViewpagerViewHoler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_super_viewpager, parent, false)
        return DetailSuperImgViewpagerViewHoler(view, this)
    }

    override fun onBindViewHolder(holder: DetailSuperImgViewpagerViewHoler, position: Int) {
        holder.bind(imgList[position%(imgList.size)], position%(imgList.size))
    }

    override fun getItemCount(): Int = (imgList.size * 100)

    fun startLookImgActivity(num : Int){
        var imgString = ""

        for(index in imgList.indices){
            imgString += imgList[index] + " "
        }

        if(imgString != "" && imgString[imgString.length - 1] == ' '){
            imgString = imgString.substring(0 until (imgString.length - 1))
        }

        if(version ==  1){
            (activity as DetailSuperActivity).startLookImageActivity(num, imgString)
        } else {
            (activity as MenuSelectActivity).startLookImageActivity(num, imgString)
        }
    }
}