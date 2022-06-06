package com.eggplant.qiezisocial.model

import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response

/**
 * Created by Administrator on 2021/1/5.
 */

class NearByModel {
    fun getNearByInfo(longitude: String, latitude: String, callback: JsonCallback<BaseEntry<UserEntry>>) {
        OkGo.post<BaseEntry<UserEntry>>(API.GET_NEARBY_INFO)
                .params("longitude", longitude)
                .params("latitude", latitude)
                .execute(callback)
    }

    fun setLocaltion(longitude: String, latitude: String) {
        OkGo.post<BaseEntry<UserEntry>>(API.GET_NEARBY_INFO)
                .params("longitude", longitude)
                .params("latitude", latitude)
                .execute(object :JsonCallback<BaseEntry<UserEntry>>(){
                    override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {

                    }
                })
    }
}
