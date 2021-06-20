package com.example.coupangeats.src.menuSelect

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.databinding.ActivityMenuSelectBinding
import com.example.coupangeats.src.cart.model.CartMenuInfo
import com.example.coupangeats.src.detailSuper.detailSuperFragment.adapter.DetailSuperImgViewPagerAdapter
import com.example.coupangeats.src.menuSelect.adapter.MenuDetailParentAdapter
import com.example.coupangeats.src.menuSelect.model.MenuDetailResponse
import com.example.coupangeats.src.menuSelect.model.SelectMenu
import com.example.coupangeats.util.CartMenuDatabase
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class MenuSelectActivity : BaseActivity<ActivityMenuSelectBinding>(ActivityMenuSelectBinding::inflate), MenuSelectActivityView {
    private var menuIdx = -1
    private var mStoreIdx = -1
    private var mMenuPrice = 0
    private var mSelectedMenu : Array<SelectMenu>? = null
    private var mSelectMenuCheck : Array<Boolean>? = null
    private lateinit var mDBHelper: CartMenuDatabase
    private lateinit var mDB: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuIdx = intent.getIntExtra("menuIdx", -1)
        mStoreIdx = intent.getIntExtra("storeIdx", -1)

        // 데이터베이스 셋팅
        mDBHelper = CartMenuDatabase(this, "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        // 메뉴 받아오는 통신
        MenuSelectService(this).tryGetMenuDetail(mStoreIdx, menuIdx)

        // 수량 제어
        binding.menuSelectNumPlus.setOnClickListener {
            val num = binding.menuSelectNum.text.toString().toInt() + 1
            binding.menuSelectNum.text = num.toString()
            val menuPrice = priceIntToString(mMenuPrice * num) + "원"
            binding.menuSelectMenuPrice.text = menuPrice
        }
        binding.menuSelectNumMinus.setOnClickListener {
            val num = binding.menuSelectNum.text.toString().toInt() - 1
            if(num >= 1){
                binding.menuSelectNum.text = num.toString()
                val menuPrice = priceIntToString(mMenuPrice * num) + "원"
                binding.menuSelectMenuPrice.text = menuPrice
            }
        }
        // 카트에 담기!!
        binding.menuSelectComplete.setOnClickListener {
            // 카트에 담는다.
            // 메인 메뉴
            val mainMenu = binding.menuSelectMenuName.text.toString()
            var sideMenu = ""
            var totalPrice =  binding.menuSelectNum.text.toString().toInt() * mMenuPrice
            val num = binding.menuSelectNum.text.toString()
            // 사이드 메뉴
            if(mSelectMenuCheck != null){
                for(index in mSelectMenuCheck!!.indices){
                    if(mSelectMenuCheck!![index]){
                        // 사이드 메뉴 있음
                        sideMenu += mSelectedMenu!![index].cotent + ", "
                        totalPrice += mSelectedMenu!![index].price
                    }
                }
            }
            Log.d("MenuSelectSide", "conent : ${sideMenu} ")
            Log.d("MenuSelectSide", "price : ${totalPrice} ")
            // 메뉴 저장
            saveMenuFinish(CartMenuInfo(null, mainMenu, num.toInt(), totalPrice, sideMenu, menuIdx))
            setResult(RESULT_OK)
            finish()
        }
    }

    // 메뉴 내용과 가격 저장
    fun saveMenuFinish(menu: CartMenuInfo){

        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putInt("storeIdx", mStoreIdx).apply()
        mDBHelper.menuInsert(mDB, menu)

    }

    fun saveMenuInfo(position: Int, selectMenu: SelectMenu){
        if(mSelectedMenu != null) {
            mSelectedMenu!![position] = selectMenu
            mSelectMenuCheck!![position] = true
        }
    }

    override fun onGetMenuDetailSuccess(response: MenuDetailResponse) {
        if(response.code == 1000){
            binding.menuSelectMenuName.text = response.result.menuName
            if(response.result.introduce != null){
                binding.menuSelectMenuIntroduce.visibility = View.VISIBLE
                binding.menuSelectMenuIntroduce.text = response.result.introduce
            } else {
                binding.menuSelectMenuIntroduce.visibility = View.GONE
            }
            val price = "${priceIntToString(response.result.price)}원"
            mMenuPrice = response.result.price
            binding.menuSelectMenuPrice.text = price
            if(response.result.menuOption != null){
                // 사이드 메뉴 있으면 추가
                binding.menuSelectRecyclerView.adapter = MenuDetailParentAdapter(response.result.menuOption, this)
                binding.menuSelectRecyclerView.layoutManager = LinearLayoutManager(this)
                mSelectedMenu = Array(response.result.menuOption.size) {i -> SelectMenu("", 0) }
                mSelectMenuCheck = Array(response.result.menuOption.size) {i -> false}
            }

            if(response.result.url != null){
                // 이미지 뷰페이저
                binding.menuSelectImgViewPager.adapter = DetailSuperImgViewPagerAdapter(response.result.url)
                binding.menuSelectImgViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

                // 넘기는 이벤트
                // ViewPager 에 따라 숫자 넘기기
                val totalNum = " / ${response.result.url.size}"
                binding.menuSelectImgNumTotal.text = totalNum
                binding.menuSelectImgViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.menuSelectImgNum.text = (position + 1).toString()
                    }
                })
            } else {
                binding.menuSelectImgViewPager.visibility = View.GONE
                binding.menuSelectImgBox.visibility = View.GONE
            }
        }
    }

    override fun onGetMenuDetailFailure(message: String) {
        //showCustomToast("메뉴 불러오기 실패")
    }
    fun priceIntToString(value: Int) : String {
        val target = value.toString()
        val size = target.length
        return if(size > 3){
            val last = target.substring(size - 3 until size)
            val first = target.substring(0..(size - 4))
            "$first,$last"
        } else target
    }
}