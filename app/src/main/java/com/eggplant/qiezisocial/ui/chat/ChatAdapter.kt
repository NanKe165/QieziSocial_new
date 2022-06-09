package com.eggplant.qiezisocial.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.czt.mp3recorder.Log
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.EmojiEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.widget.ChatMediaView
import com.eggplant.qiezisocial.widget.QzEdittext
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo
import com.luck.picture.lib.tools.MediaUtils
import com.xiao.nicevideoplayer.NiceVideoPlayer
import com.xiao.nicevideoplayer.TxVideoPlayerController
import kotlinx.android.synthetic.main.adapter_chat_other.view.*
import kotlinx.android.synthetic.main.adapter_chat_qs_title.view.*

/**
 * Created by Administrator on 2020/4/16.
 */
class ChatAdapter(mContext: Context, data: List<ChatMultiEntry<ChatEntry>>?) : BaseMultiItemQuickAdapter<ChatMultiEntry<ChatEntry>, BaseViewHolder>(data) {
    private var maxWidth: Int
    private var minWidth: Int
    private var maxHeight: Int
    var mineheadPic = ""
    var otherheadPic = ""
    var emojiList: List<EmojiEntry>? = null
    var context: Context = mContext
    var showEdit = false
    var requestcode = -1
    var fragment: Fragment? = null
    var gchat = false
    var needAnimPosition=-1
    var multModel=false

    init {
        addItemType(ChatMultiEntry.CHAT_MINE, R.layout.adapter_chat_mine)
        addItemType(ChatMultiEntry.CHAT_OTHER, R.layout.adapter_chat_other)
        addItemType(ChatMultiEntry.CHAT_MINE_AUDIO, R.layout.adapter_chat_mine_audio)
        addItemType(ChatMultiEntry.CHAT_OTHER_AUDIO, R.layout.adapter_chat_other_audio)
        addItemType(ChatMultiEntry.CHAT_MINE_VIDEO, R.layout.adapter_chat_mine_video)
        addItemType(ChatMultiEntry.CHAT_OTHER_VIDEO, R.layout.adapter_chat_other_video)
        addItemType(ChatMultiEntry.CHAT_MINE_QUESTION, R.layout.adapter_chat_mine_question)
        addItemType(ChatMultiEntry.CHAT_OTHER_QUESTION, R.layout.adapter_chat_other_question)
        addItemType(ChatMultiEntry.CHAT_QUESTION_TITLE, R.layout.adapter_chat_qs_title)
        maxWidth = ScreenUtil.getDisplayWidthPixels(mContext) / 12 * 5
        minWidth = ScreenUtil.getDisplayWidthPixels(mContext) / 24 * 7
        maxHeight = ScreenUtil.getDisplayHeightPixels(mContext) / 3
    }


