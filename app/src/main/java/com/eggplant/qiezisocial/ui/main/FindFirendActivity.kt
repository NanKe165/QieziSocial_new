package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.amap.api.services.help.Tip
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.FindUserEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.adapter.FindUserAdapter
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_find_friend.*

class FindFirendActivity : BaseActivity() {
    lateinit var adapter: FindUserAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_find_friend
    }

    override fun initView() {

        adapter = FindUserAdapter(null);
        find_friend_ry.layoutManager = LinearLayoutManager(mContext)
        find_friend_ry.adapter = adapter
    }

    override fun initData() {

    }

    override fun initEvent() {
        find_friend_topobar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        find_friend_search.setOnClickListener {
            val code = find_friend_edit.text.toString()
            if (code == null || code.isEmpty()) {
                TipsUtil.showToast(mContext, "请输入朋友号")
                return@setOnClickListener
            }
            findUser(code)
        }
        adapter.setOnItemClickListener { _, view, position ->
            val user = adapter.data[position].userinfor
            startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", user))
            overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
    }

    private fun findUser(code: String) {
        OkGo.post<BaseEntry<FindUserEntry>>(API.FIND_USER)
                .params("c", code)
                .tag(activity)
                .execute(object : JsonCallback<BaseEntry<FindUserEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<FindUserEntry>>?) {
                        if (response!!.isSuccessful) {
                            val list = response.body().list
                            if (list != null && list.isNotEmpty()) {
                            }

                            adapter.setNewData(list)


                        }
                    }
                })
    }
}
