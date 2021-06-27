package com.example.coupangeats.util

import android.R
import android.app.Activity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout


class StatusBarColorControl {
    enum class StatusBarColorType(val backgroundColorId: Int) {
        TRANSPORT_STATUS_BAR(R.color.transparent), WHITE_STATUS_BAR(R.color.white), GRAY_STATUS_BAR(R.color.darker_gray);
    }

    fun setStatusBarColor(activity: Activity, colorType: StatusBarColorType) {
        activity.window.statusBarColor =
            ContextCompat.getColor(activity, colorType.backgroundColorId)
    }
}