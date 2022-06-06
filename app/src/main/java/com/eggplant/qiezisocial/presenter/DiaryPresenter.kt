package com.eggplant.qiezisocial.presenter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.DiaryContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.entry.DiaryEntry
import com.eggplant.qiezisocial.event.DiaryEvent
import com.eggplant.qiezisocial.model.DiaryModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.extend.PubDiaryActivity
import com.eggplant.qiezisocial.ui.extend.RecordVideoActivity
import com.eggplant.qiezisocial.utils.GlideEngine
import com.eggplant.qiezisocial.utils.TipsUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.okgo.model.Response
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus

/**
 * Created by Administrator on 2020/10/15.
 */

class DiaryPresenter : BasePresenter<DiaryContract.View>(), DiaryContract.Presenter {
    override fun selectVideoSuccess(mContext: Context, imgs: ArrayList<LocalMedia>) {
        var type="pic"
        imgs.forEach {
            if (it.mimeType.contains("video/"))
                type="video"
        }
        mContext.startActivity(Intent(mContext,PubDiaryActivity::class.java).putExtra("imgs",imgs).putExtra("mediaType", type))
    }
    override fun recordVideo(activity: AppCompatActivity, requesT_CODE_RECORD: Int) {
        RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .subscribe { b ->
                    run {
                        if (b) {
                            RecordVideoActivity.startActivityToActivity(activity, PubDiaryActivity::class.java)
                        } else {
                            TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    override fun selectVideo(activity: AppCompatActivity, requesT_CODE_SELECT: Int) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .isCamera(false)
                .maxSelectNum(9)
                .maxVideoSelectNum(1)
                .forResult(requesT_CODE_SELECT)
    }
    override fun loadMoreComment(id: Int,aPosition: Int, cPosition: Int) {

        model.loadMoreComment(id,cPosition,object :JsonCallback<BaseEntry<CommentEntry>>(){
            override fun onSuccess(response: Response<BaseEntry<CommentEntry>>?) {
                if (response!!.isSuccessful){
                    if (response.body().stat=="ok"){
                        val list = response.body().list
                        if (list!=null&&list.isNotEmpty()){
                            mView?.loadMoreCommentSuccess(aPosition,list)
                        }else{
                            mView?.loadmoreCommentError(aPosition)
                        }
                    }else{
                        mView?.loadmoreCommentError(aPosition)
                    }
                }else{
                    mView?.loadmoreCommentError(aPosition)
                }
            }

            override fun onError(response: Response<BaseEntry<CommentEntry>>?) {
                super.onError(response)
                mView?.loadmoreCommentError(aPosition)
            }
        })
    }
    override fun boomDiary(id: Int) {
        model.boomDiary(id,object :JsonCallback<BaseEntry<*>>(){
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                EventBus.getDefault().post(DiaryEvent(true))
            }
        })
    }

    val model = DiaryModel()
    override fun setNewData(uid: Int) {
        model.getDiary(0, "$uid",object : JsonCallback<BaseEntry<DiaryEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<DiaryEntry>>?) {
                if (response!!.isSuccessful) {
                    var data = response.body().list
                    mView?.setNewData(data)
                }
            }
        })
    }

    override fun loadMoreData(size: Int, uid: Int) {
        model.getDiary(size, "$uid",object : JsonCallback<BaseEntry<DiaryEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<DiaryEntry>>?) {
                if (response!!.isSuccessful) {
                    var data = response.body().list
                    if (data != null && data.isNotEmpty()) {
                        mView?.let {
                            if (it.addData(data)) {
                                it.loadMoreDataComplete()
                            } else {
                                it.loadMoreDataFinish()
                            }
                        }

                    } else {
                        mView?.loadMoreDataFinish()
                    }
                }
            }
        })
    }

    override fun likeDiary(mContext: Context, id: Int, position: Int) {
        model.likDiary(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {
                        mView?.likDiarySuccess(response.body().msg, position)
                    }
                }
            }

        })
    }

    override fun commentDiary(position: Int, id: Int, toid: Int, content: String) {
        model.commentDiary(id,toid,content,object :JsonCallback<BaseEntry<CommentEntry>>(){
            override fun onSuccess(response: Response<BaseEntry<CommentEntry>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {
                        mView?.commentSuccess(response.body().record, position)
                    }
                }
            }
        })
    }

}
