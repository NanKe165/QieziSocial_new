package com.eggplant.qiezisocial.ui.main.fragment

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.GroupChatContract
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.GchatEntry
import com.eggplant.qiezisocial.entry.GchatParcelEntry
import com.eggplant.qiezisocial.event.RemoveEvent
import com.eggplant.qiezisocial.event.SocketMsgEvent
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.model.UmSetting
import com.eggplant.qiezisocial.presenter.GroupChatPresenter
import com.eggplant.qiezisocial.ui.extend.dialog.ShareDialog
import com.eggplant.qiezisocial.ui.main.MainActivity
import com.eggplant.qiezisocial.ui.main.TopSmoothScroller
import com.eggplant.qiezisocial.ui.main.adapter.GchatAdapter
import com.eggplant.qiezisocial.ui.main.dialog.GchatFuncDialog
import com.eggplant.qiezisocial.ui.main.dialog.NormalDialog
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.utils.mp3.RecorderListener
import com.eggplant.qiezisocial.widget.AuVideoTxtView
import com.eggplant.qiezisocial.widget.keyboard.FloatEmojiKeyBoard
import com.eggplant.qiezisocial.widget.popupwindow.BasePopupWindow
import com.luck.picture.lib.PictureSelector
import com.tbruyelle.rxpermissions2.RxPermissions
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.android.synthetic.main.fragment_groupchat2.*
import kotlinx.android.synthetic.main.pop_chat_add.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

/**
 * Created by Administrator on 2021/6/28.
 */

class GroupChatFragment2 : BaseMvpFragment<GroupChatPresenter>(), GroupChatContract.View {

    override fun sendSocketMessage(sendMsg: String): Boolean {
        return (activity as MainActivity).sendSocketMessage(sendMsg)
    }

    override fun addItem(chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>) {
        adapter.addNewData(chatEntryChatMultiBean)
    }

    override fun addItem(position: Int, chatEntryChatMultiBean: ChatMultiEntry<ChatEntry>) {

    }

    override fun scrollToBottom() {

    }

    override fun notifyAdapterItemChanged(position: Int) {

    }

    override fun setLittleChatSize(size: Int) {
        if (size > 0) {
            f_gchat_msg_hint.text = "$size"
            f_gchat_lightmsg.visibility = View.VISIBLE
        } else {
            f_gchat_lightmsg.visibility = View.GONE
        }
        showLittleMsg()
    }

    override fun showNewMsg() {

    }

    override fun onResume() {
        super.onResume()
        isPause = false

    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    private fun showLittleMsg() {
        if (!isPause && (activity as MainActivity).getCurrentFragment() == this && f_gchat_keyboard.edit.text.toString().isEmpty()) {
            resetKeyboard()
            mPresenter.littleTxtClick(activity!!, this, REQUEST_LITTLE_TXT)
        }
    }

    private val REQUEST_PHOTO_ALBUM = 112
    private val REQUEST_ADD_VIDEO = 113
    private val REQUEST_TAKE_PHOTO = 114
    private val REQUEST_SHOW_IMG = 115
    private val REQUEST_LITTLE_TXT = 116
    private val REQUEST_SHOW_TXT = 117

    val clickList = ArrayList<Long>()
    lateinit var adapter: GchatAdapter
    lateinit var addPopwindow: BasePopupWindow
    private lateinit var shareDialog: ShareDialog
    lateinit var dialog: NormalDialog
    private var windowHeight: Int = 0
    private var windowWidth: Int = 0
    var titleData: List<GchatEntry>? = null
    var isPause = true
    var lastTitle: String = ""
    var currentGid: Int = 0
    lateinit var funcDialog: GchatFuncDialog<ChatEntry>

    companion object {

        private var fragment: GroupChatFragment2? = null
            get() {
                if (field == null)
                    field = GroupChatFragment2()
                return field
            }

        fun instanceFragment(bundle: Bundle?): GroupChatFragment2 {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }
    }

    override fun initPresenter(): GroupChatPresenter {
        return GroupChatPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_groupchat2
    }

    override fun initView() {
        mPresenter.attachView(this)
        f_gchat_ry.layoutManager = LinearLayoutManager(mContext)
        adapter = GchatAdapter(null)
        val headview = View.inflate(mContext, R.layout.layout_gchat_head, null)
        adapter.setHeaderView(headview)
        f_gchat_ry.adapter = adapter
        initAddPopwindow()
        initDialog()
        arguments?.let {
            currentGid = it.getInt("gid")
            Log.i("chatft2","gid:$currentGid")
        }
    }

    override fun initEvent() {
        adapter.listener = object : GchatAdapter.ScrollListener {
            override fun scroll() {
//                val itemCount = adapter.data.size
                var firstPosition = (f_gchat_ry.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (firstPosition == 0) {
                    scrollToPosition(0)
                }
            }
        }

        f_gchat_share.setOnClickListener {
            //            refreshDataList(currentGid, 1)
            shareDialog.show()
        }
        f_gchat_ry.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_UP)
                resetKeyboard()
            false
        }
        f_gchat_lightmsg.setOnClickListener {
            resetKeyboard()
            mPresenter.littleTxtClick(activity!!, this, REQUEST_LITTLE_TXT)
        }
        adapter.headClickListener = { uid ->
            mPresenter.toOtherActivity(activity!!, uid)
        }
        adapter.itemLongClickListener = { entry ->
            resetKeyboard()
            val type = entry.bean.type
            when (entry.itemType) {
                ChatMultiEntry.CHAT_OTHER, ChatMultiEntry.CHAT_MINE -> {
                    if (type == "gtxt") {

                    } else if (type == "gpic") {
                        val content = entry.bean.content
                        funcDialog.downloadFile = content
                        funcDialog.fileType = "image/jpeg"
                        funcDialog.bean = entry.bean
                        funcDialog.show()
                    }
                }
                ChatMultiEntry.CHAT_OTHER_VIDEO, ChatMultiEntry.CHAT_MINE_VIDEO -> {
                    val content = entry.bean.content
                    funcDialog.downloadFile = content
                    funcDialog.fileType = "video/mp4"
                    funcDialog.bean = entry.bean
                    funcDialog.show()
                }
            }

        }
        adapter.itemClickListener = { childPosition, v, entry ->
            resetKeyboard()
            val type = entry.bean.type
            when (entry.itemType) {
                ChatMultiEntry.CHAT_OTHER -> {
                    if (type == "gtxt") {
                        val layout = (v as AuVideoTxtView).getcTxt().layout
                        val lineCount = v.getcTxt().lineCount
                        if (layout != null) {
//                            val ellipsisCount = layout.getEllipsisCount(lineCount - 1)
//                            if(ellipsisCount>0){
//                                PrevUtils.onTxtItemClick(activity,childPosition,v,entry.bean.content)
//                            }
                            mPresenter.onTxtClick(this, entry, v, childPosition, REQUEST_SHOW_TXT)
                        }
                    } else if (type == "gpic") {
//                        mPresenter.onMediaClick(this, entry, v, REQUEST_SHOW_IMG)
                        val content = entry.bean.content
                        PrevUtils.onImageItemClick(mContext, v, content, content)
                    }
                }
                ChatMultiEntry.CHAT_OTHER_VIDEO -> {
//                    mPresenter.onMediaClick(this, entry, v, REQUEST_SHOW_IMG)
                    val content = entry.bean.content
                    val extra = entry.bean.extra
                    val name = "${entry.bean.from}${entry.bean.created}"
                    var img = ""
                    val split = extra?.split("&&".toRegex())?.dropLastWhile({ it.isEmpty() })?.toTypedArray()
                    if (split!!.isNotEmpty()) {
                        img = split[0]
                    }
                    PrevUtils.onVideoItemClick(mContext, v, content, img, true, name)
                }
                ChatMultiEntry.CHAT_MINE -> {
                    if (type == "gtxt") {
                        val layout = (v as AuVideoTxtView).getcTxt().layout
                        val lineCount = v.getcTxt().lineCount

                        if (layout != null) {
//                            val ellipsisCount = layout.getEllipsisCount(lineCount - 1)
//                            if(ellipsisCount>0){
                            PrevUtils.onTxtItemClick(activity, childPosition, v, entry.bean.content)
//                            }

                        }
                    } else if (type == "gpic") {
                        val content = entry.bean.content
                        PrevUtils.onImageItemClick(context, v, content, content)
                    }
                }
                ChatMultiEntry.CHAT_MINE_VIDEO -> {
                    val content = entry.bean.content
                    val extra = entry.bean.extra
                    val name = "${entry.bean.from}${entry.bean.created}"
                    var img = ""
                    val split = extra?.split("&&".toRegex())?.dropLastWhile({ it.isEmpty() })?.toTypedArray()
                    if (split!!.isNotEmpty()) {
                        img = split[0]
                    }
                    PrevUtils.onVideoItemClick(mContext, v, content, img, true, name)
                }
            }
        }
        f_gchat_keyboard.setOnFuncClickListener(object : FloatEmojiKeyBoard.OnFuncClickListener {
            override fun onAddClick(v: View?) {
                val loc = IntArray(2)
                v?.getLocationOnScreen(loc)
                val x = loc[0]
                val y = loc[1]
                addPopwindow.showAtLocation(v, Gravity.NO_GRAVITY, windowWidth, y - windowHeight)
            }

            override fun onSendClick(str: String?) {
                if (currentGid != 0) {
                    resetKeyboard()
                    mPresenter.sendTxt(activity!!, str!!, currentGid)
                    //设置友盟自定义事件统计
                    val map = HashMap<String, Any>()
                    map.put("action_type", "group_chat_send_txt")
                    map.put("user_info", "${application.infoBean!!.uid} ${application.infoBean!!.nick}")
                    MobclickAgent.onEventObject(activity, UmSetting.EVENT_GROUP_CHAT_SEND_TXT, map)
                }
            }

        })
        f_gchat_keyboard.setRecorderListener(object : RecorderListener {
            override fun onRecorderStart() {
                showRecordView()
            }

            override fun onRecording(volume: Double) {
                val drawable = f_gchat_voice.drawable
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
                val picPath = createChatEntry.content
                val media = ArrayList<String>()
                if (picPath != null) {
                    media.add(picPath)
                    mPresenter.uploadPicOrAudioMedia("gaudio", activity!!, "", media, null, createChatEntry, 0, currentGid)
                }
            }

            override fun onHideVoiceGp() {
                f_gchat_record.visibility = View.GONE
            }

            override fun onShowVoice() {
                setVoiceVisibility(true)
            }

            override fun onShoCancle() {
                setVoiceVisibility(false)
            }
        })
    }

    override fun initData() {

    }

    private fun initDialog() {
        funcDialog = GchatFuncDialog(mContext!!, intArrayOf(R.id.dlg_gchat_block, R.id.dlg_gchat_report, R.id.dlg_gchat_save_img))
        shareDialog = ShareDialog(mContext, intArrayOf(R.id.dlg_share_qq, R.id.dlg_share_pyq, R.id.dlg_share_wechat, R.id.dlg_share_sina))
        dialog = NormalDialog(mContext!!, intArrayOf(R.id.dlg_normal_cancel, R.id.dlg_normal_delete))
        dialog.setOnBaseDialogItemClickListener { _, view ->
            if (view.id == R.id.dlg_normal_cancel) {
                dialog.dismiss()
            } else if (view.id == R.id.dlg_normal_delete) {
                if (dialog.mode == 4) {
                    if (funcDialog.bean != null) {
                        mPresenter.addBlocklist(activity!!, funcDialog.bean!!.from)
                    }
                    dialog.mode = 0
                }
                dialog.dismiss()
            }
        }
        funcDialog.setOnBaseDialogItemClickListener { _, view ->
            when (view.id) {
                R.id.dlg_gchat_save_img -> {
                    RxPermissions(fragment!!).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .subscribe { b ->
                                if (!b) {
                                    TipsUtil.showToast(mContext, "权限申请失败")
                                } else {
                                    val downloadPath = funcDialog.downloadFile
                                    val fileType = funcDialog.fileType
                                    if (downloadPath.isNotEmpty() && fileType.isNotEmpty()) {
                                        mPresenter.saveFile(mContext!!, downloadPath, fileType)
                                        funcDialog.downloadFile = ""
                                        funcDialog.fileType = ""

                                    }
                                }
                            }
                }
                R.id.dlg_gchat_report -> {

                }
                R.id.dlg_gchat_block -> {
                    dialog.mode = 4
                    dialog.show()
                }
            }
            funcDialog.dismiss()
        }
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
            mPresenter.openGallery(this@GroupChatFragment2, REQUEST_PHOTO_ALBUM)
            addPopwindow.dismiss()
        }
        /**
         * 拍照
         */
        addpopView.pop_chat_add_takephoto.setOnClickListener {
            mPresenter.takePhoto(this@GroupChatFragment2, REQUEST_TAKE_PHOTO)
            addPopwindow.dismiss()
        }
        /**
         * 录像
         */
        addpopView.pop_chat_add_recordvideo.setOnClickListener {
            mPresenter.recordVideo(this@GroupChatFragment2, REQUEST_ADD_VIDEO)
            addPopwindow.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
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
                    imgs.forEach {
                        var path = it.androidQToPath
                        if (path == null || path.isEmpty()) {
                            path = it.path
                        }
                        when {
                            it.mimeType.contains("video/") -> {
                                Log.i("selectFile", " file is video")
                                f_gchat_ry.postDelayed({
                                    val bean = mPresenter.addVideo(mContext!!, path, currentGid)
                                    val split = bean.content!!.split("&&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                                    if (split.size == 2) {
                                        mPresenter.compressAndUploadVideo(activity!!, currentGid, split[0], split[1], bean)
                                    }
                                }, delay)
                                delay += 1200

                            }
                            it.mimeType.contains("image/") -> {
                                Log.i("selectFile", " file is audio")
                                f_gchat_ry.postDelayed({
                                    val bean = mPresenter.addImg(mContext!!, path, currentGid)
                                    val picPath = bean.content
                                    val media = ArrayList<String>()
                                    if (picPath != null) {
                                        media.add(picPath)
                                        mPresenter.uploadPicOrAudioMedia("gpic", activity!!, "", media, null, bean, 0, currentGid)
                                    }
                                }, delay)
                                delay += 1200
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
                    val bean = mPresenter.addImg(mContext!!, path, currentGid)
                    val picPath = bean.content
                    val media = ArrayList<String>()
                    if (picPath != null) {
                        media.add(picPath)
                        mPresenter.uploadPicOrAudioMedia("gpic", activity!!, "", media, null, bean, 0, currentGid)
                    }
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
                    val bean = mPresenter.addVideo(mContext!!, path, currentGid)
                    val split = bean.content!!.split("&&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    if (split.size == 2) {
                        mPresenter.compressAndUploadVideo(activity!!, currentGid, split[0], split[1], bean)
                    }
//
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
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshTitle(data: List<GchatEntry>) {
        if (data.isNotEmpty()) {
            titleData = data
            val data = data[0]
            f_gchat_topiccount.text = data.topiccount.toString()
            if (data.title != lastTitle) {
                f_gchat_title.text = data.title
                f_gchat_title.setFontFormat(data.font)
                adapter.data.clear()
                adapter.notifyDataSetChanged()
                if (addPopwindow.isShowing) {
//                    TipsUtil.showToast(mContext, "当前话题已更新")
                    addPopwindow.dismiss()
                }
                currentGid = data.id
                lastTitle = data.title
                if (!first) {
                    refreshDataList(currentGid, 6)
                }

            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun newMsg(entry: GchatParcelEntry) {
        mPresenter.addNewMsg(entry)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun remove(entry: RemoveEvent) {
        val chatEntry = entry.entry
        mPresenter.removeLittleTxt(chatEntry)
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

    fun expressionClick(str: String?) {
        f_gchat_keyboard.input(f_gchat_keyboard.edit, str)
    }

    fun expressionaddRecent(str: String?) {
        f_gchat_keyboard.expressionaddRecent(str)
    }

    fun expressionDeleteClick(v: View?) {
        f_gchat_keyboard.delete(f_gchat_keyboard.edit)
    }

    fun resetKeyboard() {
        f_gchat_keyboard.reset()
    }

    fun scrollToPosition(position: Int) {
        var position = position
        if (position < 0)
            position = 0
        var scroller = TopSmoothScroller(activity!!)
        scroller.targetPosition = position
        f_gchat_ry.layoutManager.startSmoothScroll(scroller)

//        f_gchat_ry.smoothScrollToPosition(position,5500,null)

    }

    private fun setVoiceVisibility(b: Boolean) {
        if (b) {
            f_gchat_voice.visibility = View.VISIBLE
            f_gchat_audio_top_cancle.visibility = View.GONE
        } else {
            f_gchat_voice.visibility = View.GONE
            f_gchat_audio_top_cancle.visibility = View.VISIBLE
        }
    }


    private fun showRecordView() {
        f_gchat_record.visibility = View.VISIBLE
        f_gchat_audio_top.visibility = View.VISIBLE
        f_gchat_voice.visibility = View.VISIBLE
        f_gchat_audio_top_cancle.visibility = View.GONE
    }

    var first = true
    fun requestData() {
        if (first) {
            refreshDataList(currentGid, 6)
        }
        first = false
    }
}
