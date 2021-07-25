package com.example.coupangeats.src.lookImage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R

class LookImageViewPagerAdapter(val imgList: ArrayList<String>) : RecyclerView.Adapter<LookImageViewPagerAdapter.LookImageViewPagerViewHolder>() {
    class LookImageViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById<ImageView>(R.id.item_look_image_view_pager_img)

        fun bind(item: String){
            Glide.with(img).load(item).into(img)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LookImageViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_look_image_view_pager, parent, false)
        return LookImageViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: LookImageViewPagerViewHolder, position: Int) {
        holder.bind(imgList[position%(imgList.size)])
    }

    override fun getItemCount(): Int = (imgList.size * 100)
}