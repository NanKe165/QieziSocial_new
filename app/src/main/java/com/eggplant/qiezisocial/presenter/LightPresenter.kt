package com.eggplant.qiezisocial.presenter

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.LightChatContract
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.model.ChatModel
import com.eggplant.qiezisocial.utils.xml.XmlUtils
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2021/4/14.
 */

class LightPresenter : BasePresenter<LightChatContract.View>(), LightChatContract.Presenter {
    var model: ChatModel = ChatModel()
    var currentChatEntry: ChatEntry? = null
    override fun contentData(data: ArrayList<ChatEntry>) {
        val infoBean = QzApplication.get().infoBean!!
        val chatEntry = data[0]
        currentChatEntry = chatEntry
        chatEntry.type = "gtxt"
        val layout = chatEntry.layout
        val layoutType = chatEntry.layoutType
        if (layoutType == "gpic" && !TextUtils.isEmpty(layout)) {
            val picByXmlStr = XmlUtils.getPicByXmlStr(layout)
            mView?.setImg(picByXmlStr.src)
        } else if (layoutType == "gvideo" && !TextUtils.isEmpty(layout)) {
            val movieByXmlstr = XmlUtils.getMovieByXmlstr(layout)
            mView?.setVideo(movieByXmlstr.src, movieByXmlstr.dura, movieByXmlstr.poster)
        }else if (layoutType==null||layout.isEmpty()){
            mView?.setEmptyMedia()
        }



        if (!TextUtils.isEmpty(chatEntry.extra)) {
            val chatEntryOne = chatEntry.copyEntry()
            chatEntryOne.type = "gtxt"
            chatEntryOne.content = chatEntry.extra
            chatEntryOne.sex = infoBean.sex
            chatEntryOne.nick = infoBean.nick
            chatEntryOne.face = infoBean.face
            val dataOne = ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE, chatEntryOne)
            mView?.addItem(dataOne)
        }
        val bean = ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER, chatEntry)
        mView?.addItem(bean)
        if (layoutType=="gpic"||layoutType=="gvideo") {
            if (data.size > 1) {
                mView?.setMulMediaVisiable(true)
            } else {
                mView?.setMulMediaVisiable(false)
            }
        }else{
            mView?.setMulMediaVisiable(false)
        }
    }

    /**
     * @deprecated
     */
    override fun sendLittleTxt(activity: AppCompatActivity, sendTxt: String, currentChatId: Long, lastTxt: String, img: String) {
        val myid = QzApplication.get().infoBean!!.uid
        if (myid != 0) {
            var isSuccess = false
            val id = QzApplication.get().msgUUID
            QzApplication.get().msgUUID = "0"
            getMsgId(activity)
            val msg = getSendMsg("littletxt", id, myid.toString(), currentChatId, sendTxt, lastTxt, img)
            isSuccess = mView?.sendSocketMessage(msg)!!
            mView?.setNextData()
        }
    }

    override fun sendLittleTxt(activity: AppCompatActivity, sendTxt: String, lastTxt: String) {
        val myid = QzApplication.get().infoBean!!.uid
        if (myid != 0 && currentChatEntry != null) {
            val id = QzApplication.get().msgUUID
            QzApplication.get().msgUUID = "0"
            getMsgId(activity)
            val msg = getSendLittleMsg(id, myid.toString(), sendTxt, lastTxt)
            mView?.sendSocketMessage(msg)!!
            mView?.setNextData()
        }
    }

    private fun getSendLittleMsg(id: String, from: String, txt: String, lastTxt: String): String {
        val `object` = JSONObject()
        `object`.put("type", "message")
        `object`.put("act", "littletxt")
        `object`.put("created", System.currentTimeMillis())
        `object`.put("range", "private")
        `object`.put("from", from)
        `object`.put("to", currentChatEntry!!.from)
        `object`.put("id", id)
        val data = JSONObject()
        data.put("created", System.currentTimeMillis())
        data.put("expire", System.currentTimeMillis())
        data.put("content", txt)
        data.put("extra", lastTxt)
        data.put("layout_type", currentChatEntry!!.layoutType)
        data.put("layout", currentChatEntry!!.layout)
        `object`.put("data", data)
//        Log.i("groupChat", "${`object`}")
        return `object`.toString()
    }

    /**
     * 聊天消息包装
     * @param from
     * @param to
     * @param content
     * @param extra
     * @return
     */
    fun getSendMsg(act: String, id: String, from: String, to: Long, content: String, extra: String, layout: String): String {
        val `object` = JSONObject()
        try {
            `object`.put("type", "message")
            `object`.put("act", act)
            `object`.put("created", System.currentTimeMillis())
            `object`.put("range", "private")
            `object`.put("from", from)
            `object`.put("to", to)
            `object`.put("id", id)
            val data = JSONObject()
            data.put("created", System.currentTimeMillis())
            data.put("expire", System.currentTimeMillis())
            data.put("layout", layout)
            data.put("extra", extra)

            val conXml = StringBuilder()
            when {
                TextUtils.equals(act, "gtxt") -> conXml.append("<txt>")
                        .append(content)
                        .append("</txt>")
                TextUtils.equals(act, "gpic") -> {
                    val split = extra.split("&&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    var height = ""
                    var width = ""
                    if (split != null && split.size == 2) {
                        height = split[0]
                        width = split[1]

                    }
                    conXml.append("<pic src=\"")
                            .append(content)
                            .append("\"width=\"")
                            .append(width)
                            .append("\"height=\"")
                            .append(height)
                            .append("\"></pic>")
                }
                TextUtils.equals(act, "gaudio") -> {
                    val dura = extra.replace("dura".toRegex(), "")
                    conXml.append("<sound src=\"")
                            .append(content)
                            .append("\"dura=\"")
                            .append(dura)
                            .append("\"></sound>")
                }
                TextUtils.equals(act, "gvideo") -> {
                    var img = ""
                    var dura = ""
                    val split = extra.split("&&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    if (split.isNotEmpty()) {
                        img = split[0]
                        if (split.size == 2) {
                            dura = split[1]
                        }
                    }
                    conXml.append("<movie src=\"")
                            .append(content)
                            .append("\"dura=\"")
                            .append(dura)
                            .append("\"poster=\"")
                            .append(img)
                            .append("\"></movie>")

                }
                TextUtils.equals(act, "littletxt") -> {
                    conXml.append(content)
                }
            }
            data.put("content", conXml.toString())
            `object`.put("data", data)
            Log.i("groupChat", "${`object`}")
            return `object`.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 获取一次msg UUID
     */
    fun getMsgId(activity: Activity) {
        model.getId(activity)
    }

}
