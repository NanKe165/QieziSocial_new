package com.eggplant.qiezisocial.ui.main

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.HomeModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.adapter.HomeAdapter
import com.eggplant.qiezisocial.ui.main.fragment.DividerLinearItemDecoration
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_mine_pubbox.*

/**
 * Created by Administrator on 2020/8/4.
 */

class MinePubBoxActivity : BaseActivity() {
    lateinit var adapter: HomeAdapter
    private var model = HomeModel()
    override fun getLayoutId(): Int {
        return R.layout.activity_mine_pubbox
    }

    override fun initView() {
        adapter = HomeAdapter(null)
        adapter.mineType=true
        mine_pbox_ry.layoutManager = LinearLayoutManager(mContext)
        mine_pbox_ry.adapter = adapter
        mine_pbox_ry.addItemDecoration(DividerLinearItemDecoration(1, ContextCompat.getColor(mContext!!, R.color.edit_bg), 0, 0))

    }

    override fun initData() {
        model.getMyQuestion(0, object : JsonCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                response?.let {
                    if (response.isSuccessful && response.body()!!.stat == "ok") {
                        adapter.setNewData(response.body().list)
                    }
                }
            }

        })

    }

    override fun initEvent() {
        adapter.setOnLoadMoreListener({
            loadLoreData()
        }, mine_pbox_ry)
        mine_pbox_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
    }

    private fun loadLoreData() {
        if (adapter.data.isEmpty()) {
            return
        }
        model.getMyQuestion(adapter.data.size, object : JsonCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                response?.let {
                    if (response.isSuccessful && response.body()!!.stat == "ok") {
                        var data = response.body().list
                        if (data == null || data.isEmpty()) {
                            adapter.loadMoreEnd(true)
                        } else {
                            adapter.addData(data as ArrayList)
                            adapter.loadMoreComplete()
                        }
                    }
                }
            }

        })

    }
}
