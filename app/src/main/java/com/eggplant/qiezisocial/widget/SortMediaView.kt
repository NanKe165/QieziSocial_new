package com.eggplant.qiezisocial.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.MediaEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo
import kotlinx.android.synthetic.main.layout_pub_txt_media.view.*

/**
 * Created by Administrator on 2020/6/1.
 */

class SortMediaView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {


    fun setImages(beans: List<MediaEntry>) {
        var imginfo = ArrayList<ImageInfo>()
        beans.forEach {
            var info = ImageInfo()
            info.bigImageUrl = API.PIC_PREFIX + it.org
            info.thumbnailUrl = API.PIC_PREFIX + it.extra
            imginfo.add(info)
        }
        beans.forEachIndexed { index, mediaEntry ->
            addView(getViewWithImg(mediaEntry, index,imginfo))
        }
    }

    fun setVideo(beans: List<MediaEntry>) {
        addView(getViewWithVideo(beans))
    }

    private fun getViewWithVideo(beans: List<MediaEntry>): View {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_home_media, null, false)
        v.ly_media_delete.visibility = View.GONE
        v.ly_media_play.visibility = View.VISIBLE
        var imgPath = ""
        var videoPath = ""
        beans.forEach {
            if (TextUtils.equals(it.type, "pic")) {
                imgPath = API.PIC_PREFIX + it.org
                Glide.with(context).load(API.PIC_PREFIX + it.extra).into(v.ly_media_img)
            } else if (TextUtils.equals(it.type, "video")) {
                videoPath = API.PIC_PREFIX + it.org
            }
        }
        v.setOnClickListener {
            PrevUtils.onVideoItemClick(context, v, videoPath, imgPath)
        }
        return v
    }


    private fun getViewWithImg(bean: MediaEntry, index: Int, imginfo: ArrayList<ImageInfo>): View {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_home_media, null, false)
        v.ly_media_delete.visibility = View.GONE
        Glide.with(context).load(API.PIC_PREFIX + bean.extra).into(v.ly_media_img)
        v.setOnClickListener {
            PrevUtils.onImageItemClick(context, v, index,imginfo)
        }
        return v
    }


}