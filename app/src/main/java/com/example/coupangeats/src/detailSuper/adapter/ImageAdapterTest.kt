package com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R

class ImageAdapterTest(val itemList: ArrayList<Int>) : RecyclerView.Adapter<ImageAdapterTest.ImageTestViewHolder>() {
    class ImageTestViewHolder(item: View) : RecyclerView.ViewHolder(item){
        val image = item.findViewById<ImageView>(R.id.test_item_image)

        fun bind(imageNum: Int){
            image.setImageResource(imageNum)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageTestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
        return ImageTestViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageTestViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}