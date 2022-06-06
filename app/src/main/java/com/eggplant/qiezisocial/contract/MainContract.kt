package com.eggplant.qiezisocial.contract

import android.content.Context
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback

/**
 * Created by Administrator on 2020/4/14.
 */

interface MainContract{
    interface Model{
        fun getMsgUUID(tag: Any)
        fun getFilterData(jsonCallback: JsonCallback<BaseEntry<FilterEntry>>)
        fun setFilterData(goal: String?, sid: String,people: String?, jsonCallback: JsonCallback<BaseEntry<FilterEntry>>)

    }
    interface View :BaseView{
        fun setMsgNum(msgSum: Int)
        fun setFilterData(filter: FilterEntry)
        fun setApplyListNum(applyNum: Int)
        fun setFilterIsCollect(fav: Boolean)

    }
    interface Presenter{
        /**
         * 开启websocket通信服务
         */
        fun startWebService(context: Context)

        fun getFilterData()
        fun setFilter(data: FilterEntry)

    }
}
