package com.eggplant.qiezisocial.contract

import com.eggplant.qiezisocial.base.BaseView

/**
 * Created by Administrator on 2020/4/14.
 */
interface MineContract {
    interface Model {

    }

    interface View:BaseView {
        fun setSex(sex: String?)
        fun setSign(sign: String?)
        fun setNick(nick: String?)
        fun setFace(face: String?)


        fun showTost(it: String) {}
        fun setLabel(birth: String?, sex: String?, wh: String, labelData: ArrayList<String>)
        fun setMood(mood: String?)
    }

    interface Presenter
}