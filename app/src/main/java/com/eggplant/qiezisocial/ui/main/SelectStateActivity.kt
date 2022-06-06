package com.eggplant.qiezisocial.ui.main

import android.app.Activity
import android.support.v7.widget.GridLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.StateEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.LoginModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.adapter.SelectStateAdapter
import com.eggplant.qiezisocial.ui.main.fragment.GridSpacingItemDecoration
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.TopBarListener
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_select_state.*

/**
 * Created by Administrator on 2021/2/2.
 */

class SelectStateActivity : BaseActivity() {
    lateinit var selectAdapter: SelectStateAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_select_state
    }

    override fun initView() {
        selectAdapter = SelectStateAdapter(null)
        state_ry.layoutManager = GridLayoutManager(mContext, 3)
        state_ry.addItemDecoration(GridSpacingItemDecoration(3, resources!!.getDimension(R.dimen.qb_px_20).toInt(), resources!!.getDimension(R.dimen.qb_px_20).toInt()))
        state_ry.adapter = selectAdapter
    }

    override fun initData() {
        var data: ArrayList<StateEntry> = ArrayList()
        data.add(StateEntry(getString(R.string.state1), R.mipmap.state_ku))
        data.add(StateEntry(getString(R.string.state2), R.mipmap.state_liekai))
        data.add(StateEntry(getString(R.string.state3), R.mipmap.state_kaixin))
        data.add(StateEntry(getString(R.string.state4), R.mipmap.state_kun))
        data.add(StateEntry(getString(R.string.state5), R.mipmap.state_fadai))
        data.add(StateEntry(getString(R.string.state6), R.mipmap.state_gudan))
        data.add(StateEntry(getString(R.string.state7), R.mipmap.state_youshang))
        data.add(StateEntry(getString(R.string.state8), R.mipmap.state_fennu))
        data.add(StateEntry(getString(R.string.state9), R.mipmap.state_chigua))
        selectAdapter.setNewData(data)
    }

    override fun initEvent() {
        state_bar.setTbListener(object : TopBarListener {
            override fun onReturn() {
                finish()
            }

            override fun onTxtClick() {
                val selectItem = selectAdapter.selectItem
                if (selectItem == -1) {
                    LoginModel.modifyInfo(activity, "mood", "", object : JsonCallback<BaseEntry<UserEntry>>() {
                        override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                            if (response!!.isSuccessful) {
                                TipsUtil.showToast(mContext,response.body().msg!!)
                                if (response.body().stat == "ok") {
                                    application.infoBean = response.body().userinfor
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                            }
                        }
                    })
                } else {
                    var des = selectAdapter.data[selectItem].des
                    LoginModel.modifyInfo(activity, "mood", des, object : JsonCallback<BaseEntry<UserEntry>>() {
                        override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                            if (response!!.isSuccessful) {
                                TipsUtil.showToast(mContext,response.body().msg!!)
                                if (response!!.body().stat == "ok") {
                                    application.infoBean = response!!.body().userinfor
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                            }
                        }

                    })
                }
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }
}
