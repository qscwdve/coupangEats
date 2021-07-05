package com.example.coupangeats.src.discount.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.src.discount.DiscountActivity
import com.example.coupangeats.src.discount.model.SuperCoupon

class CouponInfoAdapter(val superCouponList: ArrayList<SuperCoupon>, var selectUserCouponIdx: Int, val version: Int, val discountActivity: DiscountActivity) : RecyclerView.Adapter<CouponInfoAdapter.CouponInfoViewHolder>() {
    var couponPriceString = ""

    class CouponInfoViewHolder(itemView: View, val adapter: CouponInfoAdapter) :
        RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.item_coupon_name)
        val price = itemView.findViewById<TextView>(R.id.item_coupon_price)
        val check = itemView.findViewById<ImageView>(R.id.item_coupon_select_check)
        val minPrice = itemView.findViewById<TextView>(R.id.item_coupon_min_price)
        val expiration = itemView.findViewById<TextView>(R.id.item_coupon_expiration_date)
        val parent = itemView.findViewById<LinearLayout>(R.id.item_coupon_parent)

        fun bind(item: SuperCoupon, position: Int) {
            name.text = item.couponName
            price.text = item.discountPrice
            minPrice.text = item.minOrderPrice
            expiration.text = item.expirationDate

            // version = -1 : 해당쿠폰 조회 , 나머지 : myeats
            if (adapter.version == -1) {
                // 해당 쿠폰 조회
                if (item.isAvailable == "N") {
                    parent.setBackgroundResource(R.drawable.round_gray_box_transport)
                    name.setTextColor(Color.parseColor("#5F000000"))
                    price.setTextColor(Color.parseColor("#5F00AFFE"))
                    minPrice.setTextColor(Color.parseColor("#5F949DA6"))
                    expiration.setTextColor(Color.parseColor("#5F949DA6"))
                } else {
                    parent.setBackgroundResource(R.drawable.round_gray_box)
                    name.setTextColor(Color.parseColor("#000000"))
                    price.setTextColor(Color.parseColor("#00AFFE"))
                    minPrice.setTextColor(Color.parseColor("#949DA6"))
                    expiration.setTextColor(Color.parseColor("#949DA6"))
                }
            } else {
                // myeats
                check.visibility = View.GONE
                if (item.isAvailable == "N") {
                    parent.setBackgroundResource(R.drawable.round_gray_fill_box_transport)
                    name.setTextColor(Color.parseColor("#5F000000"))
                    price.setTextColor(Color.parseColor("#5F00AFFE"))
                    minPrice.setTextColor(Color.parseColor("#5F949DA6"))
                    expiration.setTextColor(Color.parseColor("#5F949DA6"))
                } else {
                    parent.setBackgroundResource(R.drawable.round_gray_box)
                    name.setTextColor(Color.parseColor("#000000"))
                    price.setTextColor(Color.parseColor("#00AFFE"))
                    minPrice.setTextColor(Color.parseColor("#949DA6"))
                    expiration.setTextColor(Color.parseColor("#949DA6"))
                }
            }

            if (adapter.version == -1) {
                if (item.userCouponIdx == adapter.selectUserCouponIdx) {
                    // 유저가 선택한 쿠폰일 경우
                    parent.setBackgroundResource(R.drawable.round_blue_coupon)
                    check.visibility = View.VISIBLE
                    adapter.couponPriceString = item.discountPrice
                    adapter.selectUserCouponIdx = item.userCouponIdx
                    adapter.discountActivity.changeCouponIdx(item.userCouponIdx)
                } else {
                    check.visibility = View.GONE
                    parent.setBackgroundResource(R.drawable.round_gray_box)
                }
                itemView.setOnClickListener {
                    // 유저가 쿠폰을 선택했을 경우
                    if (item.isAvailable == "Y") {
                        if (item.userCouponIdx == adapter.selectUserCouponIdx) {
                            // 이미 선택한 쿠폰일 경우
                            adapter.selectUserCouponIdx = -1
                            parent.setBackgroundResource(R.drawable.round_gray_box)
                            check.visibility = View.GONE
                            adapter.couponPriceString = ""
                            adapter.discountActivity.changeApplyText("쿠폰적용 안함")
                            adapter.discountActivity.changeCouponIdx(-1)
                        } else {
                            // 새로운 쿠폰을 선택했을 경우
                            adapter.selectUserCouponIdx = item.userCouponIdx
                            adapter.notifyDataSetChanged()
                            adapter.couponPriceString = item.discountPrice
                            adapter.discountActivity.changeApplyText("적용하기")
                            adapter.discountActivity.changeCouponIdx(item.userCouponIdx)
                        }
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coupon, parent, false)
        return CouponInfoViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: CouponInfoViewHolder, position: Int) {
        holder.bind(superCouponList[position], position)
    }

    override fun getItemCount(): Int = superCouponList.size

    fun isCouponCheck(): Boolean {
        return selectUserCouponIdx != -1
    }

    fun getCouponPrice(): String {
        return couponPriceString
    }
}