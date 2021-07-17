package com.example.coupangeats.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.HorizontalScrollView
import androidx.core.widget.NestedScrollView

class CategorySuperCustomScrollView: NestedScrollView {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ) {
        overScrollMode = OVER_SCROLL_NEVER
    }

    var mStickyScroll = 240
    var header: View? = null
        set(value) {
            field = value
            field?.let {
                it.translationZ = 1f
            }
        }

    var originHorizonScrollView: HorizontalScrollView? = null
    var stickyHorizonScrollView: HorizontalScrollView? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val scrolly = t
        if(mStickyScroll < scrolly) {
            header?.visibility = View.VISIBLE
            if(stickyHorizonScrollView != null){
                val stickyPositionX = stickyHorizonScrollView!!.scrollX
                val stickyPositionY = stickyHorizonScrollView!!.scrollY
                originHorizonScrollView?.scrollTo(stickyPositionX, stickyPositionY)
            }
        } else {
            header?.visibility = View.GONE
            if(originHorizonScrollView != null){
                val stickyPositionX = originHorizonScrollView!!.scrollX
                val stickyPositionY = originHorizonScrollView!!.scrollY
                stickyHorizonScrollView?.scrollTo(stickyPositionX, stickyPositionY)
            }
        }
    }
}