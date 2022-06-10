package com.eggplant.qiezisocial.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo
import kotlinx.android.synthetic.main.layout_pub_txt_media.view.*

/**
 * Created by Administrator on 2020/6/1.
 */

class ChatMediaView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {


    fun setImages(data: List<String>) {
        var imginfo = ArrayList<ImageInfo>()
        data.forEach {
            var info = ImageInfo()
            info.bigImageUrl = it
            info.thumbnailUrl = it
            imginfo.add(info)
        }
        data.forEachIndexed { index, s ->
            addView(getViewWithImg(s, index, imginfo))
        }
    }

    fun setVideo(videoPath: String, picPath: String) {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_home_media, null, false)
        var w = context.resources.getDimension(R.dimen.qb_px_60).toInt()
//        var padding = context.resources.getDimension(R.dimen.qb_px_6).toInt()
//        var params=FrameLayout.LayoutParams(w, w)
//        v.ly_media_img.layoutParams=params
//        v.ly_media_img.setPadding(padding,padding,0,padding)
        v.ly_media_delete.visibility = View.GONE
        v.ly_media_play.visibility = View.VISIBLE
        Glide.with(context).load(picPath).into(v.ly_media_img)
        v.ly_media_play.isClickable = false
        v.setOnClickListener {
            PrevUtils.onVideoItemClick(context, v, videoPath, picPath)
        }
        v.setOnLongClickListener {
            false
        }
        addView(v)
    }

    private fun getViewWithImg(path: String, index: Int, imginfo: ArrayList<ImageInfo>): View {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_home_media, null, false)
//        var w = context.resources.getDimension(R.dimen.qb_px_60).toInt()
//        var padding = context.resources.getDimension(R.dimen.qb_px_6).toInt()
//        var params=FrameLayout.LayoutParams(w, w)
//        v.ly_media_img.layoutParams=params
//        v.ly_media_img.setPadding(padding,padding,0,padding)
        v.ly_media_delete.visibility = View.GONE
        Glide.with(context).load(path).into(v.ly_media_img)
        v.setOnClickListener {
            PrevUtils.onImageItemClick(context, v, index, imginfo)
        }
        v.setOnLongClickListener {
            false
        }
        return v
    }


}