package com.eggplant.qiezisocial.contract

import android.app.Activity
import android.content.Context
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.MsgMultiltem
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.callback.JsonCallback

/**
 * Created by Administrator on 2020/4/14.
 */

interface MsgContract{
    interface Model{
        fun getUserInfo(activity: Activity, uid: Int, callback: JsonCallback<BaseEntry<UserEntry>>)

    }
    interface View :BaseView{
        fun setAdapterData(data: ArrayList<MainInfoBean>)
        fun setTitleSize(friendmsgSize: Int)
        fun setTitle(title: String)
        fun setAdapterData2(data: ArrayList<MsgMultiltem<MainInfoBean>>)
        fun setFriendData(friendList: ArrayList<MainInfoBean>)
        fun setApplyData(applyList: ArrayList<MainInfoBean>)
        fun setSystemData(hasSystemBean: Boolean, systemNumb: Int)

    }
    interface Presenter{
        fun deleteChat(context:Context,it: MainInfoBean)
        fun toOtherActivity(activity: Activity, uid: Long)

    }
}