    override fun convert(helper: BaseViewHolder?, item: ChatMultiEntry<ChatEntry>?) {
        helper!!.addOnClickListener(R.id.adapter_chat_multselect)
        helper!!.addOnLongClickListener(R.id.center)
        if (multModel){
            helper!!.getView<ImageView>(R.id.adapter_chat_multselect).visibility=View.VISIBLE
        }
        if (helper!!.itemViewType == ChatMultiEntry.CHAT_QUESTION_TITLE) {
            if (TextUtils.equals(item!!.bean.layout, "男")) {
                helper.itemView.adapter_chat_qs_sex.setImageResource(R.mipmap.sex_boy)
            } else {
                helper.itemView.adapter_chat_qs_sex.setImageResource(R.mipmap.sex_girl)
            }
            helper.itemView.adapter_chat_qs_content.text = item.bean.content
            val face = item.bean.face
            if (face.isNotEmpty()) {
                Glide.with(mContext).load(API.PIC_PREFIX + face).into(helper.itemView.adapter_chat_qs_head)
            }
            val mood = item.bean.extra
            if (mood == null || mood.isEmpty()) {
                helper.itemView.adapter_chat_qs_state.visibility = View.GONE
            } else {
                helper.itemView.adapter_chat_qs_state.visibility = View.VISIBLE
                helper.itemView.adapter_chat_qs_state.text = mood
                setState(mood, helper.itemView.adapter_chat_qs_state)
            }

        } else {
            val hintTv = helper?.getView<TextView>(R.id.chat_hint_tv)
            val created = item?.bean?.created
            val time = created?.toLong()
            if (item?.bean?.isShowCreated!! && !TextUtils.isEmpty(created) && !TextUtils.equals(created, "0")) {
                when {
                    DateUtils.isToday(created) -> hintTv?.text = DateUtils.timeMinute(created)
                    DateUtils.isSameWeek(created) -> hintTv?.text = DateUtils.getWeek(time) + " " + DateUtils.timeMinute(created)
                    DateUtils.IsYesterday(time!!) -> hintTv?.text = "昨天 " + DateUtils.timeMinute(created)
                    else -> hintTv?.text = DateUtils.timet(created)
                }
                hintTv?.visibility = View.VISIBLE
            } else {
                hintTv?.visibility = View.GONE
            }
            val face = item.bean.face
            val head = helper?.getView<ImageView>(R.id.adapter_chat_head)
            if (item.itemType == ChatMultiEntry.CHAT_MINE || item.itemType == ChatMultiEntry.CHAT_MINE_AUDIO
                    || item.itemType == ChatMultiEntry.CHAT_MINE_VIDEO || item.itemType == ChatMultiEntry.CHAT_MINE_QUESTION) {
                if (mineheadPic.isNotEmpty()) {
                    Glide.with(context).load(API.PIC_PREFIX + mineheadPic).into(head!!)
                } else if (!TextUtils.isEmpty(face)) {
                    Glide.with(context).load(API.PIC_PREFIX + face).into(head!!)
                }
            } else {
                if (otherheadPic.isNotEmpty()) {
                    Glide.with(context).load(API.PIC_PREFIX + otherheadPic).into(head!!)
                } else if (!TextUtils.isEmpty(face)) {
                    Glide.with(context).load(API.PIC_PREFIX + face).into(head!!)
                }
            }
            helper.addOnClickListener(R.id.adapter_chat_head)

            setData(helper, item)

            if (item.itemType == ChatMultiEntry.CHAT_OTHER) {
                if (emojiList != null && emojiList!!.isNotEmpty() && data.size == 1) {
                    var emojiGp = helper.itemView.adapter_chat_emojiview
                    emojiGp.visibility = View.VISIBLE
                    emojiGp.removeAllViews()
                    emojiList!!.forEach { entry ->
                        var img = ImageView(mContext)
                        img.setImageResource(R.drawable.emoji1)
                        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                        params.setMargins(0, 0, 20, 0)
                        img.layoutParams = params
                        img.setOnClickListener {
                            listerner?.emojiClick(entry)
                        }
                        Glide.with(mContext).load(API.PIC_PREFIX + entry.path).into(img)

                        emojiGp.addView(img)
                    }
                } else {
                    helper.itemView.adapter_chat_emojiview.removeAllViews()
                    helper.itemView.adapter_chat_emojiview.visibility = View.GONE
                }
            }
        }

        if (needAnimPosition==helper.adapterPosition-headerLayoutCount){
            helper.itemView.animation = AnimationUtils.loadAnimation(mContext, R.anim.chat_item_add_anim)
        }
        needAnimPosition=-1
    }

    @SuppressLint("CheckResult")
    private fun setData(helper: BaseViewHolder, item: ChatMultiEntry<ChatEntry>) {

        when (item.itemType) {
            ChatMultiEntry.CHAT_MINE, ChatMultiEntry.CHAT_OTHER
            -> {
                //文本消息
                setTxtData(helper, item)
            }

            ChatMultiEntry.CHAT_OTHER_AUDIO, ChatMultiEntry.CHAT_MINE_AUDIO
            -> {
                //音频消息
                setAudioData(helper, item)
            }

            ChatMultiEntry.CHAT_OTHER_VIDEO, ChatMultiEntry.CHAT_MINE_VIDEO
            -> {
                //视频消息
                setVideoData(helper, item)
            }
            ChatMultiEntry.CHAT_OTHER_QUESTION, ChatMultiEntry.CHAT_MINE_QUESTION
            -> {
                //设置问题
                setQuestionData(helper, item)
            }

        }
        if (gchat) {
            when (item.itemType) {
                ChatMultiEntry.CHAT_MINE -> {
                    helper.getView<RelativeLayout>(R.id.center).background = ContextCompat.getDrawable(mContext, R.drawable.msg_gchat_mine_bg)
                }

                ChatMultiEntry.CHAT_MINE_AUDIO
                -> {
                    helper.getView<FrameLayout>(R.id.center).background = ContextCompat.getDrawable(mContext, R.drawable.msg_gchat_mine_bg)
                }

                ChatMultiEntry.CHAT_OTHER_AUDIO -> {
                    helper.getView<FrameLayout>(R.id.center).background = ContextCompat.getDrawable(mContext, R.drawable.msg_gchat_other_bg)
                }

                ChatMultiEntry.CHAT_OTHER
                -> {
                    helper.getView<RelativeLayout>(R.id.center).background = ContextCompat.getDrawable(mContext, R.drawable.msg_gchat_other_bg)

                }
            }
        }

    }

