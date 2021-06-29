package com.example.coupangeats.src.reviewWrite.adapter.model

data class ReviewWriteMenu(
    val menuIdx: Int,
    val menuName: String,
    val menuSideName: String?,
    var menuGood: Boolean? = null,
    val menuOpinion: ArrayList<Boolean> = arrayListOf(false, false, false, false, false),
    var menuEtc: String? = null
)
