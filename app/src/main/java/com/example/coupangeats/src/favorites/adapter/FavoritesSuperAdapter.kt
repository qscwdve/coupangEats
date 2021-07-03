package com.example.coupangeats.src.favorites.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.favorites.FavoritesActivity
import com.example.coupangeats.src.favorites.model.FavoritesSuperInfo
import kotlin.math.acos

class FavoritesSuperAdapter(var superList: ArrayList<FavoritesSuperInfo>, val activity: FavoritesActivity) : RecyclerView.Adapter<FavoritesSuperAdapter.FavoritesSuperViewHolder>() {
    var version : Int = 1 // 1이면 그냥 가게 가기 , 2면 수정!
    var selectCheckList = Array(superList.size){i -> false}  // 가게 삭제 선택 리스트
    class FavoritesSuperViewHolder(itemView: View, val adapter: FavoritesSuperAdapter) : RecyclerView.ViewHolder(itemView){
        val img = itemView.findViewById<ImageView>(R.id.item_favorites_super_img)
        val superName = itemView.findViewById<TextView>(R.id.item_favorites_super_store_name)
        val cheetah = itemView.findViewById<ImageView>(R.id.item_favorites_super_cheetah)
        val cheetahDown = itemView.findViewById<ImageView>(R.id.item_favorites_super_cheetah_down)
        val totalReviewParent = itemView.findViewById<LinearLayout>(R.id.item_favorites_super_review_parent)
        val totalReviewText = itemView.findViewById<TextView>(R.id.item_favorites_super_review_text)
        val distance = itemView.findViewById<TextView>(R.id.item_favorites_super_distance)
        val dot1 = itemView.findViewById<TextView>(R.id.item_favorites_super_distance_dot)
        val time = itemView.findViewById<TextView>(R.id.item_favorites_super_time)
        val dot2 = itemView.findViewById<TextView>(R.id.item_favorites_super_time_dot)
        val deliveryPrice = itemView.findViewById<TextView>(R.id.item_favorites_super_delivery_price)
        val deliveryPriceFree = itemView.findViewById<TextView>(R.id.item_favorites_super_delivery_price_free)
        val discountParent = itemView.findViewById<LinearLayout>(R.id.item_favorites_super_discount_parent)
        val discountText = itemView.findViewById<TextView>(R.id.item_favorites_super_discount_text)
        val select = itemView.findViewById<ImageView>(R.id.item_favorites_select)
        val parent = itemView.findViewById<LinearLayout>(R.id.item_favorites_super_parent)

        fun bind(item: FavoritesSuperInfo, position: Int) {
            if(item.imgUrl == null){
                img.visibility = View.GONE
            } else {
                img.visibility = View.VISIBLE
                Glide.with(img).load(item.imgUrl).into(img)
            }
            superName.text = item.storeName  // 슈퍼 이름
            if(item.cheetah == null || item.cheetah == "N"){
                cheetah.visibility = View.GONE
                cheetahDown.visibility = View.GONE
            } else {
                val maxLength = if(adapter.version == 1) 12 else 8

                if(item.storeName.length >=  maxLength){
                    cheetah.visibility = View.GONE
                    cheetahDown.visibility = View.VISIBLE
                } else {
                    cheetah.visibility = View.VISIBLE
                    cheetahDown.visibility = View.GONE
                }
            }
            if(item.totalReview == null){
                totalReviewParent.visibility = View.GONE
            } else {
                totalReviewParent.visibility = View.VISIBLE
                totalReviewText.text = item.totalReview
            }
            distance.text = item.distance

            if(item.deliveryTime == null){
                dot1.visibility = View.GONE
                dot2.visibility = View.GONE
                deliveryPrice.visibility = View.GONE
                deliveryPriceFree.visibility = View.GONE
                time.visibility = View.GONE
            }
            else {
                time.visibility = View.VISIBLE
                dot1.visibility = View.VISIBLE
                dot2.visibility = View.VISIBLE
                time.text = item.deliveryTime
                if(item.deliveryPrice == "무료배달"){
                    deliveryPriceFree.visibility = View.VISIBLE
                    deliveryPrice.visibility = View.GONE

                } else {
                    deliveryPriceFree.visibility = View.GONE
                    deliveryPrice.visibility = View.VISIBLE

                    deliveryPrice.text = item.deliveryPrice
                }
            }

            if(adapter.version == 1){
                // 그냥 가게
                select.visibility = View.GONE
            } else {
                // 수정 모드
                select.visibility = View.VISIBLE
            }

            // 할인금액 추가 필요
            if(item.coupon == null){
                discountParent.visibility = View.GONE
            } else {
                discountParent.visibility = View.VISIBLE
                discountText.text = item.coupon
            }
            parent.setOnClickListener {
                if(adapter.version == 1){
                    // 그냥 가게로 이동
                    adapter.activity.startDetailSuper(item.storeIdx)
                } else {
                    // 수정 선택
                    changeSelect(position)
                }
            }
            select.setOnClickListener {
                // 수정 선택!!
                changeSelect(position)
            }

        }

        fun changeSelect(position: Int) {
            if(adapter.selectCheckList[position]){
                adapter.selectCheckList[position] = false
                select.setImageResource(R.drawable.ic_necessary_option)
                select.setBackgroundResource(R.drawable.circle_ripple_white_loginblue)
            } else {
                adapter.selectCheckList[position] = true
                select.setImageResource(R.drawable.ic_necessary_option_click)
                select.setBackgroundResource(R.drawable.circle_ripple_white_to_gray)
            }
            adapter.checkSelectNum()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesSuperViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorites_super, parent, false)
        return FavoritesSuperViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: FavoritesSuperViewHolder, position: Int) {
        holder.bind(superList[position], position)
    }

    override fun getItemCount(): Int = superList.size

    fun changeVersion(v: Int){
        version = v
        if(version == 1){
            selectCheckList = Array(superList.size){i -> false}  // 가게 삭제 선택 리스트
            activity.changeSelectLook(false, 0)
        }
        notifyDataSetChanged()
    }

    fun getSelectData(): ArrayList<Int> {
        val storeIdxList = ArrayList<Int>()

        for(index in selectCheckList.indices){
            if(selectCheckList[index]){
                // 선택된
                storeIdxList.add(superList[index].storeIdx)
            }
        }
        return storeIdxList
    }

    fun checkSelectNum() {
        var num = 0
        for(index in selectCheckList.indices){
            if(selectCheckList[index]) num++
        }
        if(num == 0){
            activity.changeSelectLook(false)
        } else {
            activity.changeSelectLook(true, num)
        }
    }
}