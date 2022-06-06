package com.eggplant.qiezisocial.presenter

import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.SpaceContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.ChatModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.TipsUtil
import com.lzy.okgo.model.Response

/**
 * Created by Administrator on 2020/4/27.
 */

class SpacePresenter : BasePresenter<SpaceContract.View>(), SpaceContract.Presenter {
    var model = ChatModel()
    override fun addFriend(activity: AppCompatActivity, uid: Int) {
        model.applyFriend(activity, uid, callback = object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (it.isSuccessful)
                        it.body().msg?.let { TipsUtil.showToast(activity, it) }
                }
            }
        })
    }

    fun setInfoData(bean: UserEntry) {
        mView?.setFace(API.PIC_PREFIX + bean.face)
        mView?.setNick(bean.nick)
        mView?.setSign(bean.sign)
        mView?.setAccount(bean.card)
        mView?.setSex(bean.sex)
        var label = bean.label
        var height = bean.height
        var weight = bean.weight
        var careers = bean.careers
        var wh = ""
        var labelData = ArrayList<String>()
        label?.split(" ")?.forEach {
            if (!TextUtils.isEmpty(it.trim())) {
                labelData.add(it)
            }
        }
        if (!TextUtils.isEmpty(height)) {
            wh += height.replace("cm", "").replace("CM", "") + "cm"
        }
        if (!TextUtils.isEmpty(weight)) {
            wh += " " + weight.replace("kg", "").replace("KG", "") + "kg"
        }
        if (!TextUtils.isEmpty(careers)) {
            labelData.add(careers)
        }
//        mView?.setLabel(bean.birth,bean.sex,wh, labelData)
        mView?.setPic(bean.pic)
        mView?.setAdd(bean.friend)
        mView?.setBirth(bean.birth)
        mView?.setCareer(bean.careers)
        mView?.setEdu(bean.edu)
        mView?.setHW(wh)
        mView?.setInster(bean.label)
        mView?.setObj(bean.`object`)
        mView?.setMood(bean.mood)
        mView?.setIsFriend(bean.friend)
        if (bean.uid == QzApplication.get().infoBean!!.uid) {
            mView?.setAddVisiable(View.GONE)
        }

    }


}
