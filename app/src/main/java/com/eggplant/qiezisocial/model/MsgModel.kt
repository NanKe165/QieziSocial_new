package com.eggplant.qiezisocial.model

import android.app.Activity
import com.eggplant.qiezisocial.contract.MsgContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo

/**
 * Created by Administrator on 2020/4/14.
 */

class MsgModel:MsgContract.Model{
    override fun getUserInfo(activity: Activity, uid: Int, callback: JsonCallback<BaseEntry<UserEntry>>) {
        OkGo.post<BaseEntry<UserEntry>>(API.GET_USERINFO)
                .tag(activity)
                .params("uid", uid)
                .execute(callback)
    }

}
