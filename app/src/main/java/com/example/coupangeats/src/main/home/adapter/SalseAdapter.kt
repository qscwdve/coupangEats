package com.example.coupangeats.src.main.home.adapter

import android.annotation.SuppressLint
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.main.home.model.HomeInfo.OnSaleStores
import org.w3c.dom.Text

class SalseAdapter(var saleList: ArrayList<OnSaleStores>, val fragment: HomeFragment) : RecyclerView.Adapter<SalseAdapter.SalseViewHolder>() {

    class SalseViewHolder(itemView: View, val salseAdapter: SalseAdapter) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.item_salse_super_img)
        val name = itemView.findViewById<TextView>(R.id.item_salse_super_name)
        val star = itemView.findViewById<ImageView>(R.id.item_salse_super_star)
        val review = itemView.findViewById<TextView>(R.id.item_salse_super_review)
        val dot = itemView.findViewById<TextView>(R.id.item_salse_super_dot)
        val distance = itemView.findViewById<TextView>(R.id.item_salse_super_distance)
        val discount = itemView.findViewById<TextView>(R.id.item_salse_super_discount)
        val slaseParent = itemView.findViewById<LinearLayout>(R.id.item_salse_parent)
        val thelook = itemView.findViewById<RelativeLayout>(R.id.item_salse_end_parent)
        val firstLine = itemView.findViewById<View>(R.id.item_salse_super_first_line)
        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: OnSaleStores, last: Boolean, first: Boolean) {
            // 첫번째 좀 더 띄움
            firstLine.visibility = if(first) View.VISIBLE else View.GONE

            // 더보기 설정
            if(last){
                thelook.visibility = View.VISIBLE
                thelook.setOnClickListener {
                    // 할인 중인 맛집 더보기
                    salseAdapter.fragment.startSalseSuper()
                }
            } else {
                thelook.visibility = View.GONE
            }

            // 가게 정보 설정
            slaseParent.visibility = View.VISIBLE
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
            discount.text = item.coupon

            slaseParent.setOnClickListener {
                // 가게 선택함
                salseAdapter.fragment.setAddressQuestionDown()  // 배달주소 맞는지 물어보는거 내림
                salseAdapter.fragment.startSuper(item.storeIdx)
            }

            itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP)
                    salseAdapter.fragment.setAddressQuestionDown()  // 배달주소 맞는지 물어보는거 내림
                if(!salseAdapter.fragment.mScrollFinish) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (salseAdapter.fragment.mScrollStart) {
                            salseAdapter.fragment.scrollFinish()
                            salseAdapter.fragment.mScrollStart = false
                            salseAdapter.fragment.mScrollFlag = false
                        } else {
                            salseAdapter.fragment.mScrollFlag = false
                        }
                    } else if (event.action == MotionEvent.ACTION_DOWN) {
                        // 누름
                        salseAdapter.fragment.mScrollFlag = true
                        salseAdapter.fragment.mScrollValue = -1
                    }
                }
                false
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalseViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_salse_super, parent, false)
       return SalseViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: SalseViewHolder, position: Int) {
        holder.bind(saleList[position], position == saleList.size - 1, position == 0)
    }

    override fun getItemCount(): Int = saleList.size

    fun changeItem(array: ArrayList<OnSaleStores>){
        saleList = array
        notifyDataSetChanged()
    }
}