package com.eggplant.qiezisocial.ui.extend.fragment

import android.content.Intent
import android.os.Bundle
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.extend.PubDiaryActivity
import com.eggplant.qiezisocial.utils.GlideEngine
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.MediaView
import com.eggplant.qiezisocial.widget.topbar.TopBarListener
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.fragment_pub_diary.*

/**
 * Created by Administrator on 2020/10/29.
 */

class PubDiaryFragment : BaseFragment() {
    companion object {

        fun newInstance(bundle: Bundle?): PubDiaryFragment {
            var fragment = PubDiaryFragment()
            if (bundle != null)
                fragment.arguments = bundle
            return fragment
        }
    }

    private val REQUEST_CODE_SELECT = 101
    var mediaPath: String? = ""
    var mediaType: String? = ""
    var medias: ArrayList<LocalMedia>? = null
    var pubMedia = ArrayList<String>()
    //        var firstTxt = "这一刻你想说点什么"
    override fun getLayoutId(): Int {
        return R.layout.fragment_pub_diary
    }

    override fun initView() {
        pub_diary_emojikeyboard.setEmojiContent(pub_diary_content)
        if (arguments == null)
            return
        mediaPath = arguments!!.getString("recordPath")
        val imgs = arguments!!.getSerializable("imgs")
        mediaType = arguments!!.getString("mediaType")
        if (imgs != null) {
            medias = imgs as ArrayList<LocalMedia>
        }
//        pub_diary_content.setSelection(firstTxt.length)
//        pub_diary_content.requestFocus()
        if (mediaPath != null && mediaPath!!.isNotEmpty()) {
            pubMedia.add(mediaPath!!)
        } else if (medias != null && medias!!.isNotEmpty()) {
            medias!!.forEach {
                var path = it.androidQToPath
                if (it.isCompressed) {
                    path = it.compressPath
                } else if (path == null || path.isEmpty()) {
                    path = it.path
                }
                pubMedia.add(path)
            }
        }
        setMediaView()
    }


    private fun setMediaView() {
        pub_diary_mediagp.removeAllViews()
        pubMedia.forEach {
            val mediaView = MediaView(mContext!!)
            if (mediaType == "video") {
                mediaView.setVideo(it)
            } else {
                mediaView.setImage(it)
            }
            mediaView.listener = object : MediaView.OnMediaRemoveListener {
                override fun onMediaRemove(view: MediaView, path: String) {
                    pubMedia.remove(path)
                    setMediaView()
                }
            }
            pub_diary_mediagp.addView(mediaView)
        }
        if (mediaType=="video"){
            if (pubMedia.isEmpty()){
                val mediaView = MediaView(mContext!!)
                mediaView.setAddView()
                mediaView.addClickListener = { mediaView, v ->
                    var gallerType = PictureMimeType.ofAll()
                    PictureSelector.create(this@PubDiaryFragment)
                            .openGallery(gallerType)
                            .loadImageEngine(GlideEngine.createGlideEngine())
                            .isCamera(false)
                            .maxSelectNum(9 - pubMedia.size)
                            .maxVideoSelectNum(1)
                            .forResult(REQUEST_CODE_SELECT)

                }
                pub_diary_mediagp.addView(mediaView)
            }
        }else {
            if (pubMedia.size < 9) {
                val mediaView = MediaView(mContext!!)
                mediaView.setAddView()
                mediaView.addClickListener = { mediaView, v ->
                    var gallerType = PictureMimeType.ofAll()
                    if (pubMedia.isNotEmpty())
                        gallerType = PictureMimeType.ofImage()
                    PictureSelector.create(this@PubDiaryFragment)
                            .openGallery(gallerType)
                            .loadImageEngine(GlideEngine.createGlideEngine())
                            .isCamera(false)
                            .maxSelectNum(9 - pubMedia.size)
                            .maxVideoSelectNum(1)
                            .forResult(REQUEST_CODE_SELECT)
                }
                pub_diary_mediagp.addView(mediaView)
            }
        }
    }


    override fun initEvent() {
        pub_diary_bar.setTbListener(object : TopBarListener {
            override fun onTxtClick() {
                var context = pub_diary_content.text?.toString()
                if (context != null && context.isNotEmpty()) {
                    var superActivity = activity as PubDiaryActivity
                    superActivity.pubDiary(activity!!,context,pubMedia,mediaType)
                } else {
                    TipsUtil.showToast(mContext, "你想说些什么呢？")
                }
            }

            override fun onReturn() {
                activity?.finish()
            }
        })

//        pub_diary_next.setOnClickListener {
//            var context = pub_diary_content.text?.toString()
//            if (context!=null&&context.isNotEmpty()){
//                var superActivity = activity as PubDiaryActivity
//                superActivity.pubDiary(context)
//            }else{
//                TipsUtil.showToast(mContext, "你想说些什么呢？")
//            }
//        }
    }

    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT) {
            var imgs = PictureSelector.obtainMultipleResult(data)
            if (imgs == null || imgs.size <= 0) {
                return
            }
            mediaType="pic"
            imgs.forEach {
                if (it.mimeType.contains("video/")) {
                    mediaType = "video"
                }
                var path = it.androidQToPath
                if (it.isCompressed) {
                    path = it.compressPath
                } else if (path == null || path.isEmpty()) {
                    path = it.path
                }
                pubMedia.add(path)
            }
            setMediaView()
        }
    }

    fun expressionClick(str: String?) {
        pub_diary_emojikeyboard.input(pub_diary_content, str)
    }

    fun expressionaddRecent(str: String?) {
        pub_diary_emojikeyboard.expressionaddRecent(str)
    }

    fun expressionDeleteClick() {
        pub_diary_emojikeyboard.delete(pub_diary_content)
    }

    override fun onDestroyView() {
        pub_diary_emojikeyboard.reset()
        super.onDestroyView()
    }
}
