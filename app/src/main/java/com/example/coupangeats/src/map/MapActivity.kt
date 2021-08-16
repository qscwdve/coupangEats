package com.example.coupangeats.src.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityMapBinding
import com.example.coupangeats.databinding.DialogGpsActiveCheckBinding
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
    private var mLat = ""
    private var mLon = ""
    private var firstFlag = false
    private var tvHeight = 0
    private var tvWidth = 0
    private var flag = false
    private var version = 0   // 0 이면 선택 1이면 수정
    private lateinit var gpsControl : GpsControl
    private var nowLat = ""
    private var nowLon = ""
    private var mGpsX = 0
    private var mGpsY = 0
    private var mDetailAddressVersion = 1  // 1이면 gps_select , 2 이면 배달지 추가 , 3이면 배달지 수정
    private val PERMISSION_REQUEST_CODE = 100
    private var mNowGps = false
    private var mMainAddress = ""
    @SuppressLint("ClickableViewAccessibility", "Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition( R.anim.horizon_start_enter, R.anim.horizon_start_exit)

        mapView = findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)

        mLat = intent.getStringExtra("lat") ?: ""
        mLon = intent.getStringExtra("lon") ?: ""
        nowLat = mLat
        nowLon = mLon
        // Log.d("mapActivity", "받음 : ($nowLat , $nowLon)")
        mDetailAddressVersion = intent.getIntExtra("detailAddressVersion", 1)
        mNowGps = intent.getBooleanExtra("nowGPS", false)
        mMainAddress = intent.getStringExtra("mainAddress") ?: ""
        Log.d("address", "main : $mMainAddress")
        val n = intent.getIntExtra("version", -1) ?: -1

        if(n != -1){
            version = 1
            val text = "입력하신 배달지 위치를 확인해주세요"
            binding.mapInfo.text = text
        }

        //Log.d("position", "mLat : ${mLat}")
        //Log.d("position", "mLon : ${mLon}")
        // 대평초등학교 기중
        //mLat = 37.29755480607947
        //mLon = 126.9910868976806
        locationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE)
        gpsControl = GpsControl(this)

        // 현재 위치로 이동
        binding.mapNowGps.setOnClickListener {
            setNowGps()
        }

        // 이미지 좌표 구하기
        mapView.getMapAsync(this)

        binding.map.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN){

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
                //Log.d("click", "손 놓ㅇㅁ")
                setM(binding.gps)
                flag = true

                // 이미지 절대 좌표 구하기
                val rect = Rect()
                binding.gps.getGlobalVisibleRect(rect)
                mGpsX = (rect.left + rect.right)/2
                mGpsY = (rect.top + rect.bottom)/2

                val downTime = SystemClock.uptimeMillis()
                val eventTime = SystemClock.uptimeMillis() + 2000
                val aevent =
                    MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, mGpsX.toFloat(), mGpsY.toFloat(), 0)
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
            // 주소 선택함 -> DeliveryAddressSetting에 넘겨야함
            val address = binding.mapMainAddress.text.toString()
            val roadAddress = binding.mapAddress.text.toString()

            val edit = ApplicationClass.sSharedPreferences.edit()
            edit.putString("mainAddress", binding.mapMainAddress.text.toString())
            edit.putString("roadAddress", binding.mapAddress.text.toString())
            edit.apply()

            val intent = Intent().apply {
                this.putExtra("mainAddress",  address)
                this.putExtra("roadAddress", roadAddress)
                this.putExtra("lat", nowLat)
                this.putExtra("lon", nowLon)
            }
            setResult(RESULT_OK, intent)
            finish()

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
        //Log.d("image", "width : $tvWidth   ,  height : $tvHeight")
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
        mNaverMap.uiSettings.isIndoorLevelPickerEnabled = false
        mNaverMap.uiSettings.isZoomControlEnabled = false
        mNaverMap.uiSettings.isCompassEnabled = false
        mNaverMap.uiSettings.isScaleBarEnabled = false
        mNaverMap.uiSettings.isLocationButtonEnabled = false

        //Log.d("position", "mLat : $mLat , mLon : $mLon")
        val lat = mLat.toDouble()
        val lon = mLon.toDouble()
        if(lat != -1.0 && lon != -1.0){

            val address : ArrayList<String>? = gpsControl.getCurrentAddressArray(lat, lon)
            if(address != null){
                binding.mapMainAddress.text = if(mMainAddress != "") mMainAddress else address[1]
                binding.mapAddress.text = address[0]
            }
        }
        if(mNowGps){
            setNowGps()
        } else {
            //marker.map = mNaverMap
            //위치 및 각도 조정
            //위치 및 각도 조정
            val cameraPosition = CameraPosition(
                LatLng(lat, lon),  // 위치 지정
                18.0,  // 줌 레벨
                0.0,  // 기울임 각도
                0.0 // 방향
            )
            mNaverMap.cameraPosition = cameraPosition
        }

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

    private fun setNowGps(){
        val locationOverlay = mNaverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.iconHeight = 60
        locationOverlay.iconWidth = 60
        locationOverlay.circleRadius = 50
        locationOverlay.circleColor = Color.parseColor("#AEE0F8")

        val nowLocation = GpsControl(this).getLocation()
        if(nowLocation != null){
            locationOverlay.position = LatLng(nowLocation.latitude, nowLocation.longitude)
            mNaverMap.cameraPosition =  CameraPosition(
                LatLng(nowLocation.latitude, nowLocation.longitude),  // 위치 지정
                18.0,  // 줌 레벨
                0.0,  // 기울임 각도
                0.0 // 방향
            )
        } else {
            val gpsCheckBinding = DialogGpsActiveCheckBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(this)
            builder.setView(gpsCheckBinding.root)
            builder.setCancelable(false)

            val alertDialog = builder.create()
            alertDialog.show()

            val edit = ApplicationClass.sSharedPreferences.edit()
            gpsCheckBinding.dialogGpsActiveCheckNo.setOnClickListener {
                // gps 활성화 안함
                edit.putBoolean("gps", false).apply()
                alertDialog.dismiss()
            }
            gpsCheckBinding.dialogGpsActiveCheckYes.setOnClickListener {
                // gps 활성화
                // turnGPSOn()
                alertDialog.dismiss()
                // 퍼미션 검사
                checkRunTimePermission()
            }
        }
    }

    private fun checkRunTimePermission() {
        val PERMISSIONS_REQUEST_CODE = 100
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this@MapActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this@MapActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            ApplicationClass.sSharedPreferences.edit().putBoolean("gps", true).apply()

            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            ApplicationClass.sSharedPreferences.edit().putBoolean("gps", false).apply()

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MapActivity, REQUIRED_PERMISSIONS.get(0)
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                showCustomToast("이 앱을 실행하려면 위치 접근 권한이 필요합니다.")

                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@MapActivity,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@MapActivity,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun resultPosition(lat: Double, lon: Double){
        nowLat = lat.toString()
        nowLon = lon.toString()
        binding.gps.setImageResource(R.drawable.ic_map_person)
        binding.mapAddress.visibility = View.VISIBLE
        binding.mapMainAddress.visibility = View.VISIBLE
        binding.mapPositionSetting.visibility = View.INVISIBLE
        binding.mapDeliveryBtn.setBackgroundResource(R.drawable.map_address_box)
        if(flag){
            val address : ArrayList<String>? = gpsControl.getCurrentAddressArray(lat, lon)
            if(address != null){
                val addressText = address[1]
                if(addressText.contains("대한민국")) addressText.removePrefix("대한민국")
                val addressText2 = address[0]
                if(addressText2.contains("대한민국")) addressText2.removePrefix("대한민국")
                binding.mapMainAddress.text = addressText
                binding.mapAddress.text = addressText2
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
        //showCustomToast("배달 주소 선택에 실패하였습니다")
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
        //showCustomToast("배달주소 추가에 실패하였습니다")
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