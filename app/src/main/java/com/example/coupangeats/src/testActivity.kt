package com.example.coupangeats.src

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coupangeats.databinding.ActivityTestBinding

class testActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tesText2.text = "dsjkajls"

    }
}