package com.example.coupangeats.src.eventItem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.coupangeats.databinding.ActivityEventItemBinding
import com.example.coupangeats.src.eventItem.model.EventItemResponse
import com.softsquared.template.kotlin.config.BaseActivity

class EventItemActivity : BaseActivity<ActivityEventItemBinding>(ActivityEventItemBinding::inflate), EventItemActivityView{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val eventIdx = intent.getIntExtra("eventIdx", -1)

        if(eventIdx != -1) EventItemService(this).tryGetEventItem(eventIdx)
    }

    override fun onGetEventItemSuccess(response: EventItemResponse) {
        if(response.code == 1000){
            binding.eventItemTitle.text = response.result.title
            Glide.with(binding.eventItemImg).load(response.result.imageUrl).into(binding.eventItemImg)
        }
    }

    override fun onGetEventItemFailure(message: String) {

    }
}