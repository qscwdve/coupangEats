package com.example.coupangeats.src.menuSelect

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityMenuSelectBinding
import com.example.coupangeats.databinding.DialogCartReplaceBinding
import com.example.coupangeats.src.cart.model.CartMenuInfo
import com.example.coupangeats.src.detailSuper.adapter.DetailSuperImgViewPagerAdapter
import com.example.coupangeats.src.lookImage.LookImageActivity
import com.example.coupangeats.src.main.MainActivity
import com.example.coupangeats.src.menuSelect.adapter.MenuDetailParentAdapter
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
    private var mSuperName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition( R.anim.horizon_start_enter, R.anim.horizon_start_exit)

        menuIdx = intent.getIntExtra("menuIdx", -1)
        mStoreIdx = intent.getIntExtra("storeIdx", -1)
        mSuperName = intent.getStringExtra("storeName") ?: "???????????? ????????????"

        // ?????? ??????
        binding.toolbarSuperName.text = mSuperName
        // ?????????????????? ??????
        mDBHelper = CartMenuDatabase(this, "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        // ?????? ???????????? ??????
        MenuSelectService(this).tryGetMenuDetail(mStoreIdx, menuIdx)

        // ?????? ??????
        binding.menuSelectNumPlus.setOnClickListener {
            val num = binding.menuSelectNum.text.toString().toInt() + 1
            binding.menuSelectNum.text = num.toString()
            val menuPrice = priceIntToString(mMenuPrice * num) + "???"
            binding.menuSelectMenuPrice.text = menuPrice
        }
        binding.menuSelectNumMinus.setOnClickListener {
            val num = binding.menuSelectNum.text.toString().toInt() - 1
            if (num >= 1) {
                binding.menuSelectNum.text = num.toString()
                val menuPrice = priceIntToString(mMenuPrice * num) + "???"
                binding.menuSelectMenuPrice.text = menuPrice
            }
        }
        // ????????? ??????!!
        binding.menuSelectComplete.setOnClickListener {
            // ????????? ?????????.
            var flag = true
            // ????????? ????????? ????????? ??????
            val cartMenuNum = mDBHelper.checkMenuNum(mDB)
            if (cartMenuNum > 0) {
                if (getStoreIdx() == mStoreIdx) {
                    // ????????? ?????? ??? ??????
                } else {
                    // ????????? ??????
                    flag = false
                }
            } else {
                // ????????? ?????? ??????
            }
            // ?????? ??????
            val adapter: MenuDetailParentAdapter? =
                (binding.menuSelectRecyclerView.adapter as MenuDetailParentAdapter?)
            if (adapter == null) {
                val mainMenu = binding.menuSelectMenuName.text.toString()
                val totalPrice = mMenuPrice
                val num = binding.menuSelectNum.text.toString()
                // ?????? ??????
                if (flag) {
                    saveMenuFinish(
                        CartMenuInfo(
                            null,
                            mainMenu,
                            num.toInt(),
                            totalPrice,
                            null,
                            menuIdx
                        )
                    )
                } else {
                    startChangeCartDialog(CartMenuInfo(
                        null,
                        mainMenu,
                        num.toInt(),
                        totalPrice,
                        null,
                        menuIdx
                    ))
                }
            } else {
                val index = (binding.menuSelectRecyclerView.adapter as MenuDetailParentAdapter).checkNecessary()
                if (index == -1) {
                    val mainMenu = binding.menuSelectMenuName.text.toString()
                    var sideMenu = ""
                    var totalPrice = mMenuPrice
                    val num = binding.menuSelectNum.text.toString()
                    // ????????? ??????
                    if (mSelectMenuCheck != null) {
                        for (index in mSelectMenuCheck!!.indices) {
                            if (mSelectMenuCheck!![index]) {
                                // ????????? ?????? ??????
                                sideMenu += mSelectedMenu!![index].cotent + ", "
                                totalPrice += mSelectedMenu!![index].price
                            }
                            Log.d("MenuSelectSide", "????????? ????????? : ${index}")
                            Log.d("MenuSelectSide", "????????? ????????? : ${mSelectedMenu!![index]}")
                        }
                    }
                    if (sideMenu.length >= 2 && sideMenu[sideMenu.length - 2] == ',') {
                        sideMenu = sideMenu.substring(0 until (sideMenu.length - 2))
                    }
                    Log.d("MenuSelectSide", "conent : ${sideMenu} ")
                    Log.d("MenuSelectSide", "price : ${totalPrice} ")
                    // ?????? ??????
                    if (flag) {
                        saveMenuFinish(
                            CartMenuInfo(
                                null,
                                mainMenu,
                                num.toInt(),
                                totalPrice,
                                sideMenu,
                                menuIdx
                            )
                        )
                    } else {
                        startChangeCartDialog(CartMenuInfo(
                            null,
                            mainMenu,
                            num.toInt(),
                            totalPrice,
                            sideMenu,
                            menuIdx
                        ))
                    }
                } else {
                    //showCustomToast("??????????????? ??????????????????")
                    (binding.menuSelectRecyclerView.adapter as MenuDetailParentAdapter).changeNecessaryCheck()
                    val menuPosition = if(index == 0){
                        0
                    } else {
                        (binding.menuSelectRecyclerView.adapter as MenuDetailParentAdapter).getPosition(index - 1)
                    }
                    binding.menuSelectAppBar.setExpanded(false)
                    binding.menuSelectContent.scrollTo(0, menuPosition + binding.menuSelectRecyclerView.top)
                }
            }
        }

        // ????????????
        binding.toolbarBack.setOnClickListener { finish() }

        // ?????? ??????
        setSupportActionBar(binding.menuSelectToolbar)
        val stateListAnimator = StateListAnimator()
        stateListAnimator.addState(
            intArrayOf(),
            ObjectAnimator.ofFloat(binding.menuSelectAppBar, "elevation", 0F)
        )
        binding.menuSelectAppBar.stateListAnimator = stateListAnimator
        binding.menuSelectAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (verticalOffset == 0) {
                    if (mCollapsingToolbarState != CollapsingToolbarLayoutState.EXPANDED) {
                        binding.menuSelectToolbar.setBackgroundColor(Color.parseColor("#00000000"))
                        binding.toolbarSuperName.text = ""
                        binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_white)
                        binding.toolbarShare.setImageResource(R.drawable.ic_share_white)
                        mCollapsingToolbarState = CollapsingToolbarLayoutState.EXPANDED
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout!!.totalScrollRange) {
                    // ????????? ????????? ??? ?????? ??????
                    binding.menuSelectToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    binding.toolbarSuperName.text = mSuperName
                    binding.toolbarBack.setImageResource(R.drawable.ic_left_arrow_black)
                    binding.toolbarShare.setImageResource(R.drawable.ic_share_black)
                    mCollapsingToolbarState = CollapsingToolbarLayoutState.COLLAPSED
                } else if (Math.abs(verticalOffset) < appBarLayout.totalScrollRange) {
                    //Log.d("vertical", "?????? ????????? ?????? ??????")
                    if (mCollapsingToolbarState != CollapsingToolbarLayoutState.INTERNEDIATE) {
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

    // ???????????? ?????? ???????????????
    private fun startChangeCartDialog(menu: CartMenuInfo){
        val cartChangeBinding = DialogCartReplaceBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(cartChangeBinding.root)
        builder.setCancelable(false)

        val alertDialog = builder.create()
        val window = alertDialog.window
        if(window != null){
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            alertDialog.window!!.attributes = params
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog.show()

        cartChangeBinding.dialogCartChangeNo.setOnClickListener {
            alertDialog.dismiss()
        }
        cartChangeBinding.dialogCartChangeReplace.setOnClickListener {
            alertDialog.dismiss()
            mDBHelper.deleteTotal(mDB)
            saveMenuFinish(menu)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition( R.anim.horiaon_exit, R.anim.horizon_enter)
    }

    fun getStoreIdx(): Int = ApplicationClass.sSharedPreferences.getInt("storeIdx", -1)

    // ?????? ????????? ?????? ??????
    fun saveMenuFinish(menu: CartMenuInfo){

        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putInt("storeIdx", mStoreIdx).apply()
        mDBHelper.menuInsert(mDB, menu)
        setResult(RESULT_OK)
        finish()
    }

    fun saveMenuInfo(position: Int, selectMenu: SelectMenu){
        if(mSelectedMenu != null) {
            mSelectedMenu!![position] = selectMenu
            mSelectMenuCheck!![position] = true
        }
    }

    // ?????? ????????? ?????? ?????? ?????? ??????
    fun changeTotalPrice(price: Int) {
        val menuPrice = priceIntToString(binding.menuSelectNum.text.toString().toInt() * mMenuPrice + price) + "???"
        binding.menuSelectMenuPrice.text = menuPrice
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
            val price = "${priceIntToString(response.result.price)}???"
            mMenuPrice = response.result.price
            binding.menuSelectMenuPrice.text = price
            if(response.result.menuOption != null){
                // ????????? ?????? ????????? ??????
                binding.menuSelectRecyclerView.adapter = MenuDetailParentAdapter(response.result.menuOption, this)
                binding.menuSelectRecyclerView.layoutManager = LinearLayoutManager(this)
                mSelectedMenu = Array(response.result.menuOption.size) {i -> SelectMenu("", 0) }
                mSelectMenuCheck = Array(response.result.menuOption.size) {i -> false}
            }

            if(response.result.url != null){
                // ????????? ????????????
                binding.menuSelectImgViewPager.adapter = DetailSuperImgViewPagerAdapter(response.result.url, this, 2)
                binding.menuSelectImgViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

                // ????????? ?????????
                // ViewPager ??? ?????? ?????? ?????????
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
                binding.toolbarSuperName.text = mSuperName
            }
        } else notServerDumyData()
    }

    override fun onGetMenuDetailFailure(message: String) {
        //showCustomToast("?????? ???????????? ??????")
        notServerDumyData()
    }

    fun startLookImageActivity(num: Int, imgString: String){
        val intent = Intent(this, LookImageActivity::class.java).apply {
            putExtra("imgList", imgString)
            putExtra("position", num)
        }
        startActivity(intent)
    }

    fun notServerDumyData(){
        binding.menuSelectToolbarItem.visibility = View.VISIBLE
        binding.menuSelectImgNumParent.visibility = View.VISIBLE
        mMenuPrice = 48000
        // ????????? ?????? ????????? ??????
        val menuOptionList = ArrayList<MenuOption>()
        val option1 = ArrayList<Option>()
        option1.add(Option("??????", 0))
        option1.add(Option("?????? ??????", 3000))
        option1.add(Option("?????? ??????", 2000))
        option1.add(Option("?????? ??????", 1000))
        val option2 = ArrayList<Option>()
        option2.add(Option("?????????", 1000))
        option2.add(Option("?????? ??????", 3000))
        option2.add(Option("?????? ??????", 3000))
        option2.add(Option("?????? ??????", 1000))
        option2.add(Option("??? ??????", 1000))
        option2.add(Option("???????????? ??????", 2000))
        menuOptionList.add(MenuOption("??????", "Y", 1, option1))
        menuOptionList.add(MenuOption("????????? ??????", "N", 999, option2))

        binding.menuSelectRecyclerView.adapter = MenuDetailParentAdapter(menuOptionList, this)
        binding.menuSelectRecyclerView.layoutManager = LinearLayoutManager(this)
        mSelectedMenu = Array(menuOptionList.size) {i -> SelectMenu("", 0) }
        mSelectMenuCheck = Array(menuOptionList.size) {i -> false}

        // ????????? ????????????
        val imageList = ArrayList<String>()
        imageList.add("https://t1.daumcdn.net/cfile/tistory/22084E4B593DF4D714")
        imageList.add("https://dbscthumb-phinf.pstatic.net/2765_000_39/20181007210844284_LW82GFYCC.jpg/247063.jpg?type=m4500_4500_fst&wm=N")
        binding.menuSelectImgViewPager.adapter = DetailSuperImgViewPagerAdapter(imageList, this, 2)
        binding.menuSelectImgViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // ????????? ?????????
        // ViewPager ??? ?????? ?????? ?????????
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