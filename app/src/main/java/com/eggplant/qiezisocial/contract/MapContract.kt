package com.eggplant.qiezisocial.contract

import android.location.Location
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback

/**
 * Created by Administrator on 2021/10/21.
 */

interface MapContract{
    interface View:BaseView {
        fun setNewData(list: List<BoxEntry>)
        fun showCompressView(s: String)
        fun hideCompressView()
        fun showUploadView(s: String)
        fun addPubData(record: BoxEntry)
    }

    interface Model {
        fun getData(begin: Int, longitude: Double, latitude: Double, sid: String, s: String, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>)
        fun pubMapDynamic(txt: String?, goal: String, mediaList: ArrayList<String>, sid: String, location: Location, jsonCallback: JsonCallback<BaseEntry<BoxEntry>>)
    }

    interface Presenter {
        fun getData(begin: Int, longitude: Double, latitude: Double, sid: String, s: String)
        fun readyTopub(activity: AppCompatActivity, goal: String, sid: String, txt: String?, pubMediaPath: ArrayList<String>, pubMediaType: String, myLoca: Location)
        fun openGallery(activity: AppCompatActivity, requestCode: Int)
        fun openGalleryVideo(activity: AppCompatActivity, requestCode: Int)
    }
}
