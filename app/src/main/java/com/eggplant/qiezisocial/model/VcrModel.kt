package com.eggplant.qiezisocial.model

import com.eggplant.qiezisocial.contract.VcrContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.VcrCommentEntry
import com.eggplant.qiezisocial.entry.VcrEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import java.io.File

/**
 * Created by Administrator on 2020/7/31.
 */

class VcrModel : VcrContract.Model {
    override fun boom(id: Int, param: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.DIS_CZG)
                .params("id", id)
                .execute(param)
    }

    /**
     * 取消关注
     */
    override fun cancelCollect(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.UNATT_CZG)
                .params("id", id)
                .execute(jsonCallback)
    }

    /**
     * 删除丑照馆
     */
    override fun deletetVcr(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.DELETE_CZG)
                .params("id", id)
                .execute(jsonCallback)
    }

    /**
     * 举报丑照馆评论
     */
    override fun reoprtVcrComment(id: Int, callback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.REPORT_COMMENT)
                .params("id", id)
                .execute(callback)
    }

    /**
     * 收藏Vcr
     */
    override fun collectVcr(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.ATT_CZG)
                .params("id", id)
                .execute(jsonCallback)
    }

    /**
     * 举报Vcr
     */
    override fun reportVcr(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.REPORT_CZG)
                .params("id", id)
                .execute(jsonCallback)
    }

    /**
     *获取丑照馆列表
     *
     */
    fun getVcr(begin: Int, callback: JsonCallback<BaseEntry<VcrEntry>>) {
        OkGo.post<BaseEntry<VcrEntry>>(API.GET_CZG)
                .params("b", begin)
                .execute(callback)

    }

    /**
     * 获取我关注的vcr
     */
    override fun getFollowVcr(begin: Int, call: JsonCallback<BaseEntry<VcrEntry>>) {
        OkGo.post<BaseEntry<VcrEntry>>(API.GET_ATT_CZG)
                .params("b", begin)
                .execute(call)
    }

    /**
     * 获取我的Vcr列表
     */
    override fun getMyVcr(begin: Int, call: JsonCallback<BaseEntry<VcrEntry>>) {
        OkGo.post<BaseEntry<VcrEntry>>(API.GET_MY_CZG)
                .params("b", begin)
                .execute(call)
    }

    /**
     * 发布Vcr
     *
     */
    fun pubVcr(title: String, files: List<String>, callback: JsonCallback<BaseEntry<VcrEntry>>) {
        var params = OkGo.post<BaseEntry<VcrEntry>>(API.PUB_CZG)
                .params("title", title)
        files.forEach {
            params.params("file[]", File(it))
        }
        params.execute(callback)

    }

    /**
     * 点赞丑照馆
     */
    fun likeVcr(id: Int, callback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.LIKE_CZG)
                .params("id", id)
                .execute(callback)
    }

    /**
     * 获取评论列表
     */
    fun getVcrComment(id: Int, begin: Int, callback: JsonCallback<BaseEntry<VcrCommentEntry>>) {

        OkGo.post<BaseEntry<VcrCommentEntry>>(API.GET_CZG_COMMENT)
                .params("id", id)
                .params("b", begin)
                .execute(callback)
    }

    /**
     * 发布评论
     */
    fun pubVcrComment(id: Int, title: String, callback: JsonCallback<BaseEntry<VcrCommentEntry>>) {

        OkGo.post<BaseEntry<VcrCommentEntry>>(API.PUB_CZG_COMMENT)
                .params("id", id)
                .params("title", title)
                .execute(callback)
    }
}
