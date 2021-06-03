package com.example.coupangeats.src.categorySuper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.databinding.ActivityCategorySuperBinding
import com.example.coupangeats.src.categorySuper.adapter.CategorySuperAdapter
import com.example.coupangeats.src.categorySuper.adapter.RecommendCategoryAdapter
import com.example.coupangeats.src.categorySuper.model.CategorySuperResponse
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.main.home.adapter.RecommendAdapter
import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponse
import com.softsquared.template.kotlin.config.BaseActivity

class CategorySuperActivity : BaseActivity<ActivityCategorySuperBinding>(ActivityCategorySuperBinding::inflate), CategorySuperActivityView {
    private lateinit var option : String
    private lateinit var mLat : String
    private lateinit var mLon : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카테고리 받아옴
        option = intent.getStringExtra("categoryName") ?: ""
        mLat = intent.getStringExtra("lat") ?: ""
        mLon = intent.getStringExtra("lon") ?: ""
        // 카테고리 선택
        CategorySuperService(this).tryGetSuperCategory()
        CategorySuperService(this).tryGetCategorySuper(mLat, mLon,option, "recomm", null, null, null, null)
    }

    override fun onGetCategorySuperSuccess(response: CategorySuperResponse) {
        if(response.code == 1000){
            if(response.result.recommendStores != null){
                binding.categorySuperRecommendRecyclerView.adapter = RecommendCategoryAdapter(response.result.recommendStores, this)
                binding.categorySuperRecommendRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.categorySuperRecommendRecyclerView.visibility = View.VISIBLE
            } else {
                binding.categorySuperRecommendRecyclerView.visibility = View.GONE
            }
        }
    }

    override fun onGetCategorySuperFailure(message: String) {

    }

    override fun onGetSuperCategorySuccess(response: SuperCategoryResponse) {
        if(response.code == 1000){

            binding.categorySuperCategoryRecyclerView.adapter = CategorySuperAdapter(response.result, this, option)
            binding.categorySuperCategoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onGetSuperCategoryFailure(message: String) {

    }

    fun categoryChange(value: String) {
        // 바뀐걸로 서버 통신 해야 함
        CategorySuperService(this).tryGetCategorySuper(mLat, mLon, value, "recomm", null, null, null, null)

    }

    fun startSuper(storeIdx: Int){
        val intent = Intent(this, DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }
}