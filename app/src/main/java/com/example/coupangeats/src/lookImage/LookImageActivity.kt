package com.example.coupangeats.src.lookImage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.databinding.ActivityLookImageBinding
import com.softsquared.template.kotlin.config.BaseActivity

class LookImageActivity : BaseActivity<ActivityLookImageBinding>(ActivityLookImageBinding::inflate) {
    private var mImageString: String = ""
    private var mNow : Int = 1
    private var imgSize : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mImageString = intent.getStringExtra("imgList") ?: ""
        mNow = intent.getIntExtra("position", 1)

        if(mImageString != ""){
            setImgViewPager(changeImageStingToArray())
        }

        Log.d("imgList", mImageString)

        binding.lookImageBack.setOnClickListener {
            finish()
        }
    }

    fun changeImageStingToArray() : ArrayList<String>{
        val array = ArrayList<String>()

        val token = mImageString.split(' ')
        for(index in token.indices){
            array.add(token[index])
        }

        return array
    }

    fun setImgViewPager(imgList: ArrayList<String>){
        imgSize = imgList.size
        binding.lookImageViewPager.adapter = LookImageViewPagerAdapter(imgList)
        binding.lookImageViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.lookImageViewPager.setCurrentItem(imgSize*50 + mNow, false)

        binding.lookImageNow.text = mNow.toString()
        binding.lookImageTotal.text = imgSize.toString()

        binding.lookImageViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.lookImageNow.text = (position % imgSize + 1).toString()
            }
        })
    }
}