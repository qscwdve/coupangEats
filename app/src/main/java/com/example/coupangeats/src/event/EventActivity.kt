package com.example.coupangeats.src.event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.EventLog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityEventBinding
import com.example.coupangeats.src.event.adapter.EventDetailAdapter
import com.example.coupangeats.src.event.model.EventDetail
import com.example.coupangeats.src.event.model.EventDetailResponse
import com.example.coupangeats.src.eventItem.EventItemActivity
import com.softsquared.template.kotlin.config.BaseActivity

class EventActivity : BaseActivity<ActivityEventBinding>(ActivityEventBinding::inflate), EventActivityView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val version = intent.getIntExtra("version", -1)

        binding.eventTitle.text = if(version == -1){
            "진행중인 이벤트"
        } else {
            "전체보기"
        }

        EventService(this).tryGetEventInfo()
        binding.eventBack.setOnClickListener { finish() }
    }

    fun startEventItem(eventIdx: Int){
        val intent = Intent(this, EventItemActivity::class.java).apply{
            this.putExtra("eventIdx", eventIdx)
        }
        startActivity(intent)
    }

    override fun onGetEventInfoSuccess(response: EventDetailResponse) {
        if(response.code == 1000){
            val result = response.result
            if(result != null){
                binding.eventRecyclerView.adapter = EventDetailAdapter(result, this)
                binding.eventRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        }
    }

    override fun onGetEventInfoFailure(message: String) {
        val eventList = ArrayList<EventDetail>()

        eventList.add(EventDetail(1, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/images%2Fznvkdzero.JPG?alt=media&token=9b8d4dc2-7d1a-492e-b114-c41ed1f12d53", "~ 7/31 까지"))
        eventList.add(EventDetail(2, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/images%2Fenfpwnfm.JPG?alt=media&token=1d513824-440b-4260-ac1e-5832660988e4", "~ 7/31 까지"))
        eventList.add(EventDetail(3, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/images%2Fdsaaddsa.JPG?alt=media&token=9a2769bb-285e-47d8-974e-00c5dcd0f726", "~ 7/31 까지"))
        eventList.add(EventDetail(1, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/images%2Fznvkdzero.JPG?alt=media&token=9b8d4dc2-7d1a-492e-b114-c41ed1f12d53", "~ 7/31 까지"))
        eventList.add(EventDetail(2, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/images%2Fenfpwnfm.JPG?alt=media&token=1d513824-440b-4260-ac1e-5832660988e4", "~ 7/31 까지"))
        eventList.add(EventDetail(3, "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/images%2Fdsaaddsa.JPG?alt=media&token=9a2769bb-285e-47d8-974e-00c5dcd0f726", "~ 7/31 까지"))

        binding.eventRecyclerView.adapter = EventDetailAdapter(eventList, this)
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}