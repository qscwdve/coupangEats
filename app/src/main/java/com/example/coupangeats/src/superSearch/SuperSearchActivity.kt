package com.example.coupangeats.src.superSearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivitySuperSearchBinding
import com.example.coupangeats.src.superSearch.adapter.BaseInfoAdapter
import com.example.coupangeats.src.superSearch.model.BaseSuperInfo
import com.example.coupangeats.src.superSearch.model.DiscountSuperResponse
import com.example.coupangeats.src.superSearch.model.NewSuperResponse
import com.softsquared.template.kotlin.config.BaseActivity

class SuperSearchActivity : BaseActivity<ActivitySuperSearchBinding>(ActivitySuperSearchBinding::inflate), SuperSearchActivityView {
    var lat = ""
    var lon = ""
    var version = 1   // 1이면 할인중인 맛집 , 2이면 새로들어온 가게
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lat = intent.getStringExtra("lat") ?: ""
        lon = intent.getStringExtra("lon") ?: ""
        version = intent.getIntExtra("version", 1) ?: 1
        // 호출!!
        binding.superSearchTitle.text = if(version == 1) "할인 중인 맛집" else "새로 들어온 가게!"
        if(version == 1){
            SuperSearchService(this).tryGetDiscountSuper(lat, lon, "recomm", null,"Y", null, null)
        } else {
            binding.searchFilterRecommendText.text = "신규 매장"
            SuperSearchService(this).tryGetNewSuper(lat, lon, "new", null,null, null, null)
        }

        // 종료
        binding.superSearchBack.setOnClickListener { finish() }
    }

    override fun onGetNewSuperSuccess(response: NewSuperResponse) {
        if(response.code == 1000 && response.result.totalCount > 0){
            setRecycler(response.result.newStores!!)
        }
    }

    override fun onGetNewSuperFailure(message: String) {
        showCustomToast("새로들어온 매장 조회 실패")
    }

    override fun onGetDiscountSuperSuccess(response: DiscountSuperResponse) {
        if(response.code == 1000 && response.result.totalCount > 0){
            setRecycler(response.result.onSaleStores!!)
        }
    }

    override fun onGetDiscountSuperFailure(message: String) {
        showCustomToast("할인 매장 조회 실패")
    }

    fun setRecycler(baseSperList : ArrayList<BaseSuperInfo>) {
        binding.searchRecommendRecyclerview.adapter = BaseInfoAdapter(baseSperList)
        binding.searchRecommendRecyclerview.layoutManager = LinearLayoutManager(this)
    }
}