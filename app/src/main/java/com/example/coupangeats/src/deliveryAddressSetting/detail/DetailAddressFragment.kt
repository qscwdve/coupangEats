package com.example.coupangeats.src.deliveryAddressSetting.detail

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.example.coupangeats.R
import com.example.coupangeats.databinding.DialogCategoryChangeBinding
import com.example.coupangeats.databinding.DialogDetailAddressDeleteBinding
import com.example.coupangeats.databinding.DialogLogoutBinding
import com.example.coupangeats.databinding.FragmentDeliveryDetailAddressBinding
import com.example.coupangeats.src.deliveryAddressSetting.DeliveryAddressSettingActivity
import com.example.coupangeats.src.deliveryAddressSetting.DeliveryAddressSettingService
import com.example.coupangeats.src.deliveryAddressSetting.detail.model.*
import com.example.coupangeats.src.deliveryAddressSetting.model.DeliveryAddressAddRequest
import com.example.coupangeats.src.map.MapActivity
import com.example.coupangeats.util.DetailAddressBottomSheetDialog
import com.example.coupangeats.util.GpsControl
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment

class DetailAddressFragment : BaseFragment<FragmentDeliveryDetailAddressBinding>(FragmentDeliveryDetailAddressBinding::bind, R.layout.fragment_delivery_detail_address), DetailAddressFragmentView{
    private var mMainAddress = ""
    private var mRoadAddress = ""
    private var mCategory = -1
    private var version = 1  // 1이면 gps 선택, 2이면 배달지 주소 관리 - 배달지 추가! 3 이면 배달지 수정
    private var addressIdx = -1
    private var mModifyOrDelete = false
    private var mLat = (-1).toDouble()
    private var mLon = (-1).toDouble()
    private val MAP_ACTIVITY = 1234
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        version = arguments?.getInt("version", 1) ?: 1
        if(version == 3){
            addressIdx = arguments?.getInt("addressIdx", -1) ?: -1
            if(addressIdx != -1){
                // 서버통신 밑 삭제하기 버튼 보이기
                binding.detailAddressDelete.visibility = View.VISIBLE
                // 서버통신 시작
                showLoadingDialog(requireContext())
                DetailAddressService(this).tryDeliveryAddressDetailLook(getUserIdx(), addressIdx)
            }
        }
        mMainAddress = arguments?.getString("mainAddress").toString()
        mRoadAddress = arguments?.getString("roadAddress").toString()
        val category = arguments?.getInt("category", -1)

        if (category != null) {
            categoryCustom(category, mCategory)
        }
        if(mMainAddress != "" && mRoadAddress != "" ){
            binding.detailAddressMainAddress.text = mMainAddress
            binding.detailAddressRoadAddress.text = mRoadAddress
        }

