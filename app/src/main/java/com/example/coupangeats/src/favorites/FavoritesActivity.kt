package com.example.coupangeats.src.favorites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityFavoritesBinding
import com.example.coupangeats.src.detailSuper.DetailSuperActivity
import com.example.coupangeats.src.favorites.adapter.FavoritesSuperAdapter
import com.example.coupangeats.src.favorites.model.FavoritesInfoResponse
import com.example.coupangeats.src.favorites.model.FavoritesSuperDeleteRequest
import com.example.coupangeats.src.favorites.model.FavoritesSuperInfo
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.config.BaseResponse

class FavoritesActivity : BaseActivity<ActivityFavoritesBinding>(ActivityFavoritesBinding::inflate), FavoritesActivityView {
    private var mIsModify = false
    private var mSort = "recentAdd"  // recentAdd || recentOrder
    private lateinit var mAdapter: FavoritesSuperAdapter
    private var mIsCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 서버통신 시작
        binding.favoritesSwipeRefresh.isRefreshing = true
        FavoritesService(this).tryGetFavoritesInfoSort(getUserIdx(), mSort)

        // swipeRefresh
        binding.favoritesSwipeRefresh.setOnRefreshListener {
            FavoritesService(this).tryGetFavoritesInfoSort(getUserIdx(), mSort)
        }

        binding.favoritesBack.setOnClickListener {
            setResult(RESULT_OK, Intent().apply {
                this.putExtra("version", 1)
            })
            finish()
        }

        binding.favoritesModify.setOnClickListener {
            if(mIsCount > 0){
                if(mIsModify){
                    // true -> false
                    mIsModify = false
                    binding.favoritesModify.text = "수정"
                    binding.favoritesInfo.visibility = View.VISIBLE

                    mAdapter.changeVersion(1)
                } else {
                    // false -> true
                    mIsModify = true
                    binding.favoritesModify.text = "취소"
                    binding.favoritesInfo.visibility = View.GONE

                    mAdapter.changeVersion(2)
                }
            }

        }

        binding.favoritesSelectParent.setOnClickListener{
            val request = FavoritesSuperDeleteRequest(mAdapter.getSelectData())
            // 삭제
            FavoritesService(this).tryPostFavoritesSuperDelete(getUserIdx(), request)
        }

        binding.favoritesSuperLook.setOnClickListener {
            setResult(RESULT_OK, Intent().apply {
                this.putExtra("version", 1)
            })
            finish()
        }
    }

    fun getUserIdx() : Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    override fun onGetFavoritesInfoSortSuccess(response: FavoritesInfoResponse) {
        binding.favoritesSwipeRefresh.isRefreshing = false
        if(response.code == 1000){
            val result = response.result
            if(result != null){
                // 즐겨찾기가 있을 경우
                mIsCount = response.result.size
                binding.favoritesNoContent.visibility = View.GONE
                val numberText = "총 ${result.size}개"
                binding.favoritesNumber.text = numberText

                // 어댑터 선택
                mAdapter = FavoritesSuperAdapter(result,this)
                binding.favoritesSuperRecyclerView.adapter = mAdapter
                binding.favoritesSuperRecyclerView.layoutManager = LinearLayoutManager(this)
                changeSelectLook(false, 0)
            } else {
                // 즐겨찾기가 없을 경우
                binding.favoritesNoContent.visibility = View.VISIBLE
                mIsCount = 0
            }
        } else {
            setDummyDate()
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition( R.anim.horiaon_exit, R.anim.horizon_enter)
    }

    fun changeSelectLook(isCheck: Boolean, num: Int = 0) {
        binding.favoritesSelectNum.text = num.toString()
        if(isCheck){
            // 보여져야함
            binding.favoritesSelectParent.visibility = View.VISIBLE
        } else {
            // 안보여져야한
            binding.favoritesSelectParent.visibility = View.GONE
        }
    }

    fun startSortSearch(sort: String, filterName: String){
        mSort = sort
        binding.favoritesFilterText.text = filterName
        binding.favoritesSwipeRefresh.isRefreshing = true
        FavoritesService(this).tryGetFavoritesInfoSort(getUserIdx(), mSort)
    }

    override fun onGetFavoritesInfoSortFailure(message: String) {
        binding.favoritesSwipeRefresh.isRefreshing = false
        setDummyDate()
    }

    override fun onPostFavoritesSuperDeleteSuccess(response: BaseResponse) {
        if(response.code == 1000){
            mIsModify = false
            binding.favoritesModify.text = "수정"
            binding.favoritesInfo.visibility = View.VISIBLE
            // 이제 다시 정보 부르기
            binding.favoritesSwipeRefresh.isRefreshing = true
            FavoritesService(this).tryGetFavoritesInfoSort(getUserIdx(), mSort)
        } else showCustomToast("즐겨찾기 해제를 실패하였습니다.")
    }

    override fun onPostFavoritesSuperDeleteFailure(message: String) {
        showCustomToast("즐겨찾기 해제를 실패하였습니다.")
    }

    fun startDetailSuper(storeIdx: Int) {
        val intent = Intent(this, DetailSuperActivity::class.java).apply {
            this.putExtra("storeIdx", storeIdx)
        }
        startActivity(intent)
    }

    private fun setDummyDate(){

        val superList = ArrayList<FavoritesSuperInfo>()
        val img = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMTA1MjdfNTUg%2FMDAxNjIyMTEzMDg3Njc5.J0L7A04dtBVEKOcBVbdbKmJFgHq12BTAAq3fDHFlQoIg.0vN8BoEqOEQjqhU3i-Q7s6MFWbrQ4ElJiJfGWWxoeBQg.JPEG.hs_1472%2Foutput_2445714095.jpg&type=sc960_832"

        superList.add(FavoritesSuperInfo(1, "엽기떡볶이 수원점", img, "Y", "2.5(3)", "1.3km", "13~20분", "2,000원", "2,000원"))
        superList.add(FavoritesSuperInfo(2, "순대국 수원점", img, null, "3.8(12)", "1km", "13~30분", "6,000원", null))
        superList.add(FavoritesSuperInfo(3, "투썸 수원점", img, null, null, "4km", "23~50분", "1,000원", null))
        superList.add(FavoritesSuperInfo(4, "쌀국수 수원점", img, null, null, "2.3km", "10~20분", "2,000원", "1,000원"))
        superList.add(FavoritesSuperInfo(5, "대기만성 수원점", img, "Y", "4.9(100)", "4.3km", "20~70분", "2,000원", "3,000원"))
        superList.add(FavoritesSuperInfo(6, "스시오부리 수원점", img, null, null, "1.2km", "13~20분", "3,000원", null))
        superList.add(FavoritesSuperInfo(7, "삼겹살이 좋아 수원점", img, "Y", "4.5(40)", "2km", "10~20분", "무료배달", null))
        superList.add(FavoritesSuperInfo(8, "알탕에 빠진 찜 수원점", img, "Y", null, "4km", "15~30분", "2,500원", "1,000원"))


        val numberText = "총 ${superList.size}개"
        binding.favoritesNumber.text = numberText

        mAdapter = FavoritesSuperAdapter(superList,this)
        binding.favoritesSuperRecyclerView.adapter = mAdapter
        binding.favoritesSuperRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onBackPressed() {
        setResult(RESULT_OK, Intent().apply {
            this.putExtra("version", 1)
        })
        super.onBackPressed()
    }
}