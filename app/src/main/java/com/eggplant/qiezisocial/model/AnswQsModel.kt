package com.eggplant.qiezisocial.model

import android.app.Activity
import android.text.TextUtils
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.contract.AnswQsContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * Created by Administrator on 2020/6/3.
 */
class AnswQsModel : AnswQsContract.Model {


    /**
     * 回答问题
     *
     */
    override fun pubAnswer(tag: Any, id: Int, txt: String, path: String, callback: JsonCallback<BaseEntry<BoxEntry>>) {
        var params = OkGo.post<BaseEntry<BoxEntry>>(API.PUB_ANSWER)
                .tag(tag)
                .params("w", txt)
                .params("id", id)
        if (!TextUtils.isEmpty(path)) {
            params.params("file[]", File(path))
        }
        params.execute(callback)
    }

    /**
     * 设置问题已读
     */
    override fun setReadQuestion(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.READ_QUESTION)
                .params("id", id)
                .execute(jsonCallback)
    }

    override fun getId(activity: Activity) {

        OkGo.post<String>(API.GET_ID)
                .tag(activity)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        if (response.isSuccessful) {
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
                })
    }

    /**
     * 上传附件  音视频、图片
     * @param tag
     * @param extra
     * @param media
     * @param callback
     */
    override fun uploadMedia(tag: Activity, extra: String, media: ArrayList<String>, callback: StringCallback) {
        val request = OkGo.post<String>(API.UPLOAD_MEDIA)
                .tag(tag)
        if (!TextUtils.isEmpty(extra)) {
            request.params("e", extra)
        }
        if (media.isNotEmpty()) {
            for (path in media) {
                request.params("file[]", File(path))
            }
        }
        request.execute(callback)
    }

}