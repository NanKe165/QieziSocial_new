package com.eggplant.qiezisocial.presenter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.PubVcrContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.VcrEntry
import com.eggplant.qiezisocial.model.VcrModel
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.ui.extend.RecordVideoActivity
import com.eggplant.qiezisocial.utils.BitmapUtils
import com.eggplant.qiezisocial.utils.FileUtils
import com.eggplant.qiezisocial.utils.GlideEngine
import com.eggplant.qiezisocial.utils.TipsUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.lzy.okgo.model.Response
import com.tbruyelle.rxpermissions2.RxPermissions
import com.vincent.videocompressor.VideoCompress

/**
 * Created by Administrator on 2020/7/15.
 */

class PubVcrPresenter : BasePresenter<PubVcrContract.View>(), PubVcrContract.Presenter {
    val model = VcrModel()
    override fun recordVideo(activity: AppCompatActivity, requesT_CODE_RECORD: Int) {
        RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .subscribe { b ->
                    run {
                        if (b) {
                            activity.startActivityForResult(Intent(activity, RecordVideoActivity::class.java), requesT_CODE_RECORD)
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
                .maxSelectNum(1)
                .isCamera(false)
                .forResult(requesT_CODE_VIDEO)
    }

    override fun pubVcr(context: Context, title: String, videoPath: String, pathKind: String, imgPath: String?) {
        if (TextUtils.equals(pathKind, "selectPath")) {

            compressImageWithPub(context, title, videoPath, imgPath)
        } else {
            realPubVcr(context, title, videoPath, imgPath)
        }
//          realPubVcr(context, title, videoPath, imgPath)
    }


    private fun realPubVcr(context: Context, title: String, videoPath: String, imgPath: String?) {
        var paths = ArrayList<String>()
        paths.add(videoPath)
        Log.i("pubvcr", "realPubVcr :$videoPath  ")
        imgPath?.let {
            paths.add(it)
            Log.i("pubvcr", "realPubVcr imgpath:$it  ")
        }
        model.pubVcr(title, paths, object : DialogCallback<BaseEntry<VcrEntry>>(context as AppCompatActivity, "文件上传中...") {
            override fun onSuccess(response: Response<BaseEntry<VcrEntry>>?) {
                response?.let {
                    if (response.isSuccessful && response.body()!!.stat == "ok") {
                        var record = response.body()!!.record
//                        FileUtils.deleteFile(imgPath)
//                        FileUtils.deleteFile(videoPath)
                        mView?.finishActivity(record!!)
                    }
                }
            }
        })

    }

    private fun compressVideoWithPub(mContext: Context, title: String, srcPath: String, imgPath: String?) {
        var desPath = FileUtils.getTempFilePath(mContext) + "${System.currentTimeMillis()}.mp4"
        var listener = object : VideoCompress.CompressListener {
            override fun onSuccess() {
                mView?.dismissCompressDialog()
                realPubVcr(mContext, title, desPath, imgPath)

            }

            override fun onFail() {
                mView?.dismissCompressDialog()
                realPubVcr(mContext, title, srcPath, imgPath)
            }

            override fun onProgress(percent: Float) {
                mView?.setCompressDialogMsg("100%${percent.toInt()}")
            }

            override fun onStart() {
                mView?.showCompressDialog()
            }
        }
        VideoCompress.compressVideoLow(srcPath, desPath, listener)
    }

    private fun compressImageWithPub(context: Context, title: String, videoPath: String, imgPath: String?) {

        var bitmap = BitmapFactory.decodeFile(videoPath)
        val fileName = FileUtils.getTempFilePath(context) + "JCamera" + System.currentTimeMillis().toString() + ".jpg"
        BitmapUtils.saveBitmap2SDCard(bitmap, fileName)
        realPubVcr(context, title, fileName, imgPath)
//        var comppressfile = File(fileName)
////        if (!comppressfile.exists()) {
//            comppressfile.createNewFile()
////        }
//        try {
//            val fos = FileOutputStream(comppressfile)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
//            fos.flush()
//            fos.close()
//
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }

}
