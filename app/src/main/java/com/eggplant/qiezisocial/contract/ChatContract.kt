package com.eggplant.qiezisocial.contract

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback

/**
 * Created by Administrator on 2020/4/16.
 */

interface ChatContract
{
    interface Model {
        fun refuseAdd(activity: AppCompatActivity, uid: Int,jsonCallback: JsonCallback<BaseEntry<*>>)
        fun gChatAddBlockList(from: Long, jsonCallback: JsonCallback<BaseEntry<*>>)
    }

    interface View:BaseView {
        /**
         * 更新条目
         */
        fun notifyAdapterItemChanged(position: Int)
        /**
         * 发送消息
         */
        fun sendSocketMessage(sendMsg: String): Boolean


        fun scrollToBottom()
        fun finishActivity()
        fun setBarTitle(remark: String?)
        fun setBarRightTxt(s: String)
        fun getAdapterDataSize(): Int
        fun showNewMsg(msgNum: Int)
        fun refreshItem(onNewIntent: Boolean, data:List<ChatMultiEntry<ChatEntry>>)
        fun addItem(chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>)
        fun addItem(position :Int,chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>)
        fun Toast(s: String)
        fun setEmojiView(extra: String)
        fun setMyHead(myface: String?)
        fun setOtherFace(face: String?)
    }

    interface Presenter {
        fun deleteChat(mContext: Context, bean: ChatEntry)
        fun createQsTitle(qsTxt: String):ChatMultiEntry<ChatEntry>
        fun toOtherActivity(activity: AppCompatActivity, uid: Long)
    }
}
