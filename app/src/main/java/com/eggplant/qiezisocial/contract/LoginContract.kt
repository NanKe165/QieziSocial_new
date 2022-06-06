package com.eggplant.qiezisocial.contract

import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.LoginMsgEntry

/**
 * Created by Administrator on 2020/4/13.
 */

interface LoginContract {
    interface Model{

    }
    interface view :BaseView {
        fun addItem(serviceData: ChatMultiEntry<LoginMsgEntry>, delay:Long)
        fun hintFragment(msgDelayTime:Long)
        fun hintEidt(msgDelayTime:Long)
        fun showEidt(msgDelayTime:Long)
        fun setEditInputType(inputType: Int)
        fun showResendView(msgDelayTime:Long)
        fun showSelectBirthView(msgDelayTime:Long)
        fun showSelectSexView(msgDelayTime:Long)
        fun showSelectHead(msgDelayTime:Long)
        fun showSelectInterest(msgDelayTime:Long)
        fun goMianActivity(delay: Long)
        fun showSelectObject(delay: Long)
        fun goPubActivity(delay: Long)

    }

    interface Presenter{

    }

    /**
     * 登录步骤
     */
    enum class LoginStep{
        //输入手机号
        PHONE,
        //输入验证码
        VCODE,
        //选择生日
        BIRTH,
        //选择性别
        SEX,
        //设置昵称
        NICK,
        //选择头像
        HEAD,
        //选择兴趣
        INTEREST,
        //选择交友目的
        OBJECT,

    }

}
