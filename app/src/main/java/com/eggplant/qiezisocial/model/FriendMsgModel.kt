package com.eggplant.qiezisocial.model

import com.eggplant.qiezisocial.contract.FriendMsgContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo

/**
 * Created by Administrator on 2021/7/5.
 */

class FriendMsgModel:FriendMsgContract.Model{
    override fun deleteUser(uid: Long, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.REMOVE_FRIEND)
                .params("u",uid)
                .execute(jsonCallback)
    }

    override fun getNearByInfo(lat: Double, lng: Double, jsonCallback: JsonCallback<BaseEntry<UserEntry>>) {
        OkGo.post<BaseEntry<UserEntry>>(API.GET_NEARBY_USER)
                .params("latitude",lat)
                .params("longitude",lng)
                .execute(jsonCallback)
    }

}
