package com.example.coupangeats.src.main.order.past.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.order.adapter.OrderMenuAdapter
import com.example.coupangeats.src.main.order.model.pastOrder
import com.example.coupangeats.src.main.order.past.OrderPastFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PastOrderAdapter(val orderList: ArrayList<pastOrder>, val fragment: OrderPastFragment) : RecyclerView.Adapter<PastOrderAdapter.PastOrderViewHolder>() {
    @SuppressLint("SimpleDateFormat")
    class PastOrderViewHolder(itemView: View, val fragment: OrderPastFragment) : RecyclerView.ViewHolder(itemView){
        var mFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        var today =  Calendar.getInstance()
        val storeName = itemView.findViewById<TextView>(R.id.item_past_order_store_name)
        val date = itemView.findViewById<TextView>(R.id.item_past_order_date)
        val status = itemView.findViewById<TextView>(R.id.item_past_order_status)
        val img = itemView.findViewById<ImageView>(R.id.item_past_order_img)
        val menuRecycler = itemView.findViewById<RecyclerView>(R.id.item_past_order_menu_recycler_view)
        val totalPrice = itemView.findViewById<TextView>(R.id.item_past_order_price)
        val starParent = itemView.findViewById<LinearLayout>(R.id.item_past_order_star_parent)
        val star1 = itemView.findViewById<ImageView>(R.id.item_past_order_star1)
        val star2 = itemView.findViewById<ImageView>(R.id.item_past_order_star2)
        val star3 = itemView.findViewById<ImageView>(R.id.item_past_order_star3)
        val star4 = itemView.findViewById<ImageView>(R.id.item_past_order_star4)
        val star5 = itemView.findViewById<ImageView>(R.id.item_past_order_star5)
        val reOrder = itemView.findViewById<TextView>(R.id.item_past_order_reorder)
        val review = itemView.findViewById<TextView>(R.id.item_past_order_review)
        val reviewLimit = itemView.findViewById<TextView>(R.id.item_past_order_review_limit)
        val receipt = itemView.findViewById<TextView>(R.id.item_past_order_receipt)
        val superParent = itemView.findViewById<LinearLayout>(R.id.item_past_order_super_parent)

        fun bind(order: pastOrder) {
            storeName.text = order.storeName
            date.text = order.orderDate
            status.text = order.status
            if(order.reviewRating != null){
                setStar(order.reviewRating)
                starParent.visibility = View.VISIBLE
            } else {
                starParent.visibility = View.GONE
            }

            if(order.imageUrl != null){
                Glide.with(img).load(order.imageUrl).into(img)
                img.visibility = View.VISIBLE
            } else {
                img.visibility = View.GONE
            }

            menuRecycler.adapter = PastOrderMenuAdapter(order.orderMenus)
            menuRecycler.layoutManager = LinearLayoutManager(itemView.context)

            totalPrice.text = order.totalPrice

            // 가게 보러 가기
            superParent.setOnClickListener{
                fragment.startDetailSuper(order.storeIdx)
            }

            // 영수증 보러가기
            receipt.setOnClickListener {
                fragment.lookReceipt(order)
            }

            if(order.reviewIdx == null){
                // 리뷰를 작성한 것이 없음
                val dateFormatData = mFormat.parse(order.orderDate)
                val day = (today.time.time - dateFormatData!!.time) / (60 * 60 * 24 * 1000)
                if(day.toInt() < 3){
                    // 리뷰 작성 기간 아직 안지남
                    review.visibility = View.VISIBLE
                    reviewLimit.visibility = View.VISIBLE
                    val reviewLimitText = "리뷰 작성 기간이 ${3 - day.toInt()}일 남았습니다."
                    reviewLimit.text = reviewLimitText
                    review.setBackgroundResource(R.drawable.dialog_login_basic)
                    review.setTextColor(Color.parseColor("#00AFFE"))

                    review.setOnClickListener {
                        // 리뷰쓰러 가기
                        fragment.startReviewWrite(order.orderIdx, -1)
                    }
                } else {
                    // 리뷰 작성 기간 지남
                    review.visibility = View.GONE
                    reviewLimit.visibility = View.GONE
                }
            } else {
                // 리뷰 작성한 것이 있음
                review.visibility = View.VISIBLE
                reviewLimit.visibility = View.GONE
                review.setText("작성한 리뷰 보러 가기")
                review.setBackgroundResource(R.drawable.review_look_box)
                review.setTextColor(Color.parseColor("#000000"))

                review.setOnClickListener {
                    // 리뷰 작성한 것 보러 가기
                    fragment.startMyReview(order.orderIdx, order.reviewIdx)
                }
            }

            reOrder.setOnClickListener {
                // 재주문 하러가기

            }
        }

        fun setStar(num: Int) {
            star1.setImageResource(R.drawable.ic_star_no)
            star2.setImageResource(R.drawable.ic_star_no)
            star3.setImageResource(R.drawable.ic_star_no)
            star4.setImageResource(R.drawable.ic_star_no)
            star5.setImageResource(R.drawable.ic_star_no)
            if (num >= 1) star1.setImageResource(R.drawable.ic_star)
            if (num >= 2) star2.setImageResource(R.drawable.ic_star)
            if (num >= 3) star3.setImageResource(R.drawable.ic_star)
            if (num >= 4) star4.setImageResource(R.drawable.ic_star)
            if (num >= 5) star5.setImageResource(R.drawable.ic_star)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_past_order, parent, false)
        return PastOrderViewHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: PastOrderViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size
}