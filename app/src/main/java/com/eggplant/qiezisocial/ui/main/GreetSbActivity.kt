package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.event.SocketMsgEvent
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.MsgModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_greetsb.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2022/4/6.
 */

class GreetSbActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_greetsb
    }

    private var fdType = 0
    var bean: MainInfoBean? = null
    override fun initView() {

    }

    override fun initData() {
        bean = intent.getSerializableExtra("bean") as MainInfoBean?
        if (bean == null) {
            finish()
            return
        }
        if (bean!!.sex == "女") {
            greetsb_sex.setImageResource(R.mipmap.sex_girl)
        }
        Glide.with(mContext).load(API.PIC_PREFIX + bean!!.face).into(greetsb_head)
        if (bean!!.type == "gfriendlist") {
            fdType = 1
        }
        if (fdType == 1) {
            greetsb_add_tv.text = "私聊"
            greetsb_add_tv.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.icon_chat_top,  0, 0)
        }
    }

    override fun initEvent() {
        greetsb_bg.setOnClickListener {
            finish()
        }
        greetsb_hello_gp.setOnClickListener {
            sendHello()
            finish()
        }
        greetsb_good_gp.setOnClickListener {
            sendGood()
            finish()
        }
        greetsb_comfort_gp.setOnClickListener {
            sendComfort()
            finish()
        }
        greetsb_add_gp.setOnClickListener {
            if (fdType == 0) {
                startActivity(Intent(mContext, VerifyFriendActivity::class.java).putExtra("bean", bean).putExtra("from", "add"))
            } else {
                startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean", bean))
            }
            finish()
        }
        greetsb_head.setOnClickListener {
            toOtherAtivity()

        }
    }

    private fun toOtherAtivity() {
        MsgModel().getUserInfo(activity,bean!!.uid.toInt(),object : JsonCallback<BaseEntry<UserEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                if (response!!.isSuccessful) {
                    val userinfor = response.body().userinfor
                    if (userinfor != null) {
                        var intent = Intent(activity, OtherSpaceActivity::class.java).putExtra("bean", userinfor)
                        startActivity(intent)
                        overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                    }
                }
            }
        })
    }

    private fun sendComfort() {
        val oj=JSONObject()
        oj.put("type", "message")
        oj.put("act", "gcomfort")
        oj.put("created", System.currentTimeMillis())
        oj.put("from", getFromid())
        oj.put("to", bean!!.uid)
        oj.put("id",  getMsgId())
        Log.i("GreetAc","$oj")
        EventBus.getDefault().post(SocketMsgEvent(oj.toString()))

    }

    private fun sendGood() {
        val oj=JSONObject()
        oj.put("type", "message")
        oj.put("act", "gbutter")
        oj.put("created", System.currentTimeMillis())
        oj.put("from", getFromid())
        oj.put("to", bean!!.uid)
        oj.put("id",  getMsgId())
        Log.i("GreetAc","$oj")
        EventBus.getDefault().post(SocketMsgEvent(oj.toString()))
    }

    private fun sendHello() {
        val oj=JSONObject()
        oj.put("type", "message")
        oj.put("act", "ggreet")
        oj.put("created", System.currentTimeMillis())
        oj.put("from", getFromid())
        oj.put("to", bean!!.uid)
        oj.put("id",  getMsgId())
        Log.i("GreetAc","$oj")
        EventBus.getDefault().post(SocketMsgEvent(oj.toString()))
    }

    private fun getMsgId() :String{
        val id = QzApplication.get().msgUUID
        QzApplication.get().msgUUID = "0"
        OkGo.post<String>(API.GET_ID)
                .tag(activity)
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
        return id

    }

    private fun getFromid() :Int{
        return application.loginEntry?.userinfor?.uid ?: 0
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter2, R.anim.close_exit2)
    }
}
