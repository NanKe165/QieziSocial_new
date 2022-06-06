package com.eggplant.qiezisocial.model

import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.MxEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo

/**
 * Created by Administrator on 2022/1/27.
 */

object JindouModel{
    fun  getMx(begin:Int,callback: JsonCallback<BaseEntry<MxEntry>>){
        OkGo.post<BaseEntry<MxEntry>>(API.GET_MX)
                .params("b",begin)
                .execute(callback)
    }

}
