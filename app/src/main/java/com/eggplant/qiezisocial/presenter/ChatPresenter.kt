package com.eggplant.qiezisocial.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.ChatContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.ChatModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.socket.event.NewMsgEvent
import com.eggplant.qiezisocial.ui.chat.ChatAVActivity
import com.eggplant.qiezisocial.ui.main.OtherSpaceActivity
import com.eggplant.qiezisocial.utils.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.tbruyelle.rxpermissions2.RxPermissions
import com.vincent.videocompressor.VideoCompress
import me.leolin.shortcutbadger.ShortcutBadger
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Administrator on 2020/4/16.
 */
class ChatPresenter : BasePresenter<ChatContract.View>(), ChatContract.Presenter {
    var model: ChatModel
    var userEntry: UserEntry? = null
    var scene: String? = null
    var qsTxt: String? = null
    var mainInfoBean: MainInfoBean? = null
    var uid: Long = 0
    var msgNum = 0
    var face: String? = null
    var myface: String? = null
    var myid: Int? = 0

    init {
        model = ChatModel()
    }

    fun createMainInfoBean(uid: Long, userEntry: UserEntry?): MainInfoBean {
        val bean = MainInfoBean()
        bean.uid = uid
        bean.sex = userEntry?.sex
        bean.nick = userEntry?.nick
        bean.birth = userEntry?.birth
        bean.card = userEntry?.card
        bean.careers = userEntry?.careers
        bean.city = userEntry?.city
        bean.height = userEntry?.height
        bean.weight = userEntry?.weight
        bean.edu = userEntry?.edu
        bean.topic = userEntry?.topic
        bean.xz = userEntry?.xz
        bean.account = userEntry?.card
        bean.pic = userEntry?.pic as ArrayList<String>
        bean.face = userEntry?.face
        bean.type = "temporal"
        bean.created = System.currentTimeMillis()
        bean.userId = QzApplication.get().infoBean?.uid?.toLong()!!
        return bean

    }

