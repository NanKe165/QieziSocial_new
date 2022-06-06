package com.eggplant.qiezisocial.model

import android.util.Log
import com.eggplant.qiezisocial.contract.PubTxtContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import java.io.File

/**
 * Created by Administrator on 2020/6/3.
 */
class PubTxtModel : PubTxtContract.Model {
    override fun pubQuestion(tag: Any,scens:String,role:String, txt: String, font: String, file: List<String>, sid: String,callback: DialogCallback<BaseEntry<BoxEntry>>) {
        pubQuestion(tag, scens,role,txt,font, file, "no", 1,sid, callback)
    }

    /**
     *  destory: yes/ no
     *  model 1--private 2--group
     */
    override fun pubQuestion(tag: Any,scenes:String,role:String, txt: String, font: String, file: List<String>, destory: String, model: Int, sid:String,callback: JsonCallback<BaseEntry<BoxEntry>>) {
        var request = OkGo.post<BaseEntry<BoxEntry>>(API.PUB_QUESTION)
                .tag(tag)
                .params("w", txt)
                .params("f", font)
                .params("d", destory)
                .params("a", model)
                .params("s",scenes)
                .params("r",role)
                .params("p",0)
                .params("sid",sid)
        file?.forEach {
            request.params("file[]", File(it))
        }
        request.execute(callback)
    }

    fun pubQuestion(tag: Any,scenes:String,role:String, txt: String, font: String, file: List<String>, destory: String, model: Int, broadCast:Int,sid:String,callback: JsonCallback<BaseEntry<BoxEntry>>){
        var request = OkGo.post<BaseEntry<BoxEntry>>(API.PUB_QUESTION)
                .tag(tag)
                .params("w", txt)
                .params("f", font)
                .params("d", destory)
                .params("a", model)
                .params("s",scenes)
                .params("r",role)
                .params("p",broadCast)
                .params("sid",sid)
        file?.forEach {
            request.params("file[]", File(it))
        }
        request.execute(callback)


    }
    override fun pubPrivateQs(context: String, font: String, file:ArrayList<String>,uid: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        var request =   OkGo.post<BaseEntry<*>>(API.PUB_PRIVATE_QS)
                .params("w",context)
                .params("u",uid)
                .params("f",font)
        file.forEach {
            request.params("file[]", File(it))
        }
        request.execute(jsonCallback)
    }
}