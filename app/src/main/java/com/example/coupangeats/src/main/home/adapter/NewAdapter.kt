package com.example.coupangeats.src.main.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.main.home.model.HomeInfo.NewStores
import org.w3c.dom.DocumentFragment
import org.w3c.dom.Text

class NewAdapter(var newList: ArrayList<NewStores>, val fragment: HomeFragment): RecyclerView.Adapter<NewAdapter.NewViewHolder>() {

    class NewViewHolder(itemView: View, val newAdapter: NewAdapter) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.item_new_super_img)
        val name = itemView.findViewById<TextView>(R.id.item_new_super_name)
        val star = itemView.findViewById<ImageView>(R.id.item_new_super_star)
        val review = itemView.findViewById<TextView>(R.id.item_new_super_review)
        val dot = itemView.findViewById<TextView>(R.id.item_new_super_dot)
        val distance = itemView.findViewById<TextView>(R.id.item_new_super_distance)
        val delivery = itemView.findViewById<TextView>(R.id.item_new_super_delivery)
        val newParent = itemView.findViewById<LinearLayout>(R.id.item_new_super_parent)
        val thelook = itemView.findViewById<RelativeLayout>(R.id.item_new_super_end_parent)
        val coupon = itemView.findViewById<LinearLayout>(R.id.item_new_super_coupon)
        val couponText = itemView.findViewById<TextView>(R.id.item_new_super_discount)
        val firstLine = itemView.findViewById<View>(R.id.item_new_super_first_line)
        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: NewStores, last: Boolean, first: Boolean) {
            // 첫번째는 좀 더 많이 띄움
            firstLine.visibility = if(first) View.VISIBLE else View.GONE

            // 더보기 설정
            if(last){
                newParent.visibility = View.GONE
                thelook.visibility = View.VISIBLE

                thelook.setOnClickListener {
                    newAdapter.fragment.startNewSuper()
                }
            } else {
                thelook.visibility = View.GONE
            }

            // 가게 설정
            newParent.visibility = View.VISIBLE
            Glide.with(img).load(item.url).into(img)
            name.text = item.storeName
            if(item.totalReview != null){
                star.visibility = View.VISIBLE
                review.text = item.totalReview
                dot.visibility = View.VISIBLE
            } else {
                star.visibility = View.GONE
                review.visibility = View.GONE
                dot.visibility = View.GONE
            }
            distance.text = item.distance
            if(item.deliveryPrice != null){
                delivery.visibility = View.VISIBLE
                delivery.text = item.deliveryPrice
            } else {
                delivery.visibility = View.GONE
            }
            if(item.coupon != null){
                couponText.text = item.coupon
                coupon.visibility = View.VISIBLE
            } else {
                coupon.visibility = View.GONE
            }

            newParent.setOnClickListener {
                // 매장 선택
                newAdapter.fragment.setAddressQuestionDown()  // 배달주소 맞는지 물어보는거 내림
                newAdapter.fragment.startSuper(item.storeIdx)
            }

            // home 치타배달 관련 스크롤
            itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP)
                    newAdapter.fragment.setAddressQuestionDown()  // 배달주소 맞는지 물어보는거 내림
                if(!newAdapter.fragment.mScrollFinish) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (newAdapter.fragment.mScrollStart) {
                            newAdapter.fragment.scrollFinish()
                            newAdapter.fragment.mScrollStart = false
                            newAdapter.fragment.mScrollFlag = false
                        } else {
                            newAdapter.fragment.mScrollFlag = false
                        }
                    } else if (event.action == MotionEvent.ACTION_DOWN) {
                        // 누름
                        newAdapter.fragment.mScrollFlag = true
                        newAdapter.fragment.mScrollValue = -1
                    }
                }
                false
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_new_super, parent, false)
        return NewViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: NewViewHolder, position: Int) {
        holder.bind(newList[position], position == newList.size - 1, position == 0)
    }

    override fun getItemCount(): Int = newList.size

    fun changeItem(array: ArrayList<NewStores>){
        newList = array
        notifyDataSetChanged()
    }
}