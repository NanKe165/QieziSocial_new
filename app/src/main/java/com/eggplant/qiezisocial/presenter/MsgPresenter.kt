package com.eggplant.qiezisocial.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.MsgContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.MsgMultiltem
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.MsgModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.OtherSpaceActivity
import com.lzy.okgo.model.Response

/**
 * Created by Administrator on 2020/4/14.
 */

class MsgPresenter : BasePresenter<MsgContract.View>(), MsgContract.Presenter {
    private val model: MsgContract.Model

    init {
        model = MsgModel()
    }

    fun setData(beans: List<MainInfoBean>) {
//        var data = filterData(beans)
//        mView?.setAdapterData(data)
        var data = filterData2(beans)
        mView?.setAdapterData2(data)

    }
    fun setHeadData(beans: List<MainInfoBean>){

        val friendList = ArrayList<MainInfoBean>()
        val applyList=ArrayList<MainInfoBean>()
        var hasSystemBean=false
        var systemNumb=0
        if (beans != null && beans.isNotEmpty()) {
            beans.indices
                    .map { beans[it] }
                    .filterTo(applyList) { TextUtils.equals(it.type, "gapplylist") }
            beans.indices
                    .map { beans[it] }
                    .filterTo(friendList) {
                        TextUtils.equals(it.type, "gfriendlist")&&it.uid!=2048L }
            beans.forEach {
                if (it.uid==2048L){
                    hasSystemBean=true
                    systemNumb=it.msgNum
                }
            }
        }
        mView?.setFriendData(friendList)
        mView?.setApplyData(applyList)
        mView?.setSystemData(hasSystemBean,systemNumb)

    }

    private fun filterData2(beans: List<MainInfoBean>): ArrayList<MsgMultiltem<MainInfoBean>> {
        val arrayList = ArrayList<MsgMultiltem<MainInfoBean>>()
        var friendmsgSize = 0
        var title = ""
        if (beans != null && beans.isNotEmpty()) {
            for (i in beans.indices) {
                val bean = beans[i]
                val msg = bean.msg
                val type = bean.msgType
                val qsid = bean.qsid
                val gsid = bean.gsid
                val extractMark = bean.isExtractMark
                if (!extractMark)
                    continue
                if (gsid != 0L || qsid != 0L) {
                    if (i == beans.size - 1 && !TextUtils.equals(type, "boxanswer")) {
                        bean.`object` = ""
                    }
                    if (TextUtils.equals(bean.type, "gfriendlist") && !TextUtils.isEmpty(msg)) {

                        arrayList.add(MsgMultiltem(MsgMultiltem.QUES_MSG, bean))

                    } else if (TextUtils.equals(bean.type, "temporal") && !TextUtils.isEmpty(msg)) {

                        arrayList.add(MsgMultiltem(MsgMultiltem.QUES_MSG, bean))

                    } else if (TextUtils.equals(bean.type, "gapplylist") && !TextUtils.isEmpty(msg)) {

                        arrayList.add(MsgMultiltem(MsgMultiltem.QUES_MSG, bean))
                    }
                }
//                else {
//                    if ((TextUtils.equals(bean.type, "gfriendlist") && !TextUtils.isEmpty(msg))) {
//                        arrayList.add(MsgMultiltem(MsgMultiltem.CHAT_MSG, bean))
//                    } else if (TextUtils.equals(bean.type, "gapplylist")) {
//                        friendmsgSize++
//                        title = "${bean.nick}-请求添加好友"
//                    }
//                    if (friendmsgSize > 99) {
//                        friendmsgSize = 99
//                    }
//                    mView?.setTitle(title)
//                    mView?.setTitleSize(friendmsgSize)
//                }
            }
        }
        return arrayList
    }





//    private fun filterData(beans: List<MainInfoBean>): ArrayList<MainInfoBean> {
//        val arrayList = ArrayList<MainInfoBean>()
//        var answerList = ArrayList<MainInfoBean>()
//        var friendmsgSize = 0
//        var title = ""
//        if (beans != null && beans.isNotEmpty()) {
//            for (i in beans.indices) {
//                val bean = beans[i]
//                val msg = bean.msg
//                val type = bean.msgType
//                val qsid = bean.qsid
//                val gsid = bean.gsid
//                if (gsid != 0L || qsid != 0L) {
//                    if (i == beans.size - 1 && !TextUtils.equals(type, "boxanswer")) {
//                        bean.`object` = ""
//                    }
//                    if (TextUtils.equals(bean.type, "gfriendlist") && !TextUtils.isEmpty(msg)) {
//                        if (TextUtils.equals(type, "boxanswer")) {
//                            answerList.add(0, bean)
//                        } else {
//                            arrayList.add(bean)
//                        }
//                    } else if (TextUtils.equals(bean.type, "temporal") && !TextUtils.isEmpty(msg)) {
//                        if (TextUtils.equals(type, "boxanswer")) {
//                            answerList.add(0, bean)
//                        } else {
//                            arrayList.add(bean)
//                        }
//                    } else if (TextUtils.equals(bean.type, "gapplylist") && !TextUtils.isEmpty(msg)) {
//                        if (TextUtils.equals(type, "boxanswer")) {
//                            answerList.add(0, bean)
//                        } else {
//                            arrayList.add(bean)
//                        }
//                    }
//                } else {
//                    if ((TextUtils.equals(bean.type, "gfriendlist") && bean.msgNum > 0)) {
//                        friendmsgSize += bean.msgNum
//                        title = "${bean.nick}: ${bean.msg}"
//                    } else if (TextUtils.equals(bean.type, "gapplylist")) {
//                        friendmsgSize++
//                        title = "${bean.nick}-请求添加好友"
//                    }
//                    if (friendmsgSize > 99) {
//                        friendmsgSize = 99
//                    }
//                    mView?.setTitle(title)
//                    mView?.setTitleSize(friendmsgSize)
//                }
//            }
//            answerList.forEach {
//                arrayList.add(0, it)
//            }
//
//        }
//        return arrayList
//    }

    override fun deleteChat(context: Context, it: MainInfoBean) {
        it.msg = null
        MainDBManager.getInstance(context).updateUser(it)
    }

    override fun toOtherActivity(activity: Activity, uid: Long) {
        model.getUserInfo(activity, uid = uid.toInt(), callback = object : JsonCallback<BaseEntry<UserEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                if (response!!.isSuccessful) {
                    val userinfor = response.body().userinfor
                    if (userinfor != null) {
                        var intent = Intent(activity, OtherSpaceActivity::class.java).putExtra("bean", userinfor)
                        activity.startActivity(intent)
                        activity.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                    }
                }
            }
        })
    }


}
