package com.example.coupangeats.src.menuSelect

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityMenuSelectBinding
import com.example.coupangeats.src.cart.model.CartMenuInfo
import com.example.coupangeats.src.detailSuper.adapter.DetailSuperImgViewPagerAdapter
import com.example.coupangeats.src.menuSelect.adapter.MenuDetailParentAdapter
import com.example.coupangeats.src.menuSelect.adapter.necessaryCheckData
import com.example.coupangeats.src.menuSelect.model.MenuDetailResponse
import com.example.coupangeats.src.menuSelect.model.MenuOption
import com.example.coupangeats.src.menuSelect.model.Option
import com.example.coupangeats.src.menuSelect.model.SelectMenu
import com.example.coupangeats.util.CartMenuDatabase
import com.google.android.material.appbar.AppBarLayout
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class MenuSelectActivity : BaseActivity<ActivityMenuSelectBinding>(ActivityMenuSelectBinding::inflate), MenuSelectActivityView {
    private var menuIdx = -1
    private var mStoreIdx = -1
    private var mMenuPrice = 0
    private var mSelectedMenu : Array<SelectMenu>? = null
    private var mSelectMenuCheck : Array<Boolean>? = null
    private enum class CollapsingToolbarLayoutState {
        EXPANDED, COLLAPSED, INTERNEDIATE
    }
    private var mCollapsingToolbarState : CollapsingToolbarLayoutState? = null
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
            if((binding.menuSelectRecyclerView.adapter as MenuDetailParentAdapter).checkNecessary()){
                val mainMenu = binding.menuSelectMenuName.text.toString()
                var sideMenu = ""
                var totalPrice = mMenuPrice
                val num = binding.menuSelectNum.text.toString()
                // 사이드 메뉴
                if(mSelectMenuCheck != null){
                    for(index in mSelectMenuCheck!!.indices){
                        if(mSelectMenuCheck!![index]){
                            // 사이드 메뉴 있음
                            sideMenu += mSelectedMenu!![index].cotent + ", "
                            totalPrice += mSelectedMenu!![index].price
                        }
                        Log.d("MenuSelectSide", "있는지 없는지 : ${index}")
                        Log.d("MenuSelectSide", "내용있 있는지 : ${mSelectedMenu!![index]}")
                    }
                }
                if(sideMenu.length >= 2 && sideMenu[sideMenu.length - 2] == ','){
                    sideMenu = sideMenu.substring(0 until (sideMenu.length - 2))
                }
                Log.d("MenuSelectSide", "conent : ${sideMenu} ")
                Log.d("MenuSelectSide", "price : ${totalPrice} ")
                // 메뉴 저장
                saveMenuFinish(CartMenuInfo(null, mainMenu, num.toInt(), totalPrice, sideMenu, menuIdx))
                setResult(RESULT_OK)
                finish()
            } else {
                showCustomToast("필수선택을 체크해주세요")
            }
        }

        // 툴바 제어
        setSupportActionBar(binding.menuSelectToolbar)
        val stateListAnimator = StateListAnimator()
        stateListAnimator.addState(intArrayOf(), ObjectAnimator.ofFloat(binding.menuSelectAppBar, "elevation", 0F))
        binding.menuSelectAppBar.stateListAnimator = stateListAnimator
        binding.menuSelectAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener{
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if(verticalOffset == 0){
                    if(mCollapsingToolbarState != CollapsingToolbarLayoutState.EXPANDED){
                        binding.menuSelectToolbar.setBackgroundColor(Color.parseColor("#00000000"))
                        binding.toolbarSuperName.text = ""
                        binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_white)
                        binding.toolbarShare.setImageResource(R.drawable.ic_share_white)
                        mCollapsingToolbarState = CollapsingToolbarLayoutState.EXPANDED
                    }
                }
                else if(Math.abs(verticalOffset) >= appBarLayout!!.totalScrollRange){
                    // 액션바 스크롤 맨 위로 올림
                    binding.menuSelectToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    binding.toolbarSuperName.text = "면을 품은 활금찜닭 한마리"
                    binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_black)
                    binding.toolbarShare.setImageResource(R.drawable.ic_share_black)
                    mCollapsingToolbarState = CollapsingToolbarLayoutState.COLLAPSED
                }
                else if(Math.abs(verticalOffset) < appBarLayout.totalScrollRange){
                    //Log.d("vertical", "아직 액션바 안에 있음")
                    if(mCollapsingToolbarState != CollapsingToolbarLayoutState.INTERNEDIATE){
                        binding.toolbarSuperName.text = ""
                        binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_white)
                        binding.toolbarShare.setImageResource(R.drawable.ic_share_white)
                        binding.menuSelectToolbar.setBackgroundColor(Color.parseColor("#00000000"))
                        mCollapsingToolbarState = CollapsingToolbarLayoutState.INTERNEDIATE
                    }
                }
            }

        })

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
                binding.menuSelectImgViewPager.visibility = View.VISIBLE
                binding.menuSelectImgNumParent.visibility = View.VISIBLE
            } else {
                binding.menuSelectImgViewPager.visibility = View.GONE
                binding.menuSelectImgNumParent.visibility = View.GONE
                binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_black)
                binding.toolbarShare.setImageResource(R.drawable.ic_share_black)
                binding.toolbarSuperName.text = "면을 품은 활금찜닭 한마리"
            }
        } else notServerDumyData()
    }

    override fun onGetMenuDetailFailure(message: String) {
        //showCustomToast("메뉴 불러오기 실패")
        notServerDumyData()
    }

    fun notServerDumyData(){
        binding.menuSelectToolbarItem.visibility = View.VISIBLE
        binding.menuSelectImgNumParent.visibility = View.VISIBLE
        mMenuPrice = 48000
        // 사이드 메뉴 있으면 추가
        val menuOptionList = ArrayList<MenuOption>()
        val option1 = ArrayList<Option>()
        option1.add(Option("기본", 0))
        option1.add(Option("중국 당면", 3000))
        option1.add(Option("납작 당면", 2000))
        option1.add(Option("둥근 당면", 1000))
        val option2 = ArrayList<Option>()
        option2.add(Option("공기밥", 1000))
        option2.add(Option("고기 추가", 3000))
        option2.add(Option("치즈 추가", 3000))
        option2.add(Option("야채 추가", 1000))
        option2.add(Option("떡 추가", 1000))
        option2.add(Option("라면사리 추가", 2000))
        menuOptionList.add(MenuOption("당면", "Y", 1, option1))
        menuOptionList.add(MenuOption("사이드 추가", "N", 999, option2))

        binding.menuSelectRecyclerView.adapter = MenuDetailParentAdapter(menuOptionList, this)
        binding.menuSelectRecyclerView.layoutManager = LinearLayoutManager(this)
        mSelectedMenu = Array(menuOptionList.size) {i -> SelectMenu("", 0) }
        mSelectMenuCheck = Array(menuOptionList.size) {i -> false}

        // 이미지 뷰페이저
        val imageList = ArrayList<String>()
        imageList.add("https://t1.daumcdn.net/cfile/tistory/22084E4B593DF4D714")
        imageList.add("https://dbscthumb-phinf.pstatic.net/2765_000_39/20181007210844284_LW82GFYCC.jpg/247063.jpg?type=m4500_4500_fst&wm=N")
        binding.menuSelectImgViewPager.adapter = DetailSuperImgViewPagerAdapter(imageList)
        binding.menuSelectImgViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // 넘기는 이벤트
        // ViewPager 에 따라 숫자 넘기기
        val totalNum = " / ${imageList.size}"
        binding.menuSelectImgNumTotal.text = totalNum
        binding.menuSelectImgViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.menuSelectImgNum.text = (position + 1).toString()
            }
        })
        binding.menuSelectImgViewPager.visibility = View.VISIBLE
        binding.menuSelectImgNumParent.visibility = View.VISIBLE
        binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_white)
        binding.toolbarShare.setImageResource(R.drawable.ic_share_white)
        binding.toolbarSuperName.text = ""
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