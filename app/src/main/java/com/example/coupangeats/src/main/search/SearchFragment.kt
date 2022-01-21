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
import com.example.coupangeats.src.main.home.model.HomeInfo.StoreCategories
import com.example.coupangeats.src.main.search.adapter.ResentSearchNaviAdapter
import com.example.coupangeats.src.main.search.adapter.SearchCategoryAdapter
import com.example.coupangeats.src.main.search.model.SuperCategoryResponse
import com.example.coupangeats.src.main.search.model.SuperCategoryResponseResult
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
        } else {
            setDumyData()
        }
    }

    override fun onGetSuperCategoryFailure(message: String) {
        binding.categorySearchCategorySwipeRefresh.isRefreshing = false
        //showCustomToast("검색 카테고리 가져오기 실패")
        setDumyData()
    }

    fun setDumyData(){
        // SearchCategory RecyclerView Setting
        val categoryList = ArrayList<SuperCategoryResponseResult>()
        categoryList.add(SuperCategoryResponseResult("신규 맛집", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_new.png?alt=media&token=4b476b9a-ab86-4577-b114-c9b9fe6ac674"))
        categoryList.add(SuperCategoryResponseResult("1인분", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_one.png?alt=media&token=8b966c78-266f-48ea-a794-ba9604d83ada"))
        categoryList.add(SuperCategoryResponseResult("한식", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_korea.png?alt=media&token=ddae262b-93d9-4a24-8350-da29d0d1b740"))
        categoryList.add(SuperCategoryResponseResult("치킨", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_chicken.png?alt=media&token=59b2d3c9-ddd8-4c2e-a2cc-39870c242d32"))
        categoryList.add(SuperCategoryResponseResult("분식", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_snackbar.png?alt=media&token=4f7fba7d-6c46-4aa4-927a-e118a0390bf8"))
        categoryList.add(SuperCategoryResponseResult("돈까스", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_pork.png?alt=media&token=01abe6ea-04c6-491d-98ea-6119825d2e9d"))
        categoryList.add(SuperCategoryResponseResult("족발/보쌈", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_porkfeet.png?alt=media&token=cc1c1fd1-7ec2-4ba5-a8ca-6643e6d8c8fb"))
        categoryList.add(SuperCategoryResponseResult("찜/탕", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_steamed.png?alt=media&token=1a7a1214-a8d5-4663-83d5-78f9cf60fc5b"))
        categoryList.add(SuperCategoryResponseResult("구이", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_girll.png?alt=media&token=d427657a-a9ba-4771-9fc3-c6c1399118bc"))
        categoryList.add(SuperCategoryResponseResult("피자", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_pizza.jpg?alt=media&token=da1479d3-6a2c-4a48-8e1b-1d00d511541e"))
        categoryList.add(SuperCategoryResponseResult("중식", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_china.png?alt=media&token=bf4106b2-43cc-493a-8053-b4d514c9a781"))
        categoryList.add(SuperCategoryResponseResult("일식", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_japan.png?alt=media&token=c98e802b-8c3e-4aad-a010-d67502a8098c"))
        categoryList.add(SuperCategoryResponseResult("회/해물", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_seafood.png?alt=media&token=557775e1-9fac-4371-a935-aa14de4df839"))
        categoryList.add(SuperCategoryResponseResult("양식", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_western.png?alt=media&token=3207834b-a36f-46a9-8fbe-17368105f5d0"))
        categoryList.add(SuperCategoryResponseResult("커피/차", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_cha.png?alt=media&token=06c5c630-4581-4864-9b0f-0fdad5fd1bae"))
        categoryList.add(SuperCategoryResponseResult("디저트", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_dessert.png?alt=media&token=ecffe007-0159-44e1-b247-c8f747b83d68"))
        categoryList.add(SuperCategoryResponseResult("간식", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_snack.png?alt=media&token=d784db43-cce7-4e81-b69c-c0f3686fd2cc"))
        categoryList.add(SuperCategoryResponseResult("아시안", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_asian.png?alt=media&token=55a56695-d41a-4e22-8c5c-7c0cc9dcd000"))
        categoryList.add(SuperCategoryResponseResult("샌드위치", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_sandwich.png?alt=media&token=22427d8a-a491-46e8-8afb-923aa3918a3a"))
        categoryList.add(SuperCategoryResponseResult("샐러드", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_salad.png?alt=media&token=b8636554-6652-47d2-b25f-be6ae867022a"))
        categoryList.add(SuperCategoryResponseResult("버거", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_buger.png?alt=media&token=b3e892a7-776b-4650-a7ac-1ba069a8198c"))
        categoryList.add(SuperCategoryResponseResult("멕시칸", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_mexican.png?alt=media&token=22df2540-3529-4ca3-857e-b42426905e2c"))
        categoryList.add(SuperCategoryResponseResult("도시락", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_lunchbox.png?alt=media&token=e4d7aff1-6b3f-4bed-93e4-6f7554436292"))
        categoryList.add(SuperCategoryResponseResult("죽", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_rice.png?alt=media&token=8efdebc6-7e0c-492d-98ce-2591bfc655f0"))
        categoryList.add(SuperCategoryResponseResult("프랜차이즈", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/category_franchise.png?alt=media&token=fc05bb7e-0a16-4423-92cd-5dcedb3c16a1"))

        binding.categorySearchCategoryRecyclerview.adapter = SearchCategoryAdapter(categoryList, this)
        binding.categorySearchCategoryRecyclerview.layoutManager = GridLayoutManager(requireContext(), 3)

    }
}