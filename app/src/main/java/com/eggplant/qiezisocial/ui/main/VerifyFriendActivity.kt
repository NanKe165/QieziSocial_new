package com.eggplant.qiezisocial.ui.main

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.event.SmallTxtEvent
import com.eggplant.qiezisocial.event.SocketMsgEvent
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.ChatModel
import com.eggplant.qiezisocial.model.FriendListModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.socket.event.NewMsgEvent
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_verifyfriend.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2021/2/25.
 */

class VerifyFriendActivity : BaseActivity() {
    var from: String? = ""
    var user: UserEntry? = null
    var bean: MainInfoBean? = null
    var chatModel = ChatModel()
    override fun getLayoutId(): Int {
        return R.layout.activity_verifyfriend
    }

    override fun initView() {
        from = intent.getStringExtra("from")
        user = intent.getSerializableExtra("user") as UserEntry?
        bean = intent.getSerializableExtra("bean") as MainInfoBean?
        if (from == "add") {
            vfriend_bar.setTitle("申请添加好友")
            vfriend_hint.text = "发送添加朋友申请"
            vfriend_sure.text = "发送申请"
        } else {
            vfriend_bar.setTitle("通过朋友验证")
            vfriend_hint.text = "添加朋友申请"
            vfriend_edit.isFocusable = false
            vfriend_sure.text = "通过"
            vfriend_refuse.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        var face: String? = ""
        var sex: String? = "男"
        var nick: String? = ""
        var sign: String? = ""
        var mood: String? = ""
        if (from == "add" && user != null) {
            face = API.PIC_PREFIX + user!!.face
            sex = user!!.sex
            nick = user!!.nick
            sign = user!!.sign
            mood = user!!.mood
            vfriend_edit.setText("我是${application.infoBean!!.nick}")
        } else if (bean != null) {
            face = API.PIC_PREFIX + bean!!.face
            sex = bean!!.sex
            nick = bean!!.nick
            mood = bean!!.mood
            var msg = bean!!.message
            if (from == "add") {
                vfriend_edit.setText("我是${application.infoBean!!.nick}")
            } else if (TextUtils.isEmpty(msg)) {
                vfriend_edit.setText("我是${bean!!.nick}")
            } else {
                vfriend_edit.setText(msg)
            }
        } else {
            return
        }

        Glide.with(mContext).load(face).into(vfriend_head)
        if (sex == "女") {
            vfriend_sex.setImageResource(R.mipmap.sex_girl_big)
        }
        vfriend_nick.text = nick
        if (TextUtils.isEmpty(sign)) {
            vfriend_sign.visibility = View.GONE
        } else {
            vfriend_sign.text = sign
        }
        if (TextUtils.isEmpty(mood)) {
            vfriend_state.visibility = View.GONE
        } else {
            vfriend_state.visibility = View.VISIBLE
            vfriend_state_tv.setTextColor(ContextCompat.getColor(mContext, R.color.state_select))
            setStateIcon(mood)
        }


    }

    override fun initEvent() {
        vfriend_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        vfriend_sure.setOnClickListener {
            if (from == "add") {
                addFriend()
            } else {
                agreeAdd()
            }
        }
        vfriend_refuse.setOnClickListener {
            refuseAdd()
        }
    }

    private fun refuseAdd() {
        chatModel.refuseAdd(activity, bean!!.uid.toInt(), object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {
                        TipsUtil.showToast(mContext, "您已拒绝添加好友")
                        MainDBManager.getInstance(mContext).deleteUser(bean)
                        finish()
                    }

                }
            }

        })
    }

    private fun addFriend() {
        var uid = 0
        if (user != null)
            uid = user!!.uid
        else if (bean != null)
            uid = bean!!.uid.toInt()
        chatModel.applyFriend(activity, uid, vfriend_edit.text.toString(), object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        it.body().msg?.let { TipsUtil.showToast(activity, it) }
                        if (it.body().stat == "ok") {
                            finish()
                        }
                    }
                }
            }
        })
    }

    private fun agreeAdd() {
        FriendListModel.agreeAdd(activity, bean!!.uid.toInt(), object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        if (response.body().stat.equals("ok")) {
                            TipsUtil.showToast(mContext, "已添加好友${bean!!.nick}")
                            val chatEntry = ChatEntry()
                            chatEntry.type = "gtxt"
                            chatEntry.layout = ""
                            chatEntry.face = bean!!.face
                            chatEntry.created = System.currentTimeMillis().toString()
                            chatEntry.content = "我通过了你的朋友验证请求，现在开始聊天吧"
                            chatEntry.extra = ""
                            chatEntry.from = bean!!.uid.toLong()
                            chatEntry.msgId = System.currentTimeMillis().toString() + ""

                            chatEntry.chatId = bean!!.uid.toLong()
                            chatEntry.to = application.infoBean!!.uid.toLong()
                            chatEntry.userId = application.infoBean!!.uid.toLong()
                            chatEntry.isMsgRead = false
                            chatEntry.msgStat = 1
                            val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_OTHER, chatEntry)
                            val b = ChatDBManager.getInstance(activity).insertUser(chatEntry)


                            sendAddSuccessMsg("我通过了你的朋友验证请求，现在开始聊天吧",application.infoBean!!.uid,bean!!.uid)
                            bean!!.type = "gfriendlist"
                            bean!!.msg = "已经通过好友验证成为好友"
                            bean!!.created = System.currentTimeMillis()
                            bean!!.newMsgTime = System.currentTimeMillis()
                            bean!!.msgType = "gtxt"
                            bean!!.msgNum=1
                            bean!!.`object` = ""
                            bean!!.qsType = ""

                            MainDBManager.getInstance(mContext).updateUser(bean)
                            EventBus.getDefault().post(NewMsgEvent())
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            response.body().msg?.let { TipsUtil.showToast(mContext, it) }
                        }
                    }
                }
            }
        })
    }

    private fun sendAddSuccessMsg(content: String, from: Int, to: Long) {

        val id = QzApplication.get().msgUUID
        QzApplication.get().msgUUID = "0"
        getMsgId()
        val `object` = JSONObject()
        try {
            `object`.put("type", "message")
            `object`.put("act", "gtxt")
            `object`.put("created", System.currentTimeMillis())
            `object`.put("range", "private")
            `object`.put("from", from)
            `object`.put("to", to)
            `object`.put("id", id)

            val data = JSONObject()
            data.put("created", System.currentTimeMillis())
            data.put("expire", System.currentTimeMillis())
            data.put("layout", "")
            data.put("extra", "")
            val conXml = StringBuilder()
            conXml.append("<txt>")
                        .append(content)
                        .append("</txt>")

            data.put("content", conXml.toString())
            `object`.put("data", data)
            Log.e("ChatPresenter", "dataString:${`object`.toString()}")
            EventBus.getDefault().post(SocketMsgEvent(`object`.toString()))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun getMsgId() {
        OkGo.post<String>(API.GET_ID)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        if (response.isSuccessful) {
                            Log.i("getid","success")
                            try {
                                val `object` = JSONObject(response.body())
                                val stat = `object`.getString("stat")
                                if (TextUtils.equals(stat, "ok")) {
                                    QzApplication.get().msgUUID = `object`.getString("id")
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                        Log.i("getid","error"+response!!.message())
                    }
                })
    }

    private fun setStateIcon(mood: String?) {
        var statedraw = 0
        when (mood) {
            getString(R.string.state1) -> {
                statedraw = R.mipmap.mine_state_ku
            }
            getString(R.string.state2) -> {
                statedraw = R.mipmap.mine_state_liekai
            }
            getString(R.string.state3) -> {
                statedraw = R.mipmap.mine_state_kaixin
            }
            getString(R.string.state4) -> {
                statedraw = R.mipmap.mine_state_kun
            }
            getString(R.string.state5) -> {
                statedraw = R.mipmap.mine_state_fadai
            }
            getString(R.string.state6) -> {
                statedraw = R.mipmap.mine_state_gudan
            }
            getString(R.string.state7) -> {
                statedraw = R.mipmap.mine_state_youshang
            }
            getString(R.string.state8) -> {
                statedraw = R.mipmap.mine_state_fennu
            }
            getString(R.string.state9) -> {
                statedraw = R.mipmap.mine_state_chigua
            }
        }
        vfriend_state_tv.setCompoundDrawablesWithIntrinsicBounds(statedraw, 0, 0, 0)
    }
}