    fun applyFriend(activity: Activity) {
        model.applyFriend(activity, uid = uid.toInt(), callback = object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (it.isSuccessful)
                        it.body().msg?.let { TipsUtil.showToast(activity, it) }

                }
            }
        })
    }

    /**
     * 创建音频entry
     */
    fun createChatEntry(filePath: String, dura: Int, myid: Long, uid: Long, myface: String): ChatEntry {
        val chatEntry = ChatEntry()
        chatEntry.content = filePath
        chatEntry.extra = dura.toString()
        chatEntry.created = System.currentTimeMillis().toString()
        chatEntry.from = myid
        chatEntry.to = uid
        chatEntry.face = myface
        chatEntry.type = "gaudio"
        chatEntry.msgId = System.currentTimeMillis().toString()
        chatEntry.chatId = uid
        chatEntry.msgStat = 2
        chatEntry.userId = myid
        chatEntry.gsid = mainInfoBean!!.gsid
        chatEntry.qsid = mainInfoBean!!.qsid
        return chatEntry
    }

    /**
     * 聊天消息包装
     * @param from
     * @param to
     * @param content
     * @param extra
     * @return
     */
    fun getSendMsg(act: String, id: String, from: String, to: String, content: String, extra: String): String {
        val `object` = JSONObject()
        try {
            `object`.put("type", "message")
            `object`.put("act", act)
            `object`.put("created", System.currentTimeMillis())
            `object`.put("range", "private")
            `object`.put("from", from)
            `object`.put("to", to)
            `object`.put("id", id)
            if (mainInfoBean!!.gsid != 0L) {
                `object`.put("gsid", mainInfoBean!!.gsid)
            } else if (mainInfoBean!!.qsid != 0L) {
                `object`.put("qsid", mainInfoBean!!.qsid)
            }
            `object`.put("qs_uid", mainInfoBean!!.qsuid)
            `object`.put("qstxt", mainInfoBean!!.qsTxt)
            `object`.put("qs_nick", mainInfoBean!!.qsNick)
            `object`.put("qs_user_face", mainInfoBean!!.qsUserFace)
            if (mainInfoBean!!.media1 != null && mainInfoBean!!.media1.isNotEmpty()) {
                `object`.put("qs_media1", mainInfoBean!!.media1)
            }
            if (mainInfoBean!!.media2 != null && mainInfoBean!!.media2.isNotEmpty()) {
                `object`.put("qs_media2", mainInfoBean!!.media2)
            }
            if (mainInfoBean!!.media3 != null && mainInfoBean!!.media3.isNotEmpty()) {
                `object`.put("qs_media3", mainInfoBean!!.media3)
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
            Log.e("ChatPresenter", "dataString:${`object`.toString()}")
            return `object`.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 重新计算 新消息数
     */
    fun setShortcut(mContext: Context, edit: SharedPreferences.Editor, newMsgNumb: Int, msgNum: Int) {
        val finalMsgNum = if (newMsgNumb - msgNum >= 0) newMsgNumb - msgNum else 0
        ShortcutBadger.applyCount(mContext, if (finalMsgNum > 99) 99 else finalMsgNum)
        edit.putInt("newMsgNumb", finalMsgNum)
        edit.commit()
    }


    /**
     * 获取一次msg UUID
     */
    fun getMsgId(activity: Activity) {
        model.getId(activity)
    }

    /**
     * 下载聊天消息---图片、语音、视频
     */
    fun downloadFile(mContext: Context, content: String, contentName: String, mFileRelativeUrl: String, bean: ChatEntry) {
        FileUtils.downloadFile(mContext, content, contentName, mFileRelativeUrl, object : FileUtils.DownloadFileCallback {
            override fun onSuccess(filePath: String) {

                when {
                    TextUtils.equals(bean.type, "gpic") -> bean.content = filePath
                    TextUtils.equals(bean.type, "gaudio") -> bean.content = filePath
                    TextUtils.equals(bean.type, "gvideo") -> {
                        bean.content = filePath
                        val media = MediaMetadataRetriever()
                        media.setDataSource(filePath)
                        val frameAtTime = media.frameAtTime
                        val videoAlbumPath = FileUtils.getChatFilePath(mContext) + System.currentTimeMillis() + ".jpg"
                        BitmapUtils.saveSmallBitmap2SDCard(frameAtTime, videoAlbumPath)
                        val duration = Integer.parseInt(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
                        bean.extra = videoAlbumPath + "&&" + duration
                    }
                    TextUtils.equals(bean.type, "giftrev") -> bean.extra = filePath
                    TextUtils.equals(bean.type, "gtxt") -> {
                        bean.extra = filePath
                    }
                }
                ChatDBManager.getInstance(mContext).updateUser(bean)
            }

            override fun onError(message: String) {

            }

            override fun onProgress(progress: Float?) {

            }
        })
    }

    /**
     * 下载聊天question---图片、视频
     */
    fun downloadQsFile(mContext: Context, content: String, contentName: String, mediaType: String, flag: String, mFileRelativeUrl: String, bean: ChatEntry) {
        FileUtils.downloadFile(mContext, content, contentName, mFileRelativeUrl, object : FileUtils.DownloadFileCallback {
            override fun onSuccess(filePath: String) {
                if (TextUtils.equals(bean.type, "boxquestion")) {
                    when (flag) {
                        "q1" -> bean.question1 = filePath + "&&" + mediaType
                        "q2" -> bean.question2 = filePath + "&&" + mediaType
                        "q3" -> bean.question3 = filePath + "&&" + mediaType
                        "q4" -> bean.extra = filePath + "&&" + mediaType
                    }
                    ChatDBManager.getInstance(mContext).updateUser(bean)
                } else if (TextUtils.equals(bean.type, "gtxt")) {
                    when (flag) {
                        "m1" -> bean.question1 = filePath + "&&" + mediaType
                        "m2" -> bean.question2 = filePath + "&&" + mediaType
                        "m3" -> bean.question3 = filePath + "&&" + mediaType
                    }
                    ChatDBManager.getInstance(mContext).updateUser(bean)
                }
            }

            override fun onError(message: String) {

            }

            override fun onProgress(progress: Float?) {

            }
        })
    }


    private fun initMyInfo(mContext: Context) {
        if (QzApplication.get().infoBean != null) {
            myid = QzApplication.get().infoBean?.uid
            myface = QzApplication.get().infoBean?.face
        } else {
            val userInfo = mContext.getSharedPreferences("userEntry", AppCompatActivity.MODE_PRIVATE)
            myid = userInfo.getInt("uid", 0)
            myface = userInfo.getString("face", "")
        }
        mView?.setMyHead(myface)
    }

    /**
     * 初始化数据
     */
    fun initData(mContext: Context) {
        initMyInfo(mContext)
        val userInfo = mContext.getSharedPreferences("userEntry", AppCompatActivity.MODE_PRIVATE)
        val newMsgNumb = userInfo.getInt("newMsgNumb", 0)
        val edit = userInfo.edit()
        if (mainInfoBean != null) {
            //已添加为好友-- 可能存在聊天记录
            uid = mainInfoBean!!.uid
            checkIsMine()
            //记录当前聊天id
            QzApplication.get().chatUid = uid
            QzApplication.get().chatGsId = mainInfoBean!!.gsid
            face = mainInfoBean!!.face
            mView?.setOtherFace(face)
            val nick = mainInfoBean!!.nick
            val remark = mainInfoBean!!.remark
            val gsid = mainInfoBean!!.gsid
            val qsid = mainInfoBean!!.qsid
            msgNum = mainInfoBean!!.msgNum
            if (gsid != 0L) {
                mView?.setBarTitle("群聊")
            } else if (!TextUtils.isEmpty(remark)) {
                mView?.setBarTitle(remark)
            } else if (!TextUtils.isEmpty(nick)) {
                mView?.setBarTitle(nick)
            }
            setShortcut(mContext, edit, newMsgNumb, msgNum)
            mainInfoBean!!.msgNum = 0
            mainInfoBean!!.`object` = ""
            if (mainInfoBean!!.msg != null && mainInfoBean!!.msg == "已经通过好友验证成为好友") {
                mainInfoBean!!.msg = ""
                mainInfoBean!!.newMsgTime = 0
            }
            if (TextUtils.equals("ganswerlist", mainInfoBean!!.type)) {
                mainInfoBean!!.type = "temporal"
            }
            val queryMainUser = MainDBManager.getInstance(mContext).queryMainUser("${mainInfoBean!!.uid}")
            if ((queryMainUser == null || (TextUtils.equals("temporal", queryMainUser.type))) && gsid == 0L) {
                mView?.setBarRightTxt("加好友")
            }
            //去除消息提示
            MainDBManager.getInstance(mContext).updateUser(mainInfoBean)
            var chatEntries: List<ChatEntry>? = null
            chatEntries = when {
                mainInfoBean!!.gsid != 0L -> ChatDBManager.getInstance(mContext).queryGsUserList(mainInfoBean!!.gsid, mView?.getAdapterDataSize()!! - 1)
                mainInfoBean!!.qsid != 0L -> ChatDBManager.getInstance(mContext).queryQsUserList(uid, mainInfoBean!!.qsid, mView?.getAdapterDataSize()!! - 1)
                else -> ChatDBManager.getInstance(mContext).queryUserList(uid, mView?.getAdapterDataSize()!! - 1)
            }

            if (chatEntries != null && chatEntries.isNotEmpty()) {
                //获取数据
                var data = getData(mContext, chatEntries, true)
//                if (qsTxt != null) {
//                    var qsData = createQsTitle(qsTxt!!)
//                    (data as ArrayList<ChatMultiEntry<ChatEntry>>).add(0, qsData)
//                }
                mView?.refreshItem(onNewIntent = true, data = data)
            } else {

            }
        } else if (userEntry != null) {
            //未添加好友--无聊天记录
            val queryMainUser = MainDBManager.getInstance(mContext).queryMainUser("${userEntry!!.uid}")
            if (queryMainUser != null) {
                mainInfoBean = queryMainUser
                initData(mContext)
                return
            }
            uid = userEntry!!.uid.toLong()
            checkIsMine()
            QzApplication.get().chatUid = uid
            if (userEntry!!.friend != "yes")
                mView?.setBarRightTxt("加好友")
            val nick = userEntry!!.nick
            if (!TextUtils.isEmpty(nick)) {
                mView?.setBarTitle(nick)
            }
            //创建数据--并存入数据库
            val bean = createMainInfoBean(uid, userEntry)
            MainDBManager.getInstance(mContext).insertUser(bean)
            //重新查找本地数据
            mainInfoBean = MainDBManager.getInstance(mContext).queryMainUser(uid.toString() + "")

        } else {
            mView?.finishActivity()
            return
        }


        //最新消息提示
        if (msgNum > 0) {
            mView?.showNewMsg(msgNum)
        }
    }

    /**
     * 初始化数据
     */
    fun onNewIntent(mContext: Context) {
        if (mainInfoBean != null) {
            uid = mainInfoBean!!.uid
            QzApplication.get().chatUid = uid
            face = mainInfoBean!!.face
            mView?.setOtherFace(face)
            val nick = mainInfoBean!!.nick
            if (!TextUtils.isEmpty(nick))
                mView?.setBarTitle(nick)

//            val chatEntries = ChatDBManager.getInstance(mContext).queryUserList(uid, mView?.getAdapterDataSize()!! - 1)
            var chatEntries: List<ChatEntry>? = null
            chatEntries = when {
                mainInfoBean!!.gsid != 0L -> ChatDBManager.getInstance(mContext).queryGsUserList(mainInfoBean!!.gsid, mView?.getAdapterDataSize()!! - 1)
                mainInfoBean!!.qsid != 0L -> ChatDBManager.getInstance(mContext).queryQsUserList(uid, mainInfoBean!!.qsid, mView?.getAdapterDataSize()!! - 1)
                else -> ChatDBManager.getInstance(mContext).queryUserList(uid, mView?.getAdapterDataSize()!! - 1)
            }


            mainInfoBean!!.msgNum = 0
            MainDBManager.getInstance(mContext).updateUser(mainInfoBean)
            if (chatEntries != null && chatEntries.isNotEmpty()) {
                var data = getData(mContext, chatEntries, true)
                mView?.refreshItem(onNewIntent = true, data = data)

            }
        }
        initMyInfo(mContext)
    }

    /**
     * 检查当前聊天对象是否是自己
     */
    fun checkIsMine() {
        if (uid === QzApplication.get().infoBean?.uid?.toLong()) {
            mView?.finishActivity()
        }
    }


    /**
     * 发送文本消息
     */
    fun sendTxt(activity: Activity, content: String, extra: String, storeLocally: Boolean) {
        if (myid != 0) {
            var isSuccess = false
            val id = QzApplication.get().msgUUID
            QzApplication.get().msgUUID = "0"
            getMsgId(activity)
            if (myid != 0 && uid != 0L) {
                isSuccess = mView?.sendSocketMessage(getSendMsg("gtxt", id, myid.toString(), uid.toString(), content, extra))!!
            }
            if (!storeLocally) {
                return
            }
            val chatEntry = ChatEntry()
            chatEntry.type = "gtxt"
            chatEntry.layout = ""
            chatEntry.face = myface
            chatEntry.created = System.currentTimeMillis().toString()
            chatEntry.content = content
            chatEntry.extra = extra
            chatEntry.from = myid!!.toLong()
            if (!TextUtils.equals(id, "0")) {
                chatEntry.msgId = id
            } else {
                chatEntry.msgId = System.currentTimeMillis().toString() + ""
            }
            chatEntry.chatId = uid
            chatEntry.to = uid
            chatEntry.userId = myid!!.toLong()
            chatEntry.qsid = mainInfoBean!!.qsid
            chatEntry.gsid = mainInfoBean!!.gsid
            chatEntry.isMsgRead = true
            if (!isSuccess) {
                chatEntry.msgStat = 1
            }
            val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_MINE, chatEntry)
            val b = ChatDBManager.getInstance(activity).insertUser(chatEntry)
            if (b) {
                mainInfoBean!!.created = System.currentTimeMillis()
                mainInfoBean!!.newMsgTime = System.currentTimeMillis()
                mainInfoBean!!.msg = content
                mainInfoBean!!.msgType = "gtxt"
                mainInfoBean!!.`object` = ""
                mainInfoBean!!.qsType = ""
                MainDBManager.getInstance(activity).updateUser(mainInfoBean)
                mView?.addItem(chatEntryChatMultiBean)
                mView?.scrollToBottom()
            }
        }
    }


    /**
     * 上传视频文件
     */
    fun uploadVideoMedia(type: String, activity: Activity, s: String, media: ArrayList<String>, pTv: TextView?, bean: ChatEntry, position: Int) {
        model.uploadMedia(activity, "", media, object : StringCallback() {
            override fun onSuccess(response: Response<String>) {
                pTv!!.text = ""
                if (response.isSuccessful) {
                    paresResult(activity, type, response.body(), bean, position)
                } else {
                    bean.msgStat = 2
                    mView?.notifyAdapterItemChanged(position)
                    TipsUtil.showToast(activity, response.code().toString() + "")
                }
            }

            override fun onError(response: Response<String>) {
                super.onError(response)
                TipsUtil.showToast(activity, response.code().toString() + "")
                pTv!!.text = ""
                bean.msgStat = 2
                mView?.notifyAdapterItemChanged(position)
            }

            var uploadTime = 0L
            override fun uploadProgress(progress: Progress?) {
                val fraction = progress!!.fraction
                if (System.currentTimeMillis() - uploadTime > 300) {
                    pTv!!.text = "${(fraction * 100).toInt()}%"
                    uploadTime = System.currentTimeMillis()
                }
            }
        })
    }

    /**
     * 上传图片 或 音频文件
     */
    fun uploadPicOrAudioMedia(type: String, activity: Activity, s: String, media: ArrayList<String>, pTv: TextView?, bean: ChatEntry, position: Int) {
        model.uploadMedia(activity, "", media, object : StringCallback() {
            override fun onSuccess(response: Response<String>?) {
                if (response!!.isSuccessful) {
                    paresResult(activity, type, response.body(), bean, position)
                } else {
                    bean.msgStat = 2
                    mView?.notifyAdapterItemChanged(position)
                    TipsUtil.showToast(activity, response.code().toString() + "")
                }
            }

            override fun onError(response: Response<String>?) {
                super.onError(response)
                bean.msgStat = 2
                mView?.notifyAdapterItemChanged(position)
                TipsUtil.showToast(activity, response!!.code().toString() + "")
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
    fun paresResult(activity: Activity, type: String, result: String, entry: ChatEntry, position: Int) {
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
                        if (QzApplication.get().infoBean != null)
                            myid = QzApplication.get().infoBean?.uid
                        var isSuccess = false
                        if (myid != 0 && uid != 0L) {
                            if (TextUtils.equals(type, "gpic")) {
                                mView?.let {
                                    isSuccess = it.sendSocketMessage(getSendMsg(type, id, myid.toString(), uid.toString(), file, entry.extra!!))
                                }
                            } else {
                                mView?.let {
                                    isSuccess = it.sendSocketMessage(getSendMsg(type, id, myid.toString(), uid.toString(), file, extra))
                                }
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
                        myid = QzApplication.get().infoBean?.uid
                        val split = entry.extra!!.split("&&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        var dura = "0"
                        if (split != null && split.size == 2) {
                            dura = split[1]
                        }
                        var isSuccess: Boolean = false
                        if (myid != 0 && uid != 0L) {
                            mView?.let {
                                isSuccess = it.sendSocketMessage(getSendMsg(type, id, myid.toString(), uid.toString(), file, img + "&&" + dura))
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
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        entry.isMsgRead = true
        ChatDBManager.getInstance(activity).updateUser(entry)
        mView?.notifyAdapterItemChanged(position)

    }

    /**
     * 压缩将要上传的视频文件
     */
    fun compressVideo(v: View, inputP: String, outputP: String, bean: ChatEntry, position: Int) {
        val size = FileSizeUtil.getFileOrFilesSize(inputP, FileSizeUtil.SIZETYPE_MB)
        if (size < 10) {
            if (v != null) {
                v.visibility = View.INVISIBLE
            }
            bean.msgStat = 2
            bean.content = inputP
            v.postDelayed(
                    {
                        mView?.notifyAdapterItemChanged(position)
                    }, 150
            )
            return
        }
        VideoCompress.compressVideoMedium(inputP, outputP, object : VideoCompress.CompressListener {
            override fun onStart() {
                if (v != null) {
                    v.visibility = View.VISIBLE
                }
            }

            override fun onSuccess() {
                if (v != null) {
                    v.visibility = View.INVISIBLE
                }
                bean.msgStat = 2
                bean.content = outputP
                mView?.notifyAdapterItemChanged(position)
            }

            override fun onFail() {
                if (v != null) {
                    v.visibility = View.INVISIBLE
                }
                bean.msgStat = 3
                mView?.notifyAdapterItemChanged(position)
            }

            override fun onProgress(percent: Float) {

            }
        })
    }

    /**
     * 添加视频
     */
    fun addVideo(mContext: Context, finalVideoPath: String?) {
        var duration: Int
        val videoAlbumPath = FileUtils.getChatFilePath(mContext) + System.currentTimeMillis() + ".jpg"
        val videocompressPath = FileUtils.getChatFilePath(mContext) + System.currentTimeMillis() + ".mp4"
        val media = MediaMetadataRetriever()
        media.setDataSource(finalVideoPath)
        val frameAtTime = media.frameAtTime
        BitmapUtils.saveSmallBitmap2SDCard(frameAtTime, videoAlbumPath)
        duration = Integer.parseInt(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        val chatEntry = ChatEntry()
        chatEntry.content = finalVideoPath + "&&" + videocompressPath
        chatEntry.extra = videoAlbumPath + "&&" + duration
        chatEntry.created = System.currentTimeMillis().toString()
        chatEntry.from = myid!!.toLong()
        chatEntry.to = uid
        chatEntry.face = myface
        chatEntry.type = "gvideo"
        chatEntry.msgId = System.currentTimeMillis().toString()
        chatEntry.chatId = uid
        chatEntry.msgStat = 3
        chatEntry.userId = myid!!.toLong()
        chatEntry.gsid = mainInfoBean!!.gsid
        chatEntry.qsid = mainInfoBean!!.qsid
        val b = ChatDBManager.getInstance(mContext).insertUser(chatEntry)
        if (b) {
            mainInfoBean?.msg = "[视频]"
            mainInfoBean?.created = System.currentTimeMillis()
            mainInfoBean?.newMsgTime = System.currentTimeMillis()
            mainInfoBean?.msgType = "gvideo"
            mainInfoBean?.`object` = ""
            MainDBManager.getInstance(mContext).updateUser(mainInfoBean)
            val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_MINE_VIDEO, chatEntry)
            mView?.addItem(chatEntryChatMultiBean)
            mView?.scrollToBottom()
        }
    }

    /**
     * 添加图片
     */
    fun addImg(mContext: Context, filePath: String?) {
        var bitHeight = 1080
        var bitWidth = 720
        val compresPath = FileUtils.getChatFilePath(mContext) + System.currentTimeMillis() + ".jpg"
        val options = BitmapFactory.Options()
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true
        val bitmap = BitmapFactory.decodeFile(filePath, options)
        if (options.outHeight > 1920 && options.outWidth > 1080) {
            // 缩放图片的尺寸
            val scaleWidth = 1080.toFloat() / options.outWidth
            val scaleHeight = 1920.toFloat() / options.outHeight
            var scale = 0.5f
            if (scaleHeight < scaleWidth) {
                scale = scaleHeight
            } else {
                scale = scaleWidth
            }
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            // 产生缩放后的Bitmap对象
            val resizeBitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(filePath), 0, 0, options.outWidth, options.outHeight, matrix, false)
            BitmapUtils.saveSmallBitmap2SDCard(resizeBitmap, compresPath)
            bitHeight = (options.outHeight * scale).toInt()
            bitWidth = (options.outWidth * scale).toInt()

        } else {
            bitHeight = options.outHeight
            bitWidth = options.outWidth
            BitmapUtils.saveSmallBitmap2SDCard(BitmapFactory.decodeFile(filePath), compresPath)

        }
        val chatEntry = ChatEntry()
        chatEntry.content = compresPath
        chatEntry.extra = bitHeight.toString() + "&&" + bitWidth
        chatEntry.created = System.currentTimeMillis().toString()
        chatEntry.from = myid!!.toLong()
        chatEntry.to = uid
        chatEntry.face = myface
        chatEntry.type = "gpic"
        chatEntry.msgId = System.currentTimeMillis().toString() + ""
        chatEntry.chatId = uid
        chatEntry.msgStat = 2
        chatEntry.userId = myid!!.toLong()
        chatEntry.gsid = mainInfoBean!!.gsid
        chatEntry.qsid = mainInfoBean!!.qsid
        val b = ChatDBManager.getInstance(mContext).insertUser(chatEntry)
        if (b) {
            mainInfoBean?.msg = "[图片]"
            mainInfoBean?.created = System.currentTimeMillis()
            mainInfoBean?.newMsgTime = System.currentTimeMillis()
            mainInfoBean?.msgType = "pic"
            mainInfoBean?.`object` = ""
            MainDBManager.getInstance(mContext).updateUser(mainInfoBean)
            val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_MINE, chatEntry)
            mView?.addItem(chatEntryChatMultiBean)
            mView?.scrollToBottom()
        }
    }

    /**
     * 打开相册
     */
    fun openGallery(activity: AppCompatActivity, requestCode: Int) {
        RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { b ->
                    run {
                        if (b) {
                            PictureSelector.create(activity)
                                    .openGallery(PictureMimeType.ofAll())
                                    .isWeChatStyle(true)
                                    .loadImageEngine(GlideEngine.createGlideEngine())
                                    .isWithVideoImage(true)
                                    .videoMaxSecond(2 * 60)
                                    .maxSelectNum(9)
                                    .forResult(requestCode)
                        } else {
                            TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    /**
     * 拍照
     */
    fun takePhoto(activity: AppCompatActivity, requestCode: Int) {
        RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe { b ->
                    run {
                        if (b) {
                            PictureSelector.create(activity)
                                    .openCamera(PictureMimeType.ofImage())
                                    .forResult(requestCode)
                        } else {
                            TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    /**
     * 录像
     */
    fun recordVideo(activity: AppCompatActivity, requestCode: Int) {
        RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .subscribe { b ->
                    run {
                        if (b) {
                            PictureSelector.create(activity)
                                    .openCamera(PictureMimeType.ofVideo())
                                    .videoQuality(1)
                                    .forResult(requestCode)
                        } else {
                            TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    /**
     * 视频聊天
     */
    fun videoChat(mContext: Context) {
        if (mainInfoBean!!.gsid != 0L) {
            mView?.Toast("暂不支持群视频聊天哦！")
            return
        }
        mContext.startActivity(Intent(mContext, ChatAVActivity::class.java)
                .putExtra("start", "video")
                .putExtra("invoke", "initiator")
                .putExtra("from", myid.toString())
                .putExtra("to", uid.toString())
        )
    }

    /**
     * 语音聊天
     */
    fun audioChat(mContext: Context) {
        if (mainInfoBean!!.gsid != 0L) {
            mView?.Toast("暂不支持群语音聊天哦！")
            return
        }
        mContext.startActivity(Intent(mContext, ChatAVActivity::class.java)
                .putExtra("start", "audio")
                .putExtra("invoke", "initiator")
                .putExtra("from", myid.toString())
                .putExtra("to", uid.toString())
        )
    }


    /**
     * 获取聊天数据
     *
     * @param list
     * @return
     */
    fun getData(mContext: Context, list: List<ChatEntry>, needRefresh: Boolean): List<ChatMultiEntry<ChatEntry>> {
        val chatMultiBeans = ArrayList<ChatMultiEntry<ChatEntry>>()
        Collections.reverse(list)
        var lastTime: Long = 0
        for (i in list.indices) {
            val chatEntry = list[i]

            val from = chatEntry.from
            val to = chatEntry.to
            val created = chatEntry.created
            val gsid = chatEntry.gsid
            val time = java.lang.Long.parseLong(created)
            val isShowCreated = chatEntry.isShowCreated
            if (isShowCreated) {
                if (time - lastTime > 5 * 60 * 1000) {
                    lastTime = time
                } else {
                    chatEntry.isShowCreated = false
                }
            } else {
                if (lastTime != 0L) {
                    if (time - lastTime > 5 * 60 * 1000) {
                        chatEntry.isShowCreated = true
                        lastTime = time
                    } else {
                        chatEntry.isShowCreated = false
                    }
                } else {
                    if (System.currentTimeMillis() - time > 5 * 60 * 1000) {
                        chatEntry.isShowCreated = true
                        lastTime = time
                    } else {
                        chatEntry.isShowCreated = false
                    }
                }
            }

            val type = chatEntry.type
            val content = chatEntry.content
            if (i == list.size - 1 && needRefresh) {
                mainInfoBean!!.`object` = ""
                when {
                    TextUtils.equals(type, "gtxt") -> {
                        mainInfoBean!!.msg = content
                        if (chatEntry.extra != null && chatEntry.extra.isNotEmpty() && !chatEntry.isMsgRead) {
                            mView?.setEmojiView(chatEntry.extra)
                        }
                    }
                    TextUtils.equals(type, "gpic") -> mainInfoBean!!.msg = "[图片]"
                    TextUtils.equals(type, "gvideo") -> mainInfoBean!!.msg = "[视频]"
                    TextUtils.equals(type, "gquestion") -> mainInfoBean!!.msg = "[互动问答]"
                    TextUtils.equals(type, "gaudio") -> {
                        mainInfoBean!!.msg = "[语音]"
                        if (i - 1 > 0 && i - 1 < list.size) {
                            var qsChatBean = list[i - 1]
                            if (TextUtils.equals(qsChatBean.type, "boxquestion")) {
                                mainInfoBean!!.`object` = qsChatBean.content
                            } else {
                                mainInfoBean!!.`object` = ""
                            }
                        }
                    }
                    TextUtils.equals(type, "boxanswer") -> {
                        mainInfoBean!!.msg = content
                        if (i - 1 > 0 && i - 1 < list.size) {
                            var qsChatBean = list[i - 1]
                            if (TextUtils.equals(qsChatBean.type, "boxquestion")) {
                                mainInfoBean!!.`object` = qsChatBean.content
                            }
                        }
                    }
                    else -> mainInfoBean!!.msg = content
                }

                mainInfoBean!!.newMsgTime = java.lang.Long.parseLong(chatEntry.created)
                MainDBManager.getInstance(mContext).updateUser(mainInfoBean)
            }
            chatEntry.isMsgRead = true
            ChatDBManager.getInstance(mContext).updateUser(chatEntry)
            if (from == uid || (gsid != 0L && from != myid!!.toLong())) {
                if (TextUtils.equals(type, "gtxt") || TextUtils.equals(type, "gpic") || TextUtils.equals(type, "boxanswer")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER, chatEntry))
                } else if (TextUtils.equals(type, "gaudio")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER_AUDIO, chatEntry))
                } else if (TextUtils.equals(type, "gvideo")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER_VIDEO, chatEntry))
                } else if (TextUtils.equals(type, "boxquestion")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER_QUESTION, chatEntry))
                }
            } else {
                if (TextUtils.equals(type, "gtxt") || TextUtils.equals(type, "gpic") || TextUtils.equals(type, "boxanswer")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE, chatEntry))
                } else if (TextUtils.equals(type, "gaudio")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_AUDIO, chatEntry))
                } else if (TextUtils.equals(type, "gvideo")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_VIDEO, chatEntry))
                } else if (TextUtils.equals(type, "boxquestion")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_QUESTION, chatEntry))
                }
            }
        }

        return chatMultiBeans
    }


    /**
     * 对比adapter数据 拿到新数据
     *
     * @param list
     * @return
     */
    fun getNewData(mContext: Context, apData: List<ChatMultiEntry<ChatEntry>>): List<ChatMultiEntry<ChatEntry>> {

        var list: List<ChatEntry>? = null
        list = when {
            mainInfoBean!!.qsid != 0L -> ChatDBManager.getInstance(mContext).queryQsUserList(uid, mainInfoBean!!.qsid, 0)
            mainInfoBean!!.gsid != 0L -> ChatDBManager.getInstance(mContext).queryGsUserList(mainInfoBean!!.gsid, 0)
            else -> ChatDBManager.getInstance(mContext).queryUserList(uid, 0)
        }
        val subList = ArrayList<ChatEntry>()
        for (i in apData.indices) {
            subList.add(apData[i].bean)
        }
        list.removeAll(subList)
        val chatMultiBeans = ArrayList<ChatMultiEntry<ChatEntry>>()
        Collections.reverse(list)
        for (i in list.indices) {
            val chatEntry = list[i]
            val from = chatEntry.from
            val to = chatEntry.to
            val chaType = chatEntry.type
            val msgId = chatEntry.msgId
            val type = chatEntry.type
            val gsid = chatEntry.gsid
            val content = chatEntry.content

            if (i == list.size - 1) {
                when {
                    TextUtils.equals(chaType, "gtxt") -> {
                        mainInfoBean?.msg = content
                        if (chatEntry.extra != null && chatEntry.extra.isNotEmpty() && !chatEntry.isMsgRead) {
                            mView?.setEmojiView(chatEntry.extra)
                        }
                    }
                    TextUtils.equals(chaType, "gpic") -> mainInfoBean?.msg = "[图片]"
                    TextUtils.equals(chaType, "gaudio") -> mainInfoBean?.msg = "[语音]"
                    TextUtils.equals(chaType, "gvideo") -> mainInfoBean?.msg = "[视频]"
                    TextUtils.equals(type, "gquestion") -> mainInfoBean?.msg = "[互动问答]"
                    TextUtils.equals(type, "boxanswer") -> {
                        mainInfoBean!!.msg = content
                        if (i - 1 > 0 && i - 1 < list.size) {
                            var qsChatBean = list[i - 1]
                            if (TextUtils.equals(qsChatBean.type, "boxquestion")) {
                                mainInfoBean!!.`object` = qsChatBean.content
                            }
                        }
                    }
                    else -> mainInfoBean?.msg = content
                }
                mainInfoBean?.newMsgTime = java.lang.Long.parseLong(chatEntry.created)
                MainDBManager.getInstance(mContext).updateUser(mainInfoBean)
            }
            chatEntry.isMsgRead = true
            ChatDBManager.getInstance(mContext).updateUser(chatEntry)

            if (from == uid || (gsid != 0L && myid!!.toLong() != from)) {
                if (TextUtils.equals(type, "gtxt") || TextUtils.equals(type, "gpic") || TextUtils.equals(type, "boxanswer")) {
                    chatMultiBeans.add(ChatMultiEntry(ChatMultiEntry.CHAT_OTHER, chatEntry))
                } else if (TextUtils.equals(type, "gaudio")) {
                    chatMultiBeans.add(ChatMultiEntry(ChatMultiEntry.CHAT_OTHER_AUDIO, chatEntry))
                } else if (TextUtils.equals(type, "gvideo")) {
                    chatMultiBeans.add(ChatMultiEntry(ChatMultiEntry.CHAT_OTHER_VIDEO, chatEntry))
                } else if (TextUtils.equals(type, "boxquestion")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_QUESTION, chatEntry))
                }
            } else {
                if (TextUtils.equals(type, "gtxt") || TextUtils.equals(type, "gpic") || TextUtils.equals(type, "boxanswer")) {
                    chatMultiBeans.add(ChatMultiEntry(ChatMultiEntry.CHAT_MINE, chatEntry))
                } else if (TextUtils.equals(type, "gaudio")) {
                    chatMultiBeans.add(ChatMultiEntry(ChatMultiEntry.CHAT_MINE_AUDIO, chatEntry))
                } else if (TextUtils.equals(type, "gvideo")) {
                    chatMultiBeans.add(ChatMultiEntry(ChatMultiEntry.CHAT_MINE_VIDEO, chatEntry))
                } else if (TextUtils.equals(type, "boxquestion")) {
                    chatMultiBeans.add(ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_QUESTION, chatEntry))
                }
            }
        }

        return chatMultiBeans
    }

    override fun deleteChat(mContext: Context, bean: ChatEntry) {
        ChatDBManager.getInstance(mContext).deleteUser(bean)
        mainInfoBean?.let {

            MainDBManager.getInstance(mContext).deleteUser(it)
            EventBus.getDefault().post(NewMsgEvent())
        }
    }

    override fun createQsTitle(qsTxt: String): ChatMultiEntry<ChatEntry> {
        var entry = ChatEntry()
        if (mainInfoBean != null) {
            entry.content = qsTxt
            entry.face = mainInfoBean!!.face
            entry.layout = mainInfoBean!!.sex
            entry.extra = mainInfoBean!!.mood
        } else if (userEntry != null) {
            entry.content = qsTxt
            entry.face = userEntry!!.face
            entry.layout = userEntry!!.sex
            entry.extra = userEntry!!.mood
        }
        var data = ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_QUESTION_TITLE, entry)
        return data
    }

    override fun toOtherActivity(activity: AppCompatActivity, uid: Long) {
        model.getUserInfo(activity, uid = uid.toInt(), callback = object : JsonCallback<BaseEntry<UserEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                if (response!!.isSuccessful) {
                    val userinfor = response.body().userinfor
                    if (userinfor != null) {
                        var intent = Intent(activity, OtherSpaceActivity::class.java).putExtra("bean", userinfor)
                        activity.startActivity(intent)
                        activity.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                    }
                }
            }
        })
    }
}