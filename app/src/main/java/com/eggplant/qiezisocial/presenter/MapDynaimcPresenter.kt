package com.eggplant.qiezisocial.presenter

import android.Manifest
import android.location.Location
import android.media.MediaMetadataRetriever
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.MapContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.MapDynaimcModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.tbruyelle.rxpermissions2.RxPermissions
import com.vincent.videocompressor.VideoCompress

/**
 * Created by Administrator on 2021/10/21.
 */

class MapDynaimcPresenter:MapContract.Presenter, BasePresenter<MapContract.View>() {
    val model=MapDynaimcModel()
    override fun getData(begin: Int, longitude: Double, latitude: Double, sid: String, s: String) {
        model.getData(begin,longitude,latitude,sid,s,object :JsonCallback<BaseEntry<BoxEntry>>(){
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                    if (response!!.isSuccessful){
                        val list = response.body().list
                        if (list!=null&&list.isNotEmpty()){
                            mView?.setNewData(list)
                        }
                    }
            }
        })

    }

    override fun readyTopub(activity: AppCompatActivity, goal: String, sid: String, txt: String?, pubMediaPath: ArrayList<String>, pubMediaType: String, myLoca: Location) {
        val mediaList = ArrayList<String>()

        if (pubMediaType == "image") {
            mediaList.addAll(pubMediaPath)
            pubDynamic(activity, goal, sid, txt, mediaList,myLoca)
        } else if (pubMediaType == "video") {
            val videoAlbumPath = FileUtils.getChatFilePath(activity) + System.currentTimeMillis() + ".jpg"
            val videocompressPath = FileUtils.getChatFilePath(activity) + System.currentTimeMillis() + ".mp4"
            val media = MediaMetadataRetriever()
            media.setDataSource(pubMediaPath[0])
            val frameAtTime = media.frameAtTime
            val dura = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            Log.i("readyTopub", "dura:$dura")
            BitmapUtils.saveSmallBitmap2SDCard(frameAtTime, videoAlbumPath)
            mediaList.add(videoAlbumPath)
            val size = FileSizeUtil.getFileOrFilesSize(pubMediaPath[0], FileSizeUtil.SIZETYPE_MB)
            if (size < 20) {
                mediaList.add(pubMediaPath[0])
                pubDynamic(activity, goal, sid, txt, mediaList,myLoca)
            } else {
                VideoCompress.compressVideoMedium(pubMediaPath[0], videocompressPath, object : VideoCompress.CompressListener {
                    override fun onStart() {
                        mView?.showCompressView("正在压缩...")
                    }

                    override fun onSuccess() {
                        mView?.hideCompressView()
                        mediaList.add(videocompressPath)
                        pubDynamic(activity, goal, sid, txt, mediaList,myLoca)
                    }

                    override fun onFail() {
                        mView?.hideCompressView()
//                        TipsUtil.showToast(activity,"视频压缩失败,正在尝试原视频上传...")
                        mediaList.add(pubMediaPath[0])
                        pubDynamic(activity, goal, sid, txt, mediaList,myLoca)
                    }

                    override fun onProgress(percent: Float) {
                        mView?.showCompressView("正在压缩(${percent.toInt()}%)")
                    }
                })
            }
        } else {
            pubDynamic(activity, goal, sid, txt, mediaList,location = myLoca)
        }

    }


    private fun pubDynamic(activity: AppCompatActivity, goal: String, sid: String, txt: String?, mediaList: ArrayList<String>,location: Location) {
//        mView?.showUploadView("正在上传...")
        var startTime=0L
        model.pubMapDynamic(txt, goal, mediaList, sid, location,object : JsonCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
//                mView?.hideCompressView()
                if (response!!.isSuccessful) {
//                    TipsUtil.showToast(activity, response.body().msg!!)
                    if (response.body().record != null) {
                        mView!!.addPubData(response.body().record!!)
                    }
                }
            }

            override fun onStart(request: Request<BaseEntry<BoxEntry>, out Request<Any, Request<*, *>>>?) {
                super.onStart(request)
                startTime=System.currentTimeMillis()
            }

            override fun onFinish() {
                super.onFinish()
                Log.i("pubDynamic","time:${System.currentTimeMillis()-startTime}")
            }

            override fun uploadProgress(progress: Progress) {
                super.uploadProgress(progress)
//                        mView?.showUploadView("正在上传(${((progress.currentSize/progress.totalSize)*100).toInt()}%)")
                Log.i("pubDynamic", "progress: $progress")
//                val p= ((progress.currentSize.toFloat() / progress.totalSize) * 100).toInt()
//                if (p<100){
//                    mView?.showUploadView("正在上传(${((progress.currentSize.toFloat() / progress.totalSize) * 100).toInt()}%)")
//                }else{
//                    mView?.showUploadView("正在上传...")
//                }
            }

            override fun onError(response: Response<BaseEntry<BoxEntry>>?) {
                super.onError(response)
//                mView?.hideCompressView()
            }
        })
    }
    /**
     * 打开相册
     */
    override fun openGallery(activity: AppCompatActivity, requestCode: Int) {
        RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { b ->
                    run {
                        if (b) {
                            PictureSelector.create(activity)
                                    .openGallery(PictureMimeType.ofImage())
                                    .loadImageEngine(GlideEngine.createGlideEngine())
                                    .isWithVideoImage(false)
                                    .maxSelectNum(9)
                                    .compress(true)
                                    .videoMaxSecond(2*60)
                                    .forResult(requestCode)
                        } else {
                            TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    override fun openGalleryVideo(activity: AppCompatActivity, requestCode: Int) {
        RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { b ->
                    run {
                        if (b) {
                            PictureSelector.create(activity)
                                    .openGallery(PictureMimeType.ofVideo())
                                    .loadImageEngine(GlideEngine.createGlideEngine())
                                    .maxSelectNum(1)
                                    .compress(true)
                                    .videoMaxSecond(2*60)
                                    .forResult(requestCode)
                        } else {
                            TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }
}
