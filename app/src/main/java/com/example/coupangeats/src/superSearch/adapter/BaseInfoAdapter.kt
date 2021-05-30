package com.example.coupangeats.src.superSearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.home.model.HomeInfo.RecommendStores
import com.example.coupangeats.src.superSearch.model.BaseSuperInfo

class BaseInfoAdapter(val baseInfoList: ArrayList<BaseSuperInfo>) :
    RecyclerView.Adapter<BaseInfoAdapter.BaseInfoViewHolder>() {
    class BaseInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        fun bind(item: BaseSuperInfo) {
            val image = item.url
            if(image != null){
                if (image.size > 0) {
                    Glide.with(mainimg).load(item.url[0]).centerCrop().into(mainimg)
                }
                if (image.size == 3) {
                    subImgParent.visibility = View.VISIBLE
                    Glide.with(sub1).load(image[1]).into(sub1)
                    Glide.with(sub2).load(image[2]).into(sub2)
                } else {
                    subImgParent.visibility = View.GONE
                }
            }
            name.text = item.storeName
            when (item.markIcon) {
                "신규" -> markIcon.setImageResource(R.drawable.new_super)
                "치타배달" -> markIcon.setImageResource(R.drawable.cheetah)
                else -> markIcon.visibility = View.GONE
            }
            time.text = item.deliveryTime
            val visible = if (item.totalReview != null) View.VISIBLE else View.GONE
            star.visibility = visible
            review.visibility = visible
            dot1.visibility = visible
            review.text = item.totalReview ?: ""

            distance.text = item.distance

            if (item.deliveryPrice != null) {
                dot2.visibility = View.VISIBLE
                delivery.visibility = View.VISIBLE
                delivery.text = item.deliveryPrice
            } else {
                dot2.visibility = View.GONE
                delivery.visibility = View.GONE
            }

            if (item.coupon != null) {
                couponParent.visibility = View.VISIBLE
                coupon.text = item.coupon
            } else {
                couponParent.visibility = View.GONE
            }
            itemView.setOnClickListener {
                // 매장 선택
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommend_super, parent, false)
        return BaseInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseInfoViewHolder, position: Int) {
        holder.bind(baseInfoList[position])
    }

    override fun getItemCount(): Int = baseInfoList.size
}