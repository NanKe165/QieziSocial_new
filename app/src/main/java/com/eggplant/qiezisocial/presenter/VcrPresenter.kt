package com.eggplant.qiezisocial.presenter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.VcrContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.VcrEntry
import com.eggplant.qiezisocial.model.VcrModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.extend.PubVcrActivity
import com.eggplant.qiezisocial.ui.extend.RecordVideoActivity
import com.eggplant.qiezisocial.utils.GlideEngine
import com.eggplant.qiezisocial.utils.TipsUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.lzy.okgo.model.Response
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * Created by Administrator on 2020/7/15.
 */

class VcrPresenter : BasePresenter<VcrContract.View>(), VcrContract.Presenter {
    override fun boom(id: Int) {
        model.boom(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {

            }
        })
    }

    private var vcrModel = 0
    val model = VcrModel()
    override fun initData() {
        var call = object : JsonCallback<BaseEntry<VcrEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<VcrEntry>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        var data = response.body()!!.list
                        mView?.setNewData(data)
                    }
                }
            }
        }
        when (vcrModel) {
            0 -> {
                model.getVcr(0, call)
            }
            1 -> {
                model.getFollowVcr(0, call)
            }
            2 -> {
                model.getMyVcr(0, call)
            }
        }

    }

    override fun loadMoreData(begin: Int) {
        var call = object : JsonCallback<BaseEntry<VcrEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<VcrEntry>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        var data = response.body()!!.list
                        mView?.addData(data)
                    }
                }
            }
        }
        when (vcrModel) {
            0 -> {
                model.getVcr(begin, call)
            }
            1 -> {
                model.getFollowVcr(begin, call)
            }
            2 -> {
                model.getMyVcr(begin, call)
            }
        }


    }

    override fun deleteVcr(clickPosition: Int, id: Int) {
        model.deletetVcr(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful && response.body()!!.stat == "ok") {
                        mView?.deleteVcrWithPosition(clickPosition)

                    }
                }
            }
        })
    }

    override fun cancelCollect(clickPosition: Int, id: Int) {
        model.cancelCollect(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful && response.body()!!.stat == "ok") {
                        mView?.cancelCollectWithPosition(clickPosition)

                    }
                }
            }
        })
    }

    override fun collectVcr(position: Int, id: Int) {
        model.collectVcr(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful && response.body()!!.stat == "ok") {
                        mView?.refreshCollectWithPosition(position)

                    }
                }
            }
        })
    }

    override fun reportVcr(position: Int, id: Int) {
        model.reportVcr(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful && response.body()!!.stat == "ok") {
                        mView?.removeReportVcrWithPosition(position)

                    }
                }
            }

        })
    }

    override fun likeVcr(position: Int, id: Int) {
        model.likeVcr(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful && response.body()!!.stat == "ok") {
                        mView?.refreshLikeWithPosition(position, response.body()!!.like)

                    }
                }
            }

        })
    }

    override fun recordVideo(activity: AppCompatActivity, requesT_CODE_RECORD: Int) {
        RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .subscribe { b ->
                    run {
                        if (b) {
                            RecordVideoActivity.startActivityToActivity(activity, PubVcrActivity::class.java)
                        } else {
                            TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    override fun selectVideo(activity: AppCompatActivity, requesT_CODE_VIDEO: Int) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .isCamera(false)
                .maxSelectNum(1)
                .forResult(requesT_CODE_VIDEO)
    }


    override fun selectVideoSuccess(mContext: Context, path: String) {
        mContext.startActivity(Intent(mContext, PubVcrActivity::class.java).putExtra("selectPath", path))
    }

    fun setFollow(model: Int) {
        this.vcrModel = model
    }


}
