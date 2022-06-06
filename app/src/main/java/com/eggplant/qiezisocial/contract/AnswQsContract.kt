package com.eggplant.qiezisocial.contract

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.MediaEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.callback.StringCallback

/**
 * Created by Administrator on 2020/4/24.
 */

interface AnswQsContract{
    interface Model {
        fun pubAnswer(tag: Any, id: Int, txt: String, path:String,callback: JsonCallback<BaseEntry<BoxEntry>>)
        fun setReadQuestion(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun getId(activity: Activity)
        fun uploadMedia(activity: Activity, s: String, media: ArrayList<String>, stringCallback: StringCallback)
    }

    interface View:BaseView {
        fun setAudioDura(dura: Int)
        fun setAudioPath(filePath: String)
        fun setHead(s: String)
        fun setSex(sex: String?)
        fun setNick(nick: String?)
        fun setTitle(text: String?)
        fun setMedia(media: List<MediaEntry>?)
        fun showTost(message: String?)
        fun onReplySuccess(boxEntry: BoxEntry?)
        fun setFont(font: String?)
        fun setHint(s: String)
        fun sendSocketMessage(sendMsg: String): Boolean?
        fun setState(mood: String)
    }

    interface Presenter {
        fun setRead(id: Int)
        fun send(activity: AppCompatActivity, bean: BoxEntry, mainBean: MainInfoBean, reply: String, audioPath: String, dura: Int)
    }
}
