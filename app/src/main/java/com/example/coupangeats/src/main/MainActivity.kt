package com.example.coupangeats.src.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityMainBinding
import com.example.coupangeats.databinding.DialogGpsActiveCheckBinding
import com.example.coupangeats.src.deliveryAddressSetting.DeliveryAddressSettingActivity
import com.example.coupangeats.src.favorites.FavoritesActivity
import com.example.coupangeats.src.main.home.HomeFragment
import com.example.coupangeats.src.main.myeats.MyeatsFragment
import com.example.coupangeats.src.main.order.OrderFragment
import com.example.coupangeats.src.main.search.SearchFragment
import com.example.coupangeats.util.CartMenuDatabase
import com.example.coupangeats.util.LoginBottomSheetDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private var mfragmentIndex = 1
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    private val FAVORITES_REQUEST_CODE = 1234
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val DRIVERYADDRESSSETTING = 1234
    lateinit var loginBottomSheetDialog : LoginBottomSheetDialog

    private lateinit var mDBHelper: CartMenuDatabase
    private lateinit var mDB: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gpsCheck()
        Log.d("jwt", ApplicationClass.sSharedPreferences.getString(ApplicationClass.X_ACCESS_TOKEN, "") ?: "")
        // 데이터베이스 셋팅
        mDBHelper = CartMenuDatabase(this, "Menu.db", null, 1)
        mDB = mDBHelper.writableDatabase

        loginBottomSheetDialog = LoginBottomSheetDialog(this)
        // 처음 화면 HomeFragment로 지정
        supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment(), "homeFragment")
                .commitAllowingStateLoss()

        binding.mainBtmNav.itemIconTintList = null
        val jwt = ApplicationClass.sSharedPreferences.getString(ApplicationClass.X_ACCESS_TOKEN, "null")
        if (jwt != null) {
            Log.d("jwt", jwt)
        }
        // 카트 초기화
        resetCart()

        binding.mainBtmNav.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_main_btm_nav_home -> {
                        mfragmentIndex = 1
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, HomeFragment(), "homeFragment")
                            .commitAllowingStateLoss()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_search -> {
                        mfragmentIndex = 2
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, SearchFragment(this, 1))
                            .commitAllowingStateLoss()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_favorites -> {
                        mfragmentIndex = 3
                        startActivityForResult(Intent(this, FavoritesActivity::class.java), FAVORITES_REQUEST_CODE)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.menu_main_btm_nav_order -> {
                        if (loginCheck()) {
                            mfragmentIndex = 4
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.main_frm, OrderFragment())
                                .commitAllowingStateLoss()
                            return@OnNavigationItemSelectedListener true
                        } else {
                            loginBottomSheetDialogShow()
                            return@OnNavigationItemSelectedListener false
                        }
                    }
                    R.id.menu_main_btm_nav_myeats -> {
                        if (loginCheck()) {
                            mfragmentIndex = 5
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.main_frm, MyeatsFragment())
                                .commitAllowingStateLoss()
                            return@OnNavigationItemSelectedListener true
                        } else {
                            loginBottomSheetDialogShow()
                            return@OnNavigationItemSelectedListener false
                        }
                    }
                }
                false
            }
        )
    }

    fun resetCart() {
        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putInt("menuNum", 0).apply()
    }

    override fun onBackPressed() {
        if(mfragmentIndex != 1) {
            setHomeFragment()
        } else {
            super.onBackPressed()
        }
    }

    fun setBottomNavigationBarGone() { binding.mainBtmNav.visibility = View.GONE }
    fun setBottomNavigationBarVisible() {binding.mainBtmNav.visibility = View.VISIBLE}

    fun setHomeFragment() {
        setBottomNavigationBarVisible()
        binding.mainBtmNav.selectedItemId = R.id.menu_main_btm_nav_home
    }
    fun setSearchAdvencedFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment(this, 2))
                .commitAllowingStateLoss()
    }
    fun loginBottomSheetDialogDismiss() {
        loginBottomSheetDialog.dismiss()
    }
    fun loginBottomSheetDialogShow(){
        loginBottomSheetDialog.show(supportFragmentManager, "Login")
    }

    fun startDeliveryAddressSettingActivityResult(version : Int = 1) {
        // version = 1 : GPS 선택 , 2 : 배달주소 관리
        intent = Intent(this, DeliveryAddressSettingActivity::class.java).apply {
            this.putExtra("version", version)
        }
        startActivityForResult(intent, DRIVERYADDRESSSETTING)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DRIVERYADDRESSSETTING && resultCode == RESULT_OK) {
            val home = supportFragmentManager.findFragmentByTag("homeFragment") as HomeFragment
            home.changeGpsInfo()
        }
        else if(requestCode == FAVORITES_REQUEST_CODE && resultCode == RESULT_OK){
            var version : Int? = null
            if(data != null){
                version = data.getIntExtra("version", -1)
            }
            if(version != null && version == 1){
                // 홈으로 바로 클릭
                setHomeFragment()
            }
        }

    }
    // 로그인 여부 확인
    fun loginCheck() : Boolean {
        return ApplicationClass.sSharedPreferences.getInt("userIdx", -1) != -1
    }
    // gps
    fun gpsCheck(){
        if (!checkLocationServicesStatus()) {
            activeGpsDialog()
        }else {
            checkRunTimePermission()
        }
    }

    // gps 가 활성화되어있는지 검사
    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    private fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            gpsStatusSave(true)

            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            gpsStatusSave(false)
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity, REQUIRED_PERMISSIONS.get(0)
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                showCustomToast("이 앱을 실행하려면 위치 접근 권한이 필요합니다.")

                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    fun gpsStatusSave(toggle: Boolean) {
        ApplicationClass.sSharedPreferences.edit().putBoolean("gps", toggle).apply()
    }

    private fun activeGpsDialog() {
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

    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String?>,
        grandResults: IntArray
    ) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            val edit = ApplicationClass.sSharedPreferences.edit()
            if (check_result) {
                // 퍼미션 수락
                edit.putBoolean("gps", true)
            } else {
                // 퍼미션 거부
                edit.putBoolean("gps", false)
            }
            edit.apply()
        }
    }

    override fun onDestroy() {
        val edit = ApplicationClass.sSharedPreferences.edit()
        edit.putInt("userMainAddressIdx", -1)

        edit.apply()
        mDBHelper.deleteTotal(mDB)
        super.onDestroy()
    }

}