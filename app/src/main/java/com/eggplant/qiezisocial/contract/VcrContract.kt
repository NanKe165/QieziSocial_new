package com.eggplant.qiezisocial.contract

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.VcrEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback

/**
 * Created by Administrator on 2020/7/15.
 */

interface VcrContract{
    interface Model {
        fun reportVcr(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun collectVcr(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun reoprtVcrComment(id: Int,callback: JsonCallback<BaseEntry<*>>)
        fun getFollowVcr(i: Int, call: JsonCallback<BaseEntry<VcrEntry>>)
        fun getMyVcr(i: Int, call: JsonCallback<BaseEntry<VcrEntry>>)
        fun deletetVcr(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun cancelCollect(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun boom(id: Int, param: JsonCallback<BaseEntry<*>>)
    }

    interface View:BaseView {
        fun setNewData(data: List<VcrEntry>?)
        fun addData(data: List<VcrEntry>?)
        fun refreshLikeWithPosition(position: Int, like: Int?)
        fun removeReportVcrWithPosition(position: Int)
        fun refreshCollectWithPosition(position: Int)
        fun deleteVcrWithPosition(clickPosition: Int)
        fun cancelCollectWithPosition(clickPosition: Int)

    }

    interface Presenter {
        fun selectVideo(activity: AppCompatActivity, requesT_CODE_VIDEO: Int)
        fun selectVideoSuccess(mContext: Context, path: String)
        fun recordVideo(activity: AppCompatActivity, requesT_CODE_RECORD: Int)
        fun initData()
        fun loadMoreData(begin:Int)
        fun likeVcr(positon:Int,id: Int)
        fun reportVcr(position: Int, id: Int)
        fun collectVcr(position: Int, id: Int)
        fun cancelCollect(clickPosition: Int, id: Int)
        fun deleteVcr(clickPosition: Int, id: Int)
        fun boom(id: Int)

    }
}
