package com.eggplant.qiezisocial.ui.award

import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.MxEntry
import com.eggplant.qiezisocial.model.JindouModel.getMx
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_mx.*

/**
 * Created by Administrator on 2022/1/27.
 */

class MingxiActivity : BaseActivity() {
    lateinit var adapter: MxAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_mx
    }

    override fun initView() {
        adapter = MxAdapter(null)
        adapter.setEnableLoadMore(true)
        mx_ry.layoutManager = LinearLayoutManager(mContext)
        mx_ry.adapter = adapter
    }

    override fun initData() {
        getMx(0)
    }

    override fun initEvent() {
        mx_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        adapter.setOnLoadMoreListener({
            getMx(adapter.data.size)
        }, mx_ry)
    }

    fun getMx(begin: Int) {
        getMx(begin, object : JsonCallback<BaseEntry<MxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<MxEntry>>?) {
                if (response!!.isSuccessful) {
                    val list = response.body().list
                    if (list != null && list.isNotEmpty()) {
                        if (begin == 0) {
                            adapter.setNewData(list)
                        } else {
                            adapter.addData(list)
                            adapter.loadMoreComplete()
                        }
                    } else {
                        adapter.loadMoreEnd(true)
                    }

                } else {
                    adapter.loadMoreComplete()
                }

            }

        })
    }
}
