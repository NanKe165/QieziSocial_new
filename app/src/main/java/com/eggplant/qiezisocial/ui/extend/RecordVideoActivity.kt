package com.eggplant.qiezisocial.ui.extend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.listener.JCameraListener
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.utils.FileUtils
import kotlinx.android.synthetic.main.activity_record.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Administrator on 2020/7/15.
 *
 */

class RecordVideoActivity : BaseActivity() {
    companion object {
        private var toActivity: Class<*>? = null
        fun startActivityToActivity(context: Context, java: Class<*>) {
            this.toActivity = java
            context.startActivity(Intent(context, RecordVideoActivity::class.java))
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_record
    }

    override fun initView() {
        record_cameraview.setSaveVideoPath(FileUtils.getTempFilePath(mContext) + "JCamera")
        record_cameraview.setFeatures(JCameraView.BUTTON_STATE_BOTH)
        record_cameraview.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH)
    }

    override fun initData() {
    }

    override fun initEvent() {
        record_cameraview.setCloseClickListener {
            finish()
        }
        record_cameraview.setJCameraLisenter(object : JCameraListener {
            override fun captureSuccess(bitmap: Bitmap?) {
                if (bitmap != null) {
                    val fileName = System.currentTimeMillis().toString() + ".jpg"
                    var file = File(FileUtils.getTempFilePath(mContext) + "JCamera", fileName)
                    try {
                        val fos = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        fos.flush()
                        fos.close()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        if (toActivity != null) {
                            startActivity(Intent(mContext, toActivity).putExtra("recordPath", file.absolutePath).putExtra("mediaType", "pic"))
                            toActivity = null
                        } else {
                            setResult(Activity.RESULT_OK, Intent().putExtra("recordPath", file.absolutePath).putExtra("mediaType", "pic"))
                        }
                    }
                }
                finish()
            }

            override fun recordSuccess(url: String?, firstFrame: Bitmap?) {
                if (toActivity != null) {
                    startActivity(Intent(mContext, toActivity).putExtra("recordPath", url).putExtra("mediaType", "video"))
                    toActivity = null
                } else {
                    setResult(Activity.RESULT_OK, Intent().putExtra("recordPath", url).putExtra("mediaType", "video"))
                }
                finish()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        record_cameraview.onResume()
    }

    override fun onPause() {
        super.onPause()
        record_cameraview.onPause()
    }
}
