package com.example.coupangeats.src.SuperInfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivitySuperInfoBinding
import com.example.coupangeats.src.SuperInfo.model.SuperInfoResponse
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.softsquared.template.kotlin.config.BaseActivity

class SuperInfoActivity : BaseActivity<ActivitySuperInfoBinding>(ActivitySuperInfoBinding::inflate),
    OnMapReadyCallback, SuperInfoActivityView {

    private var mStoreIdx = -1
    lateinit var uiSettings : UiSettings
    lateinit var mNaverMap : NaverMap
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = binding.superInfoMap
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        mStoreIdx = intent.getIntExtra("storeIdx", -1)

        binding.superInfoBack.setOnClickListener { finish()}
    }

    @SuppressLint("RtlHardcoded")
    override fun onMapReady(p0: NaverMap) {
        mNaverMap = p0

        uiSettings = mNaverMap.uiSettings
        uiSettings.isTiltGesturesEnabled = false
        uiSettings.isCompassEnabled = false
        uiSettings.isScaleBarEnabled = false
        uiSettings.isZoomControlEnabled = false
        uiSettings.isIndoorLevelPickerEnabled = false
        uiSettings.isLocationButtonEnabled = false
        uiSettings.logoGravity = (Gravity.END and Gravity.TOP)
        // 매장 정보 조회
        SuperInfoService(this).tryGetSuperInfo(mStoreIdx)

    }

    override fun onGetSuperInfoSuccess(response: SuperInfoResponse) {
        if(response.code == 1000){
            val superInfoResponse = response.result
            binding.superInfoSuperName.text = superInfoResponse.storeName

            val phone = "전화번호: ${superInfoResponse.phone}"
            val address = "주소: ${superInfoResponse.address}"
            val ceoName = "대표자명: ${superInfoResponse.ceoName}"
            val businessNum = "사업자등록번호: ${superInfoResponse.businessNumber}"
            val companyName = "상호명: ${superInfoResponse.companyName}"

            binding.superInfoPhone.text = phone
            binding.superInfoSuperAddress.text = address
            binding.superInfoSuperCeoName.text = ceoName
            binding.superInfoSuperBusinessNumber.text = businessNum
            binding.superInfoSuperCompanyName.text = companyName

            binding.superInfoSuperOpenTime.text = superInfoResponse.businessHours
            binding.superInfoSuperIntroduction.text = superInfoResponse.introduction

            if(superInfoResponse.notice != null){
                binding.superInfoSuperNoticeCategory.visibility = View.VISIBLE
                binding.superInfoSuperNotice.visibility = View.VISIBLE
                binding.superInfoSuperNotice.text = superInfoResponse.notice
            } else {
                binding.superInfoSuperNoticeCategory.visibility = View.GONE
                binding.superInfoSuperNotice.visibility = View.GONE
            }

            if(superInfoResponse.countryOfOrigin != null){
                binding.superInfoSuperCountryOfOriginCategory.visibility = View.VISIBLE
                binding.superInfoSuperCountryOfOrigin.visibility = View.VISIBLE
                binding.superInfoSuperCountryOfOrigin.text = superInfoResponse.countryOfOrigin
            } else {
                binding.superInfoSuperCountryOfOriginCategory.visibility = View.GONE
                binding.superInfoSuperCountryOfOrigin.visibility = View.GONE
            }

            // 가게 위치 표시 및 지도 조정
            val marker = Marker(LatLng(superInfoResponse.latitude.toDouble(), superInfoResponse.longitude.toDouble()))
            marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_super)
            marker.width = 70
            marker.height = 70
            marker.map = mNaverMap
            //위치 및 각도 조정
            //위치 및 각도 조정
            val cameraPosition = CameraPosition(
                LatLng(superInfoResponse.latitude.toDouble(),superInfoResponse.longitude.toDouble()),  // 위치 지정
                16.0,  // 줌 레벨
                0.0,  // 기울임 각도
                0.0 // 방향
            )
            mNaverMap.cameraPosition = cameraPosition
        }
    }

    override fun onGetSuperInfoFailure(message: String) {

    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}