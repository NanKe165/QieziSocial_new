package com.eggplant.qiezisocial.ui.extend

import android.app.Activity
import android.app.ProgressDialog
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.DiaryEntry
import com.eggplant.qiezisocial.event.DiaryEvent
import com.eggplant.qiezisocial.model.DiaryModel
import com.eggplant.qiezisocial.model.DynamicModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.extend.fragment.PubDiaryFragment
import com.eggplant.qiezisocial.utils.BitmapUtils
import com.eggplant.qiezisocial.utils.FileUtils
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.dialog.QzProgressDialog
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import org.greenrobot.eventbus.EventBus

/**
 * Created by Administrator on 2020/10/29.
 */

class PubDiaryActivity : BaseActivity(), ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {
    var fragment: PubDiaryFragment? = null
    var mediaPath: String? = ""
    var mediaType: String? = ""
    var medias: ArrayList<LocalMedia>? = null
    private lateinit var progressDialog: QzProgressDialog
    override fun expressionClick(str: String?) {
        fragment?.expressionClick(str)
    }

    override fun expressionaddRecent(str: String?) {
        fragment?.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        fragment?.expressionDeleteClick()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pub_diary
    }

    override fun initView() {
        mediaPath = intent.getStringExtra("recordPath")
        val imgs = intent.getSerializableExtra("imgs")
        mediaType = intent.getStringExtra("mediaType")
        if (imgs != null) {
            medias = imgs as ArrayList<LocalMedia>
        }
        mediaPath?.let {
            Log.i("pubDiary", "mediaPaht:$it")
        }
        medias?.let {
            Log.i("pubDiary", "imgs:${it.size}")
        }
        val bundle = Bundle()
        bundle.putString("recordPath", mediaPath)
        bundle.putSerializable("imgs", medias)
        bundle.putString("mediaType", mediaType)
        fragment = PubDiaryFragment.newInstance(bundle)
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.add(R.id.pub_diary_ft, fragment)
        beginTransaction.commit()
        initDialog()
    }

    private fun initDialog() {
        progressDialog = QzProgressDialog(mContext)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setMessage("正在压缩...")
    }

    override fun initData() {
    }

    override fun initEvent() {

    }

    fun pubDiary(activity: Activity, context: String, pubMedia: ArrayList<String>, mediaType: String?) {
        if (mediaType != null && mediaType == "video") {
            if (pubMedia.size == 1) {
                val videoPath = pubMedia[0]
                val imagePath = getVideoFrame(videoPath)
                pubMedia.add(imagePath)
            }
        }
        DynamicModel().pubDynamic(activity, context, pubMedia, object : JsonCallback<BaseEntry<BoxEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<BoxEntry>>?) {
                progressDialog.dismiss()
                if (response!!.isSuccessful) {
                    TipsUtil.showToast(mContext, response.body().msg!!)
                    if (response.body().stat == "ok") {
                        setResult(Activity.RESULT_OK)
                        EventBus.getDefault().post(DiaryEvent())
                        finish()
                    }
                }
            }

            override fun uploadProgress(progress: Progress) {
                super.uploadProgress(progress)
                val p = ((progress.currentSize.toFloat() / progress.totalSize) * 100).toInt()
                if (p < 100) {
                    showUploadView("正在上传(${((progress.currentSize.toFloat() / progress.totalSize) * 100).toInt()}%)")
                } else {
                    showUploadView("正在上传...")
                }
            }

            override fun onError(response: Response<BaseEntry<BoxEntry>>?) {
                super.onError(response)
                progressDialog.dismiss()
                finish()
            }
        })
//        DiaryModel().pubDiary(activity,context, pubMedia, object : JsonCallback<BaseEntry<DiaryEntry>>() {
//            override fun onSuccess(response: Response<BaseEntry<DiaryEntry>>?) {
//                progressDialog.dismiss()
//                if (response!!.isSuccessful) {
//                    TipsUtil.showToast(mContext, response.body().msg!!)
//                    if (response.body().stat == "ok") {
//                        setResult(Activity.RESULT_OK)
//                        EventBus.getDefault().post(DiaryEvent())
//                        finish()
//                    }
//                }
//            }
//
//            override fun uploadProgress(progress: Progress) {
//                super.uploadProgress(progress)
//                val p = ((progress.currentSize.toFloat() / progress.totalSize) * 100).toInt()
//                if (p < 100) {
//                    showUploadView("正在上传(${((progress.currentSize.toFloat() / progress.totalSize) * 100).toInt()}%)")
//                } else {
//                    showUploadView("正在上传...")
//                }
//            }
//
//            override fun onError(response: Response<BaseEntry<DiaryEntry>>?) {
//                super.onError(response)
//                progressDialog.dismiss()
//                finish()
//            }
//        })
    }

    fun showUploadView(p: String) {
        progressDialog.setMessage(p)
        progressDialog.show()
    }

    private fun getVideoFrame(vidoPath: String): String {
        val videoAlbumPath = FileUtils.getChatFilePath(mContext) + System.currentTimeMillis() + ".jpg"
        val media = MediaMetadataRetriever()
        media.setDataSource(vidoPath)
        val frameAtTime = media.frameAtTime
        BitmapUtils.saveSmallBitmap2SDCard(frameAtTime, videoAlbumPath)
        return videoAlbumPath
    }

    override fun finish() {
        super.finish()
//        overridePendingTransition(R.anim.close_enter3, R.anim.close_exit3)
    }

}