        // 상세주소 커스텀
        binding.detailAddressDetailText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                // 편집하고 있을 때 검은색으로 바뀜
                binding.detailAddressLine.visibility = View.VISIBLE
                binding.detailAddressText.setTextColor(Color.parseColor("#000000"))
            } else {
                // 원래 색깔로
                binding.detailAddressLine.visibility = View.INVISIBLE
                binding.detailAddressText.setTextColor(Color.parseColor("#949DA6"))   // baseColor
            }
        }
        binding.detailAddressDetailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // cancel 표시 / 비표시
                if(binding.detailAddressDetailText.text.toString().isNotEmpty()){
                    binding.detailAddressTextCancel.visibility = View.VISIBLE
                } else {
                    binding.detailAddressTextCancel.visibility = View.INVISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.detailAddressTextCancel.setOnClickListener {
            binding.detailAddressDetailText.setText("")
            binding.detailAddressTextCancel.visibility = View.INVISIBLE
        }
        // 별칭 주소 커스텀
        binding.detailAddressAliasEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                // 편집 중 검은색
                binding.detailAddressAliasText.setTextColor(Color.parseColor("#000000"))
                binding.detailAddressAliasLine.visibility = View.VISIBLE
            } else {
                // 원래대로
                binding.detailAddressAliasText.setTextColor(Color.parseColor("#949DA6"))  // baseColor
                binding.detailAddressAliasLine.visibility = View.INVISIBLE
            }
        }
        binding.detailAddressAliasEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.detailAddressAliasEditText.text.toString().isNotEmpty()){
                    binding.detailAddressAliaSTextCancel.visibility = View.VISIBLE
                } else {
                    binding.detailAddressAliaSTextCancel.visibility = View.INVISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?){}
        })
        binding.detailAddressAliaSTextCancel.setOnClickListener {
            binding.detailAddressAliasEditText.setText("")
            binding.detailAddressAliaSTextCancel.visibility = View.INVISIBLE
        }
        binding.detailAddressAliasLine.visibility = View.GONE
        // 카테고리 선택 커스텀  mCategory = 1 이면 집 , 2면 회사 3이면 기타
        binding.detailAddressCategoryHome.setOnClickListener {
            if(mCategory != 1){
                categoryCustom(1, mCategory)
                binding.detailAddressAliasParent.visibility = View.GONE
                binding.detailAddressAliasEditText.setText("")
                binding.detailAddressAliasLine.visibility = View.GONE
                binding.detailAddressAliasTextLine.visibility = View.GONE
            }
        }
        binding.detailAddressCategoryCompany.setOnClickListener {
            if(mCategory != 2){
                categoryCustom(2, mCategory)
                binding.detailAddressAliasParent.visibility = View.GONE
                binding.detailAddressAliasEditText.setText("")
                binding.detailAddressAliasLine.visibility = View.GONE
                binding.detailAddressAliasTextLine.visibility = View.GONE
            }
        }
        binding.detailAddressCategoryGps.setOnClickListener {
            if(mCategory != 3){
                categoryCustom(3, mCategory)
                binding.detailAddressAliasTextLine.visibility = View.VISIBLE
                binding.detailAddressAliasParent.visibility = View.VISIBLE
                binding.detailAddressAliasLine.visibility = View.VISIBLE
            }
        }

        // 지도..
        binding.detailAddressMap.setOnClickListener {
            // 배달 경도, 위도 넘겨줘야 한다.
            val intent = Intent(requireContext(), MapActivity::class.java).apply {
                this.putExtra("lat", mLat)
                this.putExtra("lon", mLon)
                this.putExtra("version", 1)
            }
            startActivity(intent)
        }

        // 배달지 주소 삭제
        binding.detailAddressDelete.setOnClickListener {
            mModifyOrDelete = false
            showDialogDetailAddressDelete()
        }

        // 완료버튼
        binding.detailAddressComplete.setOnClickListener {
            mModifyOrDelete = true
            // 상세주소 입력했는지 체크
            if(binding.detailAddressDetailText.text.toString().isNotEmpty()){
                // 상세주소 입력 완료
                checkHomeOrCompany()
            } else {
                // 상세주소 입력 바라는 다이얼로그 뿌림
                val detailAddressBottomSheetDialog = DetailAddressBottomSheetDialog(this)
                detailAddressBottomSheetDialog.show(requireFragmentManager(), "detailAddress")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 배달지 변화 있나 봄
        val mainAddress = ApplicationClass.sSharedPreferences.getString("mainAddress", "") ?: ""
        val roadAddress = ApplicationClass.sSharedPreferences.getString("roadAddress", "") ?: ""
        if(mainAddress != "" && roadAddress != ""){

            binding.detailAddressMainAddress.text = mainAddress
            binding.detailAddressRoadAddress.text = roadAddress
            mMainAddress = mainAddress
            mRoadAddress = roadAddress

            val edit = ApplicationClass.sSharedPreferences.edit()
            edit.putString("mainAddress", null)
            edit.putString("roadAddress", null)
            edit.apply()
        }
    }

    fun checkHomeOrCompany() {
        // 유저가 집과 회사를 체크했는지 확인 (서버통신)
        if(mCategory == 1) DetailAddressService(this).tryGetHomeOrCompanyChecked(getUserIdx(), "HOME")
        else if(mCategory == 2) DetailAddressService(this).tryGetHomeOrCompanyChecked(getUserIdx(), "COMPANY")
        else sendDataServer("ETC")
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    fun sendDataServer(aliasType: String) {
        if(version == 3) {
            // 배달지 수정
            if(mModifyOrDelete){
                // modify
                val address = binding.detailAddressMainAddress.text.toString()
                val roadAddress = binding.detailAddressRoadAddress.text.toString()
                val detailAddress = if(binding.detailAddressDetailText.text.toString().isNotEmpty()) binding.detailAddressDetailText.text.toString() else null
                val alias = if(binding.detailAddressAliasEditText.text.toString() == "") null else binding.detailAddressAliasEditText.text.toString()
                val deliveryAddressModifyRequest = DeliveryAddressModifyRequest(address, roadAddress, detailAddress, aliasType, alias)
                DetailAddressService(this).tryDeliveryAddressModify(getUserIdx(), addressIdx, deliveryAddressModifyRequest)
            } else {
                // delete
                DetailAddressService(this).tryDeliveryAddressDelete(getUserIdx(),addressIdx)
            }
        } else {
            val detailAddress = if(binding.detailAddressDetailText.text.toString().isNotEmpty()) binding.detailAddressDetailText.text.toString() else null
            val alias = if(binding.detailAddressAliasEditText.text.toString() == "") null else binding.detailAddressAliasEditText.text.toString()
            // if(aliasType == "HOME") mMainAddress = "집" else if(aliasType == "COMPANY") mMainAddress = "회사"
            val mainAddress = binding.detailAddressMainAddress.text.toString()
            val roadAddress = binding.detailAddressRoadAddress.text.toString()
            val deliveryAddressAddRequest = DeliveryAddressAddRequest(mainAddress, roadAddress, detailAddress, aliasType, alias, getUserIdx())
            (activity as DeliveryAddressSettingActivity).startDeliveryAdd(deliveryAddressAddRequest)
        }

    }
    private fun showDialogDetailAddressDelete() {
        val detailAddressDeleteBinding = DialogDetailAddressDeleteBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(detailAddressDeleteBinding.root)
        builder.setCancelable(false)

        val alertDialog = builder.create()
        // 너비 꽉차게
        val window = alertDialog.window
        if(window != null){
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            alertDialog.window!!.attributes = params
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        alertDialog.show()

        detailAddressDeleteBinding.dialogDetailAddressDeleteNo.setOnClickListener {alertDialog.dismiss()}
        detailAddressDeleteBinding.dialogDetailAddressDeleteYes.setOnClickListener {
            // 삭제함
            alertDialog.dismiss()
            DetailAddressService(this).tryDeliveryAddressDelete(getUserIdx(),addressIdx)
        }
    }
    private fun showDialogHomeOrCompanyReplace() {
        val homeOrCompany = if(mCategory == 1) "집" else "회사"
        val categoryChangeBinding = DialogCategoryChangeBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        categoryChangeBinding.dialogCategoryText.text =
            if(mCategory == 1) "기존 등록된 '집'주소를 대체하시겠습니까?"
            else "기존 등록된 '회사'주소를 대체하시겠습니까?"

        builder.setView(categoryChangeBinding.root)
        builder.setCancelable(false)

        val alertDialog = builder.create()
        // 너비 꽉차게
        val window = alertDialog.window
        if(window != null){
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            alertDialog.window!!.attributes = params
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        alertDialog.show()

        categoryChangeBinding.dialogCategoryNo.setOnClickListener {
            alertDialog.dismiss()
        }
        categoryChangeBinding.dialogCategoryReplace.setOnClickListener {
            alertDialog.dismiss()
            // 서버통신 가능 하지만 집으로 카테고리를 추가한 다음 , checked도 변환한 다음 HomeFragment로 돌아갈 수 있음...
            val category = if(mCategory == 1) "HOME" else "COMPANY"
            sendDataServer(category)
        }
    }

    fun categoryCustom(newClick: Int, nowClick : Int){
        // 선택함 -> 파란색으로 배경 변해야 함
        when(nowClick){
            1 -> {
                binding.detailAddressCategoryHome.setBackgroundResource(R.drawable.login_box)
                binding.detailAddressHomeText.setTextColor(Color.parseColor("#000000"))  // black
                binding.detailAddressHomeImg.setImageResource(R.drawable.ic_delivery_home)
            }
            2 -> {
                binding.detailAddressCategoryCompany.setBackgroundResource(R.drawable.login_box)
                binding.detailAddressCompanyText.setTextColor(Color.parseColor("#000000"))  // black
                binding.detailAddressCompanyImg.setImageResource(R.drawable.ic_delivery_briefcase)
            }
            3 -> {
                binding.detailAddressCategoryGps.setBackgroundResource(R.drawable.login_box)
                binding.detailAddressGpsText.setTextColor(Color.parseColor("#000000"))  // black
                binding.detailAddressGpsImg.setImageResource(R.drawable.ic_delivery_gps)
            }
        }
        when(newClick){
            1 -> {
                binding.detailAddressCategoryHome.setBackgroundResource(R.drawable.detail_address_category_box)
                binding.detailAddressHomeText.setTextColor(Color.parseColor("#00AFFE"))  // blue
                binding.detailAddressHomeImg.setImageResource(R.drawable.ic_delivery_blue_home)
            }
            2 -> {
                binding.detailAddressCategoryCompany.setBackgroundResource(R.drawable.detail_address_category_box)
                binding.detailAddressCompanyText.setTextColor(Color.parseColor("#00AFFE"))  // blue
                binding.detailAddressCompanyImg.setImageResource(R.drawable.ic_delivery_blue_briefcase)
            }
            3 -> {
                binding.detailAddressCategoryGps.setBackgroundResource(R.drawable.detail_address_category_box)
                binding.detailAddressGpsText.setTextColor(Color.parseColor("#00AFFE"))  // blue
                binding.detailAddressGpsImg.setImageResource(R.drawable.ic_delivery_blue_gps)
            }
        }
        mCategory = newClick
    }

    override fun onGetHomeOrCompanyCheckedSuccess(response: HomeOrCompanyCheckResponse) {
        if(response.code == 1000){
            if(response.result.exist == "Y"){
                // 존재한다 그러므로 대체하냐고 물어봐야 한다.
                showDialogHomeOrCompanyReplace()
            } else {
                // 배달 추가 가능!!
                val category = if(mCategory == 1) "HOME" else "COMPANY"
                sendDataServer(category)
            }
        }
    }

    override fun onGetHomeOrCompanyCheckedFailure(message: String) {
        showCustomToast("집인지 회사인지 체크 실패")
    }

    override fun onDeliveryAddressDetailLookSuccess(response: DeliveryAddressDetailResponse) {
        dismissLoadingDialog()
        if(response.code == 1000){
            binding.detailAddressMainAddress.text = response.result.address
            binding.detailAddressRoadAddress.text = response.result.roadAddress
            binding.detailAddressDetailText.setText(response.result.detailAddress ?: "")
            val aliasType = if(response.result.aliasType == "HOME") 1
                            else if(response.result.aliasType == "COMPANY") 2
                            else 3
            categoryCustom(aliasType, -1)
            if(aliasType == 3){
                binding.detailAddressAliasParent.visibility = View.VISIBLE
                binding.detailAddressAliasEditText.setText(response.result.alias ?: "")
            }
            mLat = response.result.latitude.toDouble()
            mLon = response.result.longitude.toDouble()
        }
    }

    fun returnDeliveryAddressManageHome() {
        (activity as DeliveryAddressSettingActivity).backClick(addressIdx)
    }

    override fun onDeliveryAddressDetailLookFailure(message: String) {
        dismissLoadingDialog()
    }

    override fun onDeliveryAddressDeleteSuccess(response: DeliveryAddressDeleteResponse) {
        if(response.code == 1000){
            returnDeliveryAddressManageHome()
        }
    }

    override fun onDeliveryAddressDeleteFailure(message: String) {
        showCustomToast("배달지 주소 삭제에 실패하였습니다.")
    }

    override fun onDeliveryAddressModifySuccess(response: DeliveryAddressModifyResponse) {
        if(response.code == 1000){
            returnDeliveryAddressManageHome()
        }
    }

    override fun onDeliveryAddressModifyFailure(message: String) {
        showCustomToast("배달지 주소 수정에 실패하였습니다.")
    }

    override fun onPause() {
        binding.detailAddressAliasEditText.clearFocus()
        binding.detailAddressDetailText.clearFocus()
        super.onPause()
    }
}