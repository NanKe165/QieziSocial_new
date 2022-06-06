package com.eggplant.qiezisocial.ui.extend

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.view.View
import android.view.Window
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.PubVcrContract
import com.eggplant.qiezisocial.entry.VcrEntry
import com.eggplant.qiezisocial.presenter.PubVcrPresenter
import com.eggplant.qiezisocial.ui.extend.dialog.PubVcrDialog
import com.eggplant.qiezisocial.utils.BitmapUtils
import com.eggplant.qiezisocial.utils.FileUtils
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.dialog.QzProgressDialog
import com.eggplant.qiezisocial.widget.topbar.TopBarListener
import com.luck.picture.lib.PictureSelector
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_pub_vcr.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by Administrator on 2020/7/15.
 */

class PubVcrActivity : BaseMvpActivity<PubVcrPresenter>(), PubVcrContract.View {
    private var videoPath: String? = null
    private var pathKind: String? = null
    private var imgPath: String? = null
    private val REQUEST_CODE_VIDEO = 101
    private val REQUEST_CODE_RECORD = 102
    private lateinit var dialog: PubVcrDialog

    private lateinit var progressDialog: QzProgressDialog
    override fun getLayoutId(): Int {
        return R.layout.activity_pub_vcr
    }

    override fun initPresenter(): PubVcrPresenter {
        return PubVcrPresenter()
    }

    override fun initView() {
        mPresenter.attachView(this)
        initDialog()
    }

    private fun initDialog() {
        dialog = PubVcrDialog(mContext, intArrayOf(R.id.dlg_pub_vcr_record, R.id.dlg_pub_vcr_select))
        dialog.setOnBaseDialogItemClickListener { dialog, view ->
            when (view.id) {
                R.id.dlg_pub_vcr_record -> {
                    mPresenter.recordVideo(activity, REQUEST_CODE_RECORD)
                }
                R.id.dlg_pub_vcr_select -> {
                    mPresenter.selectVideo(activity, REQUEST_CODE_VIDEO)
                }
            }
            dialog.dismiss()
        }


        progressDialog = QzProgressDialog(mContext)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setMessage("正在压缩...")
    }

    override fun initData() {
        videoPath = intent.getStringExtra("selectPath")
        pathKind = "selectPath"
        if (TextUtils.isEmpty(videoPath)) {
            videoPath = intent.getStringExtra("recordPath")
            pathKind = "recordPath"
        }
        setVideoInfo()
    }


    override fun initEvent() {
        pub_vcr_bar.setTbListener(object : TopBarListener {
            override fun onReturn() {
                finish()
            }

            override fun onTxtClick() {
                if (!TextUtils.isEmpty(videoPath) && !TextUtils.isEmpty(pathKind)) {
                    RxPermissions(this@PubVcrActivity)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe { b ->
                                if (b){
                                    mPresenter.pubVcr(mContext, pub_vcr_title.text.toString(), videoPath!!, pathKind!!, imgPath)
                                }
                            }

                } else {
                    TipsUtil.showToast(mContext, "请添加相片")
                }
            }
        })
        pub_vcr_play.setOnClickListener {
//            PrevUtils.onVideoItemClick(mContext, pub_vcr_img, videoPath, imgPath)
        }
        pub_vcr_delete.setOnClickListener {
            videoPath = null
            pathKind = null
            setVideoInfo()
        }
        pub_vcr_img.setOnClickListener {
            if (TextUtils.isEmpty(videoPath)) {
                dialog.show()
            }else{
                PrevUtils.onImageItemClick(mContext, pub_vcr_img, videoPath, videoPath)
            }
        }
    }

    override fun showCompressDialog() {
        progressDialog.show()
    }

    override fun dismissCompressDialog() {
        progressDialog.dismiss()
    }

    override fun setCompressDialogMsg(s: String) {
        progressDialog.setMessage("压缩文件:$s")
    }

    override fun finishActivity(record: VcrEntry) {
        EventBus.getDefault().post(record)
        finish()
    }

    private fun setVideoInfo() {
        if (TextUtils.isEmpty(videoPath)) {
            pub_vcr_delete.visibility = View.GONE
            pub_vcr_play.visibility = View.GONE
            pub_vcr_dura.visibility = View.GONE
            pub_vcr_img.setImageResource(R.mipmap.vcr_video_icon)
        } else {
            pub_vcr_delete.visibility = View.VISIBLE
//            pub_vcr_play.visibility = View.VISIBLE
//            pub_vcr_dura.visibility = View.VISIBLE
//            val media = MediaMetadataRetriever()
//            media.setDataSource(videoPath)
//            var albumPath = getVideoAlbumPath(media)
//            imgPath = albumPath
//            var duration = Integer.parseInt(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
//            pub_vcr_dura.text = DateUtils.videoTime(duration)
//            Glide.with(mContext).load(albumPath).into(pub_vcr_img)
            Glide.with(mContext).load(videoPath).into(pub_vcr_img)
        }
    }

    private fun getVideoAlbumPath(media: MediaMetadataRetriever): String {
        val frameAtTime = media.frameAtTime
        val videoAlbumPath = FileUtils.getChatFilePath(mContext) + System.currentTimeMillis() + ".jpg"
        BitmapUtils.saveSmallBitmap2SDCard(frameAtTime, videoAlbumPath)
        return videoAlbumPath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_VIDEO -> {
                    var imgs = PictureSelector.obtainMultipleResult(data)
                    if (imgs == null || imgs.size <= 0) {
                        return
                    }
                    val file = imgs[0]
                    var path = file.androidQToPath
                    if (path == null || path.isEmpty()) {
                        path = file.path
                    }
                    videoPath = path
                    pathKind = "selectPath"
                    setVideoInfo()
                }
                REQUEST_CODE_RECORD -> {
                    videoPath = data?.getStringExtra("recordPath")
                    pathKind = "recordPath"
                    setVideoInfo()
                }
            }
        }
    }
}
