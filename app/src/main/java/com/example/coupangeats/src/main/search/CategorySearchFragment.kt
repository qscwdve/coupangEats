package com.example.coupangeats.src.main.search

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentCategorySearchBinding
import com.example.coupangeats.src.main.search.model.searchCategory
import com.softsquared.template.kotlin.config.BaseFragment

class CategorySearchFragment : BaseFragment<FragmentCategorySearchBinding>(FragmentCategorySearchBinding::bind, R.layout.fragment_category_search) {
    private lateinit var mCategoryAdapter : SearchCategoryRecycclerAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SearchCategory RecyclerView Setting
        mCategoryAdapter = SearchCategoryRecycclerAdapter(getSearchCategoryArrayLsit())
        binding.categorySearchCategoryRecyclerview.adapter = mCategoryAdapter
        binding.categorySearchCategoryRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)

    }
    fun getSearchCategoryArrayLsit() : ArrayList<searchCategory>{
        val array = ArrayList<searchCategory>()
        array.add(searchCategory(R.drawable.category_new, "신규 맛집"))
        array.add(searchCategory(R.drawable.category_one, "1인분"))
        array.add(searchCategory(R.drawable.category_korea, "한식"))
        array.add(searchCategory(R.drawable.category_chicken, "치킨"))
        array.add(searchCategory(R.drawable.category_snackbar, "분식"))
        array.add(searchCategory(R.drawable.category_pork, "돈까스"))
        array.add(searchCategory(R.drawable.category_porkfeet, "족발/보쌈"))
        array.add(searchCategory(R.drawable.category_steamed, "찜/탕"))
        array.add(searchCategory(R.drawable.category_girll, "구이"))
        array.add(searchCategory(R.drawable.category_pizza, "피자"))
        array.add(searchCategory(R.drawable.category_china, "중식"))
        array.add(searchCategory(R.drawable.category_japan, "일식"))
        array.add(searchCategory(R.drawable.category_seafood, "회/해물"))
        array.add(searchCategory(R.drawable.category_western, "양식"))
        array.add(searchCategory(R.drawable.category_cha, "커피/차"))
        array.add(searchCategory(R.drawable.category_dessert, "디저트"))
        array.add(searchCategory(R.drawable.category_snack, "간식"))
        array.add(searchCategory(R.drawable.category_asian, "아시안"))
        array.add(searchCategory(R.drawable.category_buger, "버거"))
        array.add(searchCategory(R.drawable.category_mexican, "멕시칸"))
        array.add(searchCategory(R.drawable.category_lunchbox, "도시락"))
        array.add(searchCategory(R.drawable.category_rice, "죽"))
        array.add(searchCategory(R.drawable.category_franchise, "프랜차이즈"))
        return array
    }
}