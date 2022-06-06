package com.eggplant.qiezisocial.contract

import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.GuessFateEntry

/**
 * Created by Administrator on 2020/7/31.
 */

interface GuessFateContract{
    interface View:BaseView {
        fun setNewData(list: List<GuessFateEntry>?)
        fun addData(it: List<GuessFateEntry>)
    }

    interface Model
    interface Presenter {
        fun initData()
        fun loadMore(size: Int)
    }
}
