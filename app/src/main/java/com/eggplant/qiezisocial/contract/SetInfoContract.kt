package com.eggplant.qiezisocial.contract

import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.entry.AlbumMultiItem
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.setting.SetInfoActivity

/**
 * Created by Administrator on 2020/6/22.
 */

interface SetInfoContract{
    interface Model{
        fun delPic(tag: Any, pic: String?, callback: JsonCallback<BaseEntry<UserEntry>>)
        fun addPic(tag: Any?, pic: ArrayList<String>?, jsonCallback: JsonCallback<BaseEntry<UserEntry>>)
    }
    interface View :BaseView {
        fun setFace(face: String?)
        fun setNick(nick: String?)
        fun setSign(sign: String?)
        fun setHeight(height: String?)
        fun setWeight(weight: String?)
        fun setEdu(edu: String?)
        fun setCareers(careers: String?)
        fun showTost(it: String): Any
        fun setPic(realPic: List<AlbumMultiItem<String>>?)
        fun setLabel(label: String?)
        fun setObject(s: String?)
    }

    interface Presenter {
        fun setInfo()
        fun modifyInfo(activity: AppCompatActivity, s: String, value: String)
        fun setAlbum(infoBean: UserEntry?)
        fun addPic(setInfoActivity: SetInfoActivity, i: Int, requesT_ADD_PIC: Int)
        fun previewPic(activity: AppCompatActivity, requesT_PREV: Int, position: Int)
    }
}
