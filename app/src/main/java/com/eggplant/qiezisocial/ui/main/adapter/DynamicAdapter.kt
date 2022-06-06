package com.eggplant.qiezisocial.ui.main.adapter

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.image.CircleImageView
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo
import com.luck.picture.lib.tools.MediaUtils
import kotlinx.android.synthetic.main.ap_dynamic.view.*


/**
 * Created by Administrator on 2021/8/19.
 */

class DynamicAdapter(context: Context, data: List<BoxEntry>?) : BaseQuickAdapter<BoxEntry, BaseViewHolder>(R.layout.ap_dynamic, data) {
    var deleteOpen = false
    var maxWidth = 0
    var maxHeight = 0

    init {

        maxWidth = ScreenUtil.dip2px(context, 200)
        maxHeight = ScreenUtil.dip2px(context, 215)
    }

    override fun convert(helper: BaseViewHolder, item: BoxEntry) {
//        helper.addOnClickListener(R.id.ap_dy_like)
        helper.addOnClickListener(R.id.ap_dy_like_img)
        helper.addOnClickListener(R.id.ap_dy_head)
        helper.addOnClickListener(R.id.ap_dy_pub_comment)
        helper.addOnClickListener(R.id.ap_dy_report)
        helper.itemView.ap_dy_nick.text = item.userinfor.nick
        helper.itemView.ap_dy_like.text = "${item.like}"
        if (item.mylike) {
            helper.itemView.ap_dy_like_img.setImageResource(R.mipmap.like_select)
            helper.itemView.ap_dy_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_select, 0,0, 0)
            helper.itemView.ap_dy_like.setTextColor(ContextCompat.getColor(mContext, R.color.red_btn))
        } else {
            helper.itemView.ap_dy_like_img.setImageResource(R.mipmap.like_unselect)
            helper.itemView.ap_dy_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_unselect, 0, 0, 0)
            helper.itemView.ap_dy_like.setTextColor(ContextCompat.getColor(mContext, R.color.tv_gray2))
        }
        Glide.with(mContext).load(API.PIC_PREFIX + item.userinfor.face).into(helper.itemView.ap_dy_head)
        if (item.userinfor.sex == "男") {
            helper.itemView.ap_dy_sex.setImageResource(R.mipmap.sex_boy)
        } else {
            helper.itemView.ap_dy_sex.setImageResource(R.mipmap.sex_girl)
        }
        val created = "${item.created}000"
        val time = created.toLong()
        val timeTv = helper.itemView.ap_dy_time
        if (!TextUtils.isEmpty(created) && !TextUtils.equals(created, "0")) {
            when {
                DateUtils.isToday(created) -> timeTv.text = DateUtils.timeMinute(created)
                DateUtils.isSameWeek(created) -> timeTv.text = DateUtils.getWeek(time) + " " + DateUtils.timeMinute(created)
                DateUtils.IsYesterday(time) -> timeTv.text = "昨天 " + DateUtils.timeMinute(created)
                else -> timeTv?.text = DateUtils.timet(created)
            }
            timeTv?.visibility = View.VISIBLE
        } else {
            timeTv.text = ""
        }
        val play = helper.itemView.ap_dy_play
        val img = helper.itemView.ap_dy_img
        val mediaGp = helper.itemView.ap_dy_media
        val media = item.media

        if (media.isNotEmpty()) {
            mediaGp.visibility = View.VISIBLE
            var haveVideo=false
            var imageInfo=ArrayList<ImageInfo>()
            media.forEach {
                if (it.type=="video") {
                    haveVideo = true
                }else {
                    val image = ImageInfo()
                    image.setThumbnailUrl(API.PIC_PREFIX+it.extra)
                    image.setBigImageUrl(API.PIC_PREFIX+it.org)
                    imageInfo.add(image)
                }
            }
            if (media.size==1||haveVideo) {
//                helper.itemView.ap_dy_ninegridview.visibility=View.GONE
//                helper.itemView.ap_dy_media.visibility=View.VISIBLE
                helper.itemView.ap_dy_media_ry.visibility=View.GONE
                helper.itemView.ap_dy_media_tv.visibility=View.GONE
                helper.itemView.ap_dy_long_chart.visibility=View.GONE
                helper.itemView.ap_dy_media_one.visibility=View.VISIBLE
                var imagPath = ""
                var videoPath = ""
                play.visibility = View.GONE

                media.forEach {
                    if (it.type == "pic") {
                        Glide.with(mContext).asBitmap().load(API.PIC_PREFIX + it.org).into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                            if (mediaGp.width!=0){
//                                maxWidth = mediaGp.width
//                            }
//                            if (mediaGp.height!=0){
//                                 maxHeight =  mediaGp.height
//                            }
//                            val maxWidth = mediaGp.width
//                            val maxHeight =  mediaGp.height

                                var imgWidth = 0F
                                var imgHeight = 0F
                                when {
                                    resource.height > maxHeight -> {
                                        val eqLongImage = MediaUtils.isLongImg(resource.width,
                                                resource.height)
                                        if (eqLongImage){
                                            imgWidth=maxWidth.toFloat()*2/3
                                            imgHeight=maxHeight.toFloat()
                                            helper.itemView.ap_dy_long_chart.visibility=View.VISIBLE
                                        }else{
                                            imgWidth = resource.width.toFloat() / resource.height * maxHeight
                                            imgHeight = if (imgWidth > maxWidth) {
                                                imgWidth = maxWidth.toFloat()
                                                maxHeight.toFloat() / imgWidth * maxWidth
                                            } else {
                                                maxHeight.toFloat()
                                            }
                                        }
                                    }
                                    resource.width > maxWidth -> {
                                        imgHeight = resource.height.toFloat() / resource.width * maxHeight
                                        imgWidth = maxWidth.toFloat()
                                    }
                                    else -> {
                                        imgHeight = maxHeight.toFloat()
                                        imgWidth = maxWidth.toFloat()
                                    }
                                }
                                val layoutParams = mediaGp.layoutParams
                                layoutParams.width = imgWidth.toInt()
                                layoutParams.height = imgHeight.toInt()
                                mediaGp.layoutParams = layoutParams
//                            Log.i("dynaAdp","${resource.height} maxW:$maxWidth ${helper.adapterPosition} maxH:$maxHeight  imgW:$imgWidth   imgH:$imgHeight")
//                            if (videoPath.isEmpty()) {
//                                if (imgWidth == 0F) {
//                                    img.layoutParams =  FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
//                                } else {
//                                    img.layoutParams = FrameLayout.LayoutParams(imgWidth.toInt(), imgHeight.toInt())
//
//                                }
//                            }

                                img.setImageBitmap(resource)
                            }
                        })
                        imagPath = API.PIC_PREFIX + it.org
                    } else if (it.type == "video") {
                        play.visibility = View.VISIBLE
                        videoPath = API.PIC_PREFIX + it.org
//                    img.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    }
                }
                img.setOnClickListener {
                    if (videoPath.isNotEmpty()) {
                        PrevUtils.onVideoItemClick(mContext, img, videoPath, imagPath, true, "${System.currentTimeMillis()}.mp4")
                    } else if (imagPath.isNotEmpty()) {
                        PrevUtils.onImageItemClick(mContext, img, imagPath, imagPath)
                    }
                }
                img.setOnLongClickListener { v ->
                    if (videoPath.isNotEmpty()) {
                        imgLongClickListener?.invoke(helper.adapterPosition, v, videoPath, item)
                    } else if (imagPath.isNotEmpty()) {
                        imgLongClickListener?.invoke(helper.adapterPosition, v, imagPath, item)
                    }
                    true
                }
            }else{
                helper.itemView.ap_dy_media_ry.visibility=View.VISIBLE
                helper.itemView.ap_dy_media_tv.visibility=View.GONE
                helper.itemView.ap_dy_media_one.visibility=View.GONE
                helper.itemView.ap_dy_long_chart.visibility=View.GONE
                val layoutParams = mediaGp.layoutParams
                layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT
                layoutParams.height =RelativeLayout.LayoutParams.WRAP_CONTENT
                mediaGp.layoutParams = layoutParams
                helper.itemView.ap_dy_media_ry.layoutManager=LinearLayoutManager(mContext,HORIZONTAL,false)
                helper.itemView.ap_dy_media_ry.adapter=RoundImageAdapter(media)
                if (media.size>3){
                    helper.itemView.ap_dy_media_tv.text="${media.size-3}+"
                    helper.itemView.ap_dy_media_tv.visibility=View.VISIBLE
                }

//                helper.itemView.ap_dy_ninegridview.visibility=View.VISIBLE
//                        helper.itemView.ap_dy_media.visibility=View.GONE
//                helper.itemView.ap_dy_ninegridview.isSquare(true)
//                helper.itemView.ap_dy_ninegridview.setAdapter(NineGridViewClickAdapter(mContext,imageInfo))

            }

        } else {
            mediaGp.visibility = View.GONE
        }
        val expandableTextView = helper.itemView.ap_dy_content
        val viewWidth = ScreenUtil.dip2px(mContext, 280)
        expandableTextView.initWidth(viewWidth)
        expandableTextView.maxLines = 5
        expandableTextView.setHasAnimation(true)
        expandableTextView.setCloseInNewLine(true)
