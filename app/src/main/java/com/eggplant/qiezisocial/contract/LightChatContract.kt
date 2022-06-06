package com.eggplant.qiezisocial.contract

import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry

/**
 * Created by Administrator on 2021/4/14.
 */

interface LightChatContract{
    interface Model
    interface View:BaseView {
        fun setImg(img: String)
        fun addItem(dataOne: ChatMultiEntry<ChatEntry>)
        fun sendSocketMessage(msg: String): Boolean
        fun setNextData()
        fun setVideo(src: String, dura: String, poster: String)
        fun setMulMediaVisiable(b: Boolean)
        fun setEmptyMedia()

    }

    interface Presenter {
        fun contentData(data: ArrayList<ChatEntry>)
        fun sendLittleTxt(activity: AppCompatActivity, sendTxt: String, currentChatId: Long, lastTxt: String,img: String)
        fun sendLittleTxt(activity: AppCompatActivity, sendTxt: String, lastTxt: String)
    }
}
