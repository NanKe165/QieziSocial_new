package com.eggplant.qiezisocial.model

import com.eggplant.qiezisocial.contract.FriendListContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BlackListEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo

/**
 * Created by Administrator on 2020/4/14.
 */
object FriendListModel : FriendListContract.Model {
    /**
     * 同意添加好友
     */
    fun agreeAdd(tag: Any, uid: Int, callback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.AGREE_ADD_FRIEND)
                .tag(tag)
                .params("u", uid)
                .execute(callback)
    }
    /**
     * 添加黑名单
     */
    fun addBlack(tag: Any,uid: Int,callback: JsonCallback<BaseEntry<*>>){
        OkGo.post<BaseEntry<*>>(API.ADD_BLACK)
                .tag(tag)
                .params("u",uid)
                .execute(callback)
    }
    /**
     * 黑名单列表
     */
    fun getBlackList(tag: Any,begin:Int,callback: JsonCallback<BaseEntry<BlackListEntry>>){
        OkGo.post<BaseEntry<BlackListEntry>>(API.BLACK_LIST)
                .tag(tag)
                .params("b",begin)
                .execute(callback)
    }
}