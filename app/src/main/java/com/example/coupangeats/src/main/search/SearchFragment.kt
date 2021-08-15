package com.example.coupangeats.src.main.search

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentSearchBinding
import com.example.coupangeats.src.categorySuper.CategorySuperActivity
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.main.search.adapter.ResentSearchNaviAdapter
import com.example.coupangeats.src.main.search.adapter.SearchCategoryAdapter
import com.example.coupangeats.src.main.search.model.SuperCategoryResponse
import com.example.coupangeats.src.searchDetail.SearchDetailActivity
import com.example.coupangeats.util.ResentSearchDatabase
import com.softsquared.template.kotlin.config.BaseFragment

class SearchFragment(val mainActivity: MainActivity, val version: Int) : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind, R.layout.fragment_search), CategorySearchFragmentView {

    private var mSearchAble = false
    private lateinit var mDBHelper: ResentSearchDatabase
    private lateinit var mDB: SQLiteDatabase
    private lateinit var inputMethodManager : InputMethodManager
    private lateinit var detailSearchActivityLauncher : ActivityResultLauncher<Intent>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // 데이타베이스 셋팅
        mDBHelper = ResentSearchDatabase(requireContext(), "Search.db", null, 1)
        mDB = mDBHelper.writableDatabase

        // detailSearchActivity Result 설정
        detailSearchActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                (binding.searchResentSearchRecyclerview.adapter as ResentSearchNaviAdapter).refresh(mDBHelper.getResentDate(mDB))
            }

        // 최근 검색어 셋팅
        binding.searchResentSearchRecyclerview.adapter = ResentSearchNaviAdapter(mDBHelper.getResentDate(mDB), this)
        binding.searchResentSearchRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        // 카테고리 선택
        binding.categorySearchCategorySwipeRefresh.isRefreshing = true
        CategorySearchService(this).tryGetSuperCategory()

        binding.categorySearchCategorySwipeRefresh.setOnRefreshListener {
            CategorySearchService(this).tryGetSuperCategory()
        }

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
                binding.categorySearchCategorySwipeRefresh.visibility = View.GONE
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
                    mDBHelper.addKeyword(mDB, binding.searchEditText.text.toString())
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
                mDBHelper.addKeyword(mDB, binding.searchEditText.text.toString())
            }
        }
        // 뒤로가기 버튼
        binding.searchBackImg.setOnClickListener {
            if(version == 1){
                binding.searchKeywordParent.visibility = View.GONE
                binding.categorySearchCategorySwipeRefresh.visibility = View.VISIBLE
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
        detailSearchActivityLauncher.launch(intent)
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

    override fun onGetSuperCategorySuccess(response: SuperCategoryResponse) {
        binding.categorySearchCategorySwipeRefresh.isRefreshing = false
        if(response.code == 1000){
            // SearchCategory RecyclerView Setting
            binding.categorySearchCategoryRecyclerview.adapter = SearchCategoryAdapter(response.result, this)
            binding.categorySearchCategoryRecyclerview.layoutManager = GridLayoutManager(requireContext(), 3)

        }
    }

    override fun onGetSuperCategoryFailure(message: String) {
        binding.categorySearchCategorySwipeRefresh.isRefreshing = false
        showCustomToast("검색 카테고리 가져오기 실패")
    }
}