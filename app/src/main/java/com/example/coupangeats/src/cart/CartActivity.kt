package com.example.coupangeats.src.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coupangeats.databinding.ActivityCartBinding
import com.softsquared.template.kotlin.config.BaseActivity

class CartActivity : BaseActivity<ActivityCartBinding>(ActivityCartBinding::inflate){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.cartBack.setOnClickListener { finish() }
    }
}