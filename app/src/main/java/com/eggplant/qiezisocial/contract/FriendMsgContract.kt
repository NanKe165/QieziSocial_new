package com.eggplant.qiezisocial.contract

import android.content.Context
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.callback.JsonCallback

/**
 * Created by Administrator on 2021/7/5.
 */

interface FriendMsgContract{
    interface View:BaseView {
        fun setData(arrayList: ArrayList<MainInfoBean>)
        fun setNearUser(list: List<UserEntry>)
    }

    interface Model {
        fun deleteUser(uid: Long, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun getNearByInfo(lat: Double, lng: Double, jsonCallback: JsonCallback<BaseEntry<UserEntry>>)
    }

    interface Presenter {
        fun setData(queryMainUserList: List<MainInfoBean>?)
        fun deleteChat(mContext: Context, it: MainInfoBean)
        fun deleteUser(mContext: Context, uid: MainInfoBean)
        fun addBlacklist(mContext: Context, it: MainInfoBean)
        fun getNearbyInfo(mContext: Context,lat: Double, lng: Double)
    }
}
