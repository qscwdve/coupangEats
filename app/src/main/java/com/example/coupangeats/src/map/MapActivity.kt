package com.example.coupangeats.src.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityMapBinding
import com.example.coupangeats.src.deliveryAddressSetting.model.DeliveryAddressAddRequest
import com.example.coupangeats.src.deliveryAddressSetting.model.SearchAddrList.DeliveryAddressResponse
import com.example.coupangeats.src.deliveryAddressSetting.model.UserCheckedAddressResponse
import com.example.coupangeats.util.GpsControl
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity


class MapActivity : BaseActivity<ActivityMapBinding>(ActivityMapBinding::inflate), OnMapReadyCallback, MapActivityView{
    lateinit var uiSettings : UiSettings
    lateinit var mNaverMap : NaverMap
    private lateinit var mapView: MapView
    private lateinit var locationSource: FusedLocationSource
    private var mLat = (-1).toDouble()
    private var mLon = (-1).toDouble()
    private var firstFlag = false
    private var tvHeight = 0
    private var tvWidth = 0
    private var flag = false
    private var version = 0   // 0 이면 선택 1이면 수정
    private lateinit var gpsControl : GpsControl
    private val PERMISSION_REQUEST_CODE = 100
    @SuppressLint("ClickableViewAccessibility", "Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView = findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)

        mLat = intent.getDoubleExtra("lat", (-1).toDouble())
        mLon = intent.getDoubleExtra("lon", (-1).toDouble())
        val n = intent.getIntExtra("version", -1) ?: -1
        if(n != -1){
            version = 1
            val text = "입력하신 배달지 위치를 확인해주세요"
            binding.mapInfo.text = text
        }
        Log.d("position", "mLat : ${mLat}")
        Log.d("position", "mLon : ${mLon}")
        // 대평초등학교 기중
        //mLat = 37.29755480607947
        //mLon = 126.9910868976806
        locationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE)
        gpsControl = GpsControl(this)

        mapView.getMapAsync(this)

        binding.map.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                Log.d("click", "손 누름")
                if(!firstFlag){
                    binding.mapInfo.visibility = View.GONE
                    firstFlag = true
                }
                binding.gps.setImageResource(R.drawable.ic_map_person_ing)
                binding.mapAddress.visibility = View.INVISIBLE
                binding.mapMainAddress.visibility = View.INVISIBLE
                binding.mapPositionSetting.visibility = View.VISIBLE
                binding.mapDeliveryBtn.setBackgroundResource(R.drawable.map_address_ing_box)
            } else if(event.action == MotionEvent.ACTION_UP){
                Log.d("click", "손 놓ㅇㅁ")
                setM(binding.gps)
                flag = true

                val downTime = SystemClock.uptimeMillis()
                val eventTime = SystemClock.uptimeMillis() + 2000
                val aevent =
                    MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, 100f, 100f, 0)
                binding.map.dispatchTouchEvent(aevent)
            }
            false
        }

        // 뒤로가기
        binding.mapBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        // 배달주소로 정하기 서버와 통신하고 난 후 선택도 해야함
        binding.mapDeliveryBtn.setOnClickListener {
            if(version != 1){
                // 선택 -> 배달 추가 후 선택하 다음 종료
                val address = binding.mapMainAddress.text.toString()
                val roadAddress = binding.mapAddress.text.toString()
                val request = DeliveryAddressAddRequest(address, roadAddress, null, "ETC", null, getUserIdx())
                // 배달 -> 추가
                showLoadingDialog(this)
                MapService(this).tryPostDeliveryAddressAdd(request)
            } else {
                // 수정
                val edit = ApplicationClass.sSharedPreferences.edit()
                edit.putString("mainAddress", binding.mapMainAddress.text.toString())
                edit.putString("roadAddress", binding.mapAddress.text.toString())
                edit.apply()
                finish()
            }
        }

    }
    fun setM(view: View){
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                tvWidth = view.width // 이미지뷰 width
                tvHeight = view.height // 이미지뷰 height
                // 다 쓰고 리스너 삭제
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        Log.d("image", "width : $tvWidth   ,  height : $tvHeight")
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        setM(binding.gps)
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

    override fun onMapReady(p0: NaverMap) {
        mNaverMap = p0

        // 현재 위치 마크 표시
        uiSettings = mNaverMap.uiSettings

        if(mLat != -1.0 && mLon != -1.0){
            Log.d("mark", "마크 표시 보여야함")
            Log.d("mark", "lat : ${mLat}  , lon : ${mLon}")

            val address : ArrayList<String>? = gpsControl.getCurrentAddressArray(mLat, mLon)
            if(address != null){
                binding.mapMainAddress.text = address[1]
                binding.mapAddress.text = address[0]
            }
        }
        val marker = Marker(LatLng(mLat, mLon))
        marker.map = mNaverMap
        //위치 및 각도 조정
        //위치 및 각도 조정
        val cameraPosition = CameraPosition(
            LatLng(mLat, mLon),  // 위치 지정
            16.0,  // 줌 레벨
            0.0,  // 기울임 각도
            0.0 // 방향
        )
        mNaverMap.setCameraPosition(cameraPosition)
        mNaverMap.setOnMapClickListener { pointF, latLng ->
            // showCustomToast("위도 : ${latLng.latitude}, 경도 : ${latLng.longitude}")
            // Log.d("click", "클릭 이벤트 naver map")
            resultPosition(latLng.latitude, latLng.longitude)
        }
        mNaverMap.setOnMapLongClickListener { pointF, latLng ->
            // showCustomToast("위도 : ${latLng.latitude}, 경도 : ${latLng.longitude}")
            // Log.d("click", "클릭 긴 터치 이벤트 naver map")
            resultPosition(latLng.latitude, latLng.longitude)
        }
    }

    fun resultPosition(lat: Double, lon: Double){
        binding.gps.setImageResource(R.drawable.ic_map_person)
        binding.mapAddress.visibility = View.VISIBLE
        binding.mapMainAddress.visibility = View.VISIBLE
        binding.mapPositionSetting.visibility = View.INVISIBLE
        binding.mapDeliveryBtn.setBackgroundResource(R.drawable.map_address_box)
        if(flag){
            val address : ArrayList<String>? = gpsControl.getCurrentAddressArray(lat, lon)
            if(address != null){
                binding.mapMainAddress.text = address[1]
                binding.mapAddress.text = address[0]
            }
            flag = false
        }
    }

    override fun onPathUserCheckedAddressSuccess(response: UserCheckedAddressResponse) {
        dismissLoadingDialog()
        if(response.code == 1000){
            // 배달지 선택 완료 후 종료
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onPathUserCheckedAddressFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("배달 주소 선택에 실패하였습니다")
    }

    override fun onPostDeliveryAddressAddSuccess(response: DeliveryAddressResponse) {
        dismissLoadingDialog()
        if(response.code == 1000){
            val addressIdx = response.result.addressIdx
            // 배달지 추가 후 선택
            showLoadingDialog(this)
            MapService(this).tryPatchPathUserCheckedAddress(getUserIdx(), addressIdx)
        }
    }

    override fun onPostDeliveryAddressAddFailure(message: String) {
        dismissLoadingDialog()
        showCustomToast("배달주소 추가에 실패하였습니다")
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                mNaverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}