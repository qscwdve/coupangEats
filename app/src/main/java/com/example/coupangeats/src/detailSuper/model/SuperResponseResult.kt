package com.example.coupangeats.src.detailSuper.model

import com.google.gson.annotations.SerializedName

data class SuperResponseResult(
    @SerializedName("storeName") val storeName: String,
    @SerializedName("imageUrls") val img: ArrayList<String>,
    @SerializedName("rating") val rating: String?,
    @SerializedName("reviewCount") val reviewCount: String?,
    @SerializedName("coupon") val coupon: Coupon?,
    @SerializedName("deliveryTime") val time: String,
    @SerializedName("deliveryPrice") val deliveryPrice: Int,
    @SerializedName("minOrderPrice") val minPrice: Int,
    @SerializedName("cheetahDelivery") val cheetah: String,
    @SerializedName("photoReviews") val photoReview: ArrayList<PhotoReview>?,
    @SerializedName("menuCategories") val menu: ArrayList<Menu>?
)
