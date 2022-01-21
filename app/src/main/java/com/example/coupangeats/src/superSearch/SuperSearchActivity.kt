package com.example.coupangeats.src.superSearch

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivitySuperSearchBinding
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.main.home.model.HomeInfo.RecommendStores
import com.example.coupangeats.src.searchDetail.SearchDetailActivity
import com.example.coupangeats.src.superSearch.adapter.BaseInfoAdapter
import com.example.coupangeats.src.superSearch.model.BaseSuperInfo
import com.example.coupangeats.src.superSearch.model.DiscountSuperResponse
import com.example.coupangeats.src.superSearch.model.FilterRequest
import com.example.coupangeats.src.superSearch.model.NewSuperResponse
import com.example.coupangeats.src.superSearch.util.FilterRecommendSuperSearch
import com.example.coupangeats.src.superSearch.util.FilterSuperSearch
import com.softsquared.template.kotlin.config.BaseActivity

class SuperSearchActivity :
    BaseActivity<ActivitySuperSearchBinding>(ActivitySuperSearchBinding::inflate),
    SuperSearchActivityView {
    var lat = ""
    var lon = ""
    var version = 1   // 1이면 할인중인 맛집 , 2이면 새로들어온 가게
    var filterSelected = Array(5) { i -> false }  // 필터를 선택했는지 안했는지
    private var whiteColor = "#FFFFFF"
    private var blackColor = "#000000"
    lateinit var mFilterRequest: FilterRequest
    private var mRecommSelect = 1
    private var mSelectMinOrder = -1
    private var mSelectDelivery = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition( R.anim.horizon_start_enter, R.anim.horizon_start_exit)

        lat = intent.getStringExtra("lat") ?: ""
        lon = intent.getStringExtra("lon") ?: ""
        version = intent.getIntExtra("version", 1) ?: 1
        // 호출!!
        binding.superSearchTitle.text = if (version == 1) "할인 중인 맛집" else "새로 들어온 가게!"
        mRecommSelect = if(version == 1) 1 else 5
        if (version == 1) {
            binding.homeFilterCoupon.visibility = View.GONE
            mFilterRequest = FilterRequest(lat, lon, "recomm", null, "Y", null, null, 25, 1)
        } else {
            mFilterRequest = FilterRequest(lat, lon, "new", null, null, null, null, 25, 1)
            binding.homeFilterRecommendText.text = "신규매장순"
        }
        startFilterSuper()

        binding.superSearchBack.setOnClickListener {
            finish()
        }

        // 디테일한 검색으로 가기
        binding.superSearchDetailSearch.setOnClickListener {
            val intent = Intent(this, SearchDetailActivity::class.java).apply {
                this.putExtra("lat", lat)
                this.putExtra("lon", lon)
            }
            startActivity(intent)
        }

        // swipeRefresh
        binding.superSearchSwipeRefresh.setOnRefreshListener {
            startFilterSuper()
        }

        // 치타 배달
        binding.homeFilterCheetah.setOnClickListener {
            if (!filterSelected[1]) {
                // 선택
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah_click)
                binding.homeFilterCheetahText.visibility = View.VISIBLE
                binding.homeFilterCheetahTitle.visibility = View.GONE
                mFilterRequest.cheetah = "Y"
                filterSelected[1] = true
            } else {
                // 취소
                binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
                binding.homeFilterCheetahText.visibility = View.GONE
                binding.homeFilterCheetahTitle.visibility = View.VISIBLE
                mFilterRequest.cheetah = null
                filterSelected[1] = false
            }
            startFilterSuper()
        }
        // 할인쿠폰
        binding.homeFilterCoupon.setOnClickListener {
            if (!filterSelected[4]) {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_click)
                binding.homeFilterCouponText.visibility = View.VISIBLE
                binding.homeFilterCouponTitle.visibility = View.GONE
                // 선택
                mFilterRequest.coupon = "Y"
                filterSelected[4] = true
            } else {
                binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
                binding.homeFilterCouponText.visibility = View.GONE
                binding.homeFilterCouponTitle.visibility = View.VISIBLE
                // 선택 취소
                mFilterRequest.coupon = null
                filterSelected[4] = false
            }
            // 서버와 통신해야 함
            startFilterSuper()
        }
        // 배달비
        binding.homeFilterDeliveryPrice.setOnClickListener {
            val filterSuperSearch = FilterSuperSearch(this, 1, mSelectDelivery)
            filterSuperSearch.show(supportFragmentManager, "deliveryPrice")
        }
        // 최소주문
        binding.homeFilterMiniOrder.setOnClickListener {
            val filterSuperSearch = FilterSuperSearch(this, 2, mSelectMinOrder)
            filterSuperSearch.show(supportFragmentManager, "deliveryPrice")
        }
        // 추천순
        binding.homeFilterRecommend.setOnClickListener {
            val filterRecomemndSuperSearch = FilterRecommendSuperSearch(this, mRecommSelect)
            filterRecomemndSuperSearch.show(supportFragmentManager, "recommend")
        }
        // 초기화 누름
        binding.homeFilterClear.setOnClickListener {
            // 초기화 시키기
            if (version == 1) {
                mFilterRequest = FilterRequest(lat, lon, "recomm", null, "Y", null, null, 25, 1)
            } else {
                mFilterRequest = FilterRequest(lat, lon, "new", null, null, null, null, 25, 1)
                binding.homeFilterRecommendText.text = "신규매장순"
            }
            for (i in 0..4) {
                filterSelected[i] = false
            }
            startFilterSuper()
            refreshFilter()
        }

        var scrollY = 0
        binding.searchRecommendRecyclerview.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollY += dy
                if (scrollY > 400) {
                    binding.superSearchScrollUpBtn.visibility = View.VISIBLE
                } else {
                    binding.superSearchScrollUpBtn.visibility = View.GONE
                }
                if (scrollY > 3) {
                    binding.superSearchFilterShadow.visibility = View.VISIBLE
                } else {
                    binding.superSearchFilterShadow.visibility = View.GONE
                }
            }
        })

        binding.superSearchScrollUpBtn.setOnClickListener {
            binding.superSearchSwipeRefresh.scrollTo(0, 0)
            binding.searchRecommendRecyclerview.scrollToPosition(0)
            binding.superSearchFilterShadow.visibility = View.GONE
            scrollY = 0
        }

        // 종료
        binding.superSearchBack.setOnClickListener { finish() }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition( R.anim.horiaon_exit, R.anim.horizon_enter)
    }

    fun startFilterSuper() {
        binding.superSearchSwipeRefresh.isRefreshing = true
        if (version == 1) {
            // 할인중인 맛집
            SuperSearchService(this).tryGetDiscountSuper(
                mFilterRequest.lat,
                mFilterRequest.lon,
                mFilterRequest.sort,
                mFilterRequest.cheetah,
                mFilterRequest.coupon,
                mFilterRequest.ordermin,
                mFilterRequest.deliverymin
            )
        } else {
            SuperSearchService(this).tryGetNewSuper(
                mFilterRequest.lat,
                mFilterRequest.lon,
                mFilterRequest.sort,
                mFilterRequest.cheetah,
                mFilterRequest.coupon,
                mFilterRequest.ordermin,
                mFilterRequest.deliverymin
            )
        }
        changeClearFilter()
    }

    // 추천순 필터 바꾸는 함수 다이얼로그에서 호출
    fun changeRecommendFilter(value: String, sendServerString: String, option: Int) {
        if (value == "추천순") {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(blackColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down)
            filterSelected[0] = false
            mFilterRequest.sort = sendServerString
        } else {
            binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterRecommendText.text = value
            binding.homeFilterRecommendText.setTextColor(Color.parseColor(whiteColor))
            binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down_white)
            filterSelected[0] = true
            mFilterRequest.sort = sendServerString
        }
        mFilterRequest.sort = sendServerString
        startFilterSuper()
        mRecommSelect = option
    }

    // 배달비 필터 바꾸는 함수 다이얼로그에서 호출
    fun changeDeliveryFilter(value: Int, valueString: String, select: Int) {
        if (value != -1) {
            val str = if(valueString == "무료배달") "무료배달" else "배달비 ${valueString}원 이하"
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterDeliveryPriceText.text = str
            binding.homeFilterDeliveryPriceText.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceTitle.visibility = View.GONE
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down_white)
            // 배달비 바꾸기
            mFilterRequest.deliverymin = value
            filterSelected[2] = true
        } else {
            binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterDeliveryPriceText.text = "배달비"
            binding.homeFilterDeliveryPriceText.visibility = View.GONE
            binding.homeFilterDeliveryPriceTitle.visibility = View.VISIBLE
            binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
            mFilterRequest.deliverymin = null
            filterSelected[2] = false
        }
        mSelectDelivery = select
        startFilterSuper()
    }

    // 최소 주문 바꾸는 함수 다이얼로그에서 호출
    fun changeOrderMinFilter(value: Int, valueString: String, select: Int) {
        if (value != -1) {
            val str = "최소주문 $valueString"
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_click)
            binding.homeFilterMiniOrderText.text = str
            binding.homeFilterMiniOrderText.visibility = View.VISIBLE
            binding.homeFilterMiniOrderTitle.visibility = View.GONE
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down_white)
            // 최소주문 바꾸기
            mFilterRequest.ordermin = value
            filterSelected[3] = true
        } else {
            // 전체
            binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
            binding.homeFilterMiniOrderText.text = "최소주문"
            binding.homeFilterMiniOrderText.visibility = View.GONE
            binding.homeFilterMiniOrderTitle.visibility = View.VISIBLE
            binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
            mFilterRequest.ordermin = null
            filterSelected[3] = false
        }
        mSelectMinOrder = select
        startFilterSuper()
    }

    // 초기화 필터 체크
    private fun changeClearFilter() {
        var num = 0
        for (value in filterSelected) {
            if (value) num++
        }
        if (num == 0) {
            // 초기화 필터 다운
            binding.homeFilterClear.visibility = View.GONE
        } else {
            // 초기화 필터 오픈
            binding.homeFilterClear.visibility = View.VISIBLE
            binding.homeFilterClearNum.text = num.toString()
        }
    }
    // 초기화
    private fun refreshFilter() {
        mSelectMinOrder = 5
        mSelectDelivery = 5
        // 초기화 필터 다운
        binding.homeFilterClear.visibility = View.GONE
        // 최소 주문
        binding.homeFilterMiniOrderBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterMiniOrderText.visibility = View.GONE
        binding.homeFilterMiniOrderTitle.visibility = View.VISIBLE
        binding.homeFilterMiniOrderImg.setImageResource(R.drawable.ic_arrow_down)
        // 배달비
        binding.homeFilterDeliveryPriceBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterDeliveryPriceText.visibility = View.GONE
        binding.homeFilterDeliveryPriceTitle.visibility = View.VISIBLE
        binding.homeFilterDeliveryPriceImg.setImageResource(R.drawable.ic_arrow_down)
        // 추천순
        binding.homeFilterRecommendBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterRecommendText.text = "추천순"
        binding.homeFilterRecommendText.setTextColor(Color.parseColor(blackColor))
        binding.homeFilterRecommendImg.setImageResource(R.drawable.ic_arrow_down)
        mRecommSelect = 1
        // 할인쿠폰
        binding.homeFilterCouponBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCouponText.visibility = View.GONE
        binding.homeFilterCouponTitle.visibility = View.VISIBLE
        // 치타배달
        binding.homeFilterCheetahBackground.setBackgroundResource(R.drawable.super_filter_no_click)
        binding.homeFilterCheetahImg.setImageResource(R.drawable.main_cheetah)
        binding.homeFilterCheetahTitle.visibility = View.VISIBLE
        binding.homeFilterCheetahText.visibility = View.GONE

    }

    override fun onGetNewSuperSuccess(response: NewSuperResponse) {
        binding.superSearchSwipeRefresh.isRefreshing = false
        if (response.code == 1000 ) {
            if( response.result.totalCount > 0){
                binding.searchRecommendRecyclerview.visibility = View.VISIBLE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.GONE
                setRecycler(response.result.newStores!!)
            } else{
                binding.searchRecommendRecyclerview.visibility = View.GONE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.VISIBLE
            }
            binding.searchRecommendRecyclerview.scrollTo(0,0)
        }
    }

    override fun onGetNewSuperFailure(message: String) {
        binding.superSearchSwipeRefresh.isRefreshing = false
        //showCustomToast("새로들어온 매장 조회 실패")
        setDumyData()
    }

    override fun onGetDiscountSuperSuccess(response: DiscountSuperResponse) {
        binding.superSearchSwipeRefresh.isRefreshing = false
        if (response.code == 1000) {
            if(response.result.totalCount > 0){
                binding.searchRecommendRecyclerview.visibility = View.VISIBLE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.GONE
                setRecycler(response.result.onSaleStores!!)
            } else {
                binding.searchRecommendRecyclerview.visibility = View.GONE
                binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.VISIBLE
            }
            binding.searchRecommendRecyclerview.scrollTo(0,0)
        }
    }

    override fun onGetDiscountSuperFailure(message: String) {
        binding.superSearchSwipeRefresh.isRefreshing = false
        //showCustomToast("할인 매장 조회 실패")
        setDumyData()
    }

    fun setDumyData(){
        val recommendList = ArrayList<BaseSuperInfo>()
        recommendList.add(BaseSuperInfo(
            1, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dbreowkd.jpg?alt=media&token=d549713a-b673-45d9-9404-3db41b5d4f3b", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/EJrrnr.jpg?alt=media&token=b05bb795-91d1-4b1c-977d-712286720c4e")
            ,"든든한끼 정식", "치타배달", "4.5", "0.7km",
            "2,000원", "10~20분", "3,000원"
        ))
        recommendList.add(BaseSuperInfo(
            2, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/tkaruq.jpg?alt=media&token=cc1dc826-43c4-4f0c-9041-24559ccc1a24")
            ,"꼬꼬방집", "Y", "2.5", "2.7km",
            "3,000원", "20~30분", "3,000원"
        ))
        recommendList.add(BaseSuperInfo(
            3, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/EJrrnr.jpg?alt=media&token=b05bb795-91d1-4b1c-977d-712286720c4e")
            ,"국수나무", "신규", "3.5", "1.7km",
            "3,000원", "20~30분", "3,000원"
        ))
        recommendList.add(BaseSuperInfo(
            4, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/dlxkffldk.jpg?alt=media&token=3740442c-6d97-40ba-a486-0f756c4fa0ff", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/EJrrnr.jpg?alt=media&token=b05bb795-91d1-4b1c-977d-712286720c4e", "https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19")
            ,"우아한 아침", "Y", "3.6", "2.5km",
            "3,000원", "10~20분", "4,000원"
        ))
        recommendList.add(BaseSuperInfo(
            5, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/qneo.png?alt=media&token=2204fe56-3be0-46d0-a405-f554aba00f19")
            ,"찌개의 정석", "신규", "3.0", "1.4km",
            "3,000원", "30~40분", "2,000원"
        ))
        recommendList.add(BaseSuperInfo(
            2, arrayListOf("https://firebasestorage.googleapis.com/v0/b/coupangeats-721e3.appspot.com/o/ekfrqkf.jpg?alt=media&token=ee65ead3-0f0c-4036-9605-43459745690f")
            ,"싱싱 육회", "치타배달", "4.2", "0.9km",
            "3,000원", "10~30분", "1,000원"
        ))

        binding.searchRecommendRecyclerview.visibility = View.VISIBLE
        binding.searchDetailNoFilterParent.itemNoSuperParent.visibility = View.GONE
        setRecycler(recommendList)

        binding.searchRecommendRecyclerview.scrollTo(0,0)
    }

    fun setRecycler(baseSperList: ArrayList<BaseSuperInfo>) {
        binding.searchRecommendRecyclerview.adapter = BaseInfoAdapter(baseSperList, this)
        binding.searchRecommendRecyclerview.layoutManager = LinearLayoutManager(this)
    }

    fun startDetailSuper(storeIdx: Int){
        val intent = Intent(this, DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }
}