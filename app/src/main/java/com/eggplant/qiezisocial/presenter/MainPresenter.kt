package com.eggplant.qiezisocial.presenter

import android.content.*
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
import android.text.TextUtils
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.MainContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.MainModel
import com.eggplant.qiezisocial.model.boardcast.NetworkConnectChangedReceiver
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.socket.WebSocketService
import com.lzy.okgo.model.Response
import me.leolin.shortcutbadger.ShortcutBadger

/**
 * Created by Administrator on 2020/4/14.
 */

class MainPresenter : BasePresenter<MainContract.View>(), MainContract.Presenter {


    private val model: MainContract.Model
    internal var receiver: NetworkConnectChangedReceiver? = null

    init {
        this.model = MainModel()
    }

    override fun startWebService(context: Context) {
        val intent = Intent(context, WebSocketService::class.java)
        context.startService(intent)
        val intent1 = Intent("WEBS_BROAD")
        intent1.component = ComponentName("com.eggplant.qiezisocial", "com.eggplant.qiezisocial.socket.broadcast.WebSocketBroadCast")
        context.sendBroadcast(intent1)
    }

    override fun setFilter(data: FilterEntry) {
        model.setFilterData(data.goal, data.sid,data.people, object : JsonCallback<BaseEntry<FilterEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<FilterEntry>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().filter!=null) {
                        mView?.setFilterIsCollect(response.body().filter!!.fav)
                    }
                }
            }
        })
    }

    override fun getFilterData() {
        model.getFilterData(object : JsonCallback<BaseEntry<FilterEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<FilterEntry>>?) {
                if (response!!.isSuccessful) {
                    val filter = response.body().filter
                    if (filter != null) {
                        mView?.setFilterData(filter)
                    }
                }
            }
        })
    }

    fun registNetworkReceiver(context: Context) {
        if (receiver == null) {
            receiver = NetworkConnectChangedReceiver()
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(receiver, intentFilter)
    }

    fun unRegistNetworkReciver(context: Context) {
        if (receiver != null) {
            context.unregisterReceiver(receiver)
        }
    }

    /**
     * 初始化聊天--发送消息唯一id ，为聊天时发送消息做准备
     */
    fun initChatMsgUUID(tag: Any) {
        model.getMsgUUID(tag)
    }

    /**
     * 是否有新消息
     * @return
     */
    fun hasNewMsg(context: Context, setShortcut: Boolean): Boolean {
        val beans = MainDBManager.getInstance(context).queryMainUserList()
        var privateMsgSum = 0
        var msgSum=0
        var applyNum = 0
        if (beans != null && beans.size > 0) {
            for (i in beans.indices) {
                val bean = beans[i]
                val msgNum = bean.msgNum
                if (TextUtils.equals(bean.type, "gapplylist")) {
                    applyNum++
                } else if ((TextUtils.equals(bean.type, "gfriendlist") && msgNum > 0) ){
                        msgSum += msgNum
                }else if(TextUtils.equals(bean.type, "temporal")) {
                    if ((bean.gsid != 0L || bean.qsid != 0L) && msgNum > 0){
                        //删除ios端发送的空消息
                        if (TextUtils.isEmpty(bean.msg)){
                            MainDBManager.getInstance(context).deleteUser(bean)
                        }else{
                            privateMsgSum += msgNum
                        }
                    }
                }
            }
        }
        mView?.setMsgNum(privateMsgSum+msgSum+applyNum)
//        mView?.setApplyListNum(msgSum+applyNum)
        if (setShortcut)
            setShortcut(context, privateMsgSum+msgSum+applyNum)
        return msgSum+privateMsgSum > 0
    }

    private var userInfo: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    /**
     *设置 消息提醒数
     */
    private fun setShortcut(context: Context, msgNum: Int) {
        if (userInfo == null) {
            userInfo = context.getSharedPreferences("userEntry", MODE_PRIVATE)
        }
        val newMsgNumb = userInfo?.getInt("newMsgNumb", 0)
        val finalMsgNum = if (msgNum >= 0) msgNum else 0

        if (newMsgNumb == finalMsgNum) {
            return
        }
        if (edit == null) {
            edit = userInfo!!.edit()
        }
        ShortcutBadger.applyCount(context, if (finalMsgNum > 99) 99 else finalMsgNum)
        edit?.putInt("newMsgNumb", finalMsgNum)
        edit?.commit()
    }
}
