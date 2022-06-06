package com.eggplant.qiezisocial.presenter

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.AnswQsContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.MediaEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.AnswQsModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.socket.event.NewMsgEvent
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.utils.mp3.Mp3RecorderUtils
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2020/4/24.
 */
class AnswQsPresenter : BasePresenter<AnswQsContract.View>(), AnswQsContract.Presenter {
    var model = AnswQsModel()
    fun initData(bean: BoxEntry) {
        mView?.setHead(API.PIC_PREFIX + bean.userinfor.face)
        mView?.setSex(bean.userinfor.sex)
        mView?.setNick(bean.userinfor.nick)
        mView?.setTitle(bean.text)
        mView?.setMedia(bean.media)
        mView?.setFont(bean.font)
        mView?.setHint("回复${bean.userinfor.nick}")
        mView?.setState(bean.userinfor.mood)

    }

    override fun setRead(id: Int) {
        model.setReadQuestion(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {


            }
        })
    }

    fun startRecord(activity: Activity) {
        Mp3RecorderUtils.startRecording(activity, object : Mp3RecorderUtils.RecorderCallback {
            override fun onStop(filePath: String, duraion: Double) {
                var dura = (duraion / 1000).toInt()
                mView?.setAudioDura(dura)
                mView?.setAudioPath(filePath)
            }

            override fun onReset() {

            }

            override fun onStart() {

            }

            override fun onRecording(volume: Double) {

            }
        })
    }


    override fun send(activity: AppCompatActivity, bean: BoxEntry, mainBean: MainInfoBean, reply: String, audioPath: String, dura: Int) {
        var isSuccess = false
        val id = QzApplication.get().msgUUID
        val myid = QzApplication.get().infoBean!!.uid
        val myface = QzApplication.get().infoBean!!.face
        QzApplication.get().msgUUID = "0"
        getMsgId(activity)
        val chatEntry = ChatEntry()
        chatEntry.layout = ""
        chatEntry.face = myface
        chatEntry.created = System.currentTimeMillis().toString()
        chatEntry.from = myid.toLong()
        if (!TextUtils.equals(id, "0")) {
            chatEntry.msgId = id
        } else {
            chatEntry.msgId = System.currentTimeMillis().toString() + ""
        }
        chatEntry.chatId = bean.userinfor.uid.toLong()
        chatEntry.to = bean.userinfor.uid.toLong()
        chatEntry.userId = myid.toLong()
        chatEntry.qsid = mainBean.qsid
        chatEntry.gsid = mainBean.gsid
        chatEntry.isMsgRead = true
//        bean.media?.forEachIndexed { index, mediaEntry ->
//            when(index){
//                0->{
//                    chatEntry.question1="${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}"
//                }
//                1->{
//                    chatEntry.question2="${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}"
//                }
//                2->{
//                    chatEntry.question3="${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}"
//                }
//            }
//        }

        if (myid != 0) {
            if (!TextUtils.isEmpty(reply)) {
                chatEntry.type = "gtxt"
                chatEntry.content = reply
                mainBean.msg = reply
                mainBean.msgType = "gtxt"
                isSuccess = mView?.sendSocketMessage(getSendMsg("gtxt", id, myid.toString(), bean.userinfor.uid.toString(), reply, "", mainBean, bean.media))!!
                if (!isSuccess) {
                    chatEntry.msgStat = 1
                }
                val b = ChatDBManager.getInstance(activity).insertUser(chatEntry)
                if (b) {
                    mainBean.created = System.currentTimeMillis()
                    mainBean.newMsgTime = System.currentTimeMillis()
                    mainBean.`object` = ""
                    MainDBManager.getInstance(activity).updateUser(mainBean)
                    EventBus.getDefault().post(NewMsgEvent())
                    TipsUtil.showToast(activity, "消息已发送")
                }
                mView?.onReplySuccess(bean)

            } else {
                chatEntry.type = "gaudio"
                chatEntry.content = audioPath
                chatEntry.extra = dura.toString()
                mainBean.msg = "[语音]"
                mainBean.msgType = "gaudio"
                uploadPicOrAudioMedia("gaudio", activity, arrayListOf<String>(audioPath), chatEntry, myid, bean.userinfor.uid, mainBean)
//                mView?.sendSocketMessage(getSendMsg("gaudio", id, myid.toString(), bean.userinfor.uid.toString(), reply, "", mainBean))!!
            }
        }


    }


    /**
     * 上传图片 或 音频文件
     */
    fun uploadPicOrAudioMedia(type: String, activity: Activity, media: ArrayList<String>, bean: ChatEntry, myid: Int, uid: Int, mainBean: MainInfoBean) {
        model.uploadMedia(activity, "", media, object : StringCallback() {
            override fun onSuccess(response: Response<String>?) {
                if (response!!.isSuccessful) {
                    paresResult(activity, type, response.body(), bean, myid, uid, mainBean)
                } else {
                    TipsUtil.showToast(activity, response.code().toString() + "")
                }
            }

            override fun onError(response: Response<String>?) {
                super.onError(response)
            }
        })
    }

    /**
     * 解析后台返回数据并发送新消息
     * （last step  将音视频或图片上传至服务器）
     *
     * @param type
     * @param result
     * @param entry
     * @param position
     */
    fun paresResult(activity: Activity, type: String, result: String, entry: ChatEntry, myid: Int, uid: Int, mainBean: MainInfoBean) {
        var `object`: JSONObject? = null
        try {
            `object` = JSONObject(result)
            val stat = `object`.getString("stat")
//            val msg = `object`.getString("msg")
            if (TextUtils.equals("ok", stat)) {
                val attr = `object`.getJSONArray("att")
                val id = QzApplication.get().msgUUID
                QzApplication.get().msgUUID = "0"
                model.getId(activity)
                if (attr != null && attr.length() > 0) {
                    if (attr.length() == 1) {
                        val jsonObject = attr.getJSONObject(0)
                        val file = jsonObject.getString("file")
                        val extra = jsonObject.getString("extra")

                        var isSuccess = false
                        if (myid != 0 && uid != 0) {
                            isSuccess = if (TextUtils.equals(type, "gpic")) {
                                mView?.sendSocketMessage(getSendMsg(type, id, myid.toString(), uid.toString(), file, entry.extra!!, mainBean))!!
                            } else {
                                mView?.sendSocketMessage(getSendMsg(type, id, myid.toString(), uid.toString(), file, extra, mainBean))!!
                            }
                            if (isSuccess) {
                                entry.msgStat = 0
                                if (!TextUtils.equals(id, "0"))
                                    entry.msgId = id
                            } else {
                                entry.msgStat = 1
                            }
                        } else {
                            entry.msgStat = 1
                        }
                    }
                    if (attr.length() == 2) {
                        val jsonObject = attr.getJSONObject(0)
                        val file = jsonObject.getString("file")
                        val imgObj = attr.getJSONObject(1)
                        val img = imgObj.getString("file")
                        val split = entry.extra!!.split("&&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        var dura = "0"
                        if (split.size == 2) {
                            dura = split[1]
                        }
                        var isSuccess: Boolean
                        if (myid != 0 && uid != 0) {
                            isSuccess = mView?.sendSocketMessage(getSendMsg(type, id, myid.toString(), uid.toString(), file, img + "&&" + dura, mainBean))!!
                            if (isSuccess) {
                                entry.msgStat = 0
                                if (!TextUtils.equals(id, "0"))
                                    entry.msgId = id
                            } else {
                                entry.msgStat = 1
                            }
                        } else {
                            entry.msgStat = 1
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val b = ChatDBManager.getInstance(activity).insertUser(entry)
        if (b) {
            mainBean.created = System.currentTimeMillis()
            mainBean.newMsgTime = System.currentTimeMillis()
            mainBean.`object` = ""
            MainDBManager.getInstance(activity).updateUser(mainBean)
            EventBus.getDefault().post(NewMsgEvent())
            TipsUtil.showToast(activity, "消息已发送")
        }
        mView?.onReplySuccess(null)

    }

    fun getSendMsg(act: String, id: String, from: String, to: String, content: String, extra: String, mainBean: MainInfoBean): String {
        return getSendMsg(act, id, from, to, content, extra, mainBean, null)
    }

    private fun getSendMsg(act: String, id: String, from: String, to: String, content: String, extra: String, mainBean: MainInfoBean, media: List<MediaEntry>?): String {
        val `object` = JSONObject()
        `object`.put("type", "message")
        `object`.put("act", act)
        `object`.put("created", System.currentTimeMillis())
        `object`.put("range", "private")
        `object`.put("from", from)
        `object`.put("to", to)
        `object`.put("id", id)
        if (mainBean.gsid != 0L) {
            `object`.put("gsid", mainBean.gsid)
        } else if (mainBean.qsid != 0L) {
            `object`.put("qsid", mainBean.qsid)
        }
        `object`.put("qs_uid", mainBean.qsuid)
        `object`.put("qstxt", mainBean.qsTxt)
        `object`.put("qs_nick", mainBean.qsNick)
        `object`.put("qs_user_face", mainBean.qsUserFace)
        media?.forEachIndexed { index, mediaEntry ->
            when (index) {
                0 -> {
                    `object`.put("qs_media1", "${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}")
                }
                1 -> {
                    `object`.put("qs_media2", "${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}")
                }
                2 -> {
                    `object`.put("qs_media3", "${API.PIC_PREFIX}${mediaEntry.org}&&${mediaEntry.type}")
                }
            }
        }
        val data = JSONObject()
        data.put("created", System.currentTimeMillis())
        data.put("expire", System.currentTimeMillis())
        data.put("layout", "")
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
        }
        data.put("content", conXml.toString())

        `object`.put("data", data)
        Log.i("answpre","data: $`object`")
        return `object`.toString()
    }


    /**
     * 获取一次msg UUID
     */
    fun getMsgId(activity: Activity) {
        model.getId(activity)
    }

    fun reply(context: Context, bean: BoxEntry, txt: String, path: String, dura: Int) {
        model.pubAnswer(context, bean.id, txt, path, object : JsonCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        mView?.showTost(response.body().msg)

                        if (TextUtils.equals(response.body().stat, "ok")) {
                            response.body().record?.let {
                                bean.answertime = System.currentTimeMillis().toInt()
                                if (!TextUtils.isEmpty(txt)) {
                                    createTxtChatEntry(context, it, bean)
                                } else {
                                    createAudioChatEntry(context, it, bean, dura)
                                }
                            }
                            mView?.onReplySuccess(bean)
                        }
                    }
                }
            }

        })
    }

    private fun createTxtChatEntry(context: Context, answBean: BoxEntry, qsBean: BoxEntry) {
        createQsBean(context, answBean, qsBean)
        var answEntry = ChatEntry()
        answEntry.msgId = System.currentTimeMillis().toString()
        answEntry.type = "boxanswer"
        answEntry.chatId = qsBean.uid.toLong()
        answEntry.from = answBean.uid.toLong()
        answEntry.to = qsBean.uid.toLong()
        answEntry.userId = answBean.uid.toLong()
        answEntry.face = answBean.userinfor.face
        answEntry.id = System.currentTimeMillis()
        answEntry.created = System.currentTimeMillis().toString()
        answEntry.content = answBean.text
        ChatDBManager.getInstance(context).insertUser(answEntry)
        createMainUser(context, answBean, qsBean)
    }

    private fun createAudioChatEntry(context: Context, answBean: BoxEntry, qsBean: BoxEntry, dura: Int) {
        createQsBean(context, answBean, qsBean)
        var answEntry = ChatEntry()
        answEntry.msgId = System.currentTimeMillis().toString()
        answEntry.type = "gaudio"
        answEntry.chatId = qsBean.uid.toLong()
        answEntry.from = answBean.uid.toLong()
        answEntry.to = qsBean.uid.toLong()
        answEntry.userId = answBean.uid.toLong()
        answEntry.face = answBean.userinfor.face
        answEntry.id = System.currentTimeMillis()
        answEntry.created = System.currentTimeMillis().toString()
        answBean.media.forEach {
            if (it.type == "audio") {
                answEntry.content = API.PIC_PREFIX + it.org
            }
        }
        answEntry.extra = dura.toString()
        ChatDBManager.getInstance(context).insertUser(answEntry)
        answBean.text = "[语音]"
        createMainUser(context, answBean, qsBean)
    }

    private fun createQsBean(context: Context, answBean: BoxEntry, qsBean: BoxEntry) {
        var qsEntry = ChatEntry()
        qsEntry.msgId = (System.currentTimeMillis() - 100).toString()
        qsEntry.type = "boxquestion"
        qsEntry.chatId = qsBean.uid.toLong()
        qsEntry.from = qsBean.uid.toLong()
        qsEntry.to = answBean.uid.toLong()
        qsEntry.userId = answBean.uid.toLong()
        qsEntry.face = qsBean.userinfor.face
        qsEntry.id = System.currentTimeMillis() - 10
        qsEntry.created = (System.currentTimeMillis() - 100).toString()
        qsEntry.content = qsBean.text
        qsBean.media.forEachIndexed { index, mediaEntry ->
            when (index) {
                0 -> qsEntry.question1 = API.PIC_PREFIX + mediaEntry.org + "&&" + mediaEntry.type
                1 -> qsEntry.question2 = API.PIC_PREFIX + mediaEntry.org + "&&" + mediaEntry.type
                2 -> qsEntry.question3 = API.PIC_PREFIX + mediaEntry.org + "&&" + mediaEntry.type
                3 -> qsEntry.extra = API.PIC_PREFIX + mediaEntry.org + "&&" + mediaEntry.type
            }
        }
        ChatDBManager.getInstance(context).insertUser(qsEntry)
    }


    private fun createMainUser(context: Context, answBean: BoxEntry, qsBean: BoxEntry) {
        val queryMainUser = MainDBManager.getInstance(context).queryMainUser(qsBean.uid)
        if (queryMainUser == null) {
            val bean = MainInfoBean()
            bean.uid = qsBean.userinfor.uid.toLong()
            bean.sex = qsBean.userinfor.sex
            bean.nick = qsBean.userinfor.nick
            bean.birth = qsBean.userinfor.birth
            bean.card = qsBean.userinfor.card
            bean.careers = qsBean.userinfor.careers
            bean.city = qsBean.userinfor.city
            bean.height = qsBean.userinfor.height
            bean.weight = qsBean.userinfor.weight
            bean.edu = qsBean.userinfor.edu
            bean.xz = qsBean.userinfor.xz
            bean.account = qsBean.userinfor.card
            bean.pic = qsBean.userinfor.pic
            bean.face = qsBean.userinfor.face
            bean.type = "temporal"
            bean.created = System.currentTimeMillis()
            bean.userId = answBean.uid.toLong()
            bean.`object` = qsBean.text
            bean.msg = answBean.text
            bean.msgType = "boxanswer"
            MainDBManager.getInstance(context).insertUser(bean)
        } else {
            queryMainUser.`object` = qsBean.text
            queryMainUser.msg = answBean.text
            queryMainUser.msgType = "boxanswer"
            MainDBManager.getInstance(context).updateUser(queryMainUser)
        }
        EventBus.getDefault().post(NewMsgEvent())
    }


}