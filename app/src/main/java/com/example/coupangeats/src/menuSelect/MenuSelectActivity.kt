package com.example.coupangeats.src.menuSelect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coupangeats.databinding.ActivityMenuSelectBinding
import com.softsquared.template.kotlin.config.BaseActivity

class MenuSelectActivity : BaseActivity<ActivityMenuSelectBinding>(ActivityMenuSelectBinding::inflate) {
    private var menuIdx = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuIdx = intent.getIntExtra("menuIdx", -1)

        // 메뉴 받아오는 통신


    }
}