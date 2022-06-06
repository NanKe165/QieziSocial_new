package com.eggplant.qiezisocial.contract

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.VcrEntry

/**
 * Created by Administrator on 2020/7/15.
 */
interface PubVcrContract {
    interface Model
    interface View:BaseView {
        fun dismissCompressDialog()
        fun setCompressDialogMsg(s: String)
        fun showCompressDialog()
        fun finishActivity(record: VcrEntry)
    }

    interface Presenter {
        fun recordVideo(activity: AppCompatActivity, requesT_CODE_RECORD: Int)
        fun selectVideo(activity: AppCompatActivity, requesT_CODE_VIDEO: Int)
        fun pubVcr(context: Context, title: String, videoPath: String, pathKind: String,imgPath:String ?)
    }
}