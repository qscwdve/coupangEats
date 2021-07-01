package com.example.coupangeats.src.favorites.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName

data class FavoritesSuperInfo(
    @SerializedName("storeIdx") val storeIdx: Int,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("imageUrl") val imgUrl: String?,
    @SerializedName("cheetahDelivery") val cheetah: String?,
    @SerializedName("totalReview") val totalReview: String?,
    @SerializedName("distance") val distance: String,
    @SerializedName("deliveryTime") val deliveryTime: String?,
    @SerializedName("deliveryPrice") val deliveryPrice: String?,
    @SerializedName("coupon") val coupon: String?
)
