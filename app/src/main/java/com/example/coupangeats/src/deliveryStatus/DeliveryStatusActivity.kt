package com.example.coupangeats.src.deliveryStatus

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityDeliveryStatusBinding
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.map.MapActivityView
import com.google.android.material.appbar.AppBarLayout
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.softsquared.template.kotlin.config.BaseActivity

class DeliveryStatusActivity : BaseActivity<ActivityDeliveryStatusBinding>(ActivityDeliveryStatusBinding::inflate),
    OnMapReadyCallback {
    lateinit var uiSettings : UiSettings
    lateinit var mNaverMap : NaverMap
    private lateinit var mapView: MapView
    private var mAnimationCheck = arrayOf(false, false, false)
    private var version = 1   // 1 : 주문 후 바로 요청됨 , 2 : 배달 완료까지 진행되는 요청
    private lateinit var locationSource: FusedLocationSource
    private enum class CollapsingToolbarLayoutState {
        EXPANDED, COLLAPSED, INTERNEDIATE
    }
    private var mCollapsingToolbarState : CollapsingToolbarLayoutState? = null
    private lateinit var animTransTwits1 : Animation
    private lateinit var animTransTwits2 : Animation
    private lateinit var animTransTwits3 : Animation

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView = binding.deliveryStatusMap
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // version
        version = intent.getIntExtra("version", 1)

        animTransTwits1 = AnimationUtils.loadAnimation(this, R.anim.delivery_status_pivot)

        animTransTwits1.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                if(mAnimationCheck[0]){
                    binding.status2Img.startAnimation(animTransTwits1)
                }
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        animTransTwits2 = AnimationUtils.loadAnimation(this, R.anim.delivery_status_pivot)

        animTransTwits2.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                if(mAnimationCheck[1]){
                    binding.status3Img.startAnimation(animTransTwits2)
                }
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        animTransTwits3 = AnimationUtils.loadAnimation(this, R.anim.delivery_status_pivot)

        animTransTwits3.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                if(mAnimationCheck[2]){
                    binding.status4Img.startAnimation(animTransTwits3)
                }
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        if(version == 1){
            changeStatus(1)
        } else {

        }
        binding.toolbarBack.setOnClickListener { finish() }

        setSupportActionBar(binding.toolbar)
        binding.appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener{
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if(verticalOffset == 0){
                    if(mCollapsingToolbarState != CollapsingToolbarLayoutState.EXPANDED){
                        binding.toolbar.setBackgroundColor(Color.parseColor("#00000000"))
                        binding.toolbarBack.setImageResource(R.drawable.ic_cancel)
                        mCollapsingToolbarState = CollapsingToolbarLayoutState.EXPANDED
                    }
                }
                else if(Math.abs(verticalOffset) >= appBarLayout!!.totalScrollRange){
                    // 액션바 스크롤 맨 위로 올림
                    binding.toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    binding.toolbarBack.setImageResource(R.drawable.ic_down_chevron)
                    mCollapsingToolbarState = CollapsingToolbarLayoutState.COLLAPSED
                }
                else if(Math.abs(verticalOffset) < appBarLayout.totalScrollRange){
                    //Log.d("vertical", "아직 액션바 안에 있음")
                    if(mCollapsingToolbarState != CollapsingToolbarLayoutState.INTERNEDIATE){
                        binding.toolbarBack.setImageResource(R.drawable.ic_cancel)
                    }
                }
            }

        })

        binding.deliveryStatusMap.setOnTouchListener { v, event ->
            binding.toolbarLayout.requestDisallowInterceptTouchEvent(true)
            binding.appBar.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    fun changeStatus(version: Int){
        // 수락된 상태 = version
        binding.status1Img.visibility = View.INVISIBLE
        binding.status2Img.visibility = View.INVISIBLE
        binding.status3Img.visibility = View.INVISIBLE
        binding.status4Img.visibility = View.INVISIBLE
        when(version){
            1 -> { changeStatusImg(R.drawable.ic_delivery_status_1, binding.status2Img, 1) }
            2 -> { changeStatusImg(R.drawable.ic_delivery_status_2, binding.status3Img, 2) }
            3 -> { changeStatusImg(R.drawable.ic_delivery_status_3, binding.status4Img, 3) }
            4 -> { binding.statusParent.setImageResource(R.drawable.ic_delivery_status_4) }
        }
        if(version >= 1){ changeStatusOk(binding.status1Img, null) }
        if(version >= 2){ changeStatusOk(binding.status2Img, 0) }
        if(version >= 3){ changeStatusOk(binding.status3Img, 1) }
        if(version >= 4){ changeStatusOk(binding.status4Img, 2) }
    }

    fun changeStatusOk(okImageView: ImageView, num:Int?){
        okImageView.visibility = View.VISIBLE
        okImageView.setImageResource(R.drawable.ic_delivery_status_ok)
        if(num != null) {
            mAnimationCheck[num] = false
            //Log.d("mAnimation", " mAnimationCheck[${num}] : ${mAnimationCheck[num]}")
        }
    }

    fun changeStatusImg(parentImg: Int, circleImageView: ImageView, num: Int) {
        binding.statusParent.setImageResource(parentImg)
        circleImageView.setImageResource(R.drawable.ic_delivery_status_circle)
        if(num == 1){ circleImageView.startAnimation(animTransTwits1) }
        if(num == 2){ circleImageView.startAnimation(animTransTwits2) }
        if(num == 3) { circleImageView.startAnimation(animTransTwits3) }
        mAnimationCheck[num - 1] = true
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

    override fun onMapReady(p0: NaverMap) {
        mNaverMap = p0

        // 현재 위치 마크 표시
        uiSettings = mNaverMap.uiSettings
        uiSettings.isZoomControlEnabled = true

        val bounds = LatLngBounds.Builder()
            .include(LatLng(37.5640984, 126.9712268))
            .include(LatLng(37.5651279, 126.9767904))
            .build()

        val marker = Marker(LatLng(37.5640984, 126.9712268))
        marker.width = 90
        marker.height = 100
        marker.icon = OverlayImage.fromResource(R.drawable.ic_map_person)
        marker.map = mNaverMap

        val marker2 = Marker(LatLng(37.5651279, 126.9767904))
        marker2.height = 100
        marker2.width = 90
        marker2.icon = OverlayImage.fromResource(R.drawable.ic_mark_super)
        marker2.map = mNaverMap

        //위치 및 각도 조정
        //위치 및 각도 조정
        val cameraPosition = CameraPosition(
            LatLng(37.5640984, 126.9712268),  // 위치 지정
            16.0,  // 줌 레벨
            0.0,  // 기울임 각도
            0.0 // 방향
        )

        //mNaverMap.cameraPosition = cameraPosition
        mNaverMap.extent = bounds
    }
}