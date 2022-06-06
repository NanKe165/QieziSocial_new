package com.eggplant.qiezisocial.model

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.contract.DiaryContract
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.fragment.SpaceFragment
import com.lzy.okgo.OkGo
import java.io.File

/**
 * Created by Administrator on 2020/10/16.
 */

class DiaryModel : DiaryContract.Model {
    fun pubDiary(activity:Activity,context: String, file: List<String>, jsonCallback: JsonCallback<BaseEntry<DiaryEntry>>) {
        val params = OkGo.post<BaseEntry<DiaryEntry>>(API.PUB_DIARY)
                .params("w", context)
                .retryCount(0)
                .tag(activity)
        if (file.isNotEmpty())
            file.forEach {
                params.params("file[]", File(it))

            }
        params.execute(jsonCallback)
    }

    fun getDiary(i: Int, uid: String, jsonCallback: JsonCallback<BaseEntry<DiaryEntry>>) {
        OkGo.post<BaseEntry<DiaryEntry>>(API.GET_DIARY)
                .params("b", i)
                .params("u", uid)
                .execute(jsonCallback)
    }
    //访客列表
    fun getVisit(i: Int, uid: String, jsonCallback: JsonCallback<BaseEntry<UserEntry>>, activity: AppCompatActivity) {
        OkGo.post<BaseEntry<UserEntry>>(API.GET_VISIT)
                .tag(activity)
                .params("b", i)
                .params("u", uid)
                .execute(jsonCallback)
    }
    //访问空间
    fun getSpace(fragment: SpaceFragment,uid: String, jsonCallback: JsonCallback<VisitEntry>, spaceFragment: SpaceFragment) {
        OkGo.post<VisitEntry>(API.GET_SPACE)
                .params("u", uid)
                .tag(fragment)
                .tag(spaceFragment)
                .execute(jsonCallback)
    }

    override fun pubDiary(context: String, jsonCallback: JsonCallback<BaseEntry<DiaryEntry>>) {
        OkGo.post<BaseEntry<DiaryEntry>>(API.PUB_DIARY)
                .params("w", context)
                .execute(jsonCallback)
    }

    override fun commentDiary(id: Int, toid: Int, content: String, jsonCallback: JsonCallback<BaseEntry<CommentEntry>>) {
        OkGo.post<BaseEntry<CommentEntry>>(API.DIARY_COMMENT)
                .params("id", id)
                .params("toid", toid)
                .params("w", content)
                .execute(jsonCallback)
    }

    override fun loadMoreComment(id: Int, cPosition: Int, jsonCallback: JsonCallback<BaseEntry<CommentEntry>>) {
        OkGo.post<BaseEntry<CommentEntry>>(API.GET_DIARY_COMMENT)
                .params("id", id)
                .params("b", cPosition)
                .execute(jsonCallback)
    }

    /**
     * 获取日记
     */
    override fun getDiary(i: Int, jsonCallback: JsonCallback<BaseEntry<DiaryEntry>>) {
        OkGo.post<BaseEntry<DiaryEntry>>(API.GET_DIARY)
                .params("b", i)
                .execute(jsonCallback)
    }

    /**
     * 点赞日记
     */
    override fun likDiary(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.LIKE_DIARY)
                .params("id", id)
                .execute(jsonCallback)
    }

    override fun boomDiary(id: Int, callback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.BOOM_DIARY)
                .params("id", id)
                .execute(callback)
    }

    fun secretLove(uid: Int, i: Int, jsonCallback: JsonCallback<BaseEntry<*>>, spaceFragment: SpaceFragment) {
        var params=OkGo.post<BaseEntry<*>>(API.SECRET_LOVE)
                .tag(spaceFragment)
                .params("u",uid)
                if (i==0){
                    params.params("o","add")
                }else{
                    params.params("o","del")
                }
                params.execute(jsonCallback)
    }
}
