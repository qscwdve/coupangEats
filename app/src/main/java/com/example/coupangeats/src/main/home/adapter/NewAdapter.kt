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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coupangeats.R
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.main.home.model.HomeInfo.NewStores
import org.w3c.dom.DocumentFragment
import org.w3c.dom.Text

class NewAdapter(val newList: ArrayList<NewStores>, val fragment: HomeFragment): RecyclerView.Adapter<NewAdapter.NewViewHolder>() {

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

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: NewStores?, position: Int) {
            if(item == null){
                newParent.visibility = View.GONE
                thelook.visibility = View.VISIBLE

                itemView.setOnClickListener {
                    newAdapter.fragment.startNewSuper()
                }
            } else {
                newParent.visibility = View.VISIBLE
                thelook.visibility = View.GONE
                Glide.with(img).load(item.url).override(125, 90).into(img)
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
                    delivery.visibility = View.INVISIBLE
                }

                itemView.setOnClickListener {
                    // 매장 선택
                    newAdapter.fragment.startSuper(item.storeIdx)
                }
            }
            itemView.setOnTouchListener { v, event ->
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
        if(newList.size <= position){
            holder.bind(null, position)
        }
        else holder.bind(newList[position], position)
    }

    override fun getItemCount(): Int = newList.size + 1
}