package com.eggplant.qiezisocial.ui.main

import android.text.TextUtils
import android.util.Log
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.event.GreetSbEvent
import com.eggplant.qiezisocial.event.RemoveGreetSbEvent
import com.eggplant.qiezisocial.event.SocketMsgEvent
import com.eggplant.qiezisocial.model.API
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_greetsb_detail.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2022/4/14.
 */

class GreetSbDetailActivity : BaseActivity() {
    lateinit var bean:GreetSbEvent
    override fun getLayoutId(): Int {
        return R.layout.activity_greetsb_detail
    }

    override fun initView() {
        bean= intent.getSerializableExtra("data") as GreetSbEvent
        greetsb_detail_nick.text= bean.from_userinfor.nick
        when(bean.act){
            "ggreet"->{
                greetsb_detail_txt.text="给你打个招呼"
                greetsb_detail_txt.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_hello,0,0,0)
                greetsb_detail_reply.text="收到"
                greetsb_detail_flow.setEmojiType(mContext,1)
            }
            "gbutter"->{
                if (application.loginEntry?.userinfor?.sex=="男") {
                    greetsb_detail_txt.text="今天你最帅"
                }else{
                    greetsb_detail_txt.text="今天你最美"
                }
                greetsb_detail_txt.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_good,0,0,0)
                greetsb_detail_reply.text="心满意足"
                greetsb_detail_flow.setEmojiType(mContext,2)
            }
            "gcomfort"->{
                greetsb_detail_txt.text="你好久都没理我啦"
                greetsb_detail_txt.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_comfort,0,0,0)
                greetsb_detail_reply.text="安慰一下"
                greetsb_detail_flow.setEmojiType(mContext,3)
            }
        }
        Glide.with(mContext).load(API.PIC_PREFIX+bean.from_userinfor.face).into(greetsb_detail_head)
        onMsgRead()

    }

    private fun onMsgRead() {
        val `object` = JSONObject()
        `object`.put("type", "message")
        `object`.put("act", "gread")
        `object`.put("created", System.currentTimeMillis())
        `object`.put("range", "private")
        `object`.put("from", bean.from)
        `object`.put("to", bean.to)
        `object`.put("id", bean.id)
        EventBus.getDefault().post(SocketMsgEvent(`object`.toString()))
        EventBus.getDefault().post(RemoveGreetSbEvent(bean))
    }

    override fun initData() {

    }

    override fun initEvent() {
        greetsb_detail_reply.setOnClickListener {
            when(bean.act){
                "ggreet"->{
                    replyAudio()
                }
                "gbutter"->{
                    replyTxt("多谢夸奖",2)
                }
                "gcomfort"->{
                    replyTxt("安慰安慰你",3)
                }
            }
            finish()
        }
    }

    private fun replyAudio() {
        val oj= JSONObject()
        oj.put("type", "message")
        oj.put("act", "greplyaudio")
        oj.put("created", System.currentTimeMillis())
        oj.put("from", getFromid())
        oj.put("to", bean.from_userinfor.uid)
        oj.put("id",  getMsgId())
        val data = JSONObject()
        data.put("created", System.currentTimeMillis())
        data.put("txt","哼哼")
        data.put("replyType",1)
        oj.put("data",data)
        EventBus.getDefault().post(SocketMsgEvent(oj.toString()))
    }

    private fun replyTxt(s: String,replyType:Int) {
        val oj= JSONObject()
        oj.put("type", "message")
        oj.put("act", "greplytxt")
        oj.put("created", System.currentTimeMillis())
        oj.put("from", getFromid())
        oj.put("to", bean.from_userinfor.uid)
        oj.put("id",  getMsgId())
        val data = JSONObject()
        data.put("created", System.currentTimeMillis())
        data.put("txt",s)
        data.put("replyType",replyType)
        oj.put("data",data)
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
    override fun onResume() {
        super.onResume()
        greetsb_detail_flow.startAnim()
    }

    override fun onPause() {
        super.onPause()
        greetsb_detail_flow.pauseAnim()
    }

    override fun onDestroy() {
        greetsb_detail_flow.stopAnim()
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
//        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
        overridePendingTransition(R.anim.close_enter,R.anim.close_exit4)
    }
}
