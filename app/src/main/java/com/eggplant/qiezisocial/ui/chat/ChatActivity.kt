package com.eggplant.qiezisocial.ui.chat

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseWebSocketActivity
import com.eggplant.qiezisocial.contract.ChatContract
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.EmojiEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.UmSetting
import com.eggplant.qiezisocial.presenter.ChatPresenter
import com.eggplant.qiezisocial.socket.AbsBaseWebSocketService
import com.eggplant.qiezisocial.socket.WebSocketService
import com.eggplant.qiezisocial.socket.event.CommonResponse
import com.eggplant.qiezisocial.socket.event.NewMsgEvent
import com.eggplant.qiezisocial.socket.event.WebSocketSendDataErrorEvent
import com.eggplant.qiezisocial.ui.chat.dialog.ChatDelDialog
import com.eggplant.qiezisocial.utils.NotifycationUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.utils.mp3.RecorderListener
import com.eggplant.qiezisocial.utils.selectabletxt.OnSelectTextClickListener
import com.eggplant.qiezisocial.utils.selectabletxt.SelectableTextHelper
import com.eggplant.qiezisocial.widget.keyboard.EmojiEmoticonsKeyBoard
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.luck.picture.lib.PictureSelector
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import com.umeng.analytics.MobclickAgent
import com.xiao.nicevideoplayer.NiceVideoPlayerManager
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.layout_operate_windows.view.*
import kotlinx.android.synthetic.main.pop_chat_add.view.*
import kotlinx.android.synthetic.main.pop_chat_option.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import sj.keyboard.utils.EmoticonsKeyboardUtils

/**
 * Created by Administrator on 2020/4/9.
 */

class ChatActivity : BaseWebSocketActivity<ChatPresenter>(), ChatContract.View, ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {


    override fun initPresenter(): ChatPresenter {
        return ChatPresenter()
    }

    override val webSocketClass: Class<out AbsBaseWebSocketService>
        get() = WebSocketService::class.java

    lateinit var layoutManager: SmoothScrollLayoutManager
    lateinit var loadBar: View
    lateinit var adapter: ChatAdapter
    var from: String? = ""
    private var userInfo: SharedPreferences? = null

    lateinit var addPopwindow: BasePopupWindow
    lateinit var optionWindow: BasePopupWindow
    lateinit var itemOptionWindow: BasePopupWindow
    lateinit var delDlg: ChatDelDialog
    lateinit var optionView: View
    lateinit var itemOptionView: View
    private var itemOptionSelectPos = -1
    private val REQUEST_PHOTO_ALBUM = 112
    private val REQUEST_ADD_VIDEO = 113
    private val REQUEST_TAKE_PHOTO = 114
    private var windowHeight: Int = 0
    private var windowWidth: Int = 0
    private var isfriend = true

    var mSelectableTextHelper: SelectableTextHelper? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_chat
    }

    override fun initView() {
        mPresenter.attachView(this)
        adapter = ChatAdapter(mContext, null)
        chat_ry.adapter = adapter
        layoutManager = SmoothScrollLayoutManager(mContext)
//        layoutManager.stackFromEnd = true

        chat_ry.layoutManager = layoutManager
        (chat_ry.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

//        (chat_ry.itemAnimator as SimpleItemAnimator).addDuration = 1000
//        (chat_ry.itemAnimator as SimpleItemAnimator).moveDuration = 1000
//        (chat_ry.itemAnimator as SimpleItemAnimator).removeDuration = 1000
//        (chat_ry.itemAnimator as SimpleItemAnimator).changeDuration = 1000
//        val itemAnim=FadeItemAnimator()
//        itemAnim.addDuration=1000
//        itemAnim.moveDuration=1000
//        itemAnim.removeDuration=1000
//        itemAnim.changeDuration=1000
//        chat_ry.itemAnimator=itemAnim
        loadBar = LayoutInflater.from(mContext).inflate(R.layout.layout_loadbar, null, false)
        loadBar.visibility = View.GONE
        adapter.addHeaderView(loadBar)
        initAddPopwindow()
        initDialog()
        from = intent.getStringExtra("from")
        chat_bar.setRightDrawable(R.mipmap.report)
//        if (TextUtils.equals(from, "home")) {
//            chat_bar.setReturnDrawable(ContextCompat.getDrawable(mContext, R.mipmap.detail_close))
//            chat_bar.showReturn(true)
//        }

//        if (from == "home") {
//            adapter.emojiList = application.emojiList
//        }


    }

    private fun initDialog() {
        delDlg = ChatDelDialog(mContext, intArrayOf(R.id.dlg_chat_del_cancel, R.id.dlg_chat_del_sure))
        delDlg.setOnBaseDialogItemClickListener { dialog, view ->
            when (view.id) {
                R.id.dlg_chat_del_cancel -> {
                    adapter.multSelectList.clear()
                }
                R.id.dlg_chat_del_sure -> {
                    val delP = adapter.multSelectList[0].toInt() - adapter.headerLayoutCount
                    val delData = adapter.data[delP]
                    adapter.data.remove(delData)
                    adapter.multSelectList.clear()
                    adapter.notifyDataSetChanged()
                    ChatDBManager.getInstance(mContext).deleteUser(delData.bean)
                }
            }
            dialog.dismiss()
        }
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

        optionWindow = BasePopupWindow(mContext)
        optionView = LayoutInflater.from(mContext).inflate(R.layout.pop_chat_option, null, false)
        optionWindow.contentView = optionView

        optionView.pop_chat_option_add.setOnClickListener {
            mPresenter.applyFriend(activity)
            optionWindow.dismiss()
        }
        optionView.pop_chat_option_reort.setOnClickListener {
            startActivity(Intent(mContext, ReportActivity::class.java).putExtra("id", mPresenter.uid))
            optionWindow.dismiss()
        }

        /**
         * 相册
         */
        addpopView.pop_chat_add_img.setOnClickListener {
            mPresenter.openGallery(activity, REQUEST_PHOTO_ALBUM)
            addPopwindow.dismiss()
        }
        /**
         * 拍照
         */
        addpopView.pop_chat_add_takephoto.setOnClickListener {
            mPresenter.takePhoto(activity, REQUEST_TAKE_PHOTO)
            addPopwindow.dismiss()
        }
        /**
         * 录像
         */
        addpopView.pop_chat_add_recordvideo.setOnClickListener {
            mPresenter.recordVideo(activity, REQUEST_ADD_VIDEO)
            addPopwindow.dismiss()
        }
        /**
         * 语音聊天
         */
        addpopView.pop_chat_add_call.setOnClickListener {
            mPresenter.audioChat(mContext)
            addPopwindow.dismiss()
        }
        /**
         * 视频聊天
         */
        addpopView.pop_chat_add_video.setOnClickListener {
            mPresenter.videoChat(mContext)
            addPopwindow.dismiss()
        }

        itemOptionWindow = BasePopupWindow(mContext)
        itemOptionWindow.showAnimMode = 1
        itemOptionView = LayoutInflater.from(mContext).inflate(R.layout.layout_operate_windows, null, false)
        itemOptionView.tv_copy.visibility = View.GONE
        itemOptionView.tv_line.visibility = View.GONE
        itemOptionWindow.contentView = itemOptionView
        itemOptionWindow.setOnDismissListener {
            itemOptionSelectPos = -1
        }
        itemOptionView.tv_note.setOnClickListener {
            adapter.multModel = true
            adapter.multSelectList.add("${itemOptionSelectPos + adapter.headerLayoutCount}")
            adapter.notifyDataSetChanged()
            setMultSelectModel(true)
            itemOptionSelectPos = -1
            itemOptionWindow.dismiss()
        }
        itemOptionView.tv_del.setOnClickListener {
            adapter.multSelectList.add("${itemOptionSelectPos + adapter.headerLayoutCount}")
            chat_keyboard.reset()
            delDlg.show()
            itemOptionSelectPos = -1
            itemOptionWindow.dismiss()
        }

    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        userInfo = mContext.getSharedPreferences("userEntry", MODE_PRIVATE)
        mPresenter.scene = intent.getStringExtra("scene")
        mPresenter.qsTxt = intent.getStringExtra("qs")
        mPresenter.mainInfoBean = intent.getSerializableExtra("bean") as MainInfoBean?
        mPresenter.userEntry = intent.getSerializableExtra("user") as UserEntry?
        mPresenter.initData(mContext)


    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        NotifycationUtils.getInstance(mContext).cancelNotify(1)
        adapter.data.clear()
        mPresenter.mainInfoBean = intent.getSerializableExtra("bean") as MainInfoBean?
        mPresenter.onNewIntent(mContext)
    }


    override fun initEvent() {
        chat_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }

            override fun onTxtClick() {
                super.onTxtClick()
                if (!isfriend) {
                    optionView.pop_chat_option_add.visibility = View.VISIBLE
                }
                var loca = IntArray(2)
                chat_bar.getLocationOnScreen(loca)
                optionWindow.showAtLocation(chat_bar, Gravity.NO_GRAVITY, ScreenUtil.getDisplayWidthPixels(mContext) - ScreenUtil.dip2px(mContext, 110), loca[1] + ScreenUtil.dip2px(mContext, 35))
//                mPresenter.applyFriend(activity)
            }
        })
        chat_msg_hint.setOnClickListener {
            if (mPresenter.msgNum > 0) {
                scrollToPosition(adapter.data.size - mPresenter.msgNum + 1)
                dismissMsgHintAnim()
                mPresenter.msgNum = 0
            }
        }
        chat_ry.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                chat_ry.postDelayed({ scrollToPosition(adapter.data.size) }, 100)
            }
        }
        chat_ry.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                chat_keyboard.reset()
                if (mSelectableTextHelper != null) {
                    mSelectableTextHelper!!.dismiss()
                }
            }
            false
        }
        chat_ry.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                addRecyclerScrollListener()
                chat_ry.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        //录制语音回调
        chat_keyboard.setRecorderListener(object : RecorderListener {

            override fun onRecorderStart() {
                showRecordView()
            }

            override fun onRecording(volume: Double) {
                val drawable = chat_voice.drawable
                if (volume > 10) {
                    drawable.level = volume.toInt() * 100
                }
            }

            override fun onStop(filePath: String?, duration: Double) {
                var dura = (duration / 1000).toInt()
                if (dura == 0)
                    dura = 1
                val createChatEntry = mPresenter.createChatEntry(filePath!!, dura, mPresenter.myid?.toLong()!!, mPresenter.uid, mPresenter.myface!!)
                val inserSuccess = ChatDBManager.getInstance(mContext).insertUser(createChatEntry)
                if (inserSuccess) {
                    mPresenter.mainInfoBean!!.msg = "[语音]"
                    mPresenter.mainInfoBean!!.created = System.currentTimeMillis()
                    mPresenter.mainInfoBean!!.newMsgTime = System.currentTimeMillis()
                    mPresenter.mainInfoBean!!.msgType = "gaudio"
                    mPresenter.mainInfoBean!!.`object` = ""
                    MainDBManager.getInstance(mContext).updateUser(mPresenter.mainInfoBean)
                    val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_MINE_AUDIO, createChatEntry)
                    adapter.needAnimPosition = adapter.data.size
                    adapter.addData(chatEntryChatMultiBean)
                    smoothScrollBottom()
                }
            }

            override fun onHideVoiceGp() {
                chat_record.visibility = View.GONE
            }

            override fun onShowVoice() {
                setVoiceVisibility(true)
            }

            override fun onShoCancle() {
                setVoiceVisibility(false)
            }

        })
        chat_keyboard.setOnFuncClickListener(object : EmojiEmoticonsKeyBoard.OnFuncClickListener {
            override fun onSendClick(str: String?) {
                mPresenter.myid = userInfo!!.getInt("uid", 0)
                if (TextUtils.equals(mPresenter.scene, "zr")) {
                    mPresenter.sendTxt(activity, "你很有魅力我对你一见钟情哦", "", false)
                    mPresenter.scene = null
                }
                mPresenter.sendTxt(activity, str!!, "", true)
                //设置友盟自定义事件统计
                val map = HashMap<String, Any>()
                map.put("action_type", "chat_send_txt")
                map.put("user_info", "${application.infoBean!!.uid} ${application.infoBean!!.nick}")
                MobclickAgent.onEventObject(activity, UmSetting.EVENT_CHAT_SEND_TXT, map)
            }

            override fun onAddClick(v: View?) {
                val loc = IntArray(2)
                v?.getLocationOnScreen(loc)
                val x = loc[0]
                val y = loc[1]
                addPopwindow.showAtLocation(v, Gravity.NO_GRAVITY, windowWidth, y - windowHeight)
            }

        })
        adapter.setChatApListener(object : ChatAdapter.OnChactApListerner {
            override fun emojiClick(entry: EmojiEntry) {
                //TODO
                mPresenter.sendTxt(activity, "[${entry.des}]", API.PIC_PREFIX + entry.path, true)
                setEmojiView(API.PIC_PREFIX + entry.path)
                adapter.emojiList = null
                adapter.notifyDataSetChanged()
            }

            override fun onQsFileDownload(content: String, mediaType: String, flag: String, bean: ChatEntry) {
                val contentName = System.currentTimeMillis().toString()
                val mFileRelativeUrl = content.replace(API.PIC_PREFIX.toRegex(), "")
                mPresenter.downloadQsFile(mContext, content, contentName, mediaType, flag, mFileRelativeUrl, bean)
            }

            override fun onFileDownload(content: String, bean: ChatEntry) {
                val contentName = System.currentTimeMillis().toString()
                val mFileRelativeUrl = content.replace(API.PIC_PREFIX.toRegex(), "")

                mPresenter.downloadFile(mContext, content, contentName, mFileRelativeUrl, bean)
            }

            override fun onReSend(bean: ChatEntry): Boolean {
                val id = application.msgUUID
                application.msgUUID = "0"
                mPresenter.getMsgId(activity)
                val isSuccess = sendSocketMessage(mPresenter.getSendMsg(bean.type!!, id, bean.from.toString() + "", bean.to.toString() + "", bean.content!!, bean.extra!!))
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
                            mPresenter.uploadVideoMedia(type, activity, "", media, pTv, bean, position)

                        }
                    }
                    "gpic", "gaudio" -> {
                        val picPath = bean.content
                        val media = ArrayList<String>()
                        if (picPath != null) {
                            media.add(picPath)
                            mPresenter.uploadPicOrAudioMedia(type, activity, "", media, pTv, bean, position)
                        }
                    }
                }
            }

            override fun compressAndShowBar(v: View, inputP: String, outputP: String, bean: ChatEntry, position: Int) {
                mPresenter.compressVideo(v, inputP, outputP, bean, position)
            }

        })
        adapter.setOnItemChildClickListener { _, view, position ->
            chat_keyboard.reset()
            if (mSelectableTextHelper != null) {
                mSelectableTextHelper!!.dismiss()
            }
            val entry = adapter.data[position] as ChatMultiEntry<ChatEntry>
            if (view.id == R.id.adapter_chat_head) {
                if (adapter.multModel) {
                    if (adapter.multSelectList.contains("${position + adapter.headerLayoutCount}")) {
                        adapter.multSelectList.remove("${position + adapter.headerLayoutCount}")
                    } else {
                        adapter.multSelectList.add("${position + adapter.headerLayoutCount}")
                    }
                    notifyAdapterItemChanged(position + adapter.headerLayoutCount)
                    return@setOnItemChildClickListener
                }
                val uid = entry.bean.from
                mPresenter.toOtherActivity(activity, uid)
//                if (entry.itemType === ChatMultiEntry.CHAT_MINE || entry.itemType === ChatMultiEntry.CHAT_MINE_VIDEO || entry.itemType === ChatMultiEntry.CHAT_MINE_AUDIO) {
////                    startActivity(Intent(mContext, OthersSpaceActivity::class.java).putExtra("uid", entry.bean.userId.toString()))
//                    //TODO
//                } else {
////                    startActivity(Intent(mContext, OthersSpaceActivity::class.java).putExtra("uid", entry.bean.chatId.toString()))
//                    //TODO
//                }
            } else if (view.id == R.id.center) {
                if (entry.itemType == ChatMultiEntry.CHAT_MINE_SHARE_SCENE || entry.itemType == ChatMultiEntry.CHAT_OTHER_SHARE_SCENE) {
                    mPresenter.setScenes(entry)
                }
            }
        }

        adapter.setOnItemClickListener { _, view, position ->
            chat_keyboard.reset()
            if (mSelectableTextHelper != null) {
                mSelectableTextHelper!!.dismiss()
            }
            if (adapter.multModel) {
                if (adapter.multSelectList.contains("${position + adapter.headerLayoutCount}")) {
                    adapter.multSelectList.remove("${position + adapter.headerLayoutCount}")
                } else {
                    adapter.multSelectList.add("${position + adapter.headerLayoutCount}")
                }
                notifyAdapterItemChanged(position + adapter.headerLayoutCount)
            }
        }


        adapter.setOnItemChildLongClickListener { _, view, position ->
            if (view.id == R.id.center || view.id == R.id.ap_chat_album || view.id == R.id.adapter_chat_mediaview || view.id == R.id.adapter_chat_cImg) {
                val loca = IntArray(2)
                view.getLocationOnScreen(loca)
                val showWidth = ScreenUtil.dip2px(mContext, 130)
                val showHeight = ScreenUtil.dip2px(mContext, 45)
                val viewWidth = view.width
//                var offsetX: Int
//                if (showWidth > viewWidth) {
//                    offsetX = -((showWidth - viewWidth) / 2)
//                } else {
//                    offsetX = (viewWidth - showWidth) / 2
//                }
//                if (loca[0] + offsetX > 0) {
//                    loca[0] + offsetX
//                } else {
//                    0
//                }
                itemOptionSelectPos = position
                itemOptionWindow.showAtLocation(view, Gravity.NO_GRAVITY, loca[0], loca[1] - showHeight)

            } else if (view.id == R.id.adapter_chat_content) {
                if (mSelectableTextHelper != null) {
                    mSelectableTextHelper!!.dismiss()
                }
                mSelectableTextHelper = SelectableTextHelper.Builder(view as TextView)
                        .setSelectedColor(mContext.resources.getColor(R.color.translate_black2))
                        .setCursorHandleSizeInDp(20f)
                        .setCursorHandleColor(mContext.resources.getColor(R.color.black))
                        .build()
                mSelectableTextHelper!!.setOnNotesClickListener(object : OnSelectTextClickListener {
                    override fun onMultSelectClick(content: CharSequence?) {
                        adapter.multModel = true
                        adapter.multSelectList.add("${position + adapter.headerLayoutCount}")
                        adapter.notifyDataSetChanged()
                        setMultSelectModel(true)
                    }

                    override fun onDelClick() {
                        adapter.multSelectList.add("${position + adapter.headerLayoutCount}")
                        chat_keyboard.reset()
                        delDlg.show()
                    }
                })
                view.postDelayed({
                    mSelectableTextHelper!!.showSelectView()
                }, 200)
            }
            true
        }
        chat_mult_cancle.setOnClickListener {
            setMultSelectModel(false)
            adapter.multModel = false
            adapter.multSelectList.clear()
            adapter.notifyDataSetChanged()
        }
        chat_del.setOnClickListener {
            val delData = arrayListOf<ChatMultiEntry<ChatEntry>>()
            adapter.multSelectList.forEach {
                val delPos = it.toInt() - adapter.headerLayoutCount
                delData.add(adapter.data[delPos])
            }
            delData.forEach {
                adapter.data.remove(it)
                ChatDBManager.getInstance(mContext).deleteUser(it.bean)
            }
            adapter.multModel = false
            adapter.multSelectList.clear()
            adapter.notifyDataSetChanged()
            setMultSelectModel(false)
        }


    }

    var mulSelectModel = false
    private fun setMultSelectModel(b: Boolean) {
        mulSelectModel = b
        if (mulSelectModel) {
            chat_mult_cancle.visibility = View.VISIBLE
            chat_del.visibility = View.VISIBLE
            chat_keyboard.reset()
        } else {
            chat_mult_cancle.visibility = View.GONE
            chat_del.visibility = View.GONE
        }
    }


    override fun expressionClick(str: String?) {
        chat_keyboard.input(chat_keyboard.edit, str)
    }

    override fun expressionaddRecent(str: String?) {
        chat_keyboard.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        chat_keyboard.delete(chat_keyboard.edit)
    }

    /**
     * 收到新消息监听
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewMsg(event: NewMsgEvent) {
        var newData = mPresenter.getNewData(mContext, adapter.data)
        adapter.needAnimPosition = adapter.data.size
        adapter.addData(newData)
        smoothScrollBottom()
//        scrollToBottom()

    }

    override fun smoothScrollBottom() {
        val lastPosition = layoutManager.findLastVisibleItemPosition()
        if (lastPosition == adapter.data.size - 1) {
            chat_ry.smoothScrollToPosition(adapter.data.size)
        } else {
            scrollToBottom()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_PHOTO_ALBUM -> {
                    // 图片、视频、音频选择结果回调
                    var imgs = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (imgs == null || imgs.size <= 0) {
                        return
                    }
                    var delay = 0L
//                    val file = imgs[0]
                    imgs.forEach {
                        var path = it.androidQToPath
                        if (path == null || path.isEmpty()) {
                            path = it.path
                        }
                        when {
                            it.mimeType.contains("video/") -> {
                                Log.i("selectFile", " file is video")
                                chat_ry.postDelayed({
                                    mPresenter.addVideo(mContext, path)
                                }, delay)
                                delay += 500
                            }
                            it.mimeType.contains("image/") -> {
                                Log.i("selectFile", " file is image")
                                chat_ry.postDelayed({
                                    mPresenter.addImg(mContext, path)
                                }, delay)
                                delay += 500

                            }
                            else -> Log.i("selectFile", " file is other  ${it.mimeType}")
                        }
                    }
//                    file.mimeType
//                    when {
//                        file.pictureType.contains("video/") -> {
//                            Log.i("selectFile", " file is video")
//                            mPresenter.addVideo(mContext, file.path)
//                        }
//                        file.pictureType.contains("image/") -> {
//                            Log.i("selectFile", " file is audio")
//                            mPresenter.addImg(mContext, file.path)
//                        }
//                        else -> Log.i("selectFile", " file is other  ${file.pictureType}")
//                    }
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
                    mPresenter.addImg(mContext, path)
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
                    mPresenter.addVideo(mContext, path)
                }
            }

        }
    }


    private fun setVoiceVisibility(b: Boolean) {
        if (b) {
            chat_voice.visibility = View.VISIBLE
            chat_audio_top_cancle.visibility = View.GONE
        } else {
            chat_voice.visibility = View.GONE
            chat_audio_top_cancle.visibility = View.VISIBLE
        }
    }

    private fun showRecordView() {
        chat_record.visibility = View.VISIBLE
        chat_audio_top.visibility = View.VISIBLE
        chat_voice.visibility = View.VISIBLE
        chat_audio_top_cancle.visibility = View.GONE
    }

    internal var loading = false
    /**
     * 添加 recyclerview滑动监听
     */
    private fun addRecyclerScrollListener() {
        chat_ry.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView!!.layoutManager as LinearLayoutManager
                val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstCompletelyVisibleItemPosition == 0) {
                    if (mPresenter.uid != 0L && adapter.data.size >= 45 && !loading) {
                        loading = true

                        var chatEntries: List<ChatEntry>? = null
                        chatEntries = when {
                            mPresenter.mainInfoBean!!.qsid != 0L -> ChatDBManager.getInstance(mContext).queryQsUserList(mPresenter.uid, mPresenter.mainInfoBean!!.qsid, adapter.data.size)
                            mPresenter.mainInfoBean!!.gsid != 0L -> ChatDBManager.getInstance(mContext).queryGsUserList(mPresenter.mainInfoBean!!.gsid, adapter.data.size)
                            else -> ChatDBManager.getInstance(mContext).queryUserList(mPresenter.uid, adapter.data.size)
                        }

                        if (chatEntries.isNotEmpty()) {
                            loadBar.visibility = View.VISIBLE
                            layoutManager.scrollToPositionWithOffset(0, 0)
                            chat_ry.postDelayed({
                                adapter.data.addAll(0, mPresenter.getData(mContext, chatEntries, false))
                                adapter.notifyDataSetChanged()
                                layoutManager.scrollToPositionWithOffset(chatEntries.size + 1, 0)
//                                layoutManager.stackFromEnd = true
                                loadBar.visibility = View.GONE
                                loading = false
                            }, 1000)
                        }
                    }
                }

            }
        })
    }


    fun scrollToPosition(position: Int) {

        Log.i("chatScroll", "scrollToPosition")
        var position = position
        if (position < 0)
            position = 0
        layoutManager.scrollToPosition(position)
//        var scroller = TopSmoothScroller(activity) as LinearSmoothScroller
//        scroller.targetPosition = position
//        layoutManager.startSmoothScroll(scroller)
    }

    /**
     * 展示 最新消息提示
     */
    private fun showMsgHintAnim() {
        val msgHeight = resources!!.getDimension(R.dimen.qb_px_30)
        val animator = ObjectAnimator.ofFloat(chat_msg_hint, "translationY", -msgHeight, 0f)
        animator.duration = 300

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                chat_msg_hint.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animator: Animator) {
                chat_msg_hint.postDelayed(Runnable { dismissMsgHintAnim() }, 3000)
            }

            override fun onAnimationCancel(animator: Animator) {

            }

            override fun onAnimationRepeat(animator: Animator) {

            }
        })
        animator.start()
    }

    /**
     * 隐藏 最新消息提示
     */
    private fun dismissMsgHintAnim() {
        val msgHeight = resources!!.getDimension(R.dimen.qb_px_30)
        val animator = ObjectAnimator.ofFloat(chat_msg_hint, "translationY", 0f, -msgHeight)
        animator.duration = 300

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {

            }

            override fun onAnimationEnd(animator: Animator) {
                chat_msg_hint.visibility = View.GONE
            }

            override fun onAnimationCancel(animator: Animator) {
                chat_msg_hint.visibility = View.GONE
            }

            override fun onAnimationRepeat(animator: Animator) {

            }
        })
        animator.start()
    }

    /**
     * call by   initdata or onNewIntent
     */
    override fun refreshItem(onNewIntent: Boolean, data: List<ChatMultiEntry<ChatEntry>>) {
        chat_ry.alpha = 0f
        if (onNewIntent) {
            adapter.setNewData(data)
            scrollToBottom()
        } else {
            adapter.setNewData(data)
            scrollToBottom()
        }
    }

    override fun getAdapterDataSize(): Int {
        return adapter.data.size
    }


    override fun scrollToBottom() {

        chat_ry.postDelayed({
            chat_ry.scrollToPosition(adapter.data.size)
            layoutManager.scrollToPositionWithOffset(adapter.data.size, 0)//先要滚动到这个位置
            val target = layoutManager.findViewByPosition(adapter.data.size)//然后才能拿到这个View
            if (target != null) {
                layoutManager.scrollToPositionWithOffset(adapter.data.size,
                        chat_ry.measuredHeight - target.measuredHeight)//滚动偏移到底部
            }
            scrollToPosition(adapter.data.size)
            chat_ry.postDelayed({
                var lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()

                if (adapter.data.size != 0
                        && lastVisibleItem - adapter.headerLayoutCount - adapter.footerLayoutCount < adapter.data.size - 1) {
                    scrollToBottom()
                } else {
                    val alpha = chat_ry.alpha
                    if (alpha == 0f) {
                        val alphaAnim = android.animation.ObjectAnimator.ofFloat(chat_ry, "alpha", 0.0f, 1.0f)
                        alphaAnim.duration = 500
                        alphaAnim.start()
                    }
                }
            }, 300)

        }, 50)

    }

    override fun setMyHead(myface: String?) {
        myface?.let {
            adapter.mineheadPic = it
        }
    }

    override fun setOtherFace(face: String?) {
        face?.let {
            adapter.otherheadPic = it
        }
    }

    override fun addItem(chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>) {
        adapter.needAnimPosition = adapter.data.size
        adapter.addData(chatEntryChatMultiBean)
    }

    override fun addItem(position: Int, chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>) {
        if (position < adapter.data.size) {
            adapter.addData(position, chatEntryChatMultiBean)
        } else {
            adapter.needAnimPosition = adapter.data.size
            adapter.addData(chatEntryChatMultiBean)
        }
    }

    override fun setBarRightTxt(s: String) {
//        chat_bar.setRightTxt(s)
//        chat_bar.setRightDrawable(R.mipmap.icon_bar_addfriend)
        isfriend = false
    }

    override fun setBarTitle(remark: String?) {

        chat_bar.setTitle(remark)
    }

    override fun showNewMsg(msgNum: Int) {
        chat_msg_hint.text = msgNum.toString() + "条新消息"
        chat_msg_hint.postDelayed({ showMsgHintAnim() }, 500)
    }

    override fun setEmojiView(path: String) {
        chat_fallsview.addImagePath(path)
        chat_fallsview.startFallsAnim()
    }

    override fun Toast(s: String) {
        TipsUtil.showToast(mContext, s)
    }

    override fun finishActivity() {
        activity.finish()
    }

    override fun notifyAdapterItemChanged(position: Int) {
        if (chat_ry.isComputingLayout || activity.isDestroyed) {
            return
        }
        adapter.notifyItemChanged(position)
    }

    override fun onCommonResponse(response: CommonResponse) {


    }


    override fun onErrorResponse(response: WebSocketSendDataErrorEvent) {

    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            val isConsum = chat_keyboard.dispatchKeyEventInFullScreen(event)
            return if (isConsum) isConsum else super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onResume() {
        super.onResume()
        NotifycationUtils.getInstance(mContext).cancelNotify(1)
    }

    override fun onPause() {
        super.onPause()
        chat_keyboard.reset()
        chat_fallsview.pauseAnim()
    }

    override fun onStop() {
        super.onStop()
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer()
        chat_fallsview.stopAnim()
    }

    override fun onDestroy() {

        if (adapter.data.size == 1 && from == "home") {
            mPresenter.deleteChat(mContext, adapter.data[0].bean)
        }
        application.chatUid = -1
        super.onDestroy()
    }
}
