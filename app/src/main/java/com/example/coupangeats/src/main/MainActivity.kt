package com.example.coupangeats.src.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityMainBinding
import com.example.coupangeats.databinding.ActivitySplashBinding
import com.example.coupangeats.src.favorites.FavoritesActivity
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.main.myeats.MyeatsFragment
import com.example.coupangeats.src.main.order.OrderFragment
import com.example.coupangeats.src.main.search.AdvencedSearchFragment
import com.example.coupangeats.src.main.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.softsquared.template.kotlin.config.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private var mfragmentIndex = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainBtmNav.itemIconTintList = null

        binding.mainBtmNav.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when(item.itemId){
                    R.id.menu_main_btm_nav_home -> {
                        mfragmentIndex = 1
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, HomeFragment())
                            .commitAllowingStateLoss()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_search -> {
                        mfragmentIndex = 2
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, SearchFragment(this))
                            .commitAllowingStateLoss()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_favorites -> {
                        mfragmentIndex = 3
                        startActivity(Intent(this, FavoritesActivity::class.java))
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_order -> {
                        mfragmentIndex = 4
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, OrderFragment())
                            .commitAllowingStateLoss()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_myeats -> {
                        mfragmentIndex = 5
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, MyeatsFragment())
                            .commitAllowingStateLoss()
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        )
    }

    override fun onBackPressed() {
        if(mfragmentIndex != 1){
            setBottomNavigationBarVisible()
            binding.mainBtmNav.selectedItemId = R.id.menu_main_btm_nav_home
        } else {
            super.onBackPressed()
        }
    }

    // search Fragment 에서 사용할 기능
    fun setAdvencedSearchFragment() {
        binding.mainBtmNav.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AdvencedSearchFragment())
            .commitAllowingStateLoss()
    }
    fun setSearchFramgment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, SearchFragment(this))
            .commitAllowingStateLoss()
    }
    fun setBottomNavigationBarGone() { binding.mainBtmNav.visibility = View.GONE }
    fun setBottomNavigationBarVisible() {binding.mainBtmNav.visibility = View.VISIBLE}
    fun setKeyBoardShutDown(editText: EditText){
        // 키보드 내리기
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}