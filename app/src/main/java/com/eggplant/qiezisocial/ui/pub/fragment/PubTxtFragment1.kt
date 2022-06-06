package com.eggplant.qiezisocial.ui.pub.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.contract.HomeContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.FilterEntry
import com.eggplant.qiezisocial.entry.SysQuestionEntry
import com.eggplant.qiezisocial.entry.TopicEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.HomeModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.pub.PubHintDialog
import com.eggplant.qiezisocial.ui.pub.PubLabelAdapter
import com.eggplant.qiezisocial.ui.pub.PubTxtActivity
import com.eggplant.qiezisocial.utils.*
import com.eggplant.qiezisocial.widget.MediaView
import com.eggplant.qiezisocial.widget.dialog.QzProgressDialog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.tbruyelle.rxpermissions2.RxPermissions
import com.vincent.videocompressor.VideoCompress
import kotlinx.android.synthetic.main.fragment_pubtxt1.*
import sj.keyboard.utils.EmoticonsKeyboardUtils
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Administrator on 2020/4/23.
 */

class PubTxtFragment1 : BaseFragment() {
    private var model: HomeContract.Model = HomeModel()
    private val REQUEST_SELECT_MEDIA: Int = 101
    private lateinit var progressDialog: QzProgressDialog
    private lateinit var hintDialog: PubHintDialog
    private lateinit var adapter: PubLabelAdapter
    private var pubmode = 1
    private var pubDestory = true
    private var broadcaost = true
    var qsIndex = 0
    var lastX = 0f
    var lastY = 0f
    var firstTxt = "问一个深入灵魂的问题。。。"
    var from: String? = ""
    var nick: String? = ""
    var uid: Int? = 0
    var goalDector = arrayListOf<Int>(R.drawable.icon_home_label_dector1, R.drawable.icon_home_label_dector2, R.drawable.icon_home_label_dector3,
            R.drawable.icon_home_label_dector4, R.drawable.icon_home_label_dector5, R.drawable.icon_home_label_dector6, R.drawable.icon_home_label_dector7)
    var beforTxt = ""
    private val mTextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable) {
            // 先去掉监听器，否则会出现栈溢出
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforTxt = s.toString()
//            Log.i("emojitest", "beforeTextChanged s=$s  start:$start  after:$after  count:$count")
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//            Log.i("emojitest", "onTextChanged s=$s  start:$start  befor:$before  count:$count")
            val rexgString = "\\[qzxs\\d+\\]"
            val compile = Pattern.compile(rexgString)
            val matcher = compile.matcher(s)

            var lenght = s.length
            while (matcher.find()) {
                lenght = lenght - matcher.group().length + 1
            }
            pub_txt_numb?.let {
                it.text="$lenght/35"
            }
            Log.i("pubtxt", "$lenght/35 ")
//            Log.i("emojitest", "$s   $start  $before  $count")
            if (before >= 7 && count == 0) {
                if (pub_txt_content.filters != null && pub_txt_content.filters[0] is InputFilter.LengthFilter) {
                    var hasEmoji=false
                    var txt = beforTxt.replace(s.toString(), "")
                    val matcher2 = compile.matcher(txt)
                    while (matcher2.find()){
                        hasEmoji=true
                    }
                    if(hasEmoji) {
                        var max = (pub_txt_content.filters[0] as InputFilter.LengthFilter).max
                        max -= before - 1
                        Log.i("emojitest", "onTextChanged max :$max")
                        pub_txt_content.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max))
                    }
                }
            }
        }
    }

    companion object {

        fun newInstance(bundle: Bundle?): PubTxtFragment1 {
            var fragment = PubTxtFragment1()
            if (bundle != null)
                fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_pubtxt1
    }

    override fun initView() {
        from = arguments?.getString("from")
        nick = arguments?.getString("nick")
        uid = arguments?.getInt("uid")
        adapter = PubLabelAdapter(null)
        if (from != null && from == "space" && nick != null && nick!!.isNotEmpty() && uid != null && uid!! != 0) {
            pub_txt_topic.visibility = View.GONE
            pub_txt_private_txt.visibility = View.VISIBLE
            pub_txt_private_txt.text = "给${nick}发一个私人问题"
            pub_txt_next.text="发送"
            pub_txt_destory.visibility=View.GONE
        } else {
            firstTxt="话题越好 回复越多。。。"
//            pub_txt_ry.layoutManager = LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false)
//            pub_txt_ry.adapter = adapter
//            pub_txt_ry.addItemDecoration(DividerLinearItemDecoration(ScreenUtil.dip2px(mContext, 10), ContextCompat.getColor(mContext!!, R.color.translate), 0, 0))
        }
        pub_txt_dice_img.isClickable = true
        pub_txt_emojikeyboard.setEmojiContent(pub_txt_content)
        pub_txt_content.setText(firstTxt)
        pub_txt_content.randomFontFormat()
        pub_txt_content.setSelection(firstTxt.length)
        pub_txt_content.requestFocus()
//        val topicTxt = activity!!.intent.getStringExtra("question")
//        pub_txt_topic.text = topicTxt
        initDialog()
        setIcon()
//        setDector()
    }

    private fun setDector() {

        if (application.loginEntry!!.filter != null && application.loginEntry!!.filter.goal.isNotEmpty()) {
            application.loginEntry!!.scenes.forEachIndexed { index, scenesEntry ->
                if (application.loginEntry!!.filter.goal == scenesEntry.title) {
                    if (index < 6) {
                        pub_txt_icon.setImageResource(goalDector[index])
                    } else {
                        pub_txt_icon.setImageResource(goalDector[6])
                    }
                }
            }

        }
    }

    private fun setIcon() {
        val spaceback = application.infoBean!!.spaceback
        if (spaceback.isNotEmpty() && spaceback.toInt() < 12) {
            pub_txt_next.background = ContextCompat.getDrawable(mContext!!, R.drawable.home_pub_bg)
            when (spaceback.toInt()) {
                0 -> {
                    pub_txt_icon.setImageResource(R.drawable.icon_home_dector1)
                }
                1 -> {
                    pub_txt_icon.setImageResource(R.drawable.icon_home_dector2)
                }
                2 -> {
                    pub_txt_icon.setImageResource(R.drawable.icon_home_dector3)
                }
                3 -> {
                    pub_txt_icon.setImageResource(R.drawable.icon_home_dector4)
                }
                4 -> {
                    pub_txt_icon.setImageResource(R.drawable.icon_home_dector5)
                }
                5 -> {
                    pub_txt_icon.setImageResource(R.drawable.icon_home_dector6)
                }
                6 -> {
                    pub_txt_icon.setImageResource(R.drawable.icon_home_dector7)
                }
                7 -> {
                    pub_txt_icon.setImageResource(R.drawable.icon_home_dector8)
                }
                8 -> {
                    pub_txt_icon.setImageResource(R.drawable.icon_home_dector9)
                }

            }
        } else if (spaceback.isEmpty()) {
            pub_txt_icon.setImageResource(R.drawable.icon_home_dector1)
        }
    }


    private fun initDialog() {
        progressDialog = QzProgressDialog(mContext)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setMessage("正在压缩...")
        hintDialog = PubHintDialog(mContext!!, null)
        hintDialog.setOnDismissListener {
            if (application.loginEntry!!.chance > 0) {
                costChance()
            }
        }


    }

    override fun initEvent() {
        pub_txt_mediaview.setAddView()
        pub_txt_mediaview.addClickListener={
            mediaView, v ->
            RxPermissions(this)
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe { b ->
                        if (b) {
                            pub_txt_mediaview.maxSize.let {
                                var type = PictureMimeType.ofAll()
                                if (it == 3) {
                                    type = PictureMimeType.ofAll()
                                } else if (it > 0) {
                                    type = PictureMimeType.ofImage()
                                }
                                PictureSelector.create(this)
                                        .openGallery(type)
                                        .loadImageEngine(GlideEngine.createGlideEngine())
                                        .isCamera(true)//是否显示拍照按钮 true or false
                                        .enableCrop(false)// 是否裁剪 true or false
                                        .compress(true)// 是否压缩 true or false
                                        .maxSelectNum(it)
                                        .maxVideoSelectNum(1)
                                        .videoMaxSecond(2*60)
                                        .forResult(REQUEST_SELECT_MEDIA)
                            }
                        }
                    }
        }
        pub_txt_content.addTextChangedListener(mTextWatcher)
        pub_txt_next.setOnClickListener {
            var context = pub_txt_content.text?.toString()?.trimEnd()
            var hintTxt = pub_txt_content.hint?.toString()?.trimEnd()
//            val selectLabel = adapter.selectLabel
//            if (from != "space" && TextUtils.isEmpty(selectLabel)) {
//                TipsUtil.showToast(mContext, "还没选择标签哦~")
//                return@setOnClickListener
//            }
            if (context != null && !TextUtils.isEmpty(context) && !TextUtils.equals(context, firstTxt)) {
                var superActivity = activity as PubTxtActivity
                if (from == "space" && uid != null) {
                    superActivity.onNext(context, pub_txt_content.getFontFormat(), uid!!,pub_txt_mediaview.data)
                } else {
                    superActivity.onNext(adapter.selectLabel, context, pub_txt_content.getFontFormat(), pub_txt_mediaview.data, if (pubDestory) "yes" else "no", pubmode, if (broadcaost) 1 else 0)
                }
                pub_txt_next.isClickable=false
            } else if (hintTxt != null && !TextUtils.isEmpty(hintTxt) && !TextUtils.equals(hintTxt, firstTxt)) {
                var superActivity = activity as PubTxtActivity
                if (from == "space" && uid != null) {
                    superActivity.onNext(hintTxt, pub_txt_content.getFontFormat(), uid!!,pub_txt_mediaview.data)
                } else {
                    superActivity.onNext(adapter.selectLabel, hintTxt, pub_txt_content.getFontFormat(), pub_txt_mediaview.data, if (pubDestory) "yes" else "no", pubmode, if (broadcaost) 1 else 0)
                }
                pub_txt_next.isClickable=false
            } else {
                TipsUtil.showToast(mContext, "你想说些什么呢？")
            }
        }

        pub_txt_private_chat.setOnClickListener {
            pubmode = 1
            changePubMode()
        }

        pub_txt_group_chat.setOnClickListener {
            pubmode = 2
            changePubMode()
        }

        pub_txt_destory.setOnClickListener {
            setPubDestory()
        }

        ft_pub_rootview.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    EmoticonsKeyboardUtils.closeSoftKeyboard(pub_txt_content)
                    pub_txt_emojikeyboard.reset()
                    if (event.x - lastX > ScreenUtil.getDisplayWidthPixels(mContext) / 3 && (Math.abs(lastX - event.x) > Math.abs(lastY - event.y))) {
                        activity?.finish()
                    }
                    lastY = 0f
                    lastX = 0f
                }
            }
            true
        }

        pub_txt_select_media.setOnClickListener {
            RxPermissions(this)
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe { b ->
                        if (b) {
                            pub_txt_mediaview.maxSize.let {
                                var type = PictureMimeType.ofAll()
                                if (it == 4) {
                                    type = PictureMimeType.ofAll()
                                } else if (it > 0) {
                                    type = PictureMimeType.ofImage()
                                }
                                PictureSelector.create(this)
                                        .openGallery(type)
                                        .loadImageEngine(GlideEngine.createGlideEngine())
                                        .isCamera(true)//是否显示拍照按钮 true or false
                                        .enableCrop(false)// 是否裁剪 true or false
                                        .compress(true)// 是否压缩 true or false
                                        .maxSelectNum(it)
                                        .forResult(REQUEST_SELECT_MEDIA)
                            }
                        }
                    }

        }

        pub_txt_mediaview.listener = object : MediaView.OnMediaRemoveListener {
            override fun onMediaRemove(view: MediaView,path:String) {
                pub_txt_mediaview.setAddView()
//                pub_txt_select_media.visibility = View.VISIBLE
//                if (pub_txt_mediaview.maxSize == 4) {
//                    pub_txt_select_media.text = "上传照片或视频"
//                } else {
//                    pub_txt_select_media.text = "上传照片"
//                }
            }
        }

        pub_txt_next_style.setOnClickListener {
            pub_txt_content.nextFontFormat()
        }

        pub_txt_next_content.setOnClickListener {
            nextContent()
//            if (application.loginEntry!!.chance == 5 || application.loginEntry!!.chance == 0) {
//                hintDialog.show()
//            } else {
//                costChance()
//            }
        }
        pub_txt_content.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (pub_txt_content.text.toString() == firstTxt) {
                    pub_txt_content.setText("")
                    pub_txt_hint.visibility=View.GONE
                }
            }
            false
        }
    }

    private fun nextContent() {
        pub_txt_next_content.isClickable = false
        loadDice()
        pub_txt_next_content?.postDelayed({
            pub_txt_dice_img?.let {
                it.visibility = View.GONE
                nextQs()

                pub_txt_next_content?.isClickable = true
            }
        }, 2000)
    }

    private fun costChance() {
        model.costChance(object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful && response.body().stat == "ok") {
                    application.loginEntry!!.chance--
                    nextContent()
                }
            }
        })
    }

    private fun setPubDestory() {
//        pubDestory = !pubDestory
        broadcaost = !broadcaost
        if (broadcaost) {
            pub_txt_destory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_circle_select2, 0)
        } else {
            pub_txt_destory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_circle_unselect2, 0)
        }
    }

    private fun changePubMode() {
        if (pubmode == 1) {
            pub_txt_private_chat.background = ContextCompat.getDrawable(mContext!!, R.drawable.pub_select_bg)
            pub_txt_group_chat.background = ContextCompat.getDrawable(mContext!!, R.drawable.pub_unselect_bg)
        } else {
            pub_txt_private_chat.background = ContextCompat.getDrawable(mContext!!, R.drawable.pub_unselect_bg)
            pub_txt_group_chat.background = ContextCompat.getDrawable(mContext!!, R.drawable.pub_select_bg)
        }
    }

    override fun initData() {
//        var txt = activity!!.intent.getStringExtra("txt")
//        var font = activity!!.intent.getStringExtra("font")
//
//        qsIndex = activity!!.intent.getIntExtra("qsindex", 0)
//        txt.let { pub_txt_content.hint = it }
//        if (!TextUtils.isEmpty(font)) {
//            pub_txt_content.setFontFormat(font)
//        }
        if (application.qsList == null) {
            model.getQuestion(activity!!, object : JsonCallback<BaseEntry<SysQuestionEntry>>() {
                override fun onSuccess(response: Response<BaseEntry<SysQuestionEntry>>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            var qsList = it.body().list
                            QzApplication.get().qsList = qsList
                        }
                    }
                }
            })
        }
        if (application.loginEntry!!.filter != null && application.loginEntry!!.filter.goal.isNotEmpty()) {
            setLabelData(application.loginEntry!!.filter.goal)
        } else {
            getFilterData()
        }
    }

    private fun getFilterData() {
        OkGo.post<BaseEntry<FilterEntry>>(API.GET_FILTER)
                .execute(object : JsonCallback<BaseEntry<FilterEntry>>() {
                    override fun onSuccess(response: Response<BaseEntry<FilterEntry>>?) {
                        if (response!!.isSuccessful) {
                            val filter = response.body().filter
                            if (filter != null && filter.goal.isNotEmpty()) {
                                application.filterData = filter
                                setLabelData(filter.goal)
                            }
                        }
                    }
                })
    }

    private fun setLabelData(goal: String?) {
        val scenes = application.loginEntry!!.scenes
        scenes.forEach {
            if (it.title == goal) {
                adapter.setNewData(it.label)
                return
            }
        }
    }

    fun expressionClick(str: String?) {
        pub_txt_emojikeyboard.input(pub_txt_content, str)
    }

    fun expressionaddRecent(str: String?) {
        pub_txt_emojikeyboard.expressionaddRecent(str)
    }

    fun expressionDeleteClick() {
        pub_txt_emojikeyboard.delete(pub_txt_content)

    }

    var question: List<String>? = null
    private fun nextQs() {

        if (question != null && question!!.isNotEmpty()) {
            if (qsIndex >= question!!.size) {
                qsIndex = 0
            }
            var content = question!![qsIndex]
            pub_txt_content.setText(content)
            pub_txt_content.setSelection(content.length)
            pub_txt_content.requestFocus()
            var lastqsIndex = qsIndex
            qsIndex = Random().nextInt(question!!.size)
            if (qsIndex == lastqsIndex) {
                qsIndex++
            }
        } else {
            var goal = QzApplication.get().loginEntry?.filter?.goal
            var sid = QzApplication.get().loginEntry?.filter?.sid
            if (goal != null&&sid!=null)
                model.getTopic(activity!!, goal, sid,object : JsonCallback<TopicEntry>() {
                    override fun onSuccess(response: Response<TopicEntry>?) {
                        if (response!!.isSuccessful) {
                            question = response!!.body().question
                            if (question != null && question!!.isNotEmpty())
                                nextQs()
                            else
                                TipsUtil.showToast(mContext, "empty question")
                        }
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_MEDIA && resultCode == RESULT_OK) {
            var selectList = PictureSelector.obtainMultipleResult(data)
            selectList?.forEach {
                var path = it.androidQToPath
                if (path == null || path.isEmpty()) {
                    path = it.path
                }
                when {
                    it.mimeType.contains("video/") -> {
                        compressVideo(srcPath = path)
                    }
                    it.mimeType.contains("image/") -> {
                        pub_txt_mediaview.setImage(it.compressPath)
                    }
                    else -> Log.i("selectFile", " file is other  ${it.mimeType}")
                }

            }
            if (pub_txt_mediaview.maxSize==0)
                pub_txt_mediaview.removeAddView()
//            if (pub_txt_mediaview.maxSize == 0) {
//                pub_txt_select_media.visibility = View.GONE
//            } else if (pub_txt_mediaview.maxSize < 3) {
//                pub_txt_select_media.text = "上传照片"
//            }
        }
    }

    private fun compressVideo(srcPath: String) {
        val size = FileSizeUtil.getFileOrFilesSize(srcPath, FileSizeUtil.SIZETYPE_MB)
        if (size < 10) {
            pub_txt_mediaview.setVideo(srcPath)
            return
        }
        var desPath = FileUtils.getTempFilePath(context) + "${System.currentTimeMillis()}.mp4"
        var listener = object : VideoCompress.CompressListener {
            override fun onSuccess() {
                progressDialog.dismiss()
                pub_txt_mediaview.setVideo(desPath)
            }

            override fun onFail() {
                progressDialog.dismiss()
                pub_txt_mediaview.setVideo(srcPath)
            }

            override fun onProgress(percent: Float) {
                progressDialog.setMessage("100%${percent.toInt()}")
            }

            override fun onStart() {
                progressDialog.show()
            }
        }
        VideoCompress.compressVideoMedium(srcPath, desPath, listener)
    }

    private fun loadDice() {
        pub_txt_dice_img.visibility = View.VISIBLE
        pub_txt_dice_img.removeAllViews()
        var img = ImageView(mContext)
        var params = FrameLayout.LayoutParams(resources.getDimension(R.dimen.qb_px_240).toInt(), resources.getDimension(R.dimen.qb_px_240).toInt())
        params.gravity = Gravity.CENTER
        params.setMargins(0, 0, 0, 170)
        img.layoutParams = params
        pub_txt_dice_img.addView(img)
        var options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        Glide.with(mContext!!).asGif().load(R.mipmap.dice).apply(options).into(img)
    }

    override fun onDestroyView() {
        pub_txt_emojikeyboard.reset()
        pub_txt_content.removeTextChangedListener(mTextWatcher)
        super.onDestroyView()
    }

}