//        expandableTextView.setOpenSuffixColor(mContext.resources.getColor(R.color.colorAccent))
//        expandableTextView.setCloseSuffixColor(mContext.resources.getColor(R.color.colorAccent))
        if (item.text.isEmpty()) {
//            helper.itemView.ap_dy_content.text = ""
            helper.itemView.ap_dy_content.visibility = View.GONE
        } else {
            expandableTextView.setOriginalText(item.text)
//            helper.itemView.ap_dy_content.text = item.text
            helper.itemView.ap_dy_content.visibility = View.VISIBLE
        }

        if (deleteOpen) {
            helper.itemView.ap_dy_pub_comment.visibility=View.GONE
            helper.itemView.ap_dy_like.visibility=View.GONE
            helper.itemView.ap_dy_like_img.visibility=View.GONE
            helper.itemView.ap_dy_report.visibility = View.GONE
            helper.itemView.ap_dy_delete.visibility = View.VISIBLE
            helper.addOnClickListener(R.id.ap_dy_delete)
        }else{

        }
        if (item.likeuser != null && item.likeuser.isNotEmpty()) {
            val likeUser = helper.itemView.ap_dy_likeuser
            likeUser.removeAllViews()
            var likeimg = ImageView(mContext)
            likeimg.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            likeimg.setImageResource(R.mipmap.like_select)
            likeUser.addView(likeimg)
            likeUser.visibility = View.VISIBLE
            var moreSzie = 0
            item.likeuser.forEachIndexed { index, userEntry ->
                if (index < 8) {
                    likeUser.addView(getLikeView(userEntry))
                } else {
                    moreSzie++
                }
            }
            if (moreSzie > 0) {
                val margin = ScreenUtil.dip2px(mContext, 5)
                var sizeTv = QzTextView(mContext)
                sizeTv.text = "$moreSzie+"
                sizeTv.textSize = 17F
                sizeTv.setTextColor(ContextCompat.getColor(mContext, R.color.tv_gray9))
                sizeTv.layoutParams = getLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,margin)
                likeUser.addView(sizeTv)
            }
        } else {
            helper.itemView.ap_dy_likeuser.visibility = View.GONE
        }
        if (item.comment!=null&&item.comment.isNotEmpty()) {
            helper.itemView.ap_dy_comment.visibility=View.VISIBLE
            helper.itemView.ap_dy_comment.isShowLoadMore=true
            helper.itemView.ap_dy_comment.setDiaryUid(item.uid)
            helper.itemView.ap_dy_comment.datas = item.comment
            helper.itemView.ap_dy_comment.setOnItemClickListener {
                position ->
                commentListener?.invoke(helper.adapterPosition, item.comment[position])
            }
        }else{
            helper.itemView.ap_dy_comment.visibility=View.GONE
        }
//        if (deleteOpen&&item.scenes!=null&&item.scenes.isNotEmpty()){
//            helper.itemView.ap_dy_scenes.text="#${item.scenes}"
//            helper.itemView.ap_dy_scenes.visibility=View.VISIBLE
//        }else{
            helper.itemView.ap_dy_scenes.visibility=View.GONE
//        }
    }

    private fun getLikeView(it: UserEntry): CircleImageView {
        var img = CircleImageView(mContext)
        val imgWidth = ScreenUtil.dip2px(mContext, 17)
        val margin = ScreenUtil.dip2px(mContext, 5)
        val params = getLayoutParams(imgWidth,imgWidth,margin)
        img.layoutParams = params
        Glide.with(mContext).load(API.PIC_PREFIX + it.face).into(img)
        return img
    }

    private fun getLayoutParams(w: Int, h: Int,leftMargin:Int): LinearLayout.LayoutParams {
        var params = LinearLayout.LayoutParams(w, h)
        params.setMargins(leftMargin, 0, 0, 0)
        return params
    }

    var imgLongClickListener: ((Int, View, String, BoxEntry) -> Unit)? = null
    var commentListener: ((Int, CommentEntry) -> Unit)? = null
}