    //设置问题
    private fun setQuestionData(helper: BaseViewHolder, item: ChatMultiEntry<ChatEntry>) {
        var mediaView = helper.getView<ChatMediaView>(R.id.adapter_chat_mediaview)
        var videoView = helper.getView<FrameLayout>(R.id.adapter_chat_video)
        val contentImg = helper.getView<ImageView>(R.id.adapter_chat_cImg)
        var contentTv = helper.getView<QzEdittext>(R.id.adapter_chat_content)
        val content = item.bean.content
        var q1 = item.bean.question1
        var q2 = item.bean.question2
        var q3 = item.bean.question3
        var q4 = item.bean.extra
        contentTv.setText(content)
        var mediaData = ArrayList<String>()
        var finalType = "pic"
        var videoPath: String? = null
        var data1 = getMediaData(q1, "q1", item.bean)
        var data2 = getMediaData(q2, "q2", item.bean)
        var data3 = getMediaData(q3, "q3", item.bean)
        var data4 = getMediaData(q4, "q4", item.bean)

        if (data1["type"] == "video") {
            finalType = "video"
            videoPath = data1["media"]
        } else {
            data1["media"]?.let { mediaData.add(it) }
        }


        if (data2["type"] == "video") {
            finalType = "video"
            videoPath = data2["media"]
        } else {
            data2["media"]?.let { mediaData.add(it) }
        }


        if (data3["type"] == "video") {
            finalType = "video"
            videoPath = data3["media"]
        } else {
            data3["media"]?.let { mediaData.add(it) }
        }


        if (data4["type"] == "video") {
            finalType = "video"
            videoPath = data4["media"]
        } else {
            data4["media"]?.let { mediaData.add(it) }
        }


        if (finalType == "video") {
            videoView.visibility = View.VISIBLE
            mediaView.visibility = View.GONE
            if (mediaData.size > 0) {
                Glide.with(context).load(mediaData[0]).into(contentImg)
                if (videoPath != null) {
                    videoView.setOnClickListener { v ->
                        PrevUtils.onVideoItemClick(context, v, videoPath, mediaData[0])
                    }
                }
            }
        } else {
            videoView.visibility = View.GONE
            mediaView.visibility = View.VISIBLE
            Log.e("")
            mediaView.setImages(mediaData)
        }


    }

    private fun getMediaData(q1: String?, flag: String, bean: ChatEntry): Map<String, String> {
        var map = HashMap<String, String>()
        if (!TextUtils.isEmpty(q1)) {
            val split = q1?.split("&&".toRegex())?.dropLastWhile({ it.isEmpty() })?.toTypedArray()
            if (split != null && split.size == 2) {
                val media = split[0]
                var mediaType = split[1]
                map.put("media", media)
                map.put("type", mediaType)

                if (!TextUtils.isEmpty(media)) {
                    if (media.contains(API.PIC_PREFIX)) {
                        if (listerner != null) {
                            listerner?.onQsFileDownload(media, mediaType, flag, bean)
                        }
                    }
                }
            }
        }
        return map
    }

