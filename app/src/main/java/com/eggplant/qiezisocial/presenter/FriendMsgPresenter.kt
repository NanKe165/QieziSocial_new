package com.eggplant.qiezisocial.presenter

import android.content.Context
import android.text.TextUtils
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.FriendMsgContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.FriendListModel
import com.eggplant.qiezisocial.model.FriendMsgModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.TipsUtil
import com.lzy.okgo.model.Response

/**
 * Created by Administrator on 2021/7/5.
 */

class FriendMsgPresenter : BasePresenter<FriendMsgContract.View>(), FriendMsgContract.Presenter {
    val model=FriendMsgModel()
    var from: String? = null

    override fun getNearbyInfo(mContext: Context,lat: Double, lng: Double) {
        model.getNearByInfo(lat,lng,object :JsonCallback<BaseEntry<UserEntry>>(){
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                if (response!!.isSuccessful){
                    val stat = response.body().stat
                    val list = response.body().list
                    if (stat=="ok") {
                        if (list != null && list.isNotEmpty()) {
                            mView?.setNearUser(list)
                        }else{
                            TipsUtil.showToast(mContext,"没有新数据了哦")
                        }
                    }else{
                        TipsUtil.showToast(mContext,response.body().msg!!)
                    }
                }
            }
        })
    }
    override fun addBlacklist(mContext: Context, user: MainInfoBean) {
        FriendListModel.addBlack(mContext, user.uid.toInt(), object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful){
                        TipsUtil.showToast(mContext,response.body().msg!!)
                        if (response.body().stat=="ok"){
                            MainDBManager.getInstance(mContext).deleteUser(user)
                            setData(MainDBManager.getInstance(mContext).queryMainUserList())
                        }
                    }
                }
            }
        })
    }
    override fun deleteChat(mContext: Context, it: MainInfoBean) {
        it.msg = null
        it.newMsgTime=0
        MainDBManager.getInstance(mContext).updateUser(it)
        val data = ChatDBManager.getInstance(mContext).queryAllUserList(it.uid)
        if (data!=null) {
            ChatDBManager.getInstance(mContext).deleteUser(data)
            setData(MainDBManager.getInstance(mContext).queryMainUserList())
        }
    }

    override fun deleteUser(mContext: Context, user: MainInfoBean) {
        model.deleteUser(user.uid,object :JsonCallback<BaseEntry<*>>(){
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful){
                    if (response.body().stat=="ok"){
                        MainDBManager.getInstance(mContext).deleteUser(user)
                        setData(MainDBManager.getInstance(mContext).queryMainUserList())
                    }
                }
            }
        })
    }
    override fun setData(beans: List<MainInfoBean>?) {
        val arrayList = ArrayList<MainInfoBean>()
        if (beans != null && beans.isNotEmpty()) {
            if (from!=null&&from=="apply") {
                beans.indices
                        .map { beans[it] }
                        .filterTo(arrayList) { TextUtils.equals(it.type, "gapplylist") }
            }else  if (from!=null&&from=="friend") {
                beans.indices
                        .map { beans[it] }
                        .filterTo(arrayList) {
                            TextUtils.equals(it.type, "gfriendlist")&&it.uid!=2048L
                        }
            }
        }
        mView?.setData(arrayList)

    }

}
