package com.eggplant.qiezisocial.presenter

import android.Manifest
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.MediaScannerConnection
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.MimeTypeMap
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.DynamicContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.DynamicModel
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
 * Created by Administrator on 2021/8/19.
 */

class DynamicPresenter : BasePresenter<DynamicContract.View>(), DynamicContract.Presenter {


    val model = DynamicModel()
    override fun commentDynamic(position: Int, id: Int, toid: Int, content: String) {
        model.commentDynaimc(id, toid, content, object : JsonCallback<BaseEntry<CommentEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<CommentEntry>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {
                        mView?.commentSuccess(response.body().record, position)
                    }
                }
            }
        })
    }

    override fun like(position: Int, id: Int) {
        Log.i("likeDy", "id:$id")
        model.like(id, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {
                        mView?.notifyLikeView(position)
                    }
                }
            }
        })
    }

    override fun saveFile(mContext: Context, downloadPath: String) {
        val contentName = System.currentTimeMillis().toString()
        val mFileRelativeUrl = downloadPath.replace(API.PIC_PREFIX.toRegex(), "")
        FileUtils.downloadFile(mContext, downloadPath, contentName, mFileRelativeUrl, FileUtils.getStoreFilePath(mContext), object : FileUtils.DownloadFileCallback {
            override fun onSuccess(filePath: String) {
//                Log.i("gchatP", "onsuccess----$filePath  $type")
//                mMediaScanner = MediaScannerConnection(mContext, object : MediaScannerConnection.MediaScannerConnectionClient {
//                    override fun onMediaScannerConnected() {
//                        TipsUtil.showToast(mContext,"文件已保存至相册")
//                        mMediaScanner!!.scanFile(filePath, type)
//                        mMediaScanner!!.disconnect()
//                    }
//
//                    override fun onScanCompleted(path: String?, uri: Uri?) {
//
//                    }
//                })
//                mMediaScanner!!.connect()

                var extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
                var mimeTypes = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                MediaScannerConnection.scanFile(mContext, arrayOf(filePath), arrayOf(mimeTypes), null)
                TipsUtil.showToast(mContext, "文件已保存至相册")

            }

            override fun onError(message: String) {
            }

            override fun onProgress(progress: Float?) {
            }
        })
    }

    override fun getDynamic(i: Int, goal: String, sid: String) {
        model.getDynamic(i, goal, sid, object : JsonCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                if (response!!.isSuccessful) {
                    val list = response.body().list
                    if (list != null && list.isNotEmpty()) {
                        if (i == 0) {
                            mView?.setNewData(list)
                        } else {
                            mView?.addData(list)
                            mView?.loadMoreEnd(false)
                        }
                    } else {
                        mView?.loadMoreEnd(true)
                    }
                }
            }
        })
    }

    override fun readyTopub(activity: AppCompatActivity, goal: String, sid: String, txt: String?, pubMediaPath: ArrayList<String>, pubMediaType: String) {
        val mediaList = ArrayList<String>()

        if (pubMediaType == "image") {
            mediaList.addAll(pubMediaPath)
            pubDynamic(activity, goal, sid, txt, mediaList)
        } else if (pubMediaType == "video") {
            val videoAlbumPath = FileUtils.getChatFilePath(activity) + System.currentTimeMillis() + ".jpg"
            val videocompressPath = FileUtils.getChatFilePath(activity) + System.currentTimeMillis() + ".mp4"
            val media = MediaMetadataRetriever()
            media.setDataSource(pubMediaPath[0])
            val frameAtTime = media.frameAtTime
            val dura = media.extractMetadata(METADATA_KEY_DURATION)
            Log.i("readyTopub", "dura:$dura")
            BitmapUtils.saveSmallBitmap2SDCard(frameAtTime, videoAlbumPath)
            mediaList.add(videoAlbumPath)
            val size = FileSizeUtil.getFileOrFilesSize(pubMediaPath[0], FileSizeUtil.SIZETYPE_MB)
            if (size < 20) {
                mediaList.add(pubMediaPath[0])
                pubDynamic(activity, goal, sid, txt, mediaList)
            } else {
                VideoCompress.compressVideoMedium(pubMediaPath[0], videocompressPath, object : VideoCompress.CompressListener {
                    override fun onStart() {
                        mView?.showCompressView("正在压缩...")
                    }

                    override fun onSuccess() {
                        mView?.hideCompressView()
                        mediaList.add(videocompressPath)
                        pubDynamic(activity, goal, sid, txt, mediaList)
                    }

                    override fun onFail() {
                        mView?.hideCompressView()
//                        TipsUtil.showToast(activity,"视频压缩失败,正在尝试原视频上传...")
                        mediaList.add(pubMediaPath[0])
                        pubDynamic(activity, goal, sid, txt, mediaList)
                    }

                    override fun onProgress(percent: Float) {
                        mView?.showCompressView("正在压缩(${percent.toInt()}%)")
                    }
                })
            }
        } else {
            pubDynamic(activity, goal, sid, txt, mediaList)
        }


    }

    private fun pubDynamic(activity: AppCompatActivity, goal: String, sid: String, txt: String?, mediaList: ArrayList<String>) {
        mView?.showUploadView("正在上传...")
        var startTime = 0L
        model.pubDynamic(txt, goal, mediaList, sid, object : JsonCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                mView?.hideCompressView()
                if (response!!.isSuccessful) {
                    TipsUtil.showToast(activity, response.body().msg!!)
                    if (response.body().record != null) {
                        mView!!.addPubData(response.body().record!!)
                    }
                }
            }

            override fun onStart(request: Request<BaseEntry<BoxEntry>, out Request<Any, Request<*, *>>>?) {
                super.onStart(request)
                startTime = System.currentTimeMillis()
            }

            override fun onFinish() {
                super.onFinish()
                Log.i("pubDynamic", "time:${System.currentTimeMillis() - startTime}")
            }

            override fun uploadProgress(progress: Progress) {
                super.uploadProgress(progress)
                val p = ((progress.currentSize.toFloat() / progress.totalSize) * 100).toInt()
                if (p < 100) {
                    mView?.showUploadView("正在上传(${((progress.currentSize.toFloat() / progress.totalSize) * 100).toInt()}%)")
                } else {
                    mView?.showUploadView("正在上传...")
                }
            }

            override fun onError(response: Response<BaseEntry<BoxEntry>>?) {
                super.onError(response)
                mView?.hideCompressView()
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
                                    .openGallery(PictureMimeType.ofAll())
                                    .loadImageEngine(GlideEngine.createGlideEngine())
                                    .maxSelectNum(9)
                                    .maxVideoSelectNum(1)
                                    .compress(true)
                                    .videoMaxSecond(2 * 60)
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
                                    .videoMaxSecond(2 * 60)
                                    .forResult(requestCode)
                        } else {
                            TipsUtil.showToast(activity, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    override fun reportDynamic(activity: AppCompatActivity, deleteId: Int, deletePs: Int) {
        model.reportDynaimc(activity, "moment", deleteId, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    TipsUtil.showToast(activity, response.body().msg!!)
                    if (response.body().stat == "ok") {

                    }
                }
            }
        })
    }

    override fun deleteDynamic(activity: AppCompatActivity, deleteId: Int, deletePs: Int, deleteSid: Int) {
        model.deleteDynamic(deleteId,deleteSid,object :JsonCallback<BaseEntry<*>>(){
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    TipsUtil.showToast(activity, response.body().msg!!)
                }
            }

        })
    }

}
