package com.example.coupangeats.src

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityTestBinding
import com.softsquared.template.kotlin.config.BaseActivity
import kotlin.math.abs

class TestActivity : BaseActivity<ActivityTestBinding>(ActivityTestBinding::inflate) {
    var mIconHeight = 0
    var mScroll = 0
    var mScrollFlag = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 어댑터
        binding.testRecyclerView.adapter = TestRecyclerAdapter()
        binding.testRecyclerView.layoutManager = LinearLayoutManager(this)

        getIconHeight()
        binding.testRecyclerViewParent.translationZ = 1f

        binding.testRecyclerViewParent.setOnRefreshListener {
            Log.d("position", "refresh")
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                binding.testRecyclerViewParent.isRefreshing = false
            }, 1000)
        }

        /*binding.testNested.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (mScrollFlag) {
                if (scrollY < mIconHeight && mScroll > scrollY) {
                    // 유저가 상단 스크롤 하는 중으로 추정
                    binding.testRecyclerViewParent.translationY =
                        -(abs(mIconHeight - scrollY)).toFloat()
                    binding.testImg.translationY = -scrollY.toFloat()
                    mScroll = scrollY
                }
            }
        }

        binding.testRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    // 스크롤 멈췄을 때
                    binding.testImg.translationY = -mIconHeight.toFloat()
                    mScroll = mIconHeight
                    binding.testRecyclerViewParent.translationY = 0f
                    //binding.testNested.scrollTo(0, mIconHeight)
                    binding.testNested.smoothScrollTo(0, mIconHeight)
                    Log.d("position", "스크롤 멈춤")
                }
            }
        })*/
    }

    private fun getIconHeight(){
      /*  val vtree = binding.testBait.viewTreeObserver

        vtree.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.testBait.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mIconHeight = binding.testBait.measuredHeight
                //binding.testNested.smoothScrollTo(0, mIconHeight)  // 처음 스크롤을 보여져야 하는 것들 상단으로 고정
                mScroll = mIconHeight
                mScrollFlag = true
            }
        })*/
    }

    class TestRecyclerAdapter() : RecyclerView.Adapter<TestRecyclerAdapter.TestViewHolder>(){
        val strList = arrayListOf(R.drawable.category_asian, R.drawable.category_buger,
            R.drawable.category_buger, R.drawable.category_asian, R.drawable.category_buger,
            R.drawable.category_asian, R.drawable.category_buger, R.drawable.category_asian, R.drawable.category_buger)
        class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val img = itemView.findViewById<ImageView>(R.id.item_category_super_category_img)

            fun bind(str: Int){
                img.setImageResource(str)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_super_category, parent, false)
            return TestViewHolder(view)
        }

        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            holder.bind(strList[position])
        }

        override fun getItemCount(): Int = strList.size
    }
}
