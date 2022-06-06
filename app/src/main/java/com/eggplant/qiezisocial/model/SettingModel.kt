package com.eggplant.qiezisocial.model

import android.content.Context
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo

/**
 * Created by Administrator on 2020/6/11.
 */
object SettingModel {
    fun checkVersion(context: Context,callback:JsonCallback<BaseEntry<*>>){
        OkGo.post<BaseEntry<*>>(API.CHECK_VERSION)
                .tag(context)
                .params("os","android")
                .execute(callback)

    }
}