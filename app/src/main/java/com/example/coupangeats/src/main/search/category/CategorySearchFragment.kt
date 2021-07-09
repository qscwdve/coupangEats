package com.example.coupangeats.src.main.search.category

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentCategorySearchBinding
import com.example.coupangeats.src.main.search.SearchFragment
import com.example.coupangeats.src.main.search.adapter.SearchCategoryAdapter
import com.example.coupangeats.src.main.search.category.model.SuperCategoryResponse
import com.softsquared.template.kotlin.config.BaseFragment

class CategorySearchFragment(val fragment: SearchFragment) : BaseFragment<FragmentCategorySearchBinding>(FragmentCategorySearchBinding::bind, R.layout.fragment_category_search),
 CategorySearchFragmentView{
    private lateinit var mCategoryAdapter : SearchCategoryAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 서버 통신
        CategorySearchService(this).tryGetSuperCategory()
    }

    override fun onGetSuperCategorySuccess(response: SuperCategoryResponse) {
        if(response.code == 1000){
            // SearchCategory RecyclerView Setting
            mCategoryAdapter = SearchCategoryAdapter(response.result, fragment)
            binding.categorySearchCategoryRecyclerview.adapter = mCategoryAdapter
            binding.categorySearchCategoryRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)

        }
    }

    override fun onGetSuperCategoryFailure(message: String) {
        //showCustomToast("검색 카테고리 가져오기 실패")
    }
}