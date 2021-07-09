package com.example.coupangeats.src.main.search

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentSearchBinding
import com.example.coupangeats.src.categorySuper.CategorySuperActivity
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.search.adapter.ResentSearchNaviAdapter
import com.example.coupangeats.src.main.search.category.CategorySearchFragment
import com.example.coupangeats.src.searchDetail.SearchDetailActivity
import com.example.coupangeats.src.searchDetail.adapter.ResentSearchAdapter
import com.example.coupangeats.util.ResentSearchDatabase
import com.softsquared.template.kotlin.config.BaseFragment

class SearchFragment(val mainActivity: MainActivity, val version: Int) : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind, R.layout.fragment_search) {

    private var mSearchAble = false
    private lateinit var mDBHelper: ResentSearchDatabase
    private lateinit var mDB: SQLiteDatabase
    private lateinit var inputMethodManager : InputMethodManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // 데이타베이스 셋팅
        mDBHelper = ResentSearchDatabase(requireContext(), "Search.db", null, 1)
        mDB = mDBHelper.writableDatabase

        // 최근 검색어 셋팅
        binding.searchResentSearchRecyclerview.adapter = ResentSearchNaviAdapter(mDBHelper.getResentDate(mDB), this)
        binding.searchResentSearchRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        mainActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.search_fragment, CategorySearchFragment(this))
            .commitAllowingStateLoss()

        if(version == 1){
            binding.searchKeywordParent.visibility = View.GONE
        } else {
            binding.searchKeywordParent.visibility = View.VISIBLE
            binding.searchBackImg.visibility = View.VISIBLE
            mainActivity.setBottomNavigationBarGone()
            binding.searchSearchImg.setImageResource(R.drawable.ic_nav_search)
        }

        // AdvencedSearchFragment 전환
        binding.searchEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.searchKeywordParent.visibility = View.VISIBLE
                binding.searchBackImg.visibility = View.VISIBLE
                binding.searchFragment.visibility = View.GONE
                mainActivity.setBottomNavigationBarGone()
                binding.searchSearchImg.setImageResource(R.drawable.ic_nav_search)
            }
        }

        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                // 엔터키가 눌림
                    // showCustomToast("엔터키 눌림")
                if (mSearchAble) {
                    startSearch(binding.searchEditText.text.toString())
                    inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
                }
                true
            } else false
        }

        // 검색 눌렀을 때
        binding.searchSearchImg.setOnClickListener {
            if(mSearchAble){
                // 서버한테 검색 요청
                // showCustomToast("검색 가능 상태")
                inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
                startSearch(binding.searchEditText.text.toString())
            }
        }
        // 뒤로가기 버튼
        binding.searchBackImg.setOnClickListener {
            if(version == 1){
                binding.searchKeywordParent.visibility = View.GONE
                binding.searchFragment.visibility = View.VISIBLE
                binding.searchBackImg.visibility = View.GONE
                mainActivity.setBottomNavigationBarVisible()
                binding.searchEditText.clearFocus()
                binding.searchEditText.setText("")
                binding.searchSearchImg.setImageResource(R.drawable.ic_nav_click_search)
                inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
            } else {
                   mainActivity.setHomeFragment()
            }
        }
        // 엑스 버튼 눌렀을 때
        binding.searchEditDelete.setOnClickListener {
            binding.searchSearchImg.setImageResource(R.drawable.ic_nav_search)
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
                    binding.searchSearchImg.setImageResource(R.drawable.ic_nav_click_search)
                    binding.searchEditDelete.visibility = View.VISIBLE
                } else if(mSearchAble && binding.searchEditText.text.toString() == ""){
                    // 아무것도 검색하지 않았을 때
                    mSearchAble = false
                    binding.searchSearchImg.setImageResource(R.drawable.ic_nav_search)
                    binding.searchEditDelete.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }

    fun startCategorySuper(option: String) {
        val intent = Intent(requireContext(), CategorySuperActivity::class.java).apply {
            this.putExtra("lat", mainActivity.mLat)
            this.putExtra("lon", mainActivity.mLon)
            this.putExtra("categoryName", option)
        }
        startActivity(intent)
    }

    fun startSearch(keyword: String){
        val intent = Intent(requireContext(), SearchDetailActivity::class.java).apply {
            this.putExtra("lat", mainActivity.mLat)
            this.putExtra("lon", mainActivity.mLon)
            this.putExtra("keyword", keyword)
        }
        startActivity(intent)
    }

    fun startResentSearch(key: String, id: Int){
        inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        mDBHelper.modifyDate(mDB, id)
        (binding.searchResentSearchRecyclerview.adapter as ResentSearchNaviAdapter).refresh(mDBHelper.getResentDate(mDB))
        startSearch(key)
    }

    fun deleteResentSearchItem(id: Int){
        mDBHelper.deleteToId(mDB, id)
    }

    override fun onResume() {
        super.onResume()
        binding.searchEditText.setText("")
    }
}