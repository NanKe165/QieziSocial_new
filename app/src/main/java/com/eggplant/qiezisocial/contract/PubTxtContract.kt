package com.eggplant.qiezisocial.contract

import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.model.callback.JsonCallback

/**
 * Created by Administrator on 2020/4/22.
 */
interface PubTxtContract {
    interface Model {
        fun pubQuestion(tag: Any, scens:String,role:String,txt: String, font: String, file: List<String>, sid:String,callback: DialogCallback<BaseEntry<BoxEntry>>)
        fun pubQuestion(tag: Any, scens:String,role:String,txt: String, font: String, file: List<String>, destory: String, model: Int, sid: String,callback: JsonCallback<BaseEntry<BoxEntry>>)
        fun pubPrivateQs(context: String, font: String, file:ArrayList<String>, uid: Int, jsonCallback: JsonCallback<BaseEntry<*>>)
    }

    interface View :BaseView {
        fun showTost(msg: String?)
        fun pubQuestionSuccess(record: BoxEntry)
    }

    interface Presenter {
        fun pubPrivateQs(context: String, font: String, uid: Int, data: ArrayList<String>)
    }
}