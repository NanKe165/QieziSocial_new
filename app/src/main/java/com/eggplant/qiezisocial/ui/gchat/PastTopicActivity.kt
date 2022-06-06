package com.eggplant.qiezisocial.ui.gchat

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.PastTopicEntry
import com.eggplant.qiezisocial.model.PastTopicModel.getPastTopic
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.adapter.PastTopicAdapter
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_past_topic.*

/**
 * Created by Administrator on 2021/4/20.
 */

class PastTopicActivity : BaseActivity() {
    lateinit var adapter: PastTopicAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_past_topic
    }

    override fun initView() {
        adapter = PastTopicAdapter(null)

        ptopic_ry.layoutManager = LinearLayoutManager(mContext)
        ptopic_ry.adapter = adapter

    }

    override fun initData() {
        getPastTopic(0, object : JsonCallback<BaseEntry<PastTopicEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<PastTopicEntry>>?) {
                if (response!!.isSuccessful) {
                    val list = response.body().list
                    adapter.setNewData(list)
                }
            }
        })

    }

    override fun initEvent() {
        adapter.setOnItemClickListener { _, _, position ->
            val pastTopicEntry = adapter.data[position]
            startActivity(Intent(mContext,PastGroupActivity::class.java).putExtra("gid",pastTopicEntry.id).putExtra("topiccount",pastTopicEntry.topiccount).putExtra("usercount",pastTopicEntry.usercount).putExtra("txt",pastTopicEntry.text))
        }
        adapter.setOnLoadMoreListener({
            if (adapter.data.isNotEmpty()) {
                loadmore(adapter.data.size)
            }else{
                adapter.loadMoreEnd(true)
            }
        }, ptopic_ry)
        ptopic_topbar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
    }

    private fun loadmore(begin: Int) {
        getPastTopic(begin, object : JsonCallback<BaseEntry<PastTopicEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<PastTopicEntry>>?) {
                if (response!!.isSuccessful) {
                    val list = response.body().list
                    if (list != null && list.isNotEmpty()) {
                        adapter.addData(list)
                        adapter.loadMoreComplete()
                    } else {
                        adapter.loadMoreEnd(true)
                    }
                }
            }
        })
    }
}
