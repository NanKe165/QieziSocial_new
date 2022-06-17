package com.eggplant.qiezisocial.model

import android.app.Activity
import com.eggplant.qiezisocial.contract.HomeContract
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response

/**
 * Created by Administrator on 2020/4/14.
 */

class HomeModel : HomeContract.Model {
    override fun getFilterType(activity: Activity, sid: String, jsonCallback: JsonCallback<BaseEntry<ScenesEntry>>) {
        OkGo.post<BaseEntry<ScenesEntry>>(API.GET_SCENE)
                .params("id",sid)
                .execute(jsonCallback)
    }
    override fun getRedPacket(sid: String, jsonCallback: JsonCallback<BaseEntry<RedPacketEntry>>) {
        OkGo.post<BaseEntry<RedPacketEntry>>(API.GET_RED_PACKET)
                .params("sid",sid)
                .execute(jsonCallback)
    }
    override fun visitQuestion(id: Int) {
        OkGo.post<String>(API.VISIT_QUESTION)
                .params("id",id)
                .execute(object : StringCallback(){
                    override fun onSuccess(response: Response<String>?) {

                    }
                })
    }

    override fun collectScene(currentSid: String, i: Boolean, jsonCallback: JsonCallback<BaseEntry<*>>) {
        val params=OkGo.post<BaseEntry<*>>(API.COLLECT_SCENE)
                .params("sid",currentSid)
        if (!i){
            params.params("o","add")
        }else{
            params.params("o","del")
        }
        params.execute(jsonCallback)
    }
    override fun getTopic(any: Any,scens:String,sid:String,jsonCallback: JsonCallback<TopicEntry>) {
        OkGo.post<TopicEntry>(API.GET_CURRENT_TOPIC)
                .tag(any)
                .params("sid",sid)
                .params("s",scens)
                .execute(jsonCallback)
    }

    override fun costChance(jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.COST_CHANCE)
                .execute(jsonCallback)
    }

    override fun getBoxInfo(tag: Any, begin: Int, sid:String,callback: JsonCallback<BaseEntry<BoxEntry>>) {
        OkGo.post<BaseEntry<BoxEntry>>(API.GET_BOX)
                .tag(tag)
                .params("b", begin)
                .params("sid",sid)
                .execute(callback)
    }

    fun getBoxInfo(begin: Int, scens:String,sid: String,callback: JsonCallback<BaseEntry<BoxEntry>>) {
       val params= OkGo.post<BaseEntry<BoxEntry>>(API.GET_BOX)
                .params("b", begin)
                .params("s",scens)
                .params("sid",sid)
        params.execute(callback)
    }

    override fun getQuestion(tag: Any, callback: JsonCallback<BaseEntry<SysQuestionEntry>>) {
        OkGo.post<BaseEntry<SysQuestionEntry>>(API.GET_QUESTION)
                .tag(tag)
                .execute(callback)
    }

    override fun pubQuestion(tag: Any, txt: String, font: String,  sid:String,callback: DialogCallback<BaseEntry<BoxEntry>>) {
        var request = OkGo.post<BaseEntry<BoxEntry>>(API.PUB_QUESTION)
                .tag(tag)
                .params("sid",sid)
                .params("w", txt)
                .params("f", font)
        request.execute(callback)
    }

    fun getMyQuestion(begin: Int, callback: JsonCallback<BaseEntry<BoxEntry>>) {
        OkGo.post<BaseEntry<BoxEntry>>(API.GET_MY_BOX)
                .params("b", begin)
                .execute(callback)
    }

    /**
     * 加入群聊
     */
    override fun joinGroup(id: Int, callback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.JOIN_GROUP)
                .params("id", id)
                .execute(callback)
    }




}
