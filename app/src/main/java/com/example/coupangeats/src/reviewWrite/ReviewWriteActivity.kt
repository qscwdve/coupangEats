package com.example.coupangeats.src.reviewWrite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityReviewWriteBinding
import com.softsquared.template.kotlin.config.BaseActivity

class ReviewWriteActivity : BaseActivity<ActivityReviewWriteBinding>(ActivityReviewWriteBinding::inflate){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뒤로 가기
        binding.reviewWriteBack.setOnClickListener { finish() }

        binding.reviewWriteStar1.setOnClickListener { setStar(1, R.drawable.ic_review_star_small_no, R.drawable.ic_review_star_small) }
        binding.reviewWriteStar2.setOnClickListener { setStar(2, R.drawable.ic_review_star_small_no, R.drawable.ic_review_star_small) }
        binding.reviewWriteStar3.setOnClickListener { setStar(3, R.drawable.ic_review_star_small_no, R.drawable.ic_review_star_small) }
        binding.reviewWriteStar4.setOnClickListener { setStar(4, R.drawable.ic_review_star_small_no, R.drawable.ic_review_star_small) }
        binding.reviewWriteStar5.setOnClickListener { setStar(5, R.drawable.ic_review_star_small_no, R.drawable.ic_review_star_small) }

    }

    fun setStar(num: Int, starNo : Int, starYes: Int) {
        binding.reviewWriteStar1.setImageResource(starNo)
        binding.reviewWriteStar2.setImageResource(starNo)
        binding.reviewWriteStar3.setImageResource(starNo)
        binding.reviewWriteStar4.setImageResource(starNo)
        binding.reviewWriteStar5.setImageResource(starNo)
        if(num >= 1) binding.reviewWriteStar1.setImageResource(starYes)
        if(num >= 2) binding.reviewWriteStar2.setImageResource(starYes)
        if(num >= 3) binding.reviewWriteStar3.setImageResource(starYes)
        if(num >= 4) binding.reviewWriteStar4.setImageResource(starYes)
        if(num >= 5) binding.reviewWriteStar5.setImageResource(starYes)
    }
}