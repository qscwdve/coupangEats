package com.example.coupangeats.src.deliveryAddressSetting.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.input.InputManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.coupangeats.R
import com.example.coupangeats.databinding.DialogCategoryChangeBinding
import com.example.coupangeats.databinding.DialogDetailAddressDeleteBinding
import com.example.coupangeats.databinding.DialogLogoutBinding
import com.example.coupangeats.databinding.FragmentDeliveryDetailAddressBinding
import com.example.coupangeats.src.deliveryAddressSetting.DeliveryAddressSettingActivity
import com.example.coupangeats.src.deliveryAddressSetting.DeliveryAddressSettingService
import com.example.coupangeats.src.deliveryAddressSetting.adapter.data.SearchAddress
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
    private var version = 1  // 1?????? gps ??????, 2?????? ????????? ?????? ?????? - ????????? ??????! 3 ?????? ????????? ??????
    private var addressIdx = -1
    private var mModifyOrDelete = false
    private var mLat = ""
    private var mLon = ""
    private lateinit var imm : InputMethodManager
    private var mEditFocus = -1
    lateinit var mapActivityLauncher: ActivityResultLauncher<Intent>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if (data != null) {
                    // ????????? ?????? ??????
                    mMainAddress = data.getStringExtra("mainAddress") ?: ""
                    mRoadAddress = data.getStringExtra("roadAddress") ?: ""
                    mLat = data.getStringExtra("lat") ?: ""
                    mLon = data.getStringExtra("lon") ?: ""
                    binding.detailAddressMainAddress.text = mMainAddress
                    binding.detailAddressRoadAddress.text = mRoadAddress
                }
            }

        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        version = arguments?.getInt("version", 1) ?: 1
        mLat = arguments?.getString("lat") ?: ""
        mLon = arguments?.getString("lon") ?: ""

        if(version == 3){
            addressIdx = arguments?.getInt("addressIdx", -1) ?: -1
            if(addressIdx != -1){
                // ???????????? ??? ???????????? ?????? ?????????
                binding.detailAddressDelete.visibility = View.VISIBLE
                // ???????????? ??????
                showLoadingDialog(requireContext())
                DetailAddressService(this).tryDeliveryAddressDetailLook(getUserIdx(), addressIdx)
            }
        } else{
            binding.detailAddressDelete.visibility = View.GONE
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

        // ???????????? ?????????
        binding.detailAddressDetailText.setOnFocusChangeListener { v, hasFocus ->
            mEditFocus = if(hasFocus){ 1 } else { -1 }
        }
        binding.detailAddressDetailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // cancel ?????? / ?????????
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
        // ?????? ?????? ?????????
        binding.detailAddressAliasEditText.setOnFocusChangeListener { v, hasFocus ->
            mEditFocus = if(hasFocus){ 2 } else { -1 }
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

        // ???????????? ?????? ?????????  mCategory = 1 ?????? ??? , 2??? ?????? 3?????? ??????
        binding.detailAddressCategoryHome.setOnClickListener {
            if(mCategory != 1){
                categoryCustom(1, mCategory)
                binding.detailAddressAliasParent.visibility = View.GONE
                binding.detailAddressAliasEditText.setText("")
                binding.detailAddressAliasEditLine.visibility = View.GONE
            }
        }
        binding.detailAddressCategoryCompany.setOnClickListener {
            if(mCategory != 2){
                categoryCustom(2, mCategory)
                binding.detailAddressAliasParent.visibility = View.GONE
                binding.detailAddressAliasEditText.setText("")
                binding.detailAddressAliasEditLine.visibility = View.GONE
            }
        }
        binding.detailAddressCategoryGps.setOnClickListener {
            if(mCategory != 3){
                categoryCustom(3, mCategory)
                binding.detailAddressAliasParent.visibility = View.VISIBLE
                binding.detailAddressAliasEditLine.visibility = View.VISIBLE
            }
        }

        // ??????..
        binding.detailAddressMap.setOnClickListener {
            // ?????? ??????, ?????? ???????????? ??????.
            val intent = Intent(requireContext(), MapActivity::class.java).apply {
                this.putExtra("lat", mLat)
                this.putExtra("lon", mLon)
                this.putExtra("version", 1)
                this.putExtra("detailAddressVersion", version)
                this.putExtra("mainAddress", binding.detailAddressMainAddress.text.toString())
            }
            mapActivityLauncher.launch(intent)
        }

        // ????????? ?????? ??????
        binding.detailAddressDelete.setOnClickListener {
            mModifyOrDelete = false
            showDialogDetailAddressDelete()
        }

        // ????????????
        binding.detailAddressComplete.setOnClickListener {
            mModifyOrDelete = true
            // ???????????? ??????????????? ??????
            if(binding.detailAddressDetailText.text.toString().isNotEmpty()){
                // ???????????? ?????? ??????
                checkHomeOrCompany()
            } else {
                // ???????????? ?????? ????????? ??????????????? ??????
                val detailAddressBottomSheetDialog = DetailAddressBottomSheetDialog(this)
                detailAddressBottomSheetDialog.show(parentFragmentManager, "detailAddress")
            }
        }
    }

    override fun onDestroy() {
        if(mEditFocus == 1){
            imm.hideSoftInputFromWindow(binding.detailAddressDetailText.windowToken, 0)
        } else if(mEditFocus == 2){
            imm.hideSoftInputFromWindow(binding.detailAddressAliasEditText.windowToken, 0)
        }
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        // ????????? ?????? ?????? ???
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
        // ????????? ?????? ????????? ??????????????? ?????? (????????????)
        when(mCategory){
            1 -> DetailAddressService(this).tryGetHomeOrCompanyChecked(getUserIdx(), "HOME")
            2 -> DetailAddressService(this).tryGetHomeOrCompanyChecked(getUserIdx(), "COMPANY")
            else -> sendDataServer("ETC")
        }
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    private fun sendDataServer(aliasType: String) {
        if(version == 3) {
            // ????????? ??????
            if(mModifyOrDelete){
                // modify
                val address = binding.detailAddressMainAddress.text.toString()
                val roadAddress = binding.detailAddressRoadAddress.text.toString()
                val detailAddress = if(binding.detailAddressDetailText.text.toString().isNotEmpty()) binding.detailAddressDetailText.text.toString() else null
                val alias = if(binding.detailAddressAliasEditText.text.toString() == "") null else binding.detailAddressAliasEditText.text.toString()
                val deliveryAddressModifyRequest = DeliveryAddressModifyRequest(address, roadAddress, detailAddress, aliasType, alias, setAddressString(mLat), setAddressString(mLon))
                DetailAddressService(this).tryDeliveryAddressModify(getUserIdx(), addressIdx, deliveryAddressModifyRequest)
            } else {
                // delete
                DetailAddressService(this).tryDeliveryAddressDelete(getUserIdx(),addressIdx)
            }
        } else {
            val detailAddress = if(binding.detailAddressDetailText.text.toString().isNotEmpty()) binding.detailAddressDetailText.text.toString() else null
            val alias = if(binding.detailAddressAliasEditText.text.toString() == "") null else binding.detailAddressAliasEditText.text.toString()
            // if(aliasType == "HOME") mMainAddress = "???" else if(aliasType == "COMPANY") mMainAddress = "??????"
            val mainAddress = binding.detailAddressMainAddress.text.toString()
            val roadAddress = binding.detailAddressRoadAddress.text.toString()
            val deliveryAddressAddRequest = DeliveryAddressAddRequest(mainAddress, roadAddress, detailAddress, aliasType, alias, getUserIdx(), setAddressString(mLat), setAddressString(mLon))
            (activity as DeliveryAddressSettingActivity).startDeliveryAdd(deliveryAddressAddRequest)
        }

    }
    private fun setAddressString(address: String) : String {
        var str = ""
        var count = 0
        var flag = false
        for(element in address){
            str += element
            if(element == '.') flag = true
            if(flag){
                count++
                if(count > 10) break
            }
        }
        return str
    }
    private fun showDialogDetailAddressDelete() {
        val detailAddressDeleteBinding = DialogDetailAddressDeleteBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(detailAddressDeleteBinding.root)
        builder.setCancelable(false)

        val alertDialog = builder.create()
        // ?????? ?????????
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
            // ?????????
            alertDialog.dismiss()
            DetailAddressService(this).tryDeliveryAddressDelete(getUserIdx(),addressIdx)
        }
    }
    private fun showDialogHomeOrCompanyReplace() {
        val homeOrCompany = if(mCategory == 1) "?????? ????????? '???'????????? ?????????????????????????"
                            else "?????? ????????? '??????'????????? ?????????????????????????"
        val categoryChangeBinding = DialogCategoryChangeBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        categoryChangeBinding.dialogCategoryChangeText.text = homeOrCompany
        builder.setView(categoryChangeBinding.root)
        builder.setCancelable(false)

        val alertDialog = builder.create()
        // ?????? ?????????
        val window = alertDialog.window
        if(window != null){
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            alertDialog.window!!.attributes = params
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        alertDialog.show()

        categoryChangeBinding.dialogCategoryChangeNo.setOnClickListener {
            alertDialog.dismiss()
        }
        categoryChangeBinding.dialogCategoryChangeReplace.setOnClickListener {
            alertDialog.dismiss()
            // ???????????? ?????? ????????? ????????? ??????????????? ????????? ?????? , checked??? ????????? ?????? HomeFragment??? ????????? ??? ??????...
            val category = if(mCategory == 1) "HOME" else "COMPANY"
            sendDataServer(category)
        }
    }

    private fun categoryCustom(newClick: Int, nowClick : Int){
        // ????????? -> ??????????????? ?????? ????????? ???
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
                // ???????????? ???????????? ??????????????? ???????????? ??????.
                showDialogHomeOrCompanyReplace()
            } else {
                // ?????? ?????? ??????!!
                val category = if(mCategory == 1) "HOME" else "COMPANY"
                sendDataServer(category)
            }
        }
    }

    override fun onGetHomeOrCompanyCheckedFailure(message: String) {
        showCustomToast("????????? ???????????? ?????? ??????")
    }

    override fun onDeliveryAddressDetailLookSuccess(response: DeliveryAddressDetailResponse) {
        dismissLoadingDialog()
        if(response.code == 1000){
            binding.detailAddressMainAddress.text = response.result.address
            binding.detailAddressRoadAddress.text = response.result.roadAddress
            binding.detailAddressDetailText.setText(response.result.detailAddress ?: "")
            val aliasType = when(response.result.aliasType){
                            "HOME" -> 1
                            "COMPANY" -> 2
                            else -> 3 }
            categoryCustom(aliasType, -1)
            if(aliasType == 3){
                binding.detailAddressAliasParent.visibility = View.VISIBLE
                binding.detailAddressAliasEditLine.visibility = View.VISIBLE
                binding.detailAddressAliasEditText.setText(response.result.alias ?: "")
            }
            mLat = response.result.latitude
            mLon = response.result.longitude
        }
    }

    private fun returnDeliveryAddressManageHome() {
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
        showCustomToast("????????? ?????? ????????? ?????????????????????.")
    }

    override fun onDeliveryAddressModifySuccess(response: DeliveryAddressModifyResponse) {
        if(response.code == 1000){
            returnDeliveryAddressManageHome()
        }
    }

    override fun onDeliveryAddressModifyFailure(message: String) {
        showCustomToast("????????? ?????? ????????? ?????????????????????.")
    }

    override fun onPause() {
        binding.detailAddressAliasEditText.clearFocus()
        binding.detailAddressDetailText.clearFocus()
        super.onPause()
    }
}