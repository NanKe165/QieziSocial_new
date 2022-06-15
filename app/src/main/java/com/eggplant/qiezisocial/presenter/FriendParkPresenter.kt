package com.eggplant.qiezisocial.presenter

import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.FriendParkContract
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean

/**
 * Created by Administrator on 2022/4/6.
 */

class FriendParkPresenter : BasePresenter<FriendParkContract.View>(), FriendParkContract.Presenter {
    override fun setData(queryMainUserList: List<MainInfoBean>) {
        val data = filterData(queryMainUserList)
        mView?.setNewData(data)
    }

    private fun filterData(beans: List<MainInfoBean>): ArrayList<MainInfoBean> {
        val arrayList = ArrayList<MainInfoBean>()
        val idList=ArrayList<Long>()
        if (beans.isNotEmpty()) {
            for (i in beans.indices) {
                val bean = beans[i]
                val msg = bean.msg
                val type = bean.msgType
                val qsid = bean.qsid
                val gsid = bean.gsid
                val extractMark = bean.isExtractMark
                Log.i("FdPark","bean qsid :${bean.qsid}  gsid :${bean.gsid}  type :${bean.type}   extractMark:$extractMark")

                if (gsid != 0L || qsid != 0L) {
                    if (!extractMark) {
                        continue
                    }
                    if (i == beans.size - 1 && !TextUtils.equals(type, "boxanswer")) {
                        bean.`object` = ""
                    }
                    if (TextUtils.equals(bean.type, "gfriendlist") && !TextUtils.isEmpty(msg)&&!idList.contains(bean.uid)) {
                        arrayList.add(bean)
                        idList.add(bean.uid)
                    } else if (TextUtils.equals(bean.type, "temporal") && !TextUtils.isEmpty(msg)&&!idList.contains(bean.uid)) {
                        arrayList.add(bean)
                        idList.add(bean.uid)
                    } else if (TextUtils.equals(bean.type, "gapplylist") && !TextUtils.isEmpty(msg)&&!idList.contains(bean.uid)) {
                        arrayList.add(bean)
                        idList.add(bean.uid)
                    }
                } else if (TextUtils.equals(bean.type, "gfriendlist")&&bean.uid!=2048L&&!idList.contains(bean.uid)) {
                    arrayList.add(bean)
                    idList.add(bean.uid)
                }
            }
        }
        return arrayList
    }
}
