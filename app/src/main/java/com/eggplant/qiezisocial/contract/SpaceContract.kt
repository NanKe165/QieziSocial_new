package com.eggplant.qiezisocial.contract

import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BaseView

/**
 * Created by Administrator on 2020/4/27.
 */

interface SpaceContract{
    interface Model
    interface View :BaseView {
        fun setFace(s: String)
        fun setNick(nick: String?)
        fun setSign(sign: String?)
        fun setAccount(careers: String?)
        fun setLabel(birth:String?,sex: String?, wh:String,labelData: ArrayList<String>)
        fun setPic(pic: List<String>)
        fun setAdd(friend: String?)
        fun setSex(sex: String?)
        fun setBirth(birth: String?)
        fun setCareer(careers: String?)
        fun setHW(wh: String)
        fun setInster(s: String?)
        fun setEdu(edu: String?)
        fun setObj(s: String?)
        fun setMood(mood: String?)
        fun setIsFriend(friend: String?)
        fun setAddVisiable(visiable: Int)
    }

    interface Presenter {
        fun addFriend(activity: AppCompatActivity, uid: Int)

    }
}
