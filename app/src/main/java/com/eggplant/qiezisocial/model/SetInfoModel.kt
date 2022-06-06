package com.eggplant.qiezisocial.model

import com.eggplant.qiezisocial.contract.SetInfoContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import java.io.File

/**
 * Created by Administrator on 2020/6/22.
 */

class SetInfoModel : SetInfoContract.Model {
    /**
     * 添加相册图片
     */
    override fun addPic(tag: Any?, pic: ArrayList<String>?, jsonCallback: JsonCallback<BaseEntry<UserEntry>>) {
        val request = OkGo.post<BaseEntry<UserEntry>>(API.ADDPIC).tag(tag)
        if (pic != null && pic.size > 0) {
            for (path in pic) {
                request.params("file[]", File(path))
            }
        }
        request.execute(jsonCallback)
    }

    /**
     * 刪除相冊
     *
     * @param tag
     * @param pic      不带http://ossxxxxxx
     * @param callback
     */
    override fun delPic(tag: Any, pic: String?, callback: JsonCallback<BaseEntry<UserEntry>>) {
        OkGo.post<BaseEntry<UserEntry>>(API.DELPIC)
                .tag(tag)
                .params("d", pic)
                .execute(callback)
    }
}
