package com.example.coupangeats.src.menuSelect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.coupangeats.databinding.ActivityMenuSelectBinding
import com.example.coupangeats.src.detailSuper.adapter.DetailSuperImgViewPagerAdapter
import com.example.coupangeats.src.menuSelect.adapter.MenuDetailParentAdapter
import com.example.coupangeats.src.menuSelect.model.MenuDetailResponse
import com.example.coupangeats.src.menuSelect.model.SelectMenu
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class MenuSelectActivity : BaseActivity<ActivityMenuSelectBinding>(ActivityMenuSelectBinding::inflate), MenuSelectActivityView {
    private var menuIdx = -1
    private var mStoreIdx = -1
    private var mMenuPrice = 0
    private var mSelectedMenu : Array<SelectMenu>? = null
    private var mSelectMenuCheck : Array<Boolean>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuIdx = intent.getIntExtra("menuIdx", -1)
        mStoreIdx = intent.getIntExtra("storeIdx", -1)

        // 메뉴 받아오는 통신
        MenuSelectService(this).tryGetMenuDetail(mStoreIdx, menuIdx)

        // 수량 제어
        binding.menuSelectNumPlus.setOnClickListener {
            val num = binding.menuSelectNum.text.toString().toInt() + 1
            binding.menuSelectNum.text = num.toString()
            val menuPrice = (mMenuPrice * num).toString() + "원"
            binding.menuSelectMenuPrice.text = menuPrice
        }
        binding.menuSelectNumMinus.setOnClickListener {
            val num = binding.menuSelectNum.text.toString().toInt() - 1
            if(num >= 1){
                binding.menuSelectNum.text = num.toString()
                val menuPrice = (mMenuPrice * num).toString() + "원"
                binding.menuSelectMenuPrice.text = menuPrice
            }
        }
        // 카트에 담기!!
        binding.menuSelectComplete.setOnClickListener {
            // 카트에 담는다.
            // 메인 메뉴
            var content = binding.menuSelectMenuName.text.toString()
            var totalPrice =  binding.menuSelectNum.text.toString().toInt() * mMenuPrice
            // 사이드 메뉴
            if(mSelectMenuCheck != null){
                for(index in mSelectMenuCheck!!.indices){
                    if(mSelectMenuCheck!![index]){
                        // 사이드 메뉴 있음
                        content += ", " + mSelectedMenu!![index].cotent
                        totalPrice += mSelectedMenu!![index].price
                    }
                }
            }
            Log.d("MenuSelectSide", "conent : ${content} ")
            Log.d("MenuSelectSide", "price : ${totalPrice} ")
            // 메뉴 저장
            saveMenuFinish(content, totalPrice)
            setResult(RESULT_OK)
            finish()
        }
    }

    // 메뉴 내용과 가격 저장
    fun saveMenuFinish(content: String, price: Int){
        val menuNum = ApplicationClass.sSharedPreferences.getInt("menuNum", 0)
        val edit = ApplicationClass.sSharedPreferences.edit()
        val menu1Content = "meun${menuNum + 1}Content"
        val menu1Price = "menu${menuNum + 1}Price"
        edit.putString(menu1Content, content)
        edit.putInt(menu1Price, price)
        edit.putInt("menuNum", menuNum + 1)
        edit.apply()
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
            val price = "${response.result.price}원"
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
        showCustomToast("메뉴 불러오기 실패")
    }
}