package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.event.GreetSbEvent
import com.eggplant.qiezisocial.event.RemoveGreetSbEvent
import com.eggplant.qiezisocial.event.SocketMsgEvent
import com.eggplant.qiezisocial.ui.main.adapter.GreetSbAdapter
import com.eggplant.qiezisocial.widget.topbar.TopBarListener
import kotlinx.android.synthetic.main.activity_blacklist.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * Created by Administrator on 2022/4/14.
 */

class GreetSbListActivity : BaseActivity() {
    lateinit var adapter: GreetSbAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_blacklist
    }

    override fun initView() {
        black_bar.setTitle("消息")
        black_bar.setRightTxt("清空")
        adapter = GreetSbAdapter(null)
        black_ry.layoutManager = LinearLayoutManager(mContext)
        black_ry.adapter = adapter
    }

    override fun initData() {
        val data = intent.getSerializableExtra("data") as ArrayList<GreetSbEvent>?
        adapter.setNewData(data)
    }

    override fun initEvent() {
        black_bar.setTbListener(object : TopBarListener {
            override fun onReturn() {
                finish()
            }

            override fun onTxtClick() {
                adapter.data.forEach {
                    onMsgRead(it)
                }
                adapter.data.clear()
                adapter.notifyDataSetChanged()
            }
        })
        adapter.setOnItemClickListener { _, view, position ->
            val data = adapter.data[position]
            startActivity(Intent(mContext, GreetSbDetailActivity::class.java).putExtra("data", data))
            overridePendingTransition(R.anim.open_enter4,R.anim.open_exit3)
            adapter.remove(position)
        }
    }

    private fun onMsgRead(bean: GreetSbEvent) {
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
}
