package com.eggplant.qiezisocial.ui.main

import android.content.Context
import android.support.v7.widget.LinearSmoothScroller

/**
 * Created by Administrator on 2021/5/24.
 */

class TopSmoothScroller internal constructor(context: Context) : LinearSmoothScroller(context) {
    var mContext=context
    private var MILLISECONDS_PER_INCH = 0.03f
    override fun getHorizontalSnapPreference(): Int {
        return LinearSmoothScroller.SNAP_TO_START//具体见源码注释
    }

    override fun getVerticalSnapPreference(): Int {
        return LinearSmoothScroller.SNAP_TO_START//具体见源码注释
    }

    override fun calculateTimeForScrolling(dx: Int): Int {
        return super.calculateTimeForScrolling(dx)
    }

    fun setSpeedSlow() {
        //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
        //0.3f是自己估摸的一个值，可以根据不同需求自己修改
        MILLISECONDS_PER_INCH = mContext.resources.displayMetrics.density * 0.8f
    }

    fun setSpeedFast() {
        MILLISECONDS_PER_INCH = mContext.resources.displayMetrics.density * 0.03f
    }
}
