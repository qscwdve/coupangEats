package com.example.coupangeats.src.main.home.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.main.home.model.HomeInfo.RecommendStores

class RecommendAdapter(val recommendList: ArrayList<RecommendStores>, val fragment: HomeFragment) : RecyclerView.Adapter<RecommendAdapter.RecommendViewHolder>(){
    class RecommendViewHolder(itemView: View, val recommendAdapter: RecommendAdapter) : RecyclerView.ViewHolder(itemView) {
        val mainimg = itemView.findViewById<ImageView>(R.id.item_recommend_img)
        val subImgParent = itemView.findViewById<LinearLayout>(R.id.item_recommend_img_sub_parent)
        val sub1 = itemView.findViewById<ImageView>(R.id.item_recommend_img_sub1)
        val sub2 = itemView.findViewById<ImageView>(R.id.item_recommend_img_sub2)
        val name = itemView.findViewById<TextView>(R.id.item_recommend_name)
        val markIcon = itemView.findViewById<ImageView>(R.id.item_recommend_markIcon)
        val time = itemView.findViewById<TextView>(R.id.item_recommend_time)
        val star = itemView.findViewById<ImageView>(R.id.item_recommend_star)
        val review = itemView.findViewById<TextView>(R.id.item__recommend_review)
        val dot1 = itemView.findViewById<TextView>(R.id.item_recommend_dot1)
        val dot2 = itemView.findViewById<TextView>(R.id.item_recommend_dot2)
        val distance = itemView.findViewById<TextView>(R.id.item_recommend_distance)
        val delivery = itemView.findViewById<TextView>(R.id.item_recommend_delivery)
        val couponParent = itemView.findViewById<LinearLayout>(R.id.item_recommend_coupon_parent)
        val coupon = itemView.findViewById<TextView>(R.id.item_recommend_coupon)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: RecommendStores) {
            itemView.tag = adapterPosition
            val image = item.url
            if(image.size > 0){
                Glide.with(mainimg).load(item.url[0]).centerCrop().into(mainimg)
            }
            //Glide.with(mainimg).load(item.url[0]).into(mainimg)
            if(image.size == 3){
                subImgParent.visibility = View.VISIBLE
                Glide.with(sub1).load(image[1]).into(sub1)
                Glide.with(sub2).load(image[2]).into(sub2)
            } else {
                subImgParent.visibility = View.GONE
            }
            name.text = item.storeName
            when(item.markIcon){
                "신규" -> markIcon.setImageResource(R.drawable.new_super)
                "치타배달" -> markIcon.setImageResource(R.drawable.cheetah)
                else -> markIcon.visibility = View.GONE
            }
            time.text = item.deliveryTime
            val visible = if(item.totalReview != null) View.VISIBLE else View.GONE
            star.visibility = visible
            review.visibility = visible
            dot1.visibility = visible
            review.text = item.totalReview ?: ""

            distance.text = item.distance

            if(item.deliveryPrice != null){
                dot2.visibility = View.VISIBLE
                delivery.visibility = View.VISIBLE
                delivery.text = item.deliveryPrice
            } else {
                dot2.visibility = View.GONE
                delivery.visibility = View.GONE
            }

            if(item.coupon != null){
                couponParent.visibility = View.VISIBLE
                coupon.text = item.coupon
            } else {
                couponParent.visibility = View.GONE
            }
            itemView.setOnTouchListener { v, event ->
                if(event.action == MotionEvent.ACTION_UP){
                    if(recommendAdapter.fragment.mScrollStart){
                        recommendAdapter.fragment.mScrollStart  = false
                        recommendAdapter.fragment.mScrollFlag = false
                    } else {
                        recommendAdapter.fragment.mScrollFlag = false
                    }
                } else if(event.action == MotionEvent.ACTION_DOWN){
                    // 누름
                    recommendAdapter.fragment.mScrollFlag = true
                    recommendAdapter.fragment.mScrollValue = -1
                }
                false
            }
            itemView.setOnClickListener {
                // 매장 선택
                recommendAdapter.fragment.startSuper(item.storeIdx)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommend_super, parent, false)
        return RecommendViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: RecommendViewHolder, position: Int) {
        holder.bind(recommendList[position])
    }

    override fun getItemCount(): Int = recommendList.size
}