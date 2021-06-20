package com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R

class CategoryNameAdapterTest(val categoryList: ArrayList<String>) : RecyclerView.Adapter<CategoryNameAdapterTest.CategoryViewHolderTest>() {
    var nowSelected = 0;
    class CategoryViewHolderTest(itemView: View, val categoryNameAdapter: CategoryNameAdapterTest) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.item_detail_super_menu_category_name)
        val highlight = itemView.findViewById<View>(R.id.item_detail_super_menu_category_highlight)

        fun bind(item: String, position:Int){
            name.text = item
            if(categoryNameAdapter.nowSelected == position) highlight.visibility = View.VISIBLE
            else highlight.visibility = View.INVISIBLE

            itemView.setOnClickListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    // 해당 항목으로 메뉴 리스트 리사이클러뷰 선택해야 함
                    //categoryNameAdapter.activity.menuFouceItem(position)
                    // 강조줄 나타내기
                    if(categoryNameAdapter.nowSelected != position){
                        categoryNameAdapter.changeHighlight(position)
                    }
                }, 300)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolderTest {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_super_menu_category, parent, false)
        return CategoryViewHolderTest(view,this)
    }

    override fun onBindViewHolder(holderTest: CategoryViewHolderTest, position: Int) {
        holderTest.bind(categoryList[position], position)
    }

    override fun getItemCount(): Int = categoryList.size

    fun changeHighlight(num: Int) {
        nowSelected = num
        notifyDataSetChanged()
    }
}
