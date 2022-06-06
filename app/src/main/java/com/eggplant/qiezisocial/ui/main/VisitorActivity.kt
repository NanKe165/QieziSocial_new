package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.DiaryModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.adapter.VisitorAdapter
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_visitor.*

/**
 * Created by Administrator on 2021/11/3.
 */

class VisitorActivity : BaseActivity() {
    var model = DiaryModel()
    var user: UserEntry? = null
    lateinit var adapter: VisitorAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_visitor
    }

    override fun initView() {
        adapter = VisitorAdapter(null)
        adapter.setEnableLoadMore(true)
        visitor_ry.layoutManager = LinearLayoutManager(mContext)
        visitor_ry.adapter = adapter
    }

    override fun initData() {

        val bean = intent.getSerializableExtra("bean") ?: finish()
        val numb = intent.getIntExtra("numb", 0)
        adapter.newVisitorNumb=numb
        user = bean as UserEntry
        model.getVisit(0, "${user!!.uid}", object : JsonCallback<BaseEntry<UserEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                if (response!!.isSuccessful) {
                    adapter.setNewData(response.body().list)
                }
            }
        },activity)
    }

    override fun initEvent() {
        visitor_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        adapter.setOnItemClickListener { _, view, position ->
            val userEntry = adapter.data[position]
            startActivity(Intent(mContext,OtherSpaceActivity::class.java).putExtra("bean",userEntry))
            overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        adapter.setOnLoadMoreListener({
            if (adapter.data.isEmpty()) {
                adapter.loadMoreEnd(true)
                return@setOnLoadMoreListener
            }
            model.getVisit(adapter.data.size, "${user!!.uid}", object : JsonCallback<BaseEntry<UserEntry>>() {
                override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                    if (response!!.isSuccessful) {
                            if (response.body().list!=null&&response.body().list!!.isNotEmpty()){
                                adapter.addData(response.body().list!!)
                                adapter.loadMoreComplete()
                            }else{
                                adapter.loadMoreEnd(true)
                            }
                    }
                }
            }, activity)
        }, visitor_ry)
    }
}
