package com.eggplant.qiezisocial.model

import android.text.TextUtils

import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.contract.MainContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2020/4/14.
 */

class MainModel : MainContract.Model {
    override fun getMsgUUID(tag: Any) {
        OkGo.post<String>(API.GET_ID)
                .tag(tag)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        if (response.isSuccessful) {
                            try {
                                val `object` = JSONObject(response.body())
                                val stat = `object`.getString("stat")
                                if (TextUtils.equals(stat, "ok")) {
                                    QzApplication.get().msgUUID = `object`.getString("id")
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }
                })
    }

    override fun setFilterData(goal: String?,sid:String, people: String?,jsonCallback: JsonCallback<BaseEntry<FilterEntry>>) {
        OkGo.post<BaseEntry<FilterEntry>>(API.SET_FILTER)
                .params("goal",goal)
                .params("people",people)
                .params("sid",sid)
                .execute(jsonCallback)
    }
    override fun getFilterData(jsonCallback: JsonCallback<BaseEntry<FilterEntry>>) {
        OkGo.post<BaseEntry<FilterEntry>>(API.GET_FILTER)
                .execute(jsonCallback)
    }
}
