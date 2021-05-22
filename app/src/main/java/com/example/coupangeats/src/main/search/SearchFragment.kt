package com.example.coupangeats.src.main.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.TextureView
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentSearchBinding
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.search.model.searchCategory
import com.softsquared.template.kotlin.config.BaseFragment

class SearchFragment(val mainActivity: MainActivity) : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind, R.layout.fragment_search) {
    private lateinit var mCategoryAdapter: SearchCategoryRecycclerAdapter
    private var mSearchAble = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        mainActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.search_fragment, CategorySearchFragment())
            .commitAllowingStateLoss()

        //search_fragment
        binding.searchEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.search_fragment, AdvencedSearchFragment())
                .commitAllowingStateLoss()
                binding.searchBackImg.visibility = View.VISIBLE
                mainActivity.setBottomNavigationBarGone()
            }
        }

        // 검색 눌렀을 때
        binding.searchSearchImg.setOnClickListener {
            if(mSearchAble){
                // 서버한테 검색 요청
                showCustomToast("검색 가능 상태")
            }
        }
        // 뒤로가기 버튼
        binding.searchBackImg.setOnClickListener {
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.search_fragment, CategorySearchFragment())
                .commitAllowingStateLoss()
            binding.searchBackImg.visibility = View.GONE
            mainActivity.setBottomNavigationBarVisible()
            binding.searchEditText.clearFocus()
            binding.searchEditText.setText("")
            inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

        }
        // 엑스 버튼 눌렀을 때
        binding.searchEditDelete.setOnClickListener {
            binding.searchSearchImg.setImageResource(R.drawable.nav_search)
            binding.searchEditText.setText("")
            binding.searchEditDelete.visibility = View.GONE
            mSearchAble = false
        }

        // 검색 하는 도중
        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!mSearchAble && binding.searchEditText.text.toString() != ""){
                    // 사용자가 검색하기 시작할 때
                    mSearchAble = true
                    binding.searchSearchImg.setImageResource(R.drawable.nav_click_search)
                    binding.searchEditDelete.visibility = View.VISIBLE
                } else if(mSearchAble && binding.searchEditText.text.toString() == ""){
                    // 아무것도 검색하지 않았을 때
                    mSearchAble = false
                    binding.searchSearchImg.setImageResource(R.drawable.nav_search)
                    binding.searchEditDelete.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

    }

}