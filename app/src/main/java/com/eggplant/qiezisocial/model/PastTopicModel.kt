package com.eggplant.qiezisocial.model

import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.PChatEntry
import com.eggplant.qiezisocial.entry.PastTopicEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo

/**
 * Created by Administrator on 2021/4/27.
 */

object PastTopicModel {
    fun getPastTopic(begin: Int, jsonCallback: JsonCallback<BaseEntry<PastTopicEntry>>) {
        OkGo.post<BaseEntry<PastTopicEntry>>(API.GET_PAST_TOPIC)
                .params("b", begin)
                .execute(jsonCallback)

    }

    fun getGroupChat(begin: Int, gid: Int, jsonCallback: JsonCallback<BaseEntry<PChatEntry>>) {
        OkGo.post<BaseEntry<PChatEntry>>(API.GET_GROUP_CHAT)
                .params("b", begin)
                .params("gid", gid)
                .execute(jsonCallback)
    }
}
