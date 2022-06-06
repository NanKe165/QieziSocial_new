package com.eggplant.qiezisocial.contract

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.widget.TextView
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.GchatParcelEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.widget.AuVideoTxtView

/**
 * Created by Administrator on 2021/3/31.
 */

interface GroupChatContract{
    interface Model{}
    interface View:BaseView {
        fun sendSocketMessage(sendMsg: String): Boolean
        fun addItem(chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>)
        fun addItem(position: Int,chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>)
        fun scrollToBottom()
        fun notifyAdapterItemChanged(position: Int)
        fun setLittleChatSize(size: Int)
        fun showNewMsg()

    }

    interface Presenter {
        fun sendTxt(activity: Activity, str: String, currentGid: Int)
        fun addNewMsg(entry: GchatParcelEntry)
        fun createChatEntry(filePath: String, dura: Int, currentGid: Int):ChatEntry
        fun uploadVideoMedia(type: String, activity: Activity, s: String, media: ArrayList<String>, pTv: TextView?, bean: ChatEntry, position: Int, currentGid: Int)
        fun uploadPicOrAudioMedia(type: String, activity: Activity, s: String, media: ArrayList<String>, pTv: TextView?, bean: ChatEntry, position: Int, currentGid: Int)
        fun recordVideo(ft: BaseFragment, requestCode: Int)
        fun takePhoto(ft: BaseFragment, requestCode: Int)
        fun openGallery(ft: BaseFragment, requestCode: Int)
        fun addVideo(mContext: Context, path: String?, currentGid: Int):ChatEntry
        fun addImg(mContext: Context, path: String?, currentGid: Int):ChatEntry
        fun toOtherActivity(activity: Activity, uid: Long)
        fun sendLittleTxt(activity: Activity, to: Long, txt: String, img: String)
        fun removeLittleTxt(chatEntry: ChatEntry)
        fun littleTxtClick(activity: Activity, requestCode: Fragment, requesT_LITTLE_TXT: Int)
        fun onMediaClick(ft: Fragment, p: Int, data: List<ChatMultiEntry<ChatEntry>>, v: android.view.View, requesT_SHOW_IMG: Int)
        fun onMediaClick(ft: Fragment, data: ChatMultiEntry<ChatEntry>, v: android.view.View, requesT_SHOW_IMG: Int)
        fun sendLittleTxt(activity: Activity, to: Long, txt: String?, type: String, content: String, extra: String?)
        fun addMsg(index: Int, entry: GchatParcelEntry)
        fun onTxtClick(ft: Fragment, entry: ChatMultiEntry<ChatEntry>, v: AuVideoTxtView, childPosition: Int, requesT_SHOW_TXT: Int)
        fun saveFile(mContext: Context, downloadPath: String,type: String)
        fun addBlocklist(activity: Activity, from: Long)
        fun createMsg(note: String, currentGid: Int)


    }
}
