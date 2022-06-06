package com.eggplant.qiezisocial.presenter

import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.GuessFateContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.GuessFateEntry
import com.eggplant.qiezisocial.model.GuessFateModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.model.Response

/**
 * Created by Administrator on 2020/7/31.
 */

class GuessFatePresenter : BasePresenter<GuessFateContract.View>(), GuessFateContract.Presenter {
    override fun loadMore(size: Int) {
        model.getData(size, object : JsonCallback<BaseEntry<GuessFateEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<GuessFateEntry>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        response.body()?.list?.let {
                            mView?.addData(it)
                        }
                    }
                }
            }
        })
    }

    val model = GuessFateModel()
    override fun initData() {
        model.getData(0, object : JsonCallback<BaseEntry<GuessFateEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<GuessFateEntry>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        response.body()?.let {
                            mView?.setNewData(it.list)
                        }
                    }
                }
            }
        })
    }


}
