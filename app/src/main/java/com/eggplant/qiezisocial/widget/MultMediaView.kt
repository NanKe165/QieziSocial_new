package com.eggplant.qiezisocial.widget

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.MediaEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.widget.image.RoundRectImageView
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo

/**
 * Created by Administrator on 2021/11/2.
 */

class MultMediaView : FrameLayout {
    constructor(context: Context) : super(context) {

    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MultMediaView)
        val drawable = ta.getDrawable(R.styleable.MultMediaView_empty_src)
        ta.recycle()
        if (drawable != null)
            setEmtpyView(drawable, null)
    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MultMediaView)
        val drawable = ta.getDrawable(R.styleable.MultMediaView_empty_src)
        ta.recycle()
        if (drawable != null)
            setEmtpyView(drawable, null)
    }


    private var dp183 = 0
    private var dp90 = 0
    private var dp136 = 0
    private var dp276 = 0
    private var dp3 = 0
    fun setNewData(data: List<MediaEntry>, type: Int) {
        dp183 = ScreenUtil.dip2px(context, 183)
        dp90 = ScreenUtil.dip2px(context, 90)
        dp136 = ScreenUtil.dip2px(context, 136)
        dp276 = ScreenUtil.dip2px(context, 276)
        dp3 = ScreenUtil.dip2px(context, 3)
        removeAllViews()
        if (type == TYPE_IMG) {
            if (data.isNotEmpty())
                when (data.size) {
                    1 -> {
                        setImgViewOne(data[0])
                    }
                    2 -> {
                        setImgViewTwo(data)
                    }
                    else -> {
                        setImgviewThree(data)
                    }
                }
            if (data.size > 3) {
                setSizeView(data.size - 3)
            }
        } else {
            setVideoView(data)
        }
    }
    fun setEmtpyView(drawable: Drawable?, listener: OnClickListener?) {
        removeAllViews()
        if (drawable != null) {
            var emtptyView = ImageView(context)
            emtptyView.setImageDrawable(drawable)
            if (listener == null) {
                emtptyView.setOnClickListener { v ->
                    emptyViewClickListener?.invoke(v)
                }
            } else {
                emtptyView.setOnClickListener(listener)
            }
            addView(emtptyView)
        }
    }

    private fun setSizeView(i: Int) {
        val frameLayout = FrameLayout(context)
        var fpms = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, dp183)
        fpms.setMargins(dp183 + dp90 + dp3 * 2, 0, 0, 0)
        frameLayout.layoutParams = fpms
        val textView = QzTextView(context)
        textView.textSize = 20.0f
        textView.setTextColor(ContextCompat.getColor(context, R.color.state_select))
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.text = "$i+"
        var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.BOTTOM
        textView.layoutParams=params
        frameLayout.addView(textView)
        addView(frameLayout)
    }

    private fun setVideoView(data: List<MediaEntry>) {
        var videoPath = ""
        var imgPath = ""
        data.forEach {
            if (it.type == "pic") {
                imgPath = API.PIC_PREFIX + it.org
            } else if (it.type == "video") {
                videoPath = API.PIC_PREFIX + it.org
            }
        }
        if (imgPath.isEmpty() || videoPath.isEmpty())
            return
        val imgView = RoundRectImageView(context)
        var playView = ImageView(context)
        playView.setImageResource(R.mipmap.vcr_play)
        var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        playView.layoutParams = params
        val layoutParams = FrameLayout.LayoutParams(dp276, dp183)
        imgView.layoutParams = layoutParams
        imgView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(context).load(imgPath).into(imgView)
        playView.setOnClickListener { v ->
            PrevUtils.onVideoItemClick(context, imgView, videoPath, imgPath)
        }
        addView(imgView)
        addView(playView)
    }

    private fun setImgviewThree(data: List<MediaEntry>) {
        var imginfo = ArrayList<ImageInfo>()
        data.forEach {
            var info = ImageInfo()
            info.bigImageUrl = API.PIC_PREFIX + it.org
            info.thumbnailUrl = API.PIC_PREFIX + it.extra
            imginfo.add(info)
        }
        data.forEachIndexed { index, mediaEntry ->
            if (index < 3) {
                val imgView = RoundRectImageView(context)
                imgView.scaleType = ImageView.ScaleType.CENTER_CROP
                imgView.setOnClickListener { v ->
                    PrevUtils.onImageItemClick(context, v, index, imginfo)
                }
                when (index) {
                    0 -> {
                        val layoutParams = FrameLayout.LayoutParams(dp183, dp183)
                        imgView.layoutParams = layoutParams
                    }
                    1 -> {
                        val layoutParams = FrameLayout.LayoutParams(dp90, dp90)
                        layoutParams.setMargins(dp183 + dp3, 0, 0, 0)
                        imgView.layoutParams = layoutParams
                    }
                    2 -> {
                        val layoutParams = FrameLayout.LayoutParams(dp90, dp90)
                        layoutParams.setMargins(dp183 + dp3, dp90 + dp3, 0, 0)
                        imgView.layoutParams = layoutParams
                    }
                }
                Glide.with(context).load(API.PIC_PREFIX + mediaEntry.org).into(imgView)
                addView(imgView)
            }

        }
    }

    private fun setImgViewTwo(data: List<MediaEntry>) {
        var imginfo = ArrayList<ImageInfo>()
        data.forEach {
            var info = ImageInfo()
            info.bigImageUrl = API.PIC_PREFIX + it.org
            info.thumbnailUrl = API.PIC_PREFIX + it.extra
            imginfo.add(info)
        }
        data.forEachIndexed { index, mediaEntry ->
            if (index < 2) {
                val imgView = RoundRectImageView(context)
                imgView.scaleType = ImageView.ScaleType.CENTER_CROP
                imgView.setOnClickListener { v ->
                    PrevUtils.onImageItemClick(context, v, index, imginfo)
                }
                if (index == 0) {
                    val layoutParams = FrameLayout.LayoutParams(dp136, dp136)
                    imgView.layoutParams = layoutParams

                } else if (index == 1) {
                    val layoutParams = FrameLayout.LayoutParams(dp136, dp136)
                    layoutParams.setMargins(dp3 + dp136, 0, 0, 0)
                    imgView.layoutParams = layoutParams
                }
                Glide.with(context).load(API.PIC_PREFIX + mediaEntry.org).into(imgView)
                addView(imgView)
            }
        }
    }

    private fun setImgViewOne(mediaEntry: MediaEntry) {
        val imgView = RoundRectImageView(context)
        val layoutParams = FrameLayout.LayoutParams(dp276, dp183)
        imgView.layoutParams = layoutParams
        imgView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(context).load(API.PIC_PREFIX + mediaEntry.org).into(imgView)
        imgView.setOnClickListener { v ->
            PrevUtils.onImageItemClick(context, v, API.PIC_PREFIX + mediaEntry.org, API.PIC_PREFIX + mediaEntry.org)
        }
        addView(imgView)
    }

    companion object {
        val TYPE_IMG = 0
        var TYPE_VIDEO = 1
    }

    var emptyViewClickListener: ((View) -> Unit)? = null
}
