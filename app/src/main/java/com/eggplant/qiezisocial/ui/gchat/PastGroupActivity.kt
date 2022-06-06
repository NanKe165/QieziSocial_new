package com.eggplant.qiezisocial.ui.gchat

import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.PChatEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.model.PastTopicModel.getGroupChat
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.chat.ChatAdapter
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_pgroup.*

/**
 * Created by Administrator on 2021/4/27.
 */

class PastGroupActivity : BaseActivity() {
    lateinit var adapter: ChatAdapter
    var gid = 0
    override fun getLayoutId(): Int {
        return R.layout.activity_pgroup
    }

    override fun initView() {
        adapter = ChatAdapter(mContext, null)
        pgroup_ry.layoutManager = LinearLayoutManager(mContext)
        pgroup_ry.adapter = adapter

    }

    override fun initData() {
        gid = intent.getIntExtra("gid", 0)
        val title = intent.getStringExtra("txt")
        val usercount = intent.getIntExtra("usercount", 0)
        val topiccount = intent.getIntExtra("topiccount", 0)
        if (title.isNotEmpty()){
            pgroup_title.text=title
        }
        pgroup_topiccount.text="$topiccount"
        pgroup_usercount.text="$usercount"
        getGroupChat(0, gid, object : JsonCallback<BaseEntry<PChatEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<PChatEntry>>?) {
                if (response!!.isSuccessful) {
                    val list = response.body().list
                    if (list != null && list.isNotEmpty()) {
                        val data = convertData(list)
                        adapter.setNewData(data)
                    }
                }
            }
        })
    }


    override fun initEvent() {
        adapter.setOnLoadMoreListener({
            loadMore()
        }, pgroup_ry)
        pgroup_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })


    }

    private fun loadMore() {
        if (adapter.data.isNotEmpty()) {
            getGroupChat(adapter.data.size, gid, object : JsonCallback<BaseEntry<PChatEntry>>() {
                override fun onSuccess(response: Response<BaseEntry<PChatEntry>>?) {
                    if (response!!.isSuccessful) {
                        val list = response.body().list
                        if (list != null && list.isNotEmpty()) {
                          val  data= convertData(list)
                            adapter.addData(data)
                            adapter.loadMoreComplete()
                        }else{
                            adapter.loadMoreEnd(true)
                        }
                    }
                }
            })
        }else{
            adapter.loadMoreEnd(true)
        }
    }

    private fun convertData(list: List<PChatEntry>): ArrayList<ChatMultiEntry<ChatEntry>> {
        val myid = QzApplication.get().infoBean!!.uid
        var data = ArrayList<ChatMultiEntry<ChatEntry>>()
        list.forEach { it ->
            val convertData = it.convertData(myid)
            val realData = getRealData(myid, convertData)
            if (realData != null)
                data.add(realData)
        }
        return data
    }

    private fun getRealData(myid: Int, chatEntry: ChatEntry): ChatMultiEntry<ChatEntry>? {
        val type = chatEntry.type
        val from = chatEntry.from
        return if (from != myid.toLong()) {
            if (TextUtils.equals(type, "gtxt") || TextUtils.equals(type, "gpic") || TextUtils.equals(type, "boxanswer")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER, chatEntry)
            } else if (TextUtils.equals(type, "gaudio")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER_AUDIO, chatEntry)
            } else if (TextUtils.equals(type, "gvideo")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER_VIDEO, chatEntry)
            } else if (TextUtils.equals(type, "boxquestion")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER_QUESTION, chatEntry)
            } else {
                null
            }
        } else {
            if (TextUtils.equals(type, "gtxt") || TextUtils.equals(type, "gpic") || TextUtils.equals(type, "boxanswer")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE, chatEntry)
            } else if (TextUtils.equals(type, "gaudio")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_AUDIO, chatEntry)
            } else if (TextUtils.equals(type, "gvideo")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_VIDEO, chatEntry)
            } else if (TextUtils.equals(type, "boxquestion")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_QUESTION, chatEntry)
            } else {
                null
            }
        }
    }

}
