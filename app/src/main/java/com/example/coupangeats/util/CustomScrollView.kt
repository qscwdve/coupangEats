package com.example.coupangeats.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.HorizontalScrollView
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView

class CustomScrollView : NestedScrollView, ViewTreeObserver.OnGlobalLayoutListener {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ) {
        overScrollMode = OVER_SCROLL_NEVER
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }
    var version = 1  // 빼는거 안함 2면 home으로 간주
    var header: View? = null
        set(value) {
            field = value
            field?.let {
                it.translationZ = 1f
            }
        }

    var position: View? = null
    var headerShadow : View? = null
    var originHorizonScrollView: HorizontalScrollView? = null
    var stickyHorizonScrollView: HorizontalScrollView? = null

    private var mIsHeaderSticky = false
    var mHeaderInitPosition = 0f
    var mHeaderParentPosition = 0f

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val scrolly = t
        val distance = if(version == 1) mHeaderInitPosition + mHeaderParentPosition else mHeaderInitPosition + mHeaderParentPosition - 100
        if (scrolly > distance) {
            if (!mIsHeaderSticky) {
                if(originHorizonScrollView != null){
                    val stickyPositionX = originHorizonScrollView!!.scrollX
                    val stickyPositionY = originHorizonScrollView!!.scrollY
                    stickyHorizonScrollView?.scrollTo(stickyPositionX, stickyPositionY)
                }
                header?.visibility = View.VISIBLE
                headerShadow?.visibility = View.VISIBLE
                mIsHeaderSticky = true
            }
        } else {
            if (mIsHeaderSticky) {
                header?.visibility = View.GONE
                headerShadow?.visibility = View.GONE
                mIsHeaderSticky = false
                if(stickyHorizonScrollView != null){
                    val stickyPositionX = stickyHorizonScrollView!!.scrollX
                    val stickyPositionY = stickyHorizonScrollView!!.scrollY
                    originHorizonScrollView?.scrollTo(stickyPositionX, stickyPositionY)
                }
            }
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        mHeaderInitPosition = position?.top?.toFloat() ?: 0f
    }

}