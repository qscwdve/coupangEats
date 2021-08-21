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

class EventActivity : BaseActivity<ActivityEventBinding>(ActivityEventBinding::inflate),
    EventActivityView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val version = intent.getIntExtra("version", -1)

        binding.eventTitle.text = if (version == -1) {
            "진행중인 이벤트"
        } else {
            "전체보기"
        }

        EventService(this).tryGetEventInfo()
        binding.eventBack.setOnClickListener { finish() }
    }

    fun startEventItem(eventIdx: Int) {
        val intent = Intent(this, EventItemActivity::class.java).apply {
            this.putExtra("eventIdx", eventIdx)
        }
        startActivity(intent)
    }

    override fun onGetEventInfoSuccess(response: EventDetailResponse) {
        if (response.code == 1000 && response.result != null && response.result.isNotEmpty()) {
            binding.eventRecyclerView.adapter = EventDetailAdapter(response.result, this)
            binding.eventRecyclerView.layoutManager = LinearLayoutManager(this)
        } else dummyEvent()
    }

    override fun onGetEventInfoFailure(message: String) {
        dummyEvent()
    }

    fun dummyEvent() {
        binding.eventRecyclerView.adapter = EventDetailAdapter(
            arrayListOf(
                EventDetail(1, null, null, R.drawable.bbq_event),
                EventDetail(2, null, null, R.drawable.isaac_event)
            ), this
        )
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(this)

    }
}