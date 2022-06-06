package com.eggplant.qiezisocial.ui.setting

import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BlackListEntry
import com.eggplant.qiezisocial.model.FriendListModel.getBlackList
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.setting.adapter.BlackListAdapter
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_blacklist.*

/**
 * Created by Administrator on 2020/6/11.
 */

class BlackListActivity : BaseActivity() {
    var adapter: BlackListAdapter? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_blacklist
    }

    override fun initView() {
        adapter = BlackListAdapter(null)
        black_ry.layoutManager = LinearLayoutManager(mContext)
        black_ry.adapter = adapter
    }


    override fun initData() {
        getBlackList(activity, 0, object : JsonCallback<BaseEntry<BlackListEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BlackListEntry>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        adapter?.setNewData(it.body().black)
                    }
                }
            }
        })
    }


    override fun initEvent() {
        black_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        adapter?.setOnLoadMoreListener({
            var size = adapter?.data?.size
            if (size != null && size > 0) {
                getBlackList(activity, size, object : JsonCallback<BaseEntry<BlackListEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<BlackListEntry>>?) {
                        response?.let {
                            if (it.isSuccessful) {
                                if (it.body().black!=null&& it.body().black!!.isNotEmpty()){
                                    adapter?.addData(it.body().black!!)
                                    adapter?.loadMoreComplete()
                                }else{
                                    adapter?.loadMoreEnd(true)
                                }
                            }else {
                                adapter?.loadMoreEnd(true)
                            }
                        }
                    }
                })
            }
        }, black_ry)
    }

}