    /**
     *
     * 设置视频消息
     */
    private fun setVideoData(helper: BaseViewHolder, item: ChatMultiEntry<ChatEntry>) {
        val msgStat = item.bean.msgStat
        val ununitedImg = helper.getView<ImageView>(R.id.adapter_chat_ununited)
        val bar = helper.getView<ProgressBar>(R.id.ap_chat_pBar)
        val contentLt = helper.getView<FrameLayout>(R.id.center)
        val videoTime = helper.getView<TextView>(R.id.ap_chat_videotime)
        val albumImg = helper.getView<ImageView>(R.id.ap_chat_album)
        val progressTv = helper.getView<TextView>(R.id.ap_chat_progress)

        val content = item.bean.content
        val extra = item.bean.extra
        val type = item.itemType

        val bitWidth = minWidth
        val bitHeight = maxHeight

        if (!TextUtils.isEmpty(extra)) {
            val split = extra?.split("&&".toRegex())?.dropLastWhile({ it.isEmpty() })?.toTypedArray()
            if (split!!.isNotEmpty()) {
                var option = RequestOptions()
                        .override(bitWidth, bitHeight)
                val img = split[0]
                Glide.with(context)
                        .asBitmap()
                        .load(img)
                        .apply(option)
                        .into(albumImg)
                albumImg.setOnClickListener { v ->
                    if (msgStat != 3) {
                        if (!showEdit) {
                            PrevUtils.onVideoItemClick(context, v, content, img)
                        } else {
                            mediaClickListener?.invoke(helper.adapterPosition, v)
                        }
                    }
                }

                if (split.size == 2) {
                    val dura = split[1]
                    if (!TextUtils.isEmpty(dura)) {
                        videoTime.text = DateUtils.videoTime(Integer.parseInt(dura))
                    }
                }
            }
        }


        if (!TextUtils.isEmpty(content)) {
            if (content!!.contains(API.PIC_PREFIX)) {
                if (listerner != null) {
                    listerner?.onFileDownload(content, item.bean)
                }
            }
        }

        ununitedImg.visibility = View.GONE
        if (msgStat == 0) {
            ununitedImg.visibility = View.GONE
        } else if (msgStat == 1) {
            ununitedImg.visibility = View.VISIBLE
        } else if (msgStat == 2) {
            if (listerner != null) {
                listerner?.showUpFileProgress(item.bean.type!!, progressTv, item.bean, helper.adapterPosition)
            }
        } else if (msgStat == 3) {
            if (listerner != null) {
                val split = content!!.split("&&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if (split.size == 2) {
                    listerner?.compressAndShowBar(bar, split[0], split[1], item.bean, helper.adapterPosition)
                }
            }
        }
        ununitedImg.setOnClickListener {
            if (listerner != null) {
                listerner?.showUpFileProgress(item.bean.type!!, progressTv, item.bean, helper.adapterPosition)
            }
        }
    }

    /**
     * 设置音频消息
     */
    private fun setAudioData(helper: BaseViewHolder, item: ChatMultiEntry<ChatEntry>) {
        val msgStat = item.bean.msgStat
        val ununitedImg = helper.getView<ImageView>(R.id.adapter_chat_ununited)
        val audioTime = helper.getView<TextView>(R.id.ap_chat_atime)
        val contentLt = helper.getView<FrameLayout>(R.id.center)
        val player = helper.getView<NiceVideoPlayer>(R.id.ap_chat_player)
        val content = item.bean.content
        val extra = item.bean.extra

        //下载音频文件
        if (!TextUtils.isEmpty(content)) {
            if (content!!.contains(API.PIC_PREFIX)) {
                if (listerner != null) {
                    listerner?.onFileDownload(content, item.bean)
                }
            }
            player.setUp(content, null)
        }
        //音频时长
        if (!TextUtils.isEmpty(extra)) {
            audioTime.text = extra + "''"
        }
        ununitedImg.visibility = View.GONE
        if (msgStat == 0) {
            ununitedImg.visibility = View.GONE
            audioTime.visibility = View.VISIBLE
        } else if (msgStat == 1) {
            audioTime.visibility = View.GONE
            ununitedImg.visibility = View.VISIBLE
        } else if (msgStat == 2) {
            if (listerner != null) {
                audioTime.visibility = View.VISIBLE
                listerner?.showUpFileProgress(item.bean.type!!, null, item.bean, helper.adapterPosition)
            }
        }
        //消息重发
        ununitedImg.setOnClickListener {
            if (listerner != null) {
                val isSuccess = listerner?.onReSend(item.bean)
                if (isSuccess!!) {
                    item.bean.msgStat = 0
                    ChatDBManager.getInstance(context).updateUser(item.bean)
                    ununitedImg.visibility = View.GONE
                }
            }
        }
        contentLt.setOnClickListener {
            val ad = (helper.getView<View>(R.id.ap_chat_aimg_play) as ImageView).drawable as AnimationDrawable
            if (ad.isRunning) {
                helper.setVisible(R.id.ap_chat_aimg_play, false)
                helper.setVisible(R.id.ap_chat_aimg, true)
                ad.stop()
                player.release()
            } else {
                helper.setVisible(R.id.ap_chat_aimg_play, true)
                helper.setVisible(R.id.ap_chat_aimg, false)
                ad.start()
                player.start()
            }
        }
        var controller = TxVideoPlayerController(context)
        helper.getView<NiceVideoPlayer>(R.id.ap_chat_player).controller = controller
        controller.setOnCompleteListener(object : TxVideoPlayerController.OnCompleteListener {
            override fun onComplete() {
                helper.setVisible(R.id.ap_chat_aimg_play, false)
                helper.setVisible(R.id.ap_chat_aimg, true)
                player.release()
                val ad = (helper.getView<View>(R.id.ap_chat_aimg_play) as ImageView).drawable as AnimationDrawable
                ad.stop()
            }

            override fun onReset() {
                helper.setVisible(R.id.ap_chat_aimg_play, false)
                helper.setVisible(R.id.ap_chat_aimg, true)
                val ad = (helper.getView<View>(R.id.ap_chat_aimg_play) as ImageView).drawable as AnimationDrawable
                ad.stop()
            }

            override fun onPrepared() {

            }
        })

    }


