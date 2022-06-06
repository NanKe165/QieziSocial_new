package com.eggplant.qiezisocial.presenter

import android.text.TextUtils
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.MineContract
import com.eggplant.qiezisocial.model.MineModel

/**
 * Created by Administrator on 2020/4/14.
 */

class MinePresenter : BasePresenter<MineContract.View>(), MineContract.Presenter {
    private val model: MineContract.Model

    init {
        model = MineModel()
    }

    fun setData() {
        if (QzApplication.get().infoBean != null) {
            val infoBean = QzApplication.get().infoBean
//            var age = DateUtils.dateDiff(infoBean?.birth, DateUtils.getCurrentTime_Today(), "yyyy-MM-dd").toString() + "岁"
            mView?.setFace(infoBean?.face)
            mView?.setNick(infoBean?.nick)
            mView?.setSign(infoBean?.sign)
            mView?.setSex(infoBean?.sex)
            mView?.setMood(infoBean?.mood)
            var label = infoBean!!.label
            var height = infoBean.height
            var weight = infoBean.weight
            var careers = infoBean.careers

            var wh = ""
            var labelData = ArrayList<String>()
            label?.split(" ")?.forEach {
                if (!TextUtils.isEmpty(it.trim())) {
                    labelData.add(it)
                }
            }
            if (!TextUtils.isEmpty(height)) {
                wh += height + "cm"
            }
            if (!TextUtils.isEmpty(weight)) {
                wh += " "+weight + "kg"
            }
            if (!TextUtils.isEmpty(careers)) {
                labelData.add(careers)
            }

            mView?.setLabel(infoBean.birth,infoBean.sex,wh, labelData)


        }
    }












}
