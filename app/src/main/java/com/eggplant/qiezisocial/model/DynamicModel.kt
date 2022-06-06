package com.eggplant.qiezisocial.model

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.contract.DynamicContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import java.io.File

/**
 * Created by Administrator on 2021/8/19.
 */

class DynamicModel : DynamicContract.Model {
    fun reportDynaimc(activity: AppCompatActivity, s: String, deleteId: Int,txt: String, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.REPORT)
                .params("type", s)
                .params("id", deleteId)
                .params("des", txt)
                .tag(activity)
                .execute(jsonCallback)
    }
    override fun reportDynaimc(activity: AppCompatActivity, s: String, deleteId: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.REPORT)
                .params("type", s)
                .params("id", deleteId)
                .params("des", "")
                .tag(activity)
                .execute(jsonCallback)
    }

    override fun commentDynaimc(id: Int, toid: Int, content: String, jsonCallback: JsonCallback<BaseEntry<CommentEntry>>) {
        OkGo.post<BaseEntry<CommentEntry>>(API.DYNAMIC_COMMENT)
                .params("id", id)
                .params("toid", toid)
                .params("w", content)
                .execute(jsonCallback)
    }

    override fun deleteDynamic(id: Int,sid: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.DELETE_DYNAMIC)
                .params("id", id)
                .params("sid",sid)
                .execute(jsonCallback)
    }

    override fun getMyDynamic(i: Int, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>) {
        OkGo.post<BaseEntry<BoxEntry>>(API.GET_MY_DYNAMIC)
                .params("b", i)
                .execute(jsonCallback)
    }

    override fun like(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.LIKE_DYNAMIC)
                .params("id", id)
                .execute(jsonCallback)
    }

    override fun getDynamic(i: Int, goal: String, sid: String, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>) {
        OkGo.post<BaseEntry<BoxEntry>>(API.GET_DYNAMIC)
                .params("b", i)
                .params("s", goal)
                .params("sid", sid)
                .execute(jsonCallback)
    }

    override fun pubDynamic(txt: String?, goal: String, mediaList: ArrayList<String>, sid: String, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>) {
        val post = OkGo.post<BaseEntry<BoxEntry>>(API.PUB_DYNAMIC)
                .retryCount(0)
                .params("s", goal)
                .params("sid", sid)
        if (txt != null && txt.isNotEmpty()) {
            post.params("w", txt)
        }
        mediaList.forEach {
            post.params("file[]", File(it))
        }
        post.execute(jsonCallback)
    }

    override fun getOtherDynamic(uid: Int, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>) {
        OkGo.post<BaseEntry<BoxEntry>>(API.GET_OTHER_DYNAMIC)
                .params("u", uid)
                .execute(jsonCallback)
    }

    override fun pubDynamic(activity: Activity, txt: String, mediaList: ArrayList<String>, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>) {
        val post = OkGo.post<BaseEntry<BoxEntry>>(API.PUB_DYNAMIC)
                .retryCount(0)
                .tag(activity)
        if (txt != null && txt.isNotEmpty()) {
            post.params("w", txt)
        }
        mediaList.forEach {
            post.params("file[]", File(it))
        }
        post.execute(jsonCallback)
    }
}
