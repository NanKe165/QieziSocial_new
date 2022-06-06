package com.eggplant.qiezisocial.model

import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.contract.ChatContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.EmojiEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * Created by Administrator on 2020/4/16.
 */
class ChatModel : ChatContract.Model {
    override fun gChatAddBlockList(from: Long, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.GCHAT_ADD_BLOCKLIST)
                .params("u",from)
                .execute(jsonCallback)
    }
    override fun refuseAdd(activity: AppCompatActivity, uid: Int,jsonCallback:JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.REMOVE_FRIEND)
                .tag(activity)
                .params("u",uid)
                .execute(jsonCallback)
    }

    /**
     * 上传附件  音视频、图片
     *
     * @param tag
     * @param extra
     * @param filePath
     * @param callback
     */
    fun uploadMedia(tag: Any, extra: String, filePath: List<String>?, callback: StringCallback) {
        val request = OkGo.post<String>(API.UPLOAD_MEDIA)
                .tag(tag)
        if (!TextUtils.isEmpty(extra)) {
            request.params("e", extra)
        }
        if (filePath != null && filePath.isNotEmpty()) {
            for (path in filePath) {
                request.params("file[]", File(path))
            }
        }

        request.execute(callback)
    }

    /**
     * 上传附件  音视频、图片
     *
     * @param tag
     * @param extra
     * @param filePath
     * @param callback
     */
    fun uploadMedia(tag: Any, extra: String, filePath: List<String>?, callback: DialogCallback<String>) {
        val request = OkGo.post<String>(API.UPLOAD_MEDIA)
                .tag(tag)
        if (!TextUtils.isEmpty(extra)) {
            request.params("e", extra)
        }
        if (filePath != null && filePath.size > 0) {
            for (path in filePath) {
                request.params("file[]", File(path))
            }
        }

        request.execute(callback)
    }

    /**
     * 获取发送聊天消息唯一id;
     *
     * @param tag
     */
    fun getId(tag: Any) {
        OkGo.post<String>(API.GET_ID)
                .tag(tag)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        if (response.isSuccessful) {
                            Log.i("getid","success")
                            try {
                                val `object` = JSONObject(response.body())
                                val stat = `object`.getString("stat")
                                if (TextUtils.equals(stat, "ok")) {
                                    QzApplication.get().msgUUID = `object`.getString("id")
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                        Log.i("getid","error"+response!!.message())
                    }
                })
    }

    /**
     * 获取聊天emoji
     */
    fun getChatEmoji(callback: JsonCallback<BaseEntry<EmojiEntry>>) {
        OkGo.post<BaseEntry<EmojiEntry>>(API.GET_CHAT_EMOJI)
                .execute(callback)
    }

    /**
     * 添加好友
     */
    fun applyFriend(tag: Any, uid: Int, callback: JsonCallback<BaseEntry<*>>) {
        applyFriend(tag,uid,"",callback)

    }
    fun applyFriend(tag: Any, uid: Int, msg: String, callback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.ADD_FRIEND)
                .tag(tag)
                .params("u", uid)
                .params("mes", msg)
                .execute(callback)
    }
    fun getUserInfo(tag: Any,uid: Int,callback: JsonCallback<BaseEntry<UserEntry>>){
        OkGo.post<BaseEntry<UserEntry>>(API.GET_USERINFO)
                .tag(tag)
                .params("uid", uid)
                .execute(callback)
    }
}