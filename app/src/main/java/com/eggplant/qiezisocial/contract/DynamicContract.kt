package com.eggplant.qiezisocial.contract

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback

/**
 * Created by Administrator on 2021/8/19.
 */

interface DynamicContract{
    interface Model {
        fun pubDynamic(txt: String?,goal: String, mediaList: ArrayList<String>, sid:String,jsonCallback: JsonCallback<BaseEntry<BoxEntry>>)
        fun getDynamic(i: Int, goal: String, sid: String,jsonCallback: JsonCallback<BaseEntry<BoxEntry>>)
        fun like(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun getMyDynamic(i: Int, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>)
        fun deleteDynamic(id: Int,sid:Int, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun commentDynaimc(id: Int, toid: Int, content: String, jsonCallback: JsonCallback<BaseEntry<CommentEntry>>)
        fun reportDynaimc(activity: AppCompatActivity, s: String, deleteId: Int, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun getOtherDynamic(uid: Int, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>)
        fun pubDynamic(txt: Activity, goal: String, mediaList: ArrayList<String>, sid: JsonCallback<BaseEntry<BoxEntry>>)

    }

    interface View:BaseView {
        fun showCompressView(progress: String)
        fun hideCompressView()
        fun setNewData(list: List<BoxEntry>)
        fun addData(list: List<BoxEntry>)
        fun loadMoreEnd(b: Boolean)
        fun addPubData(record: BoxEntry)
        fun notifyLikeView(position: Int)
        fun commentSuccess(record: CommentEntry?, position: Int)
        fun showUploadView(progress:String)
    }

    interface Presenter {
        fun openGallery(activity: AppCompatActivity, requesT_PHOTO_ALBUM: Int)
        fun readyTopub(activity: AppCompatActivity, goal:String,sid: String,trimEnd: String?, pubMediaPath: ArrayList<String>, pubMediaType: String)
        fun getDynamic(i: Int, goal: String,sid: String)
        fun like(position: Int, id: Int)
        fun saveFile(mContext: Context, downloadPath: String)
        fun commentDynamic(postion: Int, id: Int, toInt: Int, content: String)
        fun openGalleryVideo(activity: AppCompatActivity, requesT_PHOTO_ALBUM: Int)
        fun reportDynamic(activity: AppCompatActivity, deleteId: Int, deletePs: Int)
        fun deleteDynamic(activity: AppCompatActivity, deleteId: Int, deletePs: Int, deleteSid: Int)
    }
}
