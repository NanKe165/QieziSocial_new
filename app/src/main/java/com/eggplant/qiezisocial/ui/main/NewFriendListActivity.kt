package com.eggplant.qiezisocial.ui.main

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.FriendListModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.adapter.NewFriendAdapter
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_newfriendlist.*
import java.util.*

/**
 * Created by Administrator on 2020/6/5.
 */

class NewFriendListActivity : BaseActivity() {
    lateinit var adaper: NewFriendAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_newfriendlist
    }

    override fun initView() {
        adaper = NewFriendAdapter(null)
        newfriend_ry.layoutManager = LinearLayoutManager(mContext)
        newfriend_ry.adapter = adaper
        adaper.bindToRecyclerView(newfriend_ry)
        adaper.setEmptyView(R.layout.layout_empty_newfriend)
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        var mainData = MainDBManager.getInstance(mContext).queryMainUserList()
        adaper.setNewData(getData(mainData))
    }
    private fun getData(mainData: List<MainInfoBean>): List<MainInfoBean> {
        val firendList = ArrayList<MainInfoBean>()
        if (mainData != null && mainData.isNotEmpty()) {
            mainData.indices
                    .map { mainData[it] }
                    .filterTo(firendList) {
                        TextUtils.equals(it.type, "gapplylist")
                    }
        }
        return firendList
    }

    override fun initEvent() {
        newfriend_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                activity.finish()
            }
        })
        adaper.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.ap_new_friend_agree) {
                var user = adaper.data[position]
                agreeAdd(user.uid, position)
            }
        }
        adaper.setOnItemClickListener { _, view, position ->
            val mainInfoBean = adaper.data[position]
            startActivity(Intent(mContext,VerifyFriendActivity::class.java).putExtra("bean",mainInfoBean))
        }
    }

    private fun agreeAdd(u: Long, position: Int) {
        FriendListModel.agreeAdd(activity, u.toInt(), object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        response.body().msg?.let { TipsUtil.showToast(mContext, it) }
                        if (response.body().stat.equals("ok") && position < adaper.data.size) {
                            val mainInfoBean = adaper.data[position]
                            mainInfoBean.type="gfriendlist"
                            MainDBManager.getInstance(mContext).updateUser(mainInfoBean)
                            adaper.remove(position)
                            setResult(Activity.RESULT_OK)
                        }
                    }
                }
            }

        })
    }
}
