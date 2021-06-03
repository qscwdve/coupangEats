package com.example.coupangeats.src.favorites

import android.os.Bundle
import com.example.coupangeats.databinding.ActivityFavoritesBinding
import com.softsquared.template.kotlin.config.BaseActivity

class FavoritesActivity : BaseActivity<ActivityFavoritesBinding>(ActivityFavoritesBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.favoritesBack.setOnClickListener {
            finish()
        }
    }
}