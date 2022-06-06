package com.eggplant.qiezisocial.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.GroupChatContract
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.event.GuserListEvent
import com.eggplant.qiezisocial.event.RemoveEvent
import com.eggplant.qiezisocial.event.SocketMsgEvent
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.UmSetting
import com.eggplant.qiezisocial.presenter.GroupChatPresenter
import com.eggplant.qiezisocial.socket.event.CustomPingEvent
import com.eggplant.qiezisocial.ui.chat.ChatAdapter
import com.eggplant.qiezisocial.ui.extend.dialog.ShareDialog
import com.eggplant.qiezisocial.ui.gchat.PastTopicActivity
import com.eggplant.qiezisocial.ui.main.MainActivity
import com.eggplant.qiezisocial.ui.main.OtherSpaceActivity
import com.eggplant.qiezisocial.ui.main.TopSmoothScroller
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.utils.mp3.RecorderListener
import com.eggplant.qiezisocial.widget.QzTextView
import com.eggplant.qiezisocial.widget.image.CircleImageView
import com.eggplant.qiezisocial.widget.keyboard.FloatEmojiKeyBoardBlack
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.luck.picture.lib.PictureSelector
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.android.synthetic.main.fragment_groupchat.*
import kotlinx.android.synthetic.main.pop_chat_add.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

/**
 * Created by Administrator on 2021/3/31.
 */

class GroupChatFragment : BaseMvpFragment<GroupChatPresenter>(), GroupChatContract.View {


    lateinit var adapter: ChatAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var addPopwindow: BasePopupWindow
    lateinit var loadBar: View
    private val REQUEST_PHOTO_ALBUM = 112
    private val REQUEST_ADD_VIDEO = 113
    private val REQUEST_TAKE_PHOTO = 114
    private val REQUEST_SHOW_IMG = 115
    private val REQUEST_LITTLE_TXT = 116
    private var windowHeight: Int = 0
    private var windowWidth: Int = 0
    private lateinit var shareDialog: ShareDialog
    var isPause = true
    var lastTitle: String = ""
    var currentGid: Int = 0
    var titleData: List<GchatEntry>? = null
    val clickList = ArrayList<Long>()

    companion object {

        private var fragment: GroupChatFragment? = null
            get() {
                if (field == null)
                    field = GroupChatFragment()
                return field
            }

        fun instanceFragment(bundle: Bundle?): GroupChatFragment {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }
    }

    override fun initPresenter(): GroupChatPresenter {
        return GroupChatPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_groupchat
    }

    override fun initView() {
        mPresenter.attachView(this)
        adapter = ChatAdapter(mContext!!, null)
        adapter.gchat = true
        adapter.showEdit = true
        adapter.requestcode = REQUEST_SHOW_IMG
        adapter.fragment = this
        loadBar = LayoutInflater.from(mContext).inflate(R.layout.layout_loadbar, null, false)
        loadBar.visibility = View.GONE
        adapter.setHeaderView(loadBar)
        layoutManager = LinearLayoutManager(mContext)
        ft_gchat_ry.layoutManager = layoutManager
        (ft_gchat_ry.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        ft_gchat_ry.adapter = adapter
        initAddPopwindow()
        initDialog()
        arguments?.let {
            currentGid = it.getInt("gid")
            val from = it.getString("from")
            if (from!=null&&from=="aplayer"){
                ft_gchat_rootview.setPadding(0,0,0,0)
                ft_gchat_bg.setImageResource(R.mipmap.music_chat_bg)
                ft_gchat_bg.scaleType=ImageView.ScaleType.CENTER
                val layoutParams = ft_gchat_bg.layoutParams as FrameLayout.LayoutParams
                layoutParams.setMargins(0,0,0,ScreenUtil.dip2px(mContext,85))
                ft_gchat_bg.layoutParams=layoutParams
                ft_chat_top_shadow.visibility=View.GONE
            }
            gPing(CustomPingEvent())
//            refreshDataList(currentGid, 6)
//            Log.i("chatft", "gid:$currentGid")
        }
    }

    private fun initDialog() {
        shareDialog = ShareDialog(mContext, intArrayOf(R.id.dlg_share_qq, R.id.dlg_share_pyq, R.id.dlg_share_wechat, R.id.dlg_share_sina))
        shareDialog.setOnBaseDialogItemClickListener { dialog, view ->
            when (view.id) {
                R.id.dlg_share_qq -> {
                    var umImage = UMImage(activity, R.mipmap.box_iauncher)
                    var web = UMWeb("https://www.baidu.com")
                    web.title = "This is music title"//标题
                    web.setThumb(umImage)  //缩略图
                    web.description = "my description"//描述
                    ShareAction(activity)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .withMedia(web)
                            .setCallback(object : UMShareListener {
                                override fun onResult(p0: SHARE_MEDIA?) {
                                    Log.i("GpShare", "share_qq  onResult")
                                }

                                override fun onCancel(p0: SHARE_MEDIA?) {
                                    Log.i("GpShare", "share_qq  onCancel")
                                }

                                override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
                                    Log.i("GpShare", "share_qq  onError:${p1?.cause} ${p1?.message} ")
                                }

                                override fun onStart(p0: SHARE_MEDIA?) {
                                    Log.i("GpShare", "share_qq  onStart")
                                }

                            })
                            .share()
                    Log.i("GpShare", "share_qq")
                }
                R.id.dlg_share_pyq -> {

                }
                R.id.dlg_share_wechat -> {

                }
                R.id.dlg_share_sina -> {

                }
            }
            dialog.dismiss()
        }
    }

    override fun initEvent() {
        ft_gchat_topic_close.setOnClickListener {
            ft_gchat_topic_packup.visibility = View.VISIBLE
            ft_gchat_topic_unfold.visibility = View.GONE
        }
        ft_gchat_topic_open.setOnClickListener {
            ft_gchat_topic_packup.visibility = View.GONE
            ft_gchat_topic_unfold.visibility = View.VISIBLE
        }
        ft_gchat_other.setOnClickListener {
            startActivity(Intent(mContext, PastTopicActivity::class.java))
        }
        ft_gchat_share.setOnClickListener {
            shareDialog.show()
        }
        ft_gchat_lightmsg.setOnClickListener {
            resetKeyboard()
            mPresenter.littleTxtClick(activity!!, this, REQUEST_LITTLE_TXT)
        }
        ft_gchat_rootview.setOnClickListener {
            resetKeyboard()
        }
        ft_gchat_ry.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_UP)
                resetKeyboard()
            false
        }
        ft_gchat_ry.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom && !isPause) {
                ft_gchat_ry.postDelayed({ scrollToPosition(adapter.data.size + adapter.headerLayoutCount) }, 50)
            }
        }
        ft_gchat_ry.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var firstNewMsgposition = adapter.data.size - newMsgCount
                var firstVisiablePosition = layoutManager.findLastCompletelyVisibleItemPosition() - adapter.headerLayoutCount
                if (firstVisiablePosition >= firstNewMsgposition) {
                    setNewMsgVisiable(false)
                }
            }
        })
        ft_gchat_newmsg_hint_gp.setOnClickListener {
            scrollToPosition(adapter.data.size + adapter.headerLayoutCount - newMsgCount)
            setNewMsgVisiable(false)
        }
        ft_gchat_keyboard.setOnFuncClickListener(object : FloatEmojiKeyBoardBlack.OnFuncClickListener {
            override fun onAddClick(v: View?) {
                val loc = IntArray(2)
                v?.getLocationOnScreen(loc)
                val x = loc[0]
                val y = loc[1]
                addPopwindow.showAtLocation(v, Gravity.NO_GRAVITY, windowWidth, y - windowHeight)
            }

            override fun onSendClick(str: String?) {
                if (currentGid != 0 && str != null && str.isNotEmpty()) {
                    mPresenter.sendTxt(activity!!, str, currentGid)
                    //设置友盟自定义事件统计
                    val map = HashMap<String, Any>()
                    map.put("action_type", "group_chat_send_txt")
                    map.put("user_info", "${application.infoBean!!.uid} ${application.infoBean!!.nick}")
                    MobclickAgent.onEventObject(activity, UmSetting.EVENT_GROUP_CHAT_SEND_TXT, map)
                }
            }
        })
        ft_gchat_keyboard.setRecorderListener(object : RecorderListener {
            override fun onRecorderStart() {
                showRecordView()
            }

            override fun onRecording(volume: Double) {
                val drawable = ft_gchat_voice.drawable
                if (volume > 10) {
                    drawable.level = volume.toInt() * 100
                }
            }

            override fun onStop(filePath: String?, duration: Double) {
                var dura = (duration / 1000).toInt()
                if (dura == 0)
                    dura = 1
                val createChatEntry = mPresenter.createChatEntry(filePath!!, dura, currentGid)
                val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_MINE_AUDIO, createChatEntry)
                addItem(chatEntryChatMultiBean = chatEntryChatMultiBean)
                scrollToBottom()
            }

            override fun onHideVoiceGp() {
                ft_gchat_record.visibility = View.GONE
            }

            override fun onShowVoice() {
                setVoiceVisibility(true)
            }

            override fun onShoCancle() {
                setVoiceVisibility(false)
            }
        })
        adapter.setChatApListener(object : ChatAdapter.OnChactApListerner {
            override fun onQsFileDownload(content: String, mediaType: String, flag: String, bean: ChatEntry) {
                //Do not have to do anything
            }

            override fun onFileDownload(content: String, bean: ChatEntry) {
                //Do not have to do anything
            }

            override fun onReSend(bean: ChatEntry): Boolean {
                val id = application.msgUUID
                application.msgUUID = "0"
                mPresenter.getMsgId(activity!!)
                val isSuccess = sendSocketMessage(mPresenter.getSendMsg(bean.type!!, id, bean.from.toString() + "", 0, currentGid, bean.content!!, bean.extra!!))
                if (isSuccess && !TextUtils.equals(id, "0")) {
                    bean.msgId = id
                }
                return isSuccess
            }

            override fun showUpFileProgress(type: String, pTv: TextView?, bean: ChatEntry, position: Int) {
                when (type) {
                    "gvideo" -> {
                        val videoPath = bean.content
                        val split = bean.extra!!.split("&&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (split.isNotEmpty()) {
                            val albunPath = split[0]
                            val media = ArrayList<String>()
                            media.add(videoPath!!)
                            media.add(albunPath)
                            mPresenter.uploadVideoMedia(type, activity!!, "", media, pTv, bean, position, currentGid)
                        }
                    }
                    "gpic", "gaudio" -> {
                        val picPath = bean.content
                        val media = ArrayList<String>()
                        if (picPath != null) {
                            media.add(picPath)
                            mPresenter.uploadPicOrAudioMedia(type, activity!!, "", media, pTv, bean, position, currentGid)
                        }
                    }
                }
            }

            override fun compressAndShowBar(v: View, inputP: String, outputP: String, bean: ChatEntry, position: Int) {
                mPresenter.compressVideo(v, inputP, outputP, bean, position)
            }

            override fun emojiClick(entry: EmojiEntry) {
                //Do not have to do anything
            }

        })
        adapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.adapter_chat_head -> {
                    var uid = adapter.data[position].bean.from
                    if (uid != 0L)
                        mPresenter.toOtherActivity(activity!!, uid)
//                    var intent = Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", userInfo)
//                    startActivity(intent)
//                    activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                }
            }
        }
        adapter.setMediaClickListener { p, v ->
            mPresenter.onMediaClick(this, p - adapter.headerLayoutCount, adapter.data, v, REQUEST_SHOW_IMG)
        }

    }

    private fun setNewMsgVisiable(b: Boolean) {
        if (b) {
            ft_gchat_newmsg_hint.text = "${newMsgCount}条新消息"
            ft_gchat_newmsg_hint_gp.visibility = View.VISIBLE
        } else {
            newMsgCount = 0
            ft_gchat_newmsg_hint_gp.visibility = View.GONE
        }
    }

    var hasSetScrollListener = false
    var loading = false
    var hasNewData = true
    private fun setRyScrollListener() {
        hasSetScrollListener = true
        ft_gchat_ry.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView!!.layoutManager as LinearLayoutManager
                val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstCompletelyVisibleItemPosition == 0 && !loading && hasNewData) {
                    var oldSize = adapter.data.size
                    loading = true
                    Log.i("groupChat", "scrollListener   -----------")
                    loadBar.visibility = View.VISIBLE
                    loadMoreData(currentGid)
                    ft_gchat_ry.postDelayed({
                        var newSize = adapter.data.size
                        if (newSize == oldSize) {
                            hasNewData = false
                        }
                        loadBar.visibility = View.GONE
                        loading = false

                    }, 1500)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    override fun initData() {

    }


    private fun initAddPopwindow() {
        addPopwindow = BasePopupWindow(mContext)
        addPopwindow.showAnimMode = 1
        addPopwindow.animationStyle = R.style.chat_pop_anim
        val addpopView = LayoutInflater.from(mContext).inflate(R.layout.pop_chat_add, null, false)
        addPopwindow.contentView = addpopView
        addpopView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        windowHeight = addpopView.measuredHeight
        windowWidth = addpopView.measuredWidth
        addPopwindow.setBgAlpha(1f)
        addpopView.pop_chat_add_video.visibility = View.GONE
        addpopView.pop_chat_add_call.visibility = View.GONE
        /**
         * 相册
         */
        addpopView.pop_chat_add_img.setOnClickListener {
            mPresenter.openGallery(this@GroupChatFragment, REQUEST_PHOTO_ALBUM)
            addPopwindow.dismiss()
        }
        /**
         * 拍照
         */
        addpopView.pop_chat_add_takephoto.setOnClickListener {
            mPresenter.takePhoto(this@GroupChatFragment, REQUEST_TAKE_PHOTO)
            addPopwindow.dismiss()
        }
        /**
         * 录像
         */
        addpopView.pop_chat_add_recordvideo.setOnClickListener {
            mPresenter.recordVideo(this@GroupChatFragment, REQUEST_ADD_VIDEO)
            addPopwindow.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PHOTO_ALBUM -> {
                    // 图片、视频、音频选择结果回调
                    var imgs = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (imgs == null || imgs.size <= 0) {
                        return
                    }
                    var delay = 0L
                    imgs.forEach {
                        var path = it.androidQToPath
                        if (path == null || path.isEmpty()) {
                            path = it.path
                        }
                        when {
                            it.mimeType.contains("video/") -> {
                                Log.i("selectFile", " file is video")
                                ft_gchat_ry.postDelayed({
                                    mPresenter.addVideo(mContext!!, path, currentGid)
                                }, delay)
                                delay += 500

                            }
                            it.mimeType.contains("image/") -> {
                                Log.i("selectFile", " file is audio")
                                ft_gchat_ry.postDelayed({
                                    mPresenter.addImg(mContext!!, path, currentGid)
                                }, delay)
                                delay += 500

                            }
                            else -> Log.i("selectFile", " file is other  ${it.mimeType}")
                        }
                    }
                }
                REQUEST_TAKE_PHOTO -> {
                    // 图片、视频、音频选择结果回调
                    var imgs = PictureSelector.obtainMultipleResult(data)
                    if (imgs == null || imgs.size <= 0) {
                        Log.i("selectFile", " file is empty")
                        return
                    }
                    val file = imgs[0]
                    var path = file.androidQToPath
                    if (path == null || path.isEmpty()) {
                        path = file.path
                    }
                    mPresenter.addImg(mContext!!, path, currentGid)
                }
                REQUEST_ADD_VIDEO -> {
                    // 图片、视频、音频选择结果回调
                    var imgs = PictureSelector.obtainMultipleResult(data)
                    if (imgs == null || imgs.size <= 0) {
                        Log.i("selectFile", " file is empty")
                        return
                    }
                    val file = imgs[0]
                    var path = file.androidQToPath
                    if (path == null || path.isEmpty()) {
                        path = file.path
                    }
                    mPresenter.addVideo(mContext!!, path, currentGid)
                }
                REQUEST_SHOW_IMG -> {
                    if (data != null) {
                        val txt = data.getStringExtra("txt")
                        val to = data.getLongExtra("uid", 0)
                        val type = data.getStringExtra("type")
                        val content = data.getStringExtra("content")
                        val extra = data.getStringExtra("extra")
                        val id = data.getLongExtra("id", 0)
                        if (to != 0L) {
                            if (txt != null && txt.isNotEmpty()) {
//                                mPresenter.sendLittleTxt(activity!!, to, txt, img)
                                mPresenter.sendLittleTxt(activity!!, to, txt, type, content, extra)
                                //设置友盟自定义事件统计
                                val map = HashMap<String, Any>()
                                map.put("action_type", "little_chat_send_txt")
                                map.put("user_info", "${application.infoBean!!.uid} ${application.infoBean!!.nick}")
                                MobclickAgent.onEventObject(activity, UmSetting.EVENT_LITTLE_CHAT_SEND_TXT, map)
                                TipsUtil.showToast(mContext, "消息已发送")
                            } else {
                                if (!clickList.contains(id)) {
                                    clickList.add(id)
                                    mPresenter.sendLittleTxt(activity!!, to, txt, type, content, extra)
                                }
                            }
                        }

                    }
                }
//                REQUEST_LITTLE_TXT ->{
//                    if (data!=null){
//                        val chatEntry = data.getSerializableExtra("remove") as ChatEntry?
//                        if (chatEntry!=null){
//                            Log.i("groupChat","REQUEST_LITTLE_TXT   remove is notempty ${chatEntry.msgId}")
//                            mPresenter.removeLittleTxt(chatEntry)
//                        }else{
//                            Log.i("groupChat","REQUEST_LITTLE_TXT   remove isempty")
//                        }
//                    }
//                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshTitle(data: List<GchatEntry>) {
//        if (data.isNotEmpty()) {
//            titleData = data
//            val data = data[0]
//            ft_gchat_usercont.text = data.usercount.toString()
//            ft_gchat_topiccount.text = data.topiccount.toString()
//            if (data.id != currentGid) {
//                Log.i("gchat","refresh ${data.id}  $currentGid")
//                ft_gchat_title.text = data.title
//                ft_gchat_title.setFontFormat(data.font)
//                adapter.data.clear()
//                adapter.notifyDataSetChanged()
//                if (addPopwindow.isShowing) {
////                    TipsUtil.showToast(mContext, "当前话题已更新")
//                    addPopwindow.dismiss()
//                }
////                refreshDataList(data.id, 6)
//                currentGid = data.id
//                lastTitle = data.title
//            }
//        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun newMsg(entry: GchatParcelEntry) {
        if (entry.entry.gsid != currentGid.toLong())
            return
//        if (adapter.data.size >= 14 && !hasSetScrollListener) {
//            setRyScrollListener()
//        }
//        if (!TextUtils.equals(entry.entry.type, "littletxt") && adapter.data.isNotEmpty()) {
//            val creatd = entry.entry.created.toLong()
//            val lastCreated = adapter.data[adapter.data.size - 1].bean.created.toLong()
//            if (creatd > lastCreated) {
//                mPresenter.addNewMsg(entry)
//                return
//            }
//            adapter.data.forEachIndexed { index, chatMultiEntry ->
//                if (creatd > chatMultiEntry.bean.created.toLong()) {
//                    if (adapter.data.size > index + 1) {
//                        var lastCreated = adapter.data[index + 1].bean.created.toLong()
//                        if (creatd < lastCreated) {
//                            mPresenter.addMsg(index + 1, entry)
//                            return
//                        }
//                    }
//                } else {
//                    mPresenter.addMsg(index, entry)
//                    return
//                }
//            }
//            mPresenter.addNewMsg(entry)
//        } else {
//            mPresenter.addNewMsg(entry)
//        }

        mPresenter.addNewMsg(entry)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun remove(entry: RemoveEvent) {
        val chatEntry = entry.entry
        mPresenter.removeLittleTxt(chatEntry)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun userlist(entry: GuserListEvent) {
        addUserHead(entry.count!!, entry.list)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gPing(entry: CustomPingEvent) {
        var json = JSONObject()
        json.put("type", "group")
        json.put("act", "gping_movie")
        json.put("created", System.currentTimeMillis())
        json.put("groupid", currentGid)
        EventBus.getDefault().post(SocketMsgEvent(json.toString()))
    }

    private fun addUserHead(count: Int, list: List<UserEntry>?) {
        val numbTxt = ft_gchat_numb_gp.getChildAt(0)
        (numbTxt as QzTextView).text = "$count"
        if (ft_gchat_numb_gp.childCount < 9) {
            ft_gchat_numb_gp.removeAllViews()
            ft_gchat_numb_gp.addView(numbTxt)
            list?.forEach {
                val user = it
                val circleImageView = CircleImageView(mContext)
                circleImageView.layoutParams = getUserHeadParams()
                Glide.with(mContext!!).load(API.PIC_PREFIX + it.face).into(circleImageView)
                circleImageView.setOnClickListener {
                    startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", user))
                    activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                }
                ft_gchat_numb_gp.addView(circleImageView)
            }
        }
    }

    private fun getUserHeadParams(): LinearLayout.LayoutParams {
        val dp22 = ScreenUtil.dip2px(mContext, 22)
        val dp6 = ScreenUtil.dip2px(mContext, 6)
        val layoutParams = LinearLayout.LayoutParams(dp22, dp22)
        layoutParams.leftMargin = dp6
        layoutParams.gravity = Gravity.CENTER_VERTICAL
        return layoutParams
    }

    private fun refreshDataList(gid: Int, count: Int) {
        var json = JSONObject()
        json.put("type", "group")
        json.put("act", "getmsg")
        json.put("fromtime", System.currentTimeMillis() / 1000)
        json.put("count", count)
        json.put("groupid", gid)
        EventBus.getDefault().post(SocketMsgEvent(json.toString()))
    }


    private fun loadMoreData(gid: Int) {
        if (adapter.data.isNotEmpty()) {
            var json = JSONObject()
            json.put("type", "group")
            json.put("act", "getmsg")
            json.put("fromtime", (adapter.data[0].bean.created.toLong()) / 1000 - 2)
            json.put("count", 15)
            json.put("groupid", gid)
            EventBus.getDefault().post(SocketMsgEvent(json.toString()))
        }

    }

    override fun notifyAdapterItemChanged(position: Int) {
        adapter.notifyItemChanged(position)
    }

    override fun addItem(chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>) {
        adapter.addData(chatEntryChatMultiBean)
    }

    override fun addItem(position: Int, chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>) {
        Log.i("groupChat", "position:$position")
        if (adapter.data.size > position) {
            adapter.data.add(position, chatEntryChatMultiBean)
            adapter.notifyItemRangeInserted(position, 1)
        }
    }

    override fun setLittleChatSize(size: Int) {
        if (size > 0) {
            ft_gchat_msg_hint.text = "$size"
            ft_gchat_lightmsg.visibility = View.VISIBLE
        } else {
            ft_gchat_lightmsg.visibility = View.GONE
        }
        showLittleMsg()
    }

    private fun showLittleMsg() {
        if (!isPause && (activity as MainActivity).getCurrentFragment() == this && ft_gchat_keyboard.edit.text.toString().isEmpty()) {
            resetKeyboard()
            mPresenter.littleTxtClick(activity!!, this, REQUEST_LITTLE_TXT)
        }
    }

    var newMsgCount = 0
    override fun showNewMsg() {
        val itemCount = adapter.data.size
        var lastPosition = layoutManager.findLastVisibleItemPosition()
//        Log.i("groupChat", "showNewMsg itemCount:$itemCount  lastPosition:$lastPosition")
        if (lastPosition < itemCount - 1 && itemCount >= 30) {
            newMsgCount++
            setNewMsgVisiable(true)
        } else {
            scrollToBottom()
        }
    }

    var scrolling = false
    var needScroll = false
    override fun scrollToBottom() {
        setNewMsgVisiable(false)
        if (!scrolling) {
            scrolling = true
            ft_gchat_ry?.postDelayed({
                scrolling = false
                ft_gchat_ry.scrollToPosition(adapter.data.size + adapter.headerLayoutCount - 1)
                layoutManager.scrollToPositionWithOffset(adapter.data.size + adapter.headerLayoutCount - 1, 0)//先要滚动到这个位置
                val target = layoutManager.findViewByPosition(adapter.data.size + adapter.headerLayoutCount - 1)//然后才能拿到这个View
                if (target != null) {
                    layoutManager.scrollToPositionWithOffset(adapter.data.size + adapter.headerLayoutCount - 1,
                            ft_gchat_ry.measuredHeight - target.measuredHeight)//滚动偏移到底部
                }
                if (needScroll) {
                    needScroll = false
                    scrollToBottom()
                }
            }, 50)
        } else {
            needScroll = true
        }
    }

    fun scrollToPosition(position: Int) {
        var position = position
        if (position < 0)
            position = 0
        var scroller = TopSmoothScroller(activity!!) as LinearSmoothScroller
        scroller.targetPosition = position
        layoutManager.startSmoothScroll(scroller)
        Log.i("groupChat", "scrollToPosition  postion:$position  dataSize:${adapter.data.size}")
//        layoutManager.scrollToPosition(position)
//        val outLocation = IntArray(2)
//        var itemLayout = layoutManager.findViewByPosition(position)
//        if (itemLayout != null) {
//            itemLayout.getLocationOnScreen(outLocation)
//            Log.i("groupChat","itemLayout x:${outLocation[0]}  y:${outLocation[1]}")
//        }else{
//            Log.i("groupChat","itemLayout is empty  postion:$position")
//        }
    }

    override fun sendSocketMessage(sendMsg: String): Boolean {
        EventBus.getDefault().post(SocketMsgEvent(sendMsg))
        return true
    }

    fun expressionClick(str: String?) {
        ft_gchat_keyboard.input(ft_gchat_keyboard.edit, str)
    }

    fun expressionaddRecent(str: String?) {
        ft_gchat_keyboard.expressionaddRecent(str)
    }

    fun expressionDeleteClick(v: View?) {
        ft_gchat_keyboard.delete(ft_gchat_keyboard.edit)
    }

    fun resetKeyboard() {
        ft_gchat_keyboard.reset()
    }

    private fun setVoiceVisibility(b: Boolean) {
        if (b) {
            ft_gchat_voice.visibility = View.VISIBLE
            ft_gchat_audio_top_cancle.visibility = View.GONE
        } else {
            ft_gchat_voice.visibility = View.GONE
            ft_gchat_audio_top_cancle.visibility = View.VISIBLE
        }
    }


    private fun showRecordView() {
        ft_gchat_record.visibility = View.VISIBLE
        ft_gchat_audio_top.visibility = View.VISIBLE
        ft_gchat_voice.visibility = View.VISIBLE
        ft_gchat_audio_top_cancle.visibility = View.GONE
    }

    var lastNote = ""
    fun setNewMsg(note: String) {
        if (lastNote != "" && lastNote == note)
            return
        mPresenter.createMsg(note, currentGid)
        lastNote = note
    }

    override fun onDestroyView() {
        fragment = null
        super.onDestroyView()
    }
}
