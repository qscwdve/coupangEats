package com.example.coupangeats.src.reviewWrite

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coupangeats.R
import com.example.coupangeats.databinding.ActivityReviewWriteBinding
import com.example.coupangeats.src.reviewWrite.adapter.ReviewWriteMenuAdapter
import com.example.coupangeats.src.reviewWrite.adapter.ReviewWritePhotoAdapter
import com.example.coupangeats.src.reviewWrite.adapter.model.ReviewWriteMenu
import com.example.coupangeats.src.reviewWrite.dialog.ReviewWriteOpinionBottomSheet
import com.example.coupangeats.src.reviewWrite.model.*
import com.example.coupangeats.src.reviewWrite.util.FirebaseControl
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.config.BaseResponse

class ReviewWriteActivity :
    BaseActivity<ActivityReviewWriteBinding>(ActivityReviewWriteBinding::inflate),
    ReviewWriteActivityView {
    private var mIsRatingBad = false
    private var mIsDeliveryBad: Boolean? = null
    val mSelectMenuBadReason = arrayOf("양이 적음", "너무 짬", "너무 싱거움", "식었음", "너무 비쌈")
    private var mRatingBadCheck = arrayOf(false, false, false, false, false, false)

    // 0번 -> 조리시간 지연, 1번 -> 주문 누락, 2번 메뉴가 잘못 나옴, 3번 포장 부실, 4번 매장 요청사항 불이행, 5번 기타
    private var mRatingBadString =
        arrayOf("조리시간 지연", "주문 누락", "메뉴가 잘못 나옴", "포장 부실", "매장 요청사항 불이행", "기타")

    // 0번 늦게 도착, 1번 흘렀음/훼손됨, 2번 음식 온도, 3번 배달 요청사항 불이행, 4번 불친절, 5번 다른 메뉴 배달, 6번 상품을 받지 못함, 7번 길게 우회되어 배달됨
    private var mDeliveryBadCheck = arrayOf(false, false, false, false, false, false, false, false)
    private var mDeliberyBadString = arrayOf(
        "늦게 도착",
        "흘렀음/훼손됨",
        "음식 온도",
        "배달 요청사항 불이행",
        "불친절",
        "다른 메뉴 배달",
        "상품을 받지 못함",
        "길게 우회되어 배달됨"
    )
    private var mRatingEtcString: String? = null
    private var mDeliveryEtcString: String? = null
    private lateinit var mPhotoAdapter: ReviewWritePhotoAdapter
    private lateinit var mMenuAdapter: ReviewWriteMenuAdapter
    var mRatingCount: Int = 0
    private var mStoreIdx: Int = -1
    private var mOrderIdx: Int = -1
    private var mReviewIdx: Int = -1
    private var mPhotoList : ArrayList<String>? = null
    private val GET_GALLERY_IMAGE = 200
    lateinit var mFireBaseControl: FirebaseControl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 리뷰 조회가 아닐경우 처음인 경우
        mOrderIdx = intent.getIntExtra("orderIdx", -1)
        mReviewIdx = intent.getIntExtra("reviewIdx", -1)
        mFireBaseControl = FirebaseControl(this, getUserIdx().toString())

        mPhotoAdapter = ReviewWritePhotoAdapter(ArrayList<String>(), this)
        binding.reviewWritePhotoRecyclerView.adapter = mPhotoAdapter
        binding.reviewWritePhotoRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 리뷰 주문 조회
        if (mReviewIdx == -1) {
            // 처음 리뷰 작성
            ReviewWriteService(this).tryGetReviewWriteInfo(getUserIdx(), mOrderIdx)
        } else {
            // 리뷰 수정
            ReviewWriteService(this).tryGetReviewWriteModify(getUserIdx(), mReviewIdx)
            binding.reviewWriteApply.text = "수정하기"
        }
        // 뒤로 가기
        binding.reviewWriteBack.setOnClickListener { finish() }

        binding.reviewWriteStar1.setOnClickListener {
            setStar(
                1,
                R.drawable.ic_review_star_small_no,
                R.drawable.ic_review_star_small
            )
        }

        binding.reviewWriteStar2.setOnClickListener {
            setStar(
                2,
                R.drawable.ic_review_star_small_no,
                R.drawable.ic_review_star_small
            )
        }
        binding.reviewWriteStar3.setOnClickListener {
            setStar(
                3,
                R.drawable.ic_review_star_small_no,
                R.drawable.ic_review_star_small
            )
        }
        binding.reviewWriteStar4.setOnClickListener {
            setStar(
                4,
                R.drawable.ic_review_star_small_no,
                R.drawable.ic_review_star_small
            )
        }
        binding.reviewWriteStar5.setOnClickListener {
            setStar(
                5,
                R.drawable.ic_review_star_small_no,
                R.drawable.ic_review_star_small
            )
        }

        // 리뷰 bad
        // 0번 -> 조리시간 지연, 1번 -> 주문 누락, 2번 메뉴가 잘못 나옴, 3번 포장 부실, 4번 매장 요청사항 불이행, 5번 기타
        binding.reviewWriteRatingLateFood.setOnClickListener {
            changeRatingBadCheck(
                0,
                binding.reviewWriteRatingLateFood
            )
        }
        binding.reviewWriteRatingOrderMiss.setOnClickListener {
            changeRatingBadCheck(
                1,
                binding.reviewWriteRatingOrderMiss
            )
        }
        binding.reviewWriteRatingMenuMiss.setOnClickListener {
            changeRatingBadCheck(
                2,
                binding.reviewWriteRatingMenuMiss
            )
        }
        binding.reviewWriteRatingPackLoss.setOnClickListener {
            changeRatingBadCheck(
                3,
                binding.reviewWriteRatingPackLoss
            )
        }
        binding.reviewWriteRatingSuperRequestNot.setOnClickListener {
            changeRatingBadCheck(
                4,
                binding.reviewWriteRatingSuperRequestNot
            )
        }
        binding.reviewWriteRatingEtc.setOnClickListener {
            changeRatingBadCheck(
                5,
                binding.reviewWriteRatingEtc
            )
        }

        // 배달 bad
        // 0번 늦게 도착, 1번 흘렀음/훼손됨, 2번 음식 온도, 3번 배달 요청사항 불이행, 4번 불친절, 5번 다른 메뉴 배달, 6번 상품을 받지 못함, 7번 길게 우회되어 배달됨
        binding.reviewWriteLate.setOnClickListener {
            changeDeliveryBadCheck(
                0,
                binding.reviewWriteLate
            )
        }
        binding.reviewWriteMessy.setOnClickListener {
            changeDeliveryBadCheck(
                1,
                binding.reviewWriteMessy
            )
        }
        binding.reviewWriteFoodCold.setOnClickListener {
            changeDeliveryBadCheck(
                2,
                binding.reviewWriteFoodCold
            )
        }
        binding.reviewWriteDeliveryRequestNot.setOnClickListener {
            changeDeliveryBadCheck(
                3,
                binding.reviewWriteDeliveryRequestNot
            )
        }
        binding.reviewWriteDeliveryUnkind.setOnClickListener {
            changeDeliveryBadCheck(
                4,
                binding.reviewWriteDeliveryUnkind
            )
        }
        binding.reviewWriteDeliveryDifferentMenu.setOnClickListener {
            changeDeliveryBadCheck(
                5,
                binding.reviewWriteDeliveryDifferentMenu
            )
        }
        binding.reviewWriteDeliveryNotFood.setOnClickListener {
            changeDeliveryBadCheck(
                6,
                binding.reviewWriteDeliveryNotFood
            )
        }
        binding.reviewWriteDeliveryLong.setOnClickListener {
            changeDeliveryBadCheck(
                7,
                binding.reviewWriteDeliveryLong
            )
        }

        // 배달 평가
        binding.reviewWriteDeliveryLike.setOnClickListener { changeDeliveryLike(true) }
        binding.reviewWriteDeliveryDislike.setOnClickListener { changeDeliveryLike(false) }

        // 사진 추가
        binding.reviewWritePhotoAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                this.setDataAndType(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            }
            startActivityForResult(intent, GET_GALLERY_IMAGE)
        }

        binding.reviewWriteDeliveryEtcModify.setOnClickListener { startEtcDialog(2) }
        binding.reviewWriteDeliveryEtcTextParent.setOnClickListener { startEtcDialog(2) }

        // 리뷰작성
        binding.reviewWriteApply.setOnClickListener {
            if (mReviewIdx == -1) startReviewCreate()
            else {
                //리뷰 수정 API조회
                startReviewModifyApply()
            }
        }
    }

    fun getUserIdx(): Int = ApplicationClass.sSharedPreferences.getInt("userIdx", -1)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            // 사진 추가 리스트에 추가해야함
            mFireBaseControl.addFireBaseImg(data.data!!)
        }
    }

    fun gonePhotoAdd(){
        binding.reviewWritePhotoAdd.visibility = View.GONE
    }

    fun visiblePhotoAdd(){
        binding.reviewWritePhotoAdd.visibility = View.VISIBLE
    }

    fun setMenuAdapter(menu: ArrayList<orderMenus>) {
        val menuList = ArrayList<ReviewWriteMenu>()
        for (index in menu.indices) {
            val value = menu[index]
            menuList.add(
                ReviewWriteMenu(
                    value.orderMenuIdx.toInt(),
                    value.orderMenuName,
                    value.orderMenuSide
                )
            )
        }
        mMenuAdapter = ReviewWriteMenuAdapter(menuList, this)
        binding.reviewWriteMenuRecyclerView.adapter = mMenuAdapter
        binding.reviewWriteMenuRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun startReviewCreate() {
        if (mRatingCount == 0) {
            showCustomToast("별점을 체크해주세요")
        } else {
            val reviewCreateRequest = ReviewWriteCreateRequest(
                mOrderIdx,
                mStoreIdx,
                mRatingCount,
                getRatingBadReason(),
                binding.reviewWriteContentText.text.toString(),
                getPhotoList(),
                mMenuAdapter.getMenuReviewData(),
                getDeliveryReview(),
                getUserIdx()
            )
            Log.d("리뷰 작성", "reviewCreateReqeust: $reviewCreateRequest")
            // 리뷰 작성
            ReviewWriteService(this).tryPostReviewWriteCreate(reviewCreateRequest)
        }
    }

    fun startReviewModifyApply(){
        val reviewWriteModifyApplyRequest = ReviewWriteModifyApplyRequest(
            mRatingCount,
            getRatingBadReason(),
            binding.reviewWriteContentText.text.toString(),
            getListSame(mPhotoList, getPhotoList()),
            getPhotoList(),
            mMenuAdapter.getMenuReviewData(),
            getDeliveryReview()
        )
        Log.d("리뷰 수정 적용", "reviewWriteModifyApplyRequest: $reviewWriteModifyApplyRequest")
        // 리뷰 수정 작성
        ReviewWriteService(this).tryGetReviewWriteModifyApply(getUserIdx(), mReviewIdx, reviewWriteModifyApplyRequest)
    }

    fun getListSame(list1: ArrayList<String>?, list2: ArrayList<String>?) : String {
        return if(list1 == null && list2 == null) "N"
        else if(list1 == null && list2 != null) "Y"
        else if(list1 != null && list2 == null) "Y"
        else {
            if(list1!!.containsAll(list2!!)) "N" else "Y"
        }
    }

    fun getPhotoList(): ArrayList<String>? {
        return mPhotoAdapter.getPhotoList()
    }

    fun getDeliveryReview(): DeliveryReview {
        val deliveryLiked: String? =
            if (mIsDeliveryBad == null) null else if (mIsDeliveryBad == true) "BAD" else "GOOD"
        return DeliveryReview(deliveryLiked, getDeliveryBadReason(), mDeliveryEtcString)
    }

    fun getDeliveryBadReason(): String? {
        var badReason: String? = ""
        for (index in mDeliveryBadCheck.indices) {
            if (mDeliveryBadCheck[index]) {
                badReason += mDeliberyBadString[index] + ","
            }
        }
        if (badReason == "") badReason = null
        else if (badReason!![badReason.length - 1] == ',') {
            badReason = badReason.substring(0 until (badReason.length - 1))
        }
        return badReason
    }

    fun getRatingBadReason(): String? {
        var badReason: String? = ""
        for (index in mRatingBadCheck.indices) {
            if (mRatingBadCheck[index]) {
                badReason += mRatingBadString[index] + ","
            }
        }
        if (badReason == "") badReason = null
        else if (badReason!![badReason.length - 1] == ',') {
            badReason = badReason.substring(0 until (badReason.length - 1))
        }
        return badReason
    }

    fun addImgToPhotoAdapter(photoData: String) {
        mPhotoAdapter.addPhoto(photoData)
    }

    fun changeDeliveryLike(value: Boolean?) {
        if (value == null || value == true) {
            mIsDeliveryBad = false
            binding.reviewWriteDeliveryLike.setBackgroundResource(R.drawable.review_like_box_check)
            binding.reviewWriteDeliveryLikeImg.setImageResource(R.drawable.ic_review_like_check)
            binding.reviewWriteDeliveryDislike.setBackgroundResource(R.drawable.review_like_box)
            binding.reviewWriteDeliveryDislikeImg.setImageResource(R.drawable.ic_review_dislike)
            binding.reviewWriteDeliveryBadParent.visibility = View.GONE
        } else if (value == false) {
            mIsDeliveryBad = true
            binding.reviewWriteDeliveryLike.setBackgroundResource(R.drawable.review_like_box)
            binding.reviewWriteDeliveryLikeImg.setImageResource(R.drawable.ic_review_like)
            binding.reviewWriteDeliveryDislike.setBackgroundResource(R.drawable.review_like_box_check)
            binding.reviewWriteDeliveryDislikeImg.setImageResource(R.drawable.ic_review_dislike_check)
            binding.reviewWriteDeliveryBadParent.visibility = View.VISIBLE
        }
        binding.reviewWriteRider.setImageResource(R.drawable.ic_review_check)
    }

    fun changeDeliveryBadCheck(position: Int, view: LinearLayout) {
        if (mDeliveryBadCheck[position]) {
            // true -> false
            mDeliveryBadCheck[position] = false
            view.setBackgroundResource(R.drawable.login_box)
        } else {
            // false -> true
            mDeliveryBadCheck[position] = true
            view.setBackgroundResource(R.drawable.detail_address_category_box)
        }
    }

    fun changeRatingBadCheck(position: Int, view: LinearLayout) {
        if (mRatingBadCheck[position]) {
            // true -> false
            mRatingBadCheck[position] = false
            view.setBackgroundResource(R.drawable.login_box)

        } else {
            // false -> true
            mRatingBadCheck[position] = true
            view.setBackgroundResource(R.drawable.detail_address_category_box)

        }
    }

    fun startEtcDialog(version: Int) {
        // 1이면 rating 2이면 delivery
        val opinion: String = when (version) {
            1 -> {
                mRatingEtcString ?: ""
            }
            else -> {
                mDeliveryEtcString ?: ""
            }
        }
        val reviewWriteOpinionBottomSheet =
            ReviewWriteOpinionBottomSheet(version, opinion, this)
        reviewWriteOpinionBottomSheet.show(supportFragmentManager, "etcOpinion")
    }


    fun changeDeliveryEtc(content: String) {
        if (content != "") {
            binding.reviewWriteDeliveryEtcText.text = content
            binding.reviewWriteDeliveryEtcText.setTextColor(Color.parseColor("#949DA6"))  // 회색
            binding.reviewWriteDeliveryEtcModify.visibility = View.VISIBLE
            mDeliveryEtcString = content
        } else {
            binding.reviewWriteDeliveryEtcText.text = "기타의견"
            binding.reviewWriteDeliveryEtcText.setTextColor(Color.parseColor("#00AFFE"))  // 파란색
            binding.reviewWriteDeliveryEtcModify.visibility = View.GONE
            mDeliveryEtcString = null
        }
    }

    fun setStar(num: Int, starNo: Int, starYes: Int) {
        binding.reviewWriteContentParent.visibility = View.VISIBLE
        binding.reviewWriteStar1.setImageResource(starNo)
        binding.reviewWriteStar2.setImageResource(starNo)
        binding.reviewWriteStar3.setImageResource(starNo)
        binding.reviewWriteStar4.setImageResource(starNo)
        binding.reviewWriteStar5.setImageResource(starNo)
        if (num >= 1) binding.reviewWriteStar1.setImageResource(starYes)
        if (num >= 2) binding.reviewWriteStar2.setImageResource(starYes)
        if (num >= 3) binding.reviewWriteStar3.setImageResource(starYes)
        if (num >= 4) binding.reviewWriteStar4.setImageResource(starYes)
        if (num >= 5) binding.reviewWriteStar5.setImageResource(starYes)
        if (num <= 3) {
            // 어떤점이 리뷰가 별로였는지 기록해야한다.
            mIsRatingBad = true
            binding.reviewWriteRatingBadParent.visibility = View.VISIBLE
        } else {
            mIsRatingBad = false
            binding.reviewWriteRatingBadParent.visibility = View.GONE
        }
        mRatingCount = num
        binding.reviewWriteSuper.setImageResource(R.drawable.ic_review_check)
    }

    override fun onPostReviewWriteCreateSuccess(response: ReviewWriteCreateResponse) {
        if (response.code != 1000) showCustomToast("리뷰 작성에 실패하였습니다")
        else {
            showCustomToast("리뷰작성 성공")
            finish()
        }
    }

    override fun onPostReviewWriteCreateFailure(message: String) {
        showCustomToast("리뷰 작성에 실패하였습니다")
    }

    override fun onGetReviewWriteInfoSuccess(response: ReviewWriteInfoResponse) {
        if (response.code == 1000) {
            val result = response.result
            binding.reviewWriteStoreName.text = result.storeName
            setMenuAdapter(result.orderMenu)
            mStoreIdx = result.storeIdx
        }
    }

    override fun onGetReviewWriteInfoFailure(message: String) {

    }

    override fun onGetReviewWriteModifySuccess(response: ReviewWriteModifyResponse) {
        if (response.code == 1000) {
            val result = response.result
            binding.reviewWriteStoreName.text = result.storeName // 매장 이름
            setStar(
                result.rating,
                R.drawable.ic_review_star_small_no,
                R.drawable.ic_review_star_small
            ) // 별점
            // 별점이 낮을 때 박스 체크
            if (result.badReason != null) setRatingCheck(result.badReason)
            binding.reviewWriteContentText.setText(result.contents) // 리뷰 내용 설정
            // 이미지 사진 추가!
            mPhotoList = result.imgList
            if (result.imgList != null) mPhotoAdapter.changeItemList(result.imgList)
            // 메뉴 추가
            val menuList = ArrayList<ReviewWriteMenu>()
            val menu = result.menuReviews
            if (menu != null) {
                for (index in menu.indices) {
                    val value = menu[index]
                    var menuLiked: Boolean? = null
                    if (value.menuLiked != null) {
                        val like: String = value.menuLiked
                        menuLiked = (like == "GOOD")
                    }
                    menuList.add(
                        ReviewWriteMenu(
                            value.orderMenuIdx, value.menuName, value.menuDetail, menuLiked,
                            getMenuOpinion(value.badReason), value.menuComment
                        )
                    )
                }
                mMenuAdapter = ReviewWriteMenuAdapter(menuList, this)
                binding.reviewWriteMenuRecyclerView.adapter = mMenuAdapter
                binding.reviewWriteMenuRecyclerView.layoutManager = LinearLayoutManager(this)
            }
            // 배달 평가
            if (result.deliveryReview != null) {
                var deliveryLiked: Boolean? = null
                if (result.deliveryReview.deliveryLiked != null) {
                    val like = result.deliveryReview.deliveryLiked
                    deliveryLiked = (like == "GOOD")
                }
                changeDeliveryLike(deliveryLiked)
                if (result.deliveryReview.deliveryComment != null) changeDeliveryEtc(result.deliveryReview.deliveryComment)
                if (result.deliveryReview.deliveryBadReason != null) setDeliveryOpinion(result.deliveryReview.deliveryBadReason)

            }

        }
    }

    override fun onGetReviewWriteModifyFailure(message: String) {

    }

    // 리뷰 수정 적용
    override fun onGetReviewWriteModifyApplySuccess(response: BaseResponse) {
        if(response.code == 1000){
            finish()
        }
    }

    override fun onGetReviewWriteModifyApplyFailure(message: String) {

    }

    fun setDeliveryOpinion(badReason: String) {
        val viewList = arrayOf(
            binding.reviewWriteLate,
            binding.reviewWriteMessy,
            binding.reviewWriteFoodCold,
            binding.reviewWriteDeliveryRequestNot,
            binding.reviewWriteDeliveryUnkind,
            binding.reviewWriteDeliveryDifferentMenu,
            binding.reviewWriteDeliveryNotFood,
            binding.reviewWriteDeliveryLong
        )

        val token = badReason.split(',')
        for (index in token.indices) {
            val str = token[index]
            for (deliveryIndex in mDeliberyBadString.indices) {
                if (str == mDeliberyBadString[deliveryIndex]) {
                    changeDeliveryBadCheck(deliveryIndex, viewList[deliveryIndex])
                    break
                }
            }
        }
    }

    fun getMenuOpinion(badReason: String?): ArrayList<Boolean> {
        val menuOpinion: ArrayList<Boolean> = arrayListOf(false, false, false, false, false)
        if (badReason != null) {
            val token = badReason.split(',')
            for (index in token.indices) {
                val str = token[index]
                for (ratingIndex in mSelectMenuBadReason.indices) {
                    if (str == mSelectMenuBadReason[ratingIndex]) {
                        menuOpinion[ratingIndex] = true
                    }
                }
            }
        }
        return menuOpinion
    }

    fun setRatingCheck(badReason: String) {
        val viewList = arrayOf(
            binding.reviewWriteRatingLateFood, binding.reviewWriteRatingOrderMiss,
            binding.reviewWriteRatingMenuMiss, binding.reviewWriteRatingPackLoss,
            binding.reviewWriteRatingSuperRequestNot, binding.reviewWriteRatingEtc
        )
        val token = badReason.split(',')
        for (index in token.indices) {
            val str = token[index]
            var flag = false
            for (ratingIndex in mRatingBadString.indices) {
                if (str == mRatingBadString[ratingIndex]) {
                    changeRatingBadCheck(ratingIndex, viewList[ratingIndex])
                    flag = true
                    break
                }
            }
            if (!flag) {
                // 기타의견
                changeRatingBadCheck(5, viewList[5])
            }
        }
    }
}