    /**
     * 设置文本消息
     */
    private fun setTxtData(helper: BaseViewHolder, item: ChatMultiEntry<ChatEntry>) {
        val msgStat = item.bean.msgStat
        val contentImg = helper.getView<ImageView>(R.id.adapter_chat_cImg)
        val ununitedImg = helper.getView<ImageView>(R.id.adapter_chat_ununited)
        val contentLt = helper.getView<RelativeLayout>(R.id.center)
        val emojiView = helper.getView<ImageView>(R.id.adapter_chat_content_emoji)
        val mediaView = helper.itemView.adapter_chat_mediaview
        mediaView.visibility = View.GONE
        emojiView.visibility = View.GONE
        val content = item.bean.content
        val extra = item.bean.extra
        val type = item.bean.type
        val layout = item.bean.layout
        val media1 = item.bean.question1
        val media2 = item.bean.question2
        val media3 = item.bean.question3
        //movie ---set  note
        if (layout == "movie") {
            Glide.with(mContext).load(R.mipmap.icon_movie_head).into(helper.itemView.adapter_chat_head)
        }

        if (gchat) {
            helper.itemView.adapter_chat_content.setTextColor(ContextCompat.getColor(mContext, R.color.tv_gray2))
            helper.itemView.adapter_chat_content.textSize = 13.0f
            if (item.itemType == ChatMultiEntry.CHAT_OTHER) {
                helper.itemView.adapter_chat_nick.visibility = View.VISIBLE
                helper.itemView.adapter_chat_nick.text = item.bean.nick
            }
        }
        ununitedImg.visibility = View.GONE
        //设置消息状态
        if (msgStat == 0) {
            ununitedImg.visibility = View.GONE
        } else if (msgStat == 1) {
            ununitedImg.visibility = View.VISIBLE
        } else if (msgStat == 2) {
            if (listerner != null) {
                listerner?.showUpFileProgress(item.bean.type!!, null, item.bean, helper.adapterPosition)
            }
        }
        //消息重发
        ununitedImg.setOnClickListener(View.OnClickListener {
            if (listerner != null) {
                if (item.bean.type!! == "pic") {
                    listerner!!.showUpFileProgress(item.bean.type!!, null, item.bean, helper.adapterPosition)
                    return@OnClickListener
                }
                val isSuccess = listerner?.onReSend(item.bean)
                if (isSuccess!!) {
                    item.bean.msgStat = 0
                    ChatDBManager.getInstance(context).updateUser(item.bean)
                    ununitedImg.visibility = View.GONE
                }
            }
        })

        android.util.Log.i("contentImg", " type:$type")
        when (type) {
            "gtxt", "boxanswer" -> {
                contentImg.visibility = View.GONE
                contentLt.visibility = View.VISIBLE
                if (extra != null && extra.isNotEmpty() && !extra.contains("</txt>")) {
                    emojiView.visibility = View.VISIBLE
                    Glide.with(mContext).load(extra).into(emojiView)
                    if (extra.contains(API.PIC_PREFIX)) {
                        // 下载emoji文件  替换本地数据库extra信息
                        if (listerner != null) {
                            listerner?.onFileDownload(extra, item.bean)
                        }
                    }
                } else if (!TextUtils.isEmpty(content)) {
                    helper.setText(R.id.adapter_chat_content, content)
//                    helper.addOnLongClickListener(R.id.adapter_chat_content)
                }
                var mediaData = ArrayList<String>()
                var finalType = "pic"
                var videoPath: String? = null
//                android.util.Log.i("chatap", "${media1}  ${media2}  ${media3}")
                val data1 = getMediaData(media1, "m1", item.bean)
                val data2 = getMediaData(media2, "m2", item.bean)
                val data3 = getMediaData(media3, "m3", item.bean)

                if (data1["type"] == "video") {
                    finalType = "video"
                    videoPath = data1["media"]
                } else {
                    data1["media"]?.let { mediaData.add(it) }
                }


                if (data2["type"] == "video") {
                    finalType = "video"
                    videoPath = data2["media"]
                } else {
                    data2["media"]?.let { mediaData.add(it) }
                }


                if (data3["type"] == "video") {
                    finalType = "video"
                    videoPath = data3["media"]
                } else {
                    data3["media"]?.let { mediaData.add(it) }
                }
                if (finalType == "video") {
                    if (mediaData.size > 0 && videoPath != null) {
                        mediaView.visibility = View.VISIBLE
                        mediaView.setVideo(videoPath, mediaData[0])
                    }
                } else {
                    if (mediaData.size > 0) {
                        mediaView.visibility = View.VISIBLE
                        mediaView.setImages(mediaData)
                    }
                }

            }
            "gpic" -> {
                android.util.Log.i("contentImg", " gpic")
                val extra = item.bean.extra
                contentImg.visibility = View.VISIBLE
                contentLt.visibility = View.GONE
                android.util.Log.i("contentImg", " gpic-- $content")
                contentImg.setOnClickListener { v ->
                    android.util.Log.i("contentImg", " click--")
                    if (!showEdit || helper.itemViewType == ChatMultiEntry.CHAT_MINE) {
//                        PrevUtils.onImageItemClick(context, v, content, content)
                        setImageClick(v, content)
                    } else {
//                        if (fragment==null) {
//                            PrevUtils.onImageItemClick(context, v, content, content, requestcode,item.bean.from)
//                        }else{
//                            PrevUtils.onImageItemClick(context,fragment, v, content, content, requestcode,item.bean.from)
//                        }
                        mediaClickListener?.invoke(helper.adapterPosition, v)
                    }
                }
                android.util.Log.i("contentImg", " gpic-- setOnClickListener")
                if (content!!.contains(API.PIC_PREFIX)) {
                    // 下载文件  替换本地数据库content信息
                    if (listerner != null) {
                        listerner?.onFileDownload(content, item.bean)
                    }
                }
                var bitWidth = minWidth
                var bitHeight = minWidth
                if (!TextUtils.isEmpty(extra)) {
                    val split = extra?.split("&&".toRegex())?.dropLastWhile({ it.isEmpty() })?.toTypedArray()
                    if (split != null && split.size == 2) {
                        val height = Integer.parseInt(split[0])
                        val width = Integer.parseInt(split[1])
                        var scal = 1.0
                        //是否是长图
                        val eqLongImage = MediaUtils.isLongImg(bitWidth, bitHeight)
                        if (width > maxWidth) {
                            when {
                                eqLongImage -> {
                                    bitWidth = maxWidth
                                    bitHeight = maxHeight
                                }
                                height > width -> {
                                    scal = minWidth / width.toDouble()
                                    bitWidth = minWidth
                                    bitHeight = (height * scal).toInt()
                                }
                                else -> {
                                    scal = maxWidth / width.toDouble()
                                    bitWidth = maxWidth
                                    bitHeight = (height * scal).toInt()
                                }
                            }
                        } else if (width < minWidth) {
                            if (eqLongImage) {
                                bitWidth = minWidth
                                bitHeight = maxHeight
                            } else {
                                scal = (width / minWidth).toDouble()
                                bitWidth = minWidth
                                bitHeight = (height / scal).toInt()
                            }
                        }
                        if (bitHeight > maxHeight) {
                            bitHeight = maxHeight

                        }
                    }
                }
                val options = RequestOptions()
                        .override(bitWidth, bitHeight)
                if (!TextUtils.isEmpty(content) && !content.contains(".gif")) {
                    Glide.with(context)
                            .asBitmap()
                            .load(content)
                            .apply(options)
                            .into(contentImg)
                } else {
                    Glide.with(context)
                            .asGif()
                            .load(content)
                            .apply(options)
                            .into(contentImg)
                }
            }
        }
    }

