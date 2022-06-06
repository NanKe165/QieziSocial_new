package com.eggplant.qiezisocial.model

import android.content.Context
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.LoginEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import java.io.File

/**
 * Created by Administrator on 2020/4/14.
 */

object LoginModel {
    /**
     * 自动登录
     *
     * @param tag
     * @param phone
     * @param token
     * @param callback
     */
    fun autoLogin(tag: Any, phone: String?, token: String?, longitude: String, latitude: String, site: String, callback: JsonCallback<LoginEntry>) {
        OkGo.post<LoginEntry>(API.AUTO_LOGIN)
                .tag(tag)
                .params("m", phone)
                .params("t", token)
                .params("longitude", longitude)
                .params("latitude", latitude)
                .params("site", site)
                .execute(callback)
    }

    /**
     * 获取验证码
     *
     * @param tag
     * @param phone
     */
    fun getCode(tag: Any, phone: String, callback: JsonCallback<BaseEntry<*>>) {
        OkGo.post<BaseEntry<*>>(API.GET_CODE)
                .tag(tag)
                .params("m", phone)
                .execute(callback)
    }

    /**
     * 短信登录
     *
     * @param tag
     * @param phone
     * @param code
     * @param callback
     */
    fun login(tag: Any, phone: String, code: String, longitude: String, latitude: String, site: String, callback: JsonCallback<LoginEntry>) {
        OkGo.post<LoginEntry>(API.LOGIN)
                .tag(tag)
                .params("m", phone)
                .params("v", code)
                .params("longitude", longitude)
                .params("latitude", latitude)
                .params("site", site)
                .execute(callback)
    }

    /**
     * 短信登录
     *
     * @param tag
     * @param phone
     * @param code
     * @param callback
     */
    fun login(tag: Any, phone: String, code: String, callback: JsonCallback<LoginEntry>) {
        login(tag, phone, code, "", "", "", callback)
    }

    /**
     * 修改个人信息
     *
     */
    fun modifyInfo(tag: Any, key: String, value: String, callback: JsonCallback<BaseEntry<UserEntry>>) {
        OkGo.post<BaseEntry<UserEntry>>(API.MODIFY)
                .tag(tag)
                .params("k", key)
                .params("v", value)
                .execute(callback)
    }

    /**
     * 修改头像
     */
    fun modifyHead(tag: Any, head: String, callback: JsonCallback<BaseEntry<UserEntry>>) {
        OkGo.post<BaseEntry<UserEntry>>(API.MODIFY)
                .tag(tag)
                .params("k", "face")
                .params("file[]", File(head))
                .execute(callback)
    }

    fun register(context: Context, nick: String, sex: String, head: String, jsonCallback: JsonCallback<BaseEntry<UserEntry>>) {
        val params = OkGo.post<BaseEntry<UserEntry>>(API.RRGISTER)
                .tag(context)
                .params("sex", sex)
                .params("nick", nick)
        if (head.isNotEmpty()) {
            params.params("file[]", File(head))
        }
        params.execute(jsonCallback)
    }
}
