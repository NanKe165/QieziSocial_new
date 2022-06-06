package com.eggplant.qiezisocial.widget

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.BitmapUtils
import com.eggplant.qiezisocial.utils.FileUtils
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo
import kotlinx.android.synthetic.main.layout_pub_txt_media.view.*

/**
 * Created by Administrator on 2020/6/1.
 */

class MediaView : LinearLayout {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    var data: ArrayList<String> = ArrayList()
    var maxSize = 3
    var hasAddview = false
    var adView: View? = null
    fun setImage(imgPath: String) {
        addView(getImgView(imgPath), 0)
    }

    fun setAddView() {
        if (!hasAddview) {
            adView = getAddView()
            addView(adView)
            hasAddview = true
        }
    }

    fun removeAddView() {
        if (hasAddview && adView != null) {
            removeView(adView)
            hasAddview = false
            adView = null
        }
    }

    fun setImages(imgPath: List<String>) {
        imgPath.forEach {
            addView(getImgView(it), 0)
        }
    }

    fun setVideo(vidoPath: String) {
        removeAllViews()
        data.clear()
        var imagePath = getVideoFrame(vidoPath)
        addView(getVideoView(imagePath, vidoPath), 0)
    }

    private fun getVideoFrame(vidoPath: String): String {
        val videoAlbumPath = FileUtils.getChatFilePath(context) + System.currentTimeMillis() + ".jpg"
        val media = MediaMetadataRetriever()
        media.setDataSource(vidoPath)
        val frameAtTime = media.frameAtTime
        BitmapUtils.saveSmallBitmap2SDCard(frameAtTime, videoAlbumPath)
        data.add(vidoPath)
        return videoAlbumPath
    }

    private fun getImgView(imgPath: String): View {
        data.add(imgPath)
        val v = LayoutInflater.from(context).inflate(R.layout.layout_pub_txt_media, null, false)
        v.ly_media_delete.setOnClickListener {
            removeView(v)
            if (data.contains(imgPath)) {
                data.remove(imgPath)
            }
            maxSize++

            if (maxSize > 3) {
                maxSize = 3
            }
            listener?.onMediaRemove(this@MediaView, imgPath)
        }
        Glide.with(context).load(imgPath).into(v.ly_media_img)
        maxSize--
        if (maxSize < 0) {
            maxSize = 0
        }
        return v
    }

    fun setImagesWithoutDelete(imagePath: List<String>) {
        removeAllViews()
        var data = ArrayList<ImageInfo>()
        imagePath.forEach {
            var info = ImageInfo()
            info.bigImageUrl = API.PIC_PREFIX + it
            info.thumbnailUrl = API.PIC_PREFIX + it
            data.add(info)
        }
        imagePath.forEachIndexed { index, s ->
            addView(getImageView(data, index))
        }
    }

    private fun getImageView(imgPath: List<ImageInfo>, position: Int): View {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_pub_txt_media, null, false)
        v.ly_media_delete.visibility = View.GONE
        Glide.with(context).load(imgPath[position].bigImageUrl).into(v.ly_media_img)
        v.ly_media_img.setOnClickListener { v ->
            PrevUtils.onImageItemClick(context, v, position, imgPath)
        }
        return v
    }

    private fun getVideoView(imgPath: String, videoPath: String): View {
        data.add(imgPath)
        val v = LayoutInflater.from(context).inflate(R.layout.layout_pub_txt_media, null, false)
        v.ly_media_delete.setOnClickListener {
            removeView(v)
            data.clear()
            maxSize = 3

            if (maxSize > 3) {
                maxSize = 3
            }
            listener?.onMediaRemove(this@MediaView, videoPath)
        }
        Glide.with(context).load(imgPath).into(v.ly_media_img)
        v.ly_media_play.visibility = View.VISIBLE
        maxSize = 0
        if (maxSize < 0) {
            maxSize = 0
        }
        return v
    }

    private fun getAddView(): View {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_pub_txt_media, null, false)
        v.ly_media_delete.visibility = View.GONE
        v.ly_media_img.visibility = View.GONE
        v.ly_media_add.visibility = View.VISIBLE
        v.setOnClickListener { v ->
            addClickListener?.invoke(this@MediaView, v)
        }
        return v
    }

    var listener: OnMediaRemoveListener? = null
    var addClickListener: ((View, View) -> Unit)? = null

    interface OnMediaRemoveListener {
        fun onMediaRemove(view: MediaView, path: String)
    }
}