    private fun setImageClick(v: View, currentContent: String?) {
        var imginfo = ArrayList<ImageInfo>()
        var index = 0
        var position = 0
        data.forEach {
            when (it.bean.type) {
                "gpic"
                -> {
                    val content = it.bean.content
                    if (currentContent == content) {
                        position = index
                    }
                    var info = ImageInfo()
                    info.bigImageUrl = content
                    info.thumbnailUrl = content
                    imginfo.add(info)
                    index++
                }
            }
        }
        PrevUtils.onImageItemClick(mContext, v, position, imginfo)
    }

    private fun setState(mood: String, stateTv: QzTextView) {
        var statedraw = 0
        var textColor = ContextCompat.getColor(mContext, R.color.white)
        var textbg: Drawable? = null
        when (mood) {
            mContext.getString(R.string.state1) -> {
                statedraw = R.mipmap.home_state_ku
                textColor = ContextCompat.getColor(mContext, R.color.tv_pink)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_pink_bg)
            }
            mContext.getString(R.string.state2) -> {
                statedraw = R.mipmap.home_state_liekai
                textColor = ContextCompat.getColor(mContext, R.color.color_orange)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_oragne_bg)
            }
            mContext.getString(R.string.state3) -> {
                statedraw = R.mipmap.home_state_kaixin
                textColor = ContextCompat.getColor(mContext, R.color.color_green)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_green_bg)
            }
            mContext.getString(R.string.state4) -> {
                statedraw = R.mipmap.home_state_kun
                textColor = ContextCompat.getColor(mContext, R.color.color_blue)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_blue_bg)
            }
            mContext.getString(R.string.state5) -> {
                statedraw = R.mipmap.home_state_fadai
                textColor = ContextCompat.getColor(mContext, R.color.color_pruple)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_prule_bg)
            }
            mContext.getString(R.string.state6) -> {
                statedraw = R.mipmap.home_state_gudan
                textColor = ContextCompat.getColor(mContext, R.color.color_orange)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_oragne_bg)
            }
            mContext.getString(R.string.state7) -> {
                statedraw = R.mipmap.home_state_youshang
                textColor = ContextCompat.getColor(mContext, R.color.color_blue)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_blue_bg)
            }
            mContext.getString(R.string.state8) -> {
                statedraw = R.mipmap.home_state_fennu
                textColor = ContextCompat.getColor(mContext, R.color.tv_pink)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_pink_bg)
            }
            mContext.getString(R.string.state9) -> {
                statedraw = R.mipmap.home_state_chigua
                textColor = ContextCompat.getColor(mContext, R.color.color_pruple)
                textbg = ContextCompat.getDrawable(mContext, R.drawable.label_state_prule_bg)
            }
        }
        stateTv.setTextColor(textColor)
        stateTv.background = textbg
        stateTv.setCompoundDrawablesWithIntrinsicBounds(statedraw, 0, 0, 0)
    }

    internal var listerner: OnChactApListerner? = null
    internal var mediaClickListener: ((Int, View) -> Unit)? = null
    fun setMediaClickListener(mediaClickListener: ((Int, View) -> Unit)) {
        this.mediaClickListener = mediaClickListener
    }

    fun setChatApListener(listerner: OnChactApListerner) {
        this.listerner = listerner
    }

    interface OnChactApListerner {
        fun onQsFileDownload(content: String, mediaType: String, flag: String, bean: ChatEntry)
        fun onFileDownload(content: String, bean: ChatEntry)

        fun onReSend(bean: ChatEntry): Boolean

        fun showUpFileProgress(type: String, pTv: TextView?, bean: ChatEntry, position: Int)

        fun compressAndShowBar(v: View, inputP: String, outputP: String, bean: ChatEntry, position: Int)

        fun emojiClick(entry: EmojiEntry)
    }
}