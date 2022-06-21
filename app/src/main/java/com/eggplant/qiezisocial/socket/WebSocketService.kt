package com.eggplant.qiezisocial.socket

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.GchatParcelEntry
import com.eggplant.qiezisocial.entry.ScenesEntry
import com.eggplant.qiezisocial.event.*
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.socket.event.*
import com.eggplant.qiezisocial.ui.chat.ChatAVActivity
import com.eggplant.qiezisocial.utils.CommentUtils
import com.eggplant.qiezisocial.utils.NotifycationUtils
import com.eggplant.qiezisocial.utils.xml.XmlUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mabeijianxi.jianxiexpression.core.ExpressionTransformEngine
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2018/11/21.
 */

class WebSocketService : AbsBaseWebSocketService() {
    //ws://120.27.197.92:8282      wss://://qiezixuanshang.com
    internal var connectUrl = "wss://qiezixuanshang.com"

    override fun onCreate() {
        super.onCreate()

    }

    override fun getConnectUrl(): String {
        return connectUrl
    }

    override fun dispathResponse(textResponse: String?) {

        Log.e(TAG, "dispathResponse: " + textResponse!!)
        if (textResponse == null) {
            EventBus.getDefault().post(WebSocketSendDataErrorEvent(textResponse, "响应数据为空"))
            return
        }
        EventBus.getDefault().post(CommonResponse(textResponse))
        var result: JSONObject? = null
        val sp = getSharedPreferences("userEntry", Context.MODE_PRIVATE)
//        val edit = sp.edit()
        val token = sp.getString("token", "")
        val phone = sp.getString("phone", "")
        val face = sp.getString("face", "")
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(phone))
            return
        val userId = sp.getInt("uid", 0)

        try {
            result = JSONObject(textResponse)
            val act = result.getString("act")
            val type = result.getString("type")
            swithAction(act, result, phone, token, userId)
            createMessage(type, result, userId, face, act)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun createMessage(msgType: String, result: JSONObject, userId: Int, face: String, act: String) {
        if (TextUtils.equals("message", msgType)) {
            var needStore = true
            var mainBeanMsg = ""
            var mainBeanQues = ""
            val activityName = CommentUtils.getRunningActivityName(this)

            val data = result.getJSONObject("data")
            val frined = result.getJSONObject("from_userinfor")

            val chatEntry = ChatEntry()

            chatEntry.msgId = result.getString("id")
            chatEntry.chatId = result.getLong("from")
            chatEntry.from = result.getLong("from")
            chatEntry.to = result.getLong("to")
            chatEntry.type = act
            chatEntry.userId = userId.toLong()

            if (result.has("qsid")) {
                //处理一下后台返回的脏数据
//                Log.e(TAG, "qsid=="+(result.get("qsid").toString()))

                if (result.get("qsid").toString() == "null") {
                    val `object` = JSONObject()
                    `object`.put("type", "message")
                    `object`.put("act", "gread")
                    `object`.put("created", System.currentTimeMillis())
                    `object`.put("range", "private")
                    `object`.put("from", result.get("from"))
                    `object`.put("to", result.get("to"))
                    `object`.put("id", result.get("id"))
                    sendText(`object`.toString())
                    return
                }
                chatEntry.qsid = result.getLong("qsid")
            }
            if (result.has("gsid")) {
                chatEntry.gsid = result.getLong("gsid")
            }
            //区别于上面的gsid，此groupid为话题群聊，无需将数据储存本地
            if (result.has("groupid")) {
                chatEntry.gsid = result.getLong("groupid")
                needStore = false
            }
//            var mainUser: MainInfoBean? = null
//            mainUser = when {
//                chatEntry.qsid != 0L -> MainDBManager.getInstance(this).queryMainUserWithQsid(result.getString("from"), chatEntry.qsid)
//                chatEntry.gsid != 0L -> MainDBManager.getInstance(this).queryMainUserWithGsId(chatEntry.gsid)
//                else -> MainDBManager.getInstance(this).queryMainUser(result.getString("from"))
//            }
//            val mainUser = MainDBManager.getInstance(this).queryMainUser(result.getString("from"))
//            if (mainUser != null) {
//                chatEntry.face = mainUser.face
//            } else {
            chatEntry.face = frined.getString("face")
            chatEntry.nick = frined.getString("nick")
            chatEntry.sex = frined.getString("sex")
            if (frined.has("birth")) {
                chatEntry.birth = frined.getString("birth")
            }
            if (frined.has("weight")) {
                chatEntry.weight = frined.getString("weight")
            }
            if (frined.has("height")) {
                chatEntry.height = frined.getString("height")
            }
            if (frined.has("careers")) {
                chatEntry.careers = frined.getString("careers")
            }
            if (frined.has("edu")) {
                chatEntry.edu = frined.getString("edu")
            }
            if (frined.has("label")) {
                chatEntry.label = frined.getString("label")
            }
//            }
            chatEntry.id = System.currentTimeMillis()
            chatEntry.created = result.getString("created")

            if (data != null) {

                var content = ""
                if (!TextUtils.equals("gquestion", act) && !TextUtils.equals("gnewquestion", act) && !TextUtils.equals("boxanswer", act) && !TextUtils.equals("gsharescenes", act)) {
                    content = data.getString("content")
                }

                if (TextUtils.equals(act, "gtxt")) {
                    val txtByXmlStr = XmlUtils.getTxtByXmlStr(content)
                    if (txtByXmlStr != null) {
                        val txt = txtByXmlStr.txt
                        chatEntry.content = txt
                        if (data.has("extra"))
                            chatEntry.extra = data.getString("extra")
                    }
                } else if (TextUtils.equals(act, "gpic")) {
                    val picByXmlStr = XmlUtils.getPicByXmlStr(content)
                    if (picByXmlStr != null) {
                        val src = picByXmlStr.src
                        val height = picByXmlStr.height
                        val width = picByXmlStr.width
                        chatEntry.extra = height + "&&" + width
                        chatEntry.content = API.PIC_PREFIX + src
                    }
                } else if (TextUtils.equals(act, "gaudio")) {
                    val soundByXmlStr = XmlUtils.getSoundByXmlStr(content)
                    if (soundByXmlStr != null) {
                        chatEntry.content = API.PIC_PREFIX + soundByXmlStr.src
                        val dura = soundByXmlStr.dura.replace("dura".toRegex(), "")
                        chatEntry.extra = dura
                    }
                } else if (TextUtils.equals(act, "gvideo")) {
                    val movieByXmlstr = XmlUtils.getMovieByXmlstr(content)
                    if (movieByXmlstr != null) {
                        chatEntry.content = API.PIC_PREFIX + movieByXmlstr.src
                        chatEntry.extra = API.PIC_PREFIX + movieByXmlstr.poster + "&&" + movieByXmlstr.dura
                    }
                } else if (TextUtils.equals(act, "gquestion")) {
                    //deprecated
                    val question1 = data.getString("question1")
                    val question2 = data.getString("question2")
                    val question3 = data.getString("question3")
                    chatEntry.question1 = getAnswer(question1)
                    chatEntry.question2 = getAnswer(question2)
                    chatEntry.question3 = getAnswer(question3)
                    val answer1 = data.getString("answer1")
                    val answer2 = data.getString("answer2")
                    val answer3 = data.getString("answer3")
                    chatEntry.answer1 = getAnswer(answer1)
                    chatEntry.answer2 = getAnswer(answer2)
                    chatEntry.answer3 = getAnswer(answer3)
                } else if (TextUtils.equals(act, "gnewquestion")) {
                    //deprecated
                    val question1 = data.getString("question1")
                    val chatEntry2 = ChatEntry()
                    chatEntry2.content = getAnswer(question1)
                    chatEntry2.type = "gtxt"

                    chatEntry2.msgId = (data.getLong("created") - 100).toString() + ""
                    chatEntry2.chatId = result.getLong("from")
                    chatEntry2.from = result.getLong("to")
                    chatEntry2.to = result.getLong("from")
                    chatEntry2.userId = userId.toLong()
                    chatEntry2.face = face
                    chatEntry2.id = chatEntry.id!! - 10
                    chatEntry2.created = (data.getLong("created") - 100).toString()
                    ChatDBManager.getInstance(this).insertUser(chatEntry2)

                    val answer1 = data.getString("answer1")
                    if (!TextUtils.isEmpty(answer1)) {
                        if (answer1.contains("</txt>")) {
                            chatEntry.content = getAnswer(answer1)
                            chatEntry.type = "gtxt"
                        } else if (answer1.contains("</sound>")) {
                            val soundByXmlStr = XmlUtils.getSoundByXmlStr(answer1.replace("\\\\".toRegex(), ""))
                            if (soundByXmlStr != null) {
                                chatEntry.content = API.PIC_PREFIX + soundByXmlStr.src
                                val dura = soundByXmlStr.dura.replace("dura".toRegex(), "")
                                chatEntry.extra = dura
                            }
                            chatEntry.type = "gaudio"
                        }
                    }
                } else if (TextUtils.equals(act, "boxanswer")) {
                    val question = data.getJSONObject("question")
                    val answer = data.getJSONObject("answer")
                    var medias = question.getJSONArray("media")
                    val chatQsEntry = ChatEntry()
                    chatQsEntry.type = question.getString("type")
                    chatQsEntry.msgId = (result.getLong("created") - 100).toString() + ""
                    chatQsEntry.chatId = result.getLong("from")
                    chatQsEntry.from = result.getLong("to")
                    chatQsEntry.to = result.getLong("from")
                    chatQsEntry.userId = userId.toLong()
                    chatQsEntry.face = face
                    chatQsEntry.id = chatEntry.id!! - 10
                    chatQsEntry.created = (result.getString("created").toLong() - 100).toString()
                    chatQsEntry.content = question.getString("text")
                    mainBeanQues = question.getString("text")
                    if (medias != null && medias.length() > 0) {
                        for (i in 0..(medias.length() - 1)) {
                            var media = medias[i] as JSONObject
                            var type = media.getString("type")
                            when (i) {
                                0 -> chatQsEntry.question1 = API.PIC_PREFIX + media.getString("org") + "&&" + type
                                1 -> chatQsEntry.question2 = API.PIC_PREFIX + media.getString("org") + "&&" + type
                                2 -> chatQsEntry.question3 = API.PIC_PREFIX + media.getString("org") + "&&" + type
                                3 -> chatQsEntry.extra = API.PIC_PREFIX + media.getString("org") + "&&" + type
                            }
                        }
                    }
                    ChatDBManager.getInstance(this).insertUser(chatQsEntry)
                    chatEntry.content = answer.getString("text")
                    chatEntry.type = answer.getString("type")
                    mainBeanMsg = answer.getString("text")
                    var answMedia = answer.getJSONArray("media")
                    if (answMedia != null && answMedia.length() > 0) {
                        var obj = answMedia[0] as JSONObject
                        var type = obj.getString("type")
                        if (type == "audio") {
                            var org = obj.getString("org")
                            var extra = obj.getString("extra").replace("dura", "")
                            chatEntry.type = "gaudio"
                            chatEntry.content = API.PIC_PREFIX + org
                            chatEntry.extra = extra
                            mainBeanMsg = "[语音]"
                        }
                    }
                } else if (TextUtils.equals(act, "littletxt")) {
                    needStore = false
                    chatEntry.content = content
                    if (data.has("extra"))
                        chatEntry.extra = data.getString("extra")
                    if (data.has("layout"))
                        chatEntry.layout = data.getString("layout")
                    if (data.has("layout_type")) {
                        chatEntry.layoutType = data.getString("layout_type")
                    }
                    val `object` = JSONObject()
                    `object`.put("type", "message")
                    `object`.put("act", "lread")
                    `object`.put("created", System.currentTimeMillis())
                    `object`.put("range", "private")
                    `object`.put("from", result.getLong("from"))
                    `object`.put("to", result.getLong("to"))
                    `object`.put("id", result.getString("id"))
                    sendText(`object`.toString())
                } else if (TextUtils.equals(act, "ggreet") || TextUtils.equals(act, "gbutter")
                        || TextUtils.equals(act, "gcomfort") || TextUtils.equals(act, "greplytxt") || TextUtils.equals(act, "greplyaudio")) {
                    needStore = false
                }else if (act=="gsharescenes"){
                    chatEntry.scene_bg=data.getString("scene_bg")
                    chatEntry.scene_title=data.getString("scene_title")
                    chatEntry.scene_uid=data.getString("scene_uid")
                    chatEntry.scene_sid=data.getString("scene_sid")
                    chatEntry.scene_type=data.getString("scene_type")
                    chatEntry.scene_pic=data.getString("scene_pic")
                    chatEntry.scene_code=data.getString("scene_code")
                    chatEntry.scene_des=data.getString("scene_des")
                    chatEntry.scene_moment=data.getString("scene_moment")
                }
            }

            if (needStore) {
                if (result.has("qs_type") && result.getString("qs_type") == "private") {
                    if (result.has("qs_media1")) {
                        chatEntry.question1 = result.getString("qs_media1")
                    }
                    if (result.has("qs_media2")) {
                        chatEntry.question2 = result.getString("qs_media2")
                    }
                    if (result.has("qs_media3")) {
                        chatEntry.question3 = result.getString("qs_media3")
                    }
                }
                val b = ChatDBManager.getInstance(this).insertUser(chatEntry)
                var queryMainUser: MainInfoBean? = null
                queryMainUser = when {
                    chatEntry.gsid != 0L -> MainDBManager.getInstance(this).queryMainUserWithGsId(chatEntry.gsid)
                    chatEntry.qsid != 0L -> MainDBManager.getInstance(this).queryMainUserWithQsid(result.getString("from"), chatEntry.qsid)
                    else -> MainDBManager.getInstance(this).queryMainUser(result.getString("from"))
                }

                if (queryMainUser == null) {
                    val bean = MainInfoBean()
                    if (result.has("gsid")) {
                        bean.gsid = result.getLong("gsid")
                    }
                    if (result.has("qsid") && bean.gsid == 0L) {
                        bean.qsid = result.getLong("qsid")
                    } else {
                        bean.qsid = 0
                    }
                    if (result.has("qs_uid")) {
                        bean.qsuid = result.getInt("qs_uid")
                    }

                    if (result.has("qstxt")) {
                        bean.qsTxt = result.getString("qstxt")
                    }
                    if (result.has("qs_user_face")) {
                        bean.qsUserFace = result.getString("qs_user_face")
                    }
                    if (result.has("qs_nick")) {
                        bean.qsNick = result.getString("qs_nick")
                    }
                    if (result.has("qs_type")) {
                        bean.qsType = result.getString("qs_type")
                    }
                    if (result.has("qs_media1")) {
                        bean.media1 = result.getString("qs_media1")
                    }
                    if (result.has("qs_media2")) {
                        bean.media2 = result.getString("qs_media2")
                    }
                    if (result.has("qs_media3")) {
                        bean.media3 = result.getString("qs_media3")
                    }
                    bean.sex = frined.getString("sex")
                    bean.uid = frined.getInt("uid").toLong()
                    bean.nick = frined.getString("nick")
                    bean.birth = frined.getString("birth")
                    bean.card = frined.getString("card")
                    bean.careers = frined.getString("careers")
                    bean.city = frined.getString("city")
                    bean.height = frined.getString("height")
                    bean.weight = frined.getString("weight")
                    bean.edu = frined.getString("edu")
                    bean.xz = frined.getString("xz")
                    bean.account = frined.getString("card")
                    if (frined.has("mood")) {
                        bean.mood = frined.getString("mood")
                    }
                    val pic = frined.getJSONArray("pic")
                    val gson = Gson()
                    val pics = gson.fromJson<List<String>>(pic.toString(), object : TypeToken<List<String>>() {

                    }.type)
                    bean.pic = pics as ArrayList<String>
                    bean.face = frined.getString("face")
                    bean.type = "temporal"
                    bean.created = System.currentTimeMillis()
                    bean.userId = userId.toLong()
                    MainDBManager.getInstance(this).insertUser(bean)

                    if (bean.qsTxt.isNotEmpty() && (bean.qsid != 0L || bean.gsid != 0L) && bean.qsuid != 0 && bean.qsType != "private") {
                        var qsChatEntry = ChatEntry()
                        qsChatEntry.msgId = "${System.currentTimeMillis()}"
                        qsChatEntry.chatId = result.getLong("from")
                        if (bean.qsuid.toLong() == chatEntry.from) {
                            qsChatEntry.from = result.getLong("from")
                            qsChatEntry.to = result.getLong("to")
                        } else {
                            qsChatEntry.from = result.getLong("to")
                            qsChatEntry.to = result.getLong("from")
                        }
                        qsChatEntry.type = "gtxt"
                        qsChatEntry.userId = userId.toLong()
                        qsChatEntry.qsid = bean.qsid
                        qsChatEntry.gsid = bean.gsid
                        qsChatEntry.created = "${result.getLong("created") - 100}"
                        qsChatEntry.face = face
                        qsChatEntry.id = chatEntry.id!! - 10
                        qsChatEntry.content = bean.qsTxt
                        qsChatEntry.question1 = bean.media1
                        qsChatEntry.question2 = bean.media2
                        qsChatEntry.question3 = bean.media3
                        ChatDBManager.getInstance(this).insertUser(qsChatEntry)
                    }
                }
                Log.e(TAG, "msg success: $b")

                val `object` = JSONObject()
                `object`.put("type", "message")
                `object`.put("act", "gread")
                `object`.put("created", System.currentTimeMillis())
                `object`.put("range", "private")
                `object`.put("from", result.getLong("from"))
                `object`.put("to", result.getLong("to"))
                `object`.put("id", result.getString("id"))
                Log.e(TAG, "gread: ${`object`}")
                sendText(`object`.toString())

                var bean: MainInfoBean? = null
                bean = when {
                    chatEntry.gsid != 0L -> MainDBManager.getInstance(this).queryMainUserWithGsId(chatEntry.gsid)
                    chatEntry.qsid != 0L -> MainDBManager.getInstance(this).queryMainUserWithQsid(result.getString("from"), chatEntry.qsid)
                    else -> MainDBManager.getInstance(this).queryMainUser(result.getString("from"))
                }

//            val bean = MainDBManager.getInstance(this).queryMainUser(result.getLong("from").toString() + "")
                if (bean != null && b) {
                    //                        bean.setHasNewMsg(true);
                    bean.isExtractMark = true
                    if (TextUtils.equals(act, "gtxt")) {
                        val txtByXmlStr = XmlUtils.getTxtByXmlStr(data!!.getString("content"))
                        if (txtByXmlStr != null) {
                            val txt = txtByXmlStr.txt
                            bean.msg = txt
                            if (!(TextUtils.equals(activityName, "com.eggplant.qiezisocial.ui.chat.ChatActivity") && (QzApplication.get().chatUid == result.getLong("from") || QzApplication.get().chatGsId == chatEntry.gsid))) {
                                val text = ExpressionTransformEngine.transformExoressionToTxt(txt)
                                NotifycationUtils.getInstance(this).addChatMsgNotify(text, bean)
                            }
                        }
                    } else if (TextUtils.equals(act, "gpic")) {
                        bean.msg = "[图片]"
                        if (!(TextUtils.equals(activityName, "com.eggplant.qiezisocial.ui.chat.ChatActivity") && (QzApplication.get().chatUid == result.getLong("from") || QzApplication.get().chatGsId == chatEntry.gsid)))
                            NotifycationUtils.getInstance(this).addChatMsgNotify("[图片]", bean)
                    } else if (TextUtils.equals(act, "gaudio")) {
                        bean.msg = "[语音]"
                        if (!(TextUtils.equals(activityName, "com.eggplant.qiezisocial.ui.chat.ChatActivity") && (QzApplication.get().chatUid == result.getLong("from") || QzApplication.get().chatGsId == chatEntry.gsid)))
                            NotifycationUtils.getInstance(this).addChatMsgNotify("[语音]", bean)

                    } else if (TextUtils.equals(act, "gvideo")) {
                        bean.msg = "[视频]"
                        if (!(TextUtils.equals(activityName, "com.eggplant.qiezisocial.ui.chat.ChatActivity") && (QzApplication.get().chatUid == result.getLong("from") || QzApplication.get().chatGsId == chatEntry.gsid)))
                            NotifycationUtils.getInstance(this).addChatMsgNotify("[视频]", bean)
                    } else if (TextUtils.equals(act, "gquestion") || TextUtils.equals(act, "gnewquestion")) {
                        bean.msg = "[互动回答]"
                        if (!(TextUtils.equals(activityName, "com.eggplant.qiezisocial.ui.chat.ChatActivity") && (QzApplication.get().chatUid == result.getLong("from") || QzApplication.get().chatGsId == chatEntry.gsid)))
                            NotifycationUtils.getInstance(this).addChatMsgNotify("[互动回答]", bean)
                    } else if (TextUtils.equals(act, "boxanswer")) {
                        bean.msg = mainBeanMsg
                        bean.`object` = mainBeanQues
                        if (!(TextUtils.equals(activityName, "com.eggplant.qiezisocial.ui.chat.ChatActivity") && (QzApplication.get().chatUid == result.getLong("from") || QzApplication.get().chatGsId == chatEntry.gsid)))
                            NotifycationUtils.getInstance(this).addChatMsgNotify("[互动回答]", bean)
                    }else if (TextUtils.equals(act, "gsharescenes")) {
                        bean.msg = "[场景分享]"
                        bean.`object` = mainBeanQues
                        if (!(TextUtils.equals(activityName, "com.eggplant.qiezisocial.ui.chat.ChatActivity") && (QzApplication.get().chatUid == result.getLong("from") || QzApplication.get().chatGsId == chatEntry.gsid)))
                            NotifycationUtils.getInstance(this).addChatMsgNotify("[场景分享]", bean)
                    }

                    else {
                        bean.msg = data!!.getString("content")
                        if (!(TextUtils.equals(activityName, "com.eggplant.qiezisocial.ui.chat.ChatActivity") && (QzApplication.get().chatUid == result.getLong("from") || QzApplication.get().chatGsId == chatEntry.gsid)))
                            NotifycationUtils.getInstance(this).addChatMsgNotify(data.getString("content"), bean)
                    }

                    if (TextUtils.equals(activityName, "com.eggplant.qiezisocial.ui.chat.ChatActivity") && (QzApplication.get().chatUid == result.getLong("from") || QzApplication.get().chatGsId == chatEntry.gsid)) {
                        bean.msgNum = 0
                    } else {
                        bean.msgNum = bean.msgNum + 1
                    }
                    bean.newMsgTime = System.currentTimeMillis()
                    bean.msgType = act
                    MainDBManager.getInstance(this).updateUser(bean)
                    EventBus.getDefault().post(NewMsgEvent())
                }
            } else {
                EventBus.getDefault().post(GchatParcelEntry(chatEntry))
            }
        }
    }


    private fun swithAction(act: String?, result: JSONObject, phone: String, token: String, userId: Int) {
        when (act) {
            "otherlogin" -> {
                QzApplication.get().isWebLogin = false
                EventBus.getDefault().post(OtherLoginEvnet())
                QzApplication.get().isWebLogin = false
                if (QzApplication.get().isLogin) {
                    val upTxt = JSONObject()
                    upTxt.put("type", "system")
                    upTxt.put("created", System.currentTimeMillis())
                    upTxt.put("act", "glogin")
                    val data = JSONObject().put("mobile", phone).put("token", token)
                    upTxt.put("data", data)
                    sendText(upTxt.toString())
                }
                QzApplication.get().isWebLogin = false
                val errData = result.getJSONObject("data")
                val errMsg = errData.getString("message")
                Log.e(TAG, "onCommonResponse: " + errMsg)

                val upTxt2 = JSONObject()
                upTxt2.put("type", "system")
                upTxt2.put("created", System.currentTimeMillis())
                upTxt2.put("act", "login")
                val data2 = JSONObject().put("mobile", phone).put("token", token)
                upTxt2.put("data", data2)
                sendText(upTxt2.toString())
            }
            "loginrequest" -> {
                QzApplication.get().isWebLogin = false
                if (QzApplication.get().isLogin) {
                    val upTxt = JSONObject()
                    upTxt.put("type", "system")
                    upTxt.put("created", System.currentTimeMillis())
                    upTxt.put("act", "glogin")
                    val data = JSONObject().put("mobile", phone).put("token", token)
                    upTxt.put("data", data)
                    sendText(upTxt.toString())
                }
                QzApplication.get().isWebLogin = false
                val errData = result.getJSONObject("data")
                val errMsg = errData.getString("message")
                Log.e(TAG, "onCommonResponse: " + errMsg)
                val upTxt2 = JSONObject()
                upTxt2.put("type", "system")
                upTxt2.put("created", System.currentTimeMillis())
                upTxt2.put("act", "login")
                val data2 = JSONObject().put("mobile", phone).put("token", token)
                upTxt2.put("data", data2)
                sendText(upTxt2.toString())
            }
            "loginerror" -> {
                QzApplication.get().isWebLogin = false
                val errData = result.getJSONObject("data")
                val errMsg = errData.getString("message")
                Log.e(TAG, "onCommonResponse: " + errMsg)
                val upTxt2 = JSONObject()
                upTxt2.put("type", "system")
                upTxt2.put("created", System.currentTimeMillis())
                upTxt2.put("act", "login")
                val data2 = JSONObject().put("mobile", phone).put("token", token)
                upTxt2.put("data", data2)
                sendText(upTxt2.toString())
            }
            "loginok" -> {
                QzApplication.get().isWebLogin = true
                val firendList = result.getJSONArray("data")

                if (firendList.length() > 0) {

                    for (i in 0 until firendList.length()) {
                        val arr = firendList.getJSONObject(i)
                        val frined = arr.getJSONObject("userinfor")
                        val online = arr.getString("online")
                        val bean = MainInfoBean()
                        bean.uid = frined.getInt("uid").toLong()
                        bean.sex = frined.getString("sex")
                        bean.nick = frined.getString("nick")
                        bean.birth = frined.getString("birth")
                        bean.card = frined.getString("card")
                        bean.careers = frined.getString("careers")
                        bean.city = frined.getString("city")
                        bean.height = frined.getString("height")
                        bean.weight = frined.getString("weight")
                        bean.edu = frined.getString("edu")
                        bean.xz = frined.getString("xz")
                        bean.account = frined.getString("card")
                        bean.online = online
                        val pic = frined.getJSONArray("pic")
                        val gson = Gson()
                        val pics = gson.fromJson<List<String>>(pic.toString(), object : TypeToken<List<String>>() {

                        }.type)
                        bean.pic = pics as ArrayList<String>
                        bean.face = frined.getString("face")
                        bean.label = frined.getString("label")
                        bean.type = act
                        bean.created = System.currentTimeMillis()
                        bean.userId = userId.toLong()
                        //                            bean.setUserId();
                        //                            dataList.add(bean);
                        MainDBManager.getInstance(this).insertUser(bean)
                        EventBus.getDefault().post(FriendListEvent(bean.type))
                    }

                }
            }
            "gfriendlist" -> {
                val firendList = result.getJSONArray("data")
                if (firendList.length() > 0) {
                    for (i in 0 until firendList.length()) {
                        val arr = firendList.getJSONObject(i)
                        val frined = arr.getJSONObject("userinfor")
                        val online = arr.getString("online")
                        val bean = MainInfoBean()
                        bean.uid = frined.getInt("uid").toLong()
                        bean.sex = frined.getString("sex")
                        bean.nick = frined.getString("nick")
                        bean.birth = frined.getString("birth")
                        bean.card = frined.getString("card")
                        bean.careers = frined.getString("careers")
                        bean.city = frined.getString("city")
                        bean.height = frined.getString("height")
                        bean.weight = frined.getString("weight")
                        bean.edu = frined.getString("edu")
                        bean.xz = frined.getString("xz")
                        bean.account = frined.getString("card")
                        bean.online = online
                        Log.i("online-web", "$online ${bean.nick}")
                        val pic = frined.getJSONArray("pic")
                        val gson = Gson()
                        val pics = gson.fromJson<List<String>>(pic.toString(), object : TypeToken<List<String>>() {

                        }.type)
                        bean.pic = pics as ArrayList<String>
                        bean.face = frined.getString("face")
//                        bean.label = frined.getString("label")
                        bean.type = act
                        bean.created = System.currentTimeMillis()
                        bean.userId = userId.toLong()

                        val b = MainDBManager.getInstance(this).insertUser(bean)

                        EventBus.getDefault().post(FriendListEvent(bean.type))
                    }
                }
            }

            "gapplylist" -> {
                val applyList = result.getJSONArray("data")
                if (applyList.length() > 0) {
                    for (i in 0 until applyList.length()) {
                        val arr = applyList.getJSONObject(i)
                        val frined = arr.getJSONObject("userinfor")
                        val online = arr.getString("online")
                        val source = arr.getString("source")
                        val bean = MainInfoBean()
                        if (arr.has("message")) {
                            bean.message = arr.getString("message")
                        }
                        bean.uid = frined.getInt("uid").toLong()
                        bean.sex = frined.getString("sex")
                        bean.nick = frined.getString("nick")
                        bean.birth = frined.getString("birth")
                        bean.card = frined.getString("card")
                        bean.careers = frined.getString("careers")
                        bean.city = frined.getString("city")
                        bean.height = frined.getString("height")
                        bean.weight = frined.getString("weight")
                        bean.edu = frined.getString("edu")
                        bean.xz = frined.getString("xz")
                        bean.account = frined.getString("card")
                        bean.source = source
                        bean.online = online
                        val pic = frined.getJSONArray("pic")
                        val gson = Gson()
                        val pics = gson.fromJson<List<String>>(pic.toString(), object : TypeToken<List<String>>() {

                        }.type)
                        bean.pic = pics as ArrayList<String>
                        bean.face = frined.getString("face")
                        if (frined.has("label"))
                            bean.label = frined.getString("label")
                        bean.created = System.currentTimeMillis()
                        bean.userId = userId.toLong()
                        bean.type = act
                        val b = MainDBManager.getInstance(this).insertUser(bean)
                        if (b) {
                            EventBus.getDefault().post(HomeMsgEvent("邀请您添加为好友", bean))
                            NotifycationUtils.getInstance(this).systemNotify("您有新的好友申请", "apply")
                        }
                        EventBus.getDefault().post(FriendListEvent(bean.type))
                    }

                }
            }
            "ganswerlist" -> {
            }
            "gcall" -> {
                val media = result.getString("media")
                val created = result.getString("created")
                val from = result.getInt("from")
                val to = result.getLong("to")
                val id = result.getString("id")
                val activityName = CommentUtils.getRunningActivityName(this)
                if (!TextUtils.equals(activityName, "ChatAVActivity")) {
                    val intent = Intent(this, ChatAVActivity::class.java)
                    intent.putExtra("from", from.toString() + "")
                            .putExtra("to", to.toString() + "")
                            .putExtra("start", media)
                            .putExtra("invoke", "received")
                            .putExtra("id", id)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {

                }
            }
            "groupinfor" -> {
//                val data = result.getJSONArray("data")
//                if (data.length() > 0) {
//                    val alist = ArrayList<GchatEntry>()
//                    for (i in 0 until data.length()) {
//                        val jobj = data.getJSONObject(i)
//                        val user = GchatEntry()
//                        user.font = jobj.getString("font")
//                        user.id = jobj.getInt("id")
//                        user.uid = jobj.getString("uid").toInt()
//                        user.title = jobj.getString("text")
//                        user.usercount = jobj.getInt("usercount")
//                        user.topiccount = jobj.getInt("topiccount")
//                        user.created = jobj.getLong("created")
//                        alist.add(user)
//                    }
//                    EventBus.getDefault().post(alist)
//                }

            }
            "smalltxt" -> {
                val data = result.getJSONObject("data")
                val msg = data.getString("msg")
                val id = data.getString("id")
                EventBus.getDefault().post(SmallTxtEvent(msg))
                val `object` = JSONObject()
                `object`.put("type", "message")
                `object`.put("act", "smallread")
                `object`.put("created", System.currentTimeMillis())
                `object`.put("range", "private")
                `object`.put("from", "$userId")
                `object`.put("id", id)
                sendText(`object`.toString())
            }
            "notice" -> {
                val `object` = JSONObject()
                `object`.put("type", "message")
                `object`.put("act", "nread")
                `object`.put("created", System.currentTimeMillis())
                if (result.has("data")) {
                    val data = result.getJSONArray("data")
                    if (data.length() > 0) {
                        val jsonObject = data.getJSONObject(0)
                        val msg = jsonObject.getString("msg")
                        val type = jsonObject.getString("type")

                        `object`.put("id", jsonObject.getString("id"))
                        sendText(`object`.toString())
                        //自建场景通过
                        if (type == "selfscenes") {
                            if (jsonObject.has("infor")) {
                                val event = Gson().fromJson<ScenesEntry>(jsonObject.getString("infor").toString(), ScenesEntry::class.java)
                                EventBus.getDefault().post(SelfScenesEvent(event))
                            }
                        } else {
                            NotifycationUtils.getInstance(this).systemNotify(msg, type)
                        }

                    }
                }

            }

            "groupuserlist" -> {
                val data = result.getJSONObject("data")
                if (data != null) {
                    if (data.has("count")) {
                        val count = data.getString("count")
                        val guser = Gson().fromJson<GuserListEvent>(data.toString(), GuserListEvent::class.java)
                        EventBus.getDefault().post(guser)
                    }
                }

            }

            "ggreet", "gbutter", "gcomfort" -> {
                val event = Gson().fromJson<GreetSbEvent>(result.toString(), GreetSbEvent::class.java)
                EventBus.getDefault().post(event)
            }
            "greplytxt", "greplyaudio" -> {
                val `object` = JSONObject()
                `object`.put("type", "message")
                `object`.put("act", "gread")
                `object`.put("created", System.currentTimeMillis())
                `object`.put("range", "private")
                `object`.put("from", result.getLong("from"))
                `object`.put("to", result.getLong("to"))
                `object`.put("id", result.getString("id"))
                Log.e(TAG, "gread: ${`object`}  ---6257cec55c85a23298370")
                sendText(`object`.toString())
                val event = Gson().fromJson<ReplyGreetSbEvent>(result.toString(), ReplyGreetSbEvent::class.java)
                EventBus.getDefault().post(event)
            }

        }
    }

    private fun getAnswer(answer: String): String {
        var result = ""
        if (answer.contains("</txt>")) {
            val txtByXmlStr = XmlUtils.getTxtByXmlStr(answer.replace("\\\\".toRegex(), ""))
            if (txtByXmlStr != null)
                result = txtByXmlStr.txt
        } else if (answer.contains("</sound>")) {
            val soundByXmlStr = XmlUtils.getSoundByXmlStr(answer.replace("\\\\".toRegex(), ""))
            if (soundByXmlStr != null) {
                val src = soundByXmlStr.src
                val dura = soundByXmlStr.dura
                result = src + "&&" + dura
            }
        } else {
            result = answer
        }
        return result
    }

    companion object {
        private val TAG = "WebSocketService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val from = intent?.getStringExtra("from")
        if (from != null && from == "alarm") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val NOTIFICATION_CHANNEL_ID = "com.zhaorenwan.social"
                val channelName = "My Background Service"
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW)
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as (NotificationManager)
                manager.createNotificationChannel(channel)
                val notification = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.box_iauncher)  // the status icon
                        .setWhen(System.currentTimeMillis())  // the time stamp
                        .setContentText("IM服务正在运行")  // the contents of the entry
                        .build()
                startForeground(2, notification)
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
                stopForeground(true)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}
