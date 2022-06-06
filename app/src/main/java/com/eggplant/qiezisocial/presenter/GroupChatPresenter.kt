package com.eggplant.qiezisocial.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.GroupChatContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.GchatParcelEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.ChatModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.LightChatActivity
import com.eggplant.qiezisocial.ui.main.MultMediaActivity
import com.eggplant.qiezisocial.ui.main.OtherSpaceActivity
import com.eggplant.qiezisocial.ui.main.TxtPreviewActivity
import com.eggplant.qiezisocial.utils.*
import com.eggplant.qiezisocial.widget.AuVideoTxtView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.tbruyelle.rxpermissions2.RxPermissions
import com.vincent.videocompressor.VideoCompress
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2021/3/31.
 */

class GroupChatPresenter : BasePresenter<GroupChatContract.View>(), GroupChatContract.Presenter {

    var model: ChatModel = ChatModel()
    // 扫描本地mp4文件并添加到本地视频库
    var mMediaScanner: MediaScannerConnection? = null

    override fun addBlocklist(activity: Activity, from: Long) {
        model.gChatAddBlockList(from, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                if (response!!.isSuccessful) {
                    if (response.body().stat == "ok") {
                        TipsUtil.showToast(activity, "已与Ta友尽啦...")
                    }
                }
            }
        })
    }

    override fun saveFile(mContext: Context, downloadPath: String, type: String) {
        val contentName = System.currentTimeMillis().toString()
        val mFileRelativeUrl = downloadPath.replace(API.PIC_PREFIX.toRegex(), "")
        FileUtils.downloadFile(mContext, downloadPath, contentName, mFileRelativeUrl, FileUtils.getStoreFilePath(mContext), object : FileUtils.DownloadFileCallback {
            override fun onSuccess(filePath: String) {
//                Log.i("gchatP", "onsuccess----$filePath  $type")
//                mMediaScanner = MediaScannerConnection(mContext, object : MediaScannerConnection.MediaScannerConnectionClient {
//                    override fun onMediaScannerConnected() {
//                        TipsUtil.showToast(mContext,"文件已保存至相册")
//                        mMediaScanner!!.scanFile(filePath, type)
//                        mMediaScanner!!.disconnect()
//                    }
//
//                    override fun onScanCompleted(path: String?, uri: Uri?) {
//
//                    }
//                })
//                mMediaScanner!!.connect()

                var extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
                var mimeTypes = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                MediaScannerConnection.scanFile(mContext, arrayOf(filePath), arrayOf(mimeTypes), null)
                TipsUtil.showToast(mContext, "文件已保存至相册")

            }

            override fun onError(message: String) {
            }

            override fun onProgress(progress: Float?) {
            }
        })
    }

    override fun onTxtClick(ft: Fragment, entry: ChatMultiEntry<ChatEntry>, view: AuVideoTxtView, position: Int, requesT_SHOW_TXT: Int) {
        val points = IntArray(2)
        view.getLocationInWindow(points)
        val height = view.height
        val width = view.width
        val intent = Intent(ft.context, TxtPreviewActivity::class.java)
        intent.putExtra("txt", entry.bean.content)
        intent.putExtra("w", width)
        intent.putExtra("h", height)
        intent.putExtra("viewX", points[0])
        intent.putExtra("viewY", points[1])
        intent.putExtra("p", position)
        intent.putExtra("uid", entry.bean.from)
        ft.startActivity(intent)
        (ft.activity)!!.overridePendingTransition(0, 0)
    }


    override fun onMediaClick(ft: Fragment, data: ChatMultiEntry<ChatEntry>, v: View, requestCode: Int) {
        val points = IntArray(2)
        v.getLocationInWindow(points)
        val newData = ArrayList<ChatMultiEntry<ChatEntry>>()
        newData.add(data)
        if (newData.size > 0) {
            ft.startActivityForResult(Intent(ft.context, MultMediaActivity::class.java)
                    .putExtra("data", newData)
                    .putExtra("imageViewWidth", v.width)
                    .putExtra("imageViewHeight", v.height)
                    .putExtra("imageViewX", points[0])
                    .putExtra("imageViewY", points[1])
//                        .putExtra("position", position)
                    .putExtra("position", 0)
                    , requestCode)

            (ft.activity)!!.overridePendingTransition(0, 0)
        }
    }

    override fun onMediaClick(ft: Fragment, p: Int, data: List<ChatMultiEntry<ChatEntry>>, v: View, requestCode: Int) {
        val points = IntArray(2)
        v.getLocationInWindow(points)
        val newData = ArrayList<ChatMultiEntry<ChatEntry>>()
        var size = 0
        var position = 0
        if (p < data.size) {
            var currentData = data[p]
//            for (i in 0 until data.size) {
//                var chatData = data[i]
//                val itemType = chatData.itemType
//                val beanType = chatData.bean.type
//                val id = chatData.bean.id
//                if ((itemType == ChatMultiEntry.CHAT_OTHER || itemType == ChatMultiEntry.CHAT_MINE) && beanType == "gpic") {
//                    newData.add(chatData)
//                    size++
//                    if (currentData.bean.id==id){
//                        position=size-1
//                    }
//
//                } else if (itemType == ChatMultiEntry.CHAT_OTHER_VIDEO || itemType == ChatMultiEntry.CHAT_MINE_VIDEO) {
//                    newData.add(chatData)
//                    size++
//                    if (currentData.bean.id==id){
//                        position=size-1
//                    }
//                }
//            }
            newData.add(currentData)
            if (newData.size > 0) {
                ft.startActivityForResult(Intent(ft.context, MultMediaActivity::class.java)
                        .putExtra("data", newData)
                        .putExtra("imageViewWidth", v.width)
                        .putExtra("imageViewHeight", v.height)
                        .putExtra("imageViewX", points[0])
                        .putExtra("imageViewY", points[1])
//                        .putExtra("position", position)
                        .putExtra("position", 0)
                        , requestCode)

                (ft.activity)!!.overridePendingTransition(0, 0)
            }
        }

    }

    override fun toOtherActivity(activity: Activity, uid: Long) {
        model.getUserInfo(activity, uid = uid.toInt(), callback = object : JsonCallback<BaseEntry<UserEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                if (response!!.isSuccessful) {
                    val userinfor = response.body().userinfor
                    if (userinfor != null) {
                        var intent = Intent(activity, OtherSpaceActivity::class.java).putExtra("bean", userinfor)
                        activity.startActivity(intent)
                        activity.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
                    }
                }
            }
        })
    }

    override fun addVideo(mContext: Context, path: String?, currentGid: Int): ChatEntry {
        var duration: Int
        val videoAlbumPath = FileUtils.getChatFilePath(mContext) + System.currentTimeMillis() + ".jpg"
        val videocompressPath = FileUtils.getChatFilePath(mContext) + System.currentTimeMillis() + ".mp4"
        val media = MediaMetadataRetriever()
        media.setDataSource(path)
        val frameAtTime = media.frameAtTime
        BitmapUtils.saveSmallBitmap2SDCard(frameAtTime, videoAlbumPath)
        duration = Integer.parseInt(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        val chatEntry = ChatEntry()
        chatEntry.content = path + "&&" + videocompressPath
        chatEntry.extra = videoAlbumPath + "&&" + duration
        chatEntry.created = System.currentTimeMillis().toString()
        chatEntry.from = QzApplication.get().infoBean!!.uid.toLong()
        chatEntry.to = 0
        chatEntry.face = QzApplication.get().infoBean!!.face
        chatEntry.type = "gvideo"
        chatEntry.msgId = System.currentTimeMillis().toString()
        chatEntry.chatId = 0
        chatEntry.msgStat = 3
        chatEntry.userId = QzApplication.get().infoBean!!.uid.toLong()
        chatEntry.gsid = currentGid.toLong()
        val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_MINE_VIDEO, chatEntry)
        mView?.addItem(chatEntryChatMultiBean)
        mView?.scrollToBottom()
        return chatEntry
    }

    override fun addImg(mContext: Context, filePath: String?, currentGid: Int): ChatEntry {
        var bitHeight = 1080
        var bitWidth = 720
        val compresPath = FileUtils.getChatFilePath(mContext) + System.currentTimeMillis() + ".jpg"
        val options = BitmapFactory.Options()
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true
        val bitmap = BitmapFactory.decodeFile(filePath, options)
        if (options.outHeight > 1920 && options.outWidth > 1080) {
            // 缩放图片的尺寸
            val scaleWidth = 1080.toFloat() / options.outWidth
            val scaleHeight = 1920.toFloat() / options.outHeight
            var scale = 0.5f
            scale = if (scaleHeight < scaleWidth) {
                scaleHeight
            } else {
                scaleWidth
            }
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            // 产生缩放后的Bitmap对象
            val resizeBitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(filePath), 0, 0, options.outWidth, options.outHeight, matrix, false)
            BitmapUtils.saveSmallBitmap2SDCard(resizeBitmap, compresPath)
            bitHeight = (options.outHeight * scale).toInt()
            bitWidth = (options.outWidth * scale).toInt()

        } else {
            bitHeight = options.outHeight
            bitWidth = options.outWidth
            BitmapUtils.saveSmallBitmap2SDCard(BitmapFactory.decodeFile(filePath), compresPath)
        }
        Log.i("selectFile", "${compresPath}Bitmap Height == " + options.outHeight + "  Width== " + options.outWidth)
        val chatEntry = ChatEntry()
        chatEntry.content = compresPath
        chatEntry.extra = bitHeight.toString() + "&&" + bitWidth
        chatEntry.created = System.currentTimeMillis().toString()
        chatEntry.from = QzApplication.get().infoBean!!.uid.toLong()
        chatEntry.to = 0
        chatEntry.face = QzApplication.get().infoBean!!.face
        chatEntry.type = "gpic"
        chatEntry.msgId = System.currentTimeMillis().toString() + ""
        chatEntry.chatId = 0
        chatEntry.msgStat = 2
        chatEntry.userId = QzApplication.get().infoBean!!.uid.toLong()
        chatEntry.gsid = currentGid.toLong()
        val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_MINE, chatEntry)
        mView?.addItem(chatEntryChatMultiBean)
        mView?.scrollToBottom()
        return chatEntry
    }

    override fun openGallery(ft: BaseFragment, requestCode: Int) {
        RxPermissions(ft)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { b ->
                    run {
                        if (b) {
                            PictureSelector.create(ft)
                                    .openGallery(PictureMimeType.ofAll())
                                    .loadImageEngine(GlideEngine.createGlideEngine())
                                    .isWithVideoImage(true)
                                    .isWeChatStyle(true)// 是否开启微信图片选择风格
                                    .maxSelectNum(9)
                                    .forResult(requestCode)
                        } else {
                            TipsUtil.showToast(ft.context, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    override fun recordVideo(ft: BaseFragment, requestCode: Int) {
        RxPermissions(ft)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .subscribe { b ->
                    run {
                        if (b) {
                            PictureSelector.create(ft)
                                    .openCamera(PictureMimeType.ofVideo())
                                    .videoQuality(1)
                                    .forResult(requestCode)
                        } else {
                            TipsUtil.showToast(ft.context, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    override fun takePhoto(ft: BaseFragment, requestCode: Int) {
        RxPermissions(ft)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe { b ->
                    run {
                        if (b) {
                            PictureSelector.create(ft)
                                    .openCamera(PictureMimeType.ofImage())
                                    .forResult(requestCode)
                        } else {
                            TipsUtil.showToast(ft.context, "您没有授权该权限，请在设置中打开授权")
                        }
                    }
                }
    }

    fun compressVideo(v: View?, inputP: String, outputP: String, bean: ChatEntry, position: Int) {
        val size = FileSizeUtil.getFileOrFilesSize(inputP, FileSizeUtil.SIZETYPE_MB)
        if (size < 10) {
            if (v != null) {
                v.visibility = View.INVISIBLE
            }
            bean.msgStat = 2
            bean.content = inputP
            mView?.notifyAdapterItemChanged(position)
            return
        }
        VideoCompress.compressVideoMedium(inputP, outputP, object : VideoCompress.CompressListener {
            override fun onStart() {
                if (v != null) {
                    v.visibility = View.VISIBLE
                }
            }

            override fun onSuccess() {
                if (v != null) {
                    v.visibility = View.INVISIBLE
                }
                bean.msgStat = 2
                bean.content = outputP
                mView?.notifyAdapterItemChanged(position)
            }

            override fun onFail() {
                if (v != null) {
                    v.visibility = View.INVISIBLE
                }
                bean.msgStat = 3
                mView?.notifyAdapterItemChanged(position)
            }

            override fun onProgress(percent: Float) {

            }
        })
    }

    fun compressAndUploadVideo(activity: Activity, currentGid: Int, inputP: String, outputP: String, bean: ChatEntry) {
        val size = FileSizeUtil.getFileOrFilesSize(inputP, FileSizeUtil.SIZETYPE_MB)
        if (size < 2) {
            bean.msgStat = 2
            bean.content = inputP
            val videoPath = bean.content
            val split = bean.extra!!.split("&&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split.isNotEmpty()) {
                val albunPath = split[0]
                val media = ArrayList<String>()
                media.add(videoPath!!)
                media.add(albunPath)
                uploadVideoMedia(bean.type, activity, "", media, null, bean, 0, currentGid)
            }
            return
        }

        VideoCompress.compressVideoLow(inputP, outputP, object : VideoCompress.CompressListener {
            override fun onStart() {
                Log.i("gchatpresenter", "start")
            }

            override fun onSuccess() {
                Log.i("gchatpresenter", "success")
//                bean.msgStat = 2
//                bean.content = outputP
                val videoPath = outputP
                val split = bean.extra!!.split("&&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                Log.i("gchatpresenter", "split is notempty:${split.isNotEmpty()}")
                if (split.isNotEmpty()) {
                    val albunPath = split[0]
                    val media = ArrayList<String>()
                    media.add(videoPath!!)
                    media.add(albunPath)
                    uploadVideoMedia(bean.type, activity, "", media, null, bean, 0, currentGid)
                }
            }

            override fun onFail() {
                Log.i("gchatpresenter", "fail")
                bean.msgStat = 3
            }

            override fun onProgress(percent: Float) {
                Log.i("gchatpresenter", "onProgress $percent")
            }
        })

    }

    override fun uploadVideoMedia(type: String, activity: Activity, s: String, media: ArrayList<String>, pTv: TextView?, bean: ChatEntry, position: Int, currentGid: Int) {
        model.uploadMedia(activity, "", media, object : StringCallback() {
            override fun onSuccess(response: Response<String>) {
                if (pTv != null) {
                    pTv.text = ""
                }
                Log.i("gchatpresenter", "uploadVideoMedia---onSuccess：${response.isSuccessful}")
                if (response.isSuccessful) {
                    paresResult(activity, type, response.body(), bean, position, currentGid)
                } else {
                    bean.msgStat = 2
                    mView?.notifyAdapterItemChanged(position)
                    TipsUtil.showToast(activity, response.code().toString() + "")
                }
            }

            override fun onError(response: Response<String>) {
                super.onError(response)
                Log.i("gchatpresenter", "uploadVideoMedia---onError")
                TipsUtil.showToast(activity, response.code().toString() + "")
                if (pTv != null) {
                    pTv.text = ""
                }
                bean.msgStat = 2
                mView?.notifyAdapterItemChanged(position)
            }

            override fun uploadProgress(progress: Progress?) {
                val fraction = progress!!.fraction
                Log.i("gchatpresenter", "uploadVideoMedia---onProgress $fraction")
                if (pTv != null) {
                    pTv.text = (fraction * 100).toString() + "%"
                }
            }
        })
    }

    override fun uploadPicOrAudioMedia(type: String, activity: Activity, s: String, media: ArrayList<String>, pTv: TextView?, bean: ChatEntry, position: Int, currentGid: Int) {
        model.uploadMedia(activity, "", media, object : StringCallback() {
            override fun onSuccess(response: Response<String>?) {
                if (response!!.isSuccessful) {
                    paresResult(activity, type, response.body(), bean, position, currentGid)
                } else {
                    bean.msgStat = 2
                    mView?.notifyAdapterItemChanged(position)
                    TipsUtil.showToast(activity, response.code().toString() + "")
                }
            }

            override fun onError(response: Response<String>?) {
                super.onError(response)
                bean.msgStat = 2
                mView?.notifyAdapterItemChanged(position)
                TipsUtil.showToast(activity, response!!.code().toString() + "")
            }
        })
    }


    /**
     * 解析后台返回数据并发送新消息
     * （last step  将音视频或图片上传至服务器）
     *
     * @param type
     * @param result
     * @param entry
     * @param position
     */
    fun paresResult(activity: Activity, type: String, result: String, entry: ChatEntry, position: Int, gid: Int) {
        var `object`: JSONObject? = null
        try {
            `object` = JSONObject(result)
            val stat = `object`.getString("stat")
//            val msg = `object`.getString("msg")
            if (TextUtils.equals("ok", stat)) {
                val attr = `object`.getJSONArray("att")

                val id = QzApplication.get().msgUUID
                QzApplication.get().msgUUID = "0"
                model.getId(activity)
                if (attr != null && attr.length() > 0) {
                    if (attr.length() == 1) {
                        val jsonObject = attr.getJSONObject(0)
                        val file = jsonObject.getString("file")
                        val extra = jsonObject.getString("extra")
                        var myid = QzApplication.get().infoBean?.uid
                        var isSuccess = false
                        if (myid != 0) {
                            isSuccess = if (TextUtils.equals(type, "gpic")) {
                                mView?.sendSocketMessage(getSendMsg(type, id, myid.toString(), 0, gid, file, entry.extra!!))!!
                            } else {
                                mView?.sendSocketMessage(getSendMsg(type, id, myid.toString(), 0, gid, file, extra))!!
                            }
                            if (isSuccess) {
                                entry.msgStat = 0
                                if (!TextUtils.equals(id, "0"))
                                    entry.msgId = id
                            } else {
                                entry.msgStat = 1
                            }
                        } else {
                            entry.msgStat = 1
                        }
                    }
                    if (attr.length() == 2) {
                        val jsonObject = attr.getJSONObject(0)
                        val file = jsonObject.getString("file")
                        val imgObj = attr.getJSONObject(1)
                        val img = imgObj.getString("file")
                        var myid = QzApplication.get().infoBean?.uid
                        val split = entry.extra!!.split("&&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        var dura = "0"
                        if (split != null && split.size == 2) {
                            dura = split[1]
                        }
                        var isSuccess: Boolean
                        if (myid != 0) {
                            isSuccess = mView?.sendSocketMessage(getSendMsg(type, id, myid.toString(), 0, gid, file, img + "&&" + dura))!!
                            if (isSuccess) {
                                entry.msgStat = 0
                                if (!TextUtils.equals(id, "0"))
                                    entry.msgId = id
                            } else {
                                entry.msgStat = 1
                            }
                        } else {
                            entry.msgStat = 1
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        entry.isMsgRead = true
        mView?.notifyAdapterItemChanged(position)


    }

    override fun createChatEntry(filePath: String, dura: Int, currentGid: Int): ChatEntry {
        val chatEntry = ChatEntry()
        chatEntry.content = filePath
        chatEntry.extra = dura.toString()
        chatEntry.created = System.currentTimeMillis().toString()
        chatEntry.from = QzApplication.get().infoBean!!.uid.toLong()
        chatEntry.to = 0
        chatEntry.face = QzApplication.get().infoBean!!.face
        chatEntry.type = "gaudio"
        chatEntry.msgId = System.currentTimeMillis().toString()
        chatEntry.chatId = 0
        chatEntry.msgStat = 2
        chatEntry.userId = QzApplication.get().infoBean!!.uid.toLong()
        chatEntry.gsid = currentGid.toLong()
        return chatEntry
    }

    override fun addMsg(index: Int, entry: GchatParcelEntry) {
        var data = convertData(entry)
        if (data != null)
            mView?.addItem(index, data)
    }

    override fun addNewMsg(entry: GchatParcelEntry) {
        if (TextUtils.equals(entry.entry.type, "littletxt")) {
            addLittleTxt(entry)
        } else {
            var data = convertData(entry)
            if (data != null)
                mView?.addItem(data)
//            mView?.scrollToBottom()
            mView?.showNewMsg()
        }
    }

    var littleTxtData = ArrayList<ChatEntry>()
    private fun addLittleTxt(entry: GchatParcelEntry) {
        val chatEntry = entry.entry
        littleTxtData.add(chatEntry)
        mView?.setLittleChatSize(littleTxtData.size)
    }

    override fun removeLittleTxt(chatEntry: ChatEntry) {
        if (littleTxtData.contains(chatEntry)) {
            littleTxtData.remove(chatEntry)
            mView?.setLittleChatSize(littleTxtData.size)
        }
    }

    var lastTime: Long = 0
    private fun convertData(entry: GchatParcelEntry): ChatMultiEntry<ChatEntry>? {
        val myid = QzApplication.get().infoBean!!.uid
        val chatEntry = entry.entry
        val type = chatEntry.type
        val from = chatEntry.from
        val created = chatEntry.created
        val time = java.lang.Long.parseLong(created)
        val isShowCreated = chatEntry.isShowCreated
        if (isShowCreated!!) {
            if (time - lastTime > 5 * 60 * 1000) {
                lastTime = time
            } else {
                chatEntry.isShowCreated = false
            }
        } else {
            if (lastTime != 0L) {
                if (time - lastTime > 5 * 60 * 1000) {
                    chatEntry.isShowCreated = true
                    lastTime = time
                } else {
                    chatEntry.isShowCreated = false
                }
            } else {
                if (System.currentTimeMillis() - time > 5 * 60 * 1000) {
                    chatEntry.isShowCreated = true
                    lastTime = time
                } else {
                    chatEntry.isShowCreated = false
                }
            }
        }

        return if (from != myid.toLong()) {
            if (TextUtils.equals(type, "gtxt") || TextUtils.equals(type, "gpic") || TextUtils.equals(type, "boxanswer")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER, chatEntry)
            } else if (TextUtils.equals(type, "gaudio")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER_AUDIO, chatEntry)
            } else if (TextUtils.equals(type, "gvideo")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER_VIDEO, chatEntry)
            } else if (TextUtils.equals(type, "boxquestion")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_OTHER_QUESTION, chatEntry)
            } else {
                null
            }
        } else {
            if (TextUtils.equals(type, "gtxt") || TextUtils.equals(type, "gpic") || TextUtils.equals(type, "boxanswer")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE, chatEntry)
            } else if (TextUtils.equals(type, "gaudio")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_AUDIO, chatEntry)
            } else if (TextUtils.equals(type, "gvideo")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_VIDEO, chatEntry)
            } else if (TextUtils.equals(type, "boxquestion")) {
                ChatMultiEntry<ChatEntry>(ChatMultiEntry.CHAT_MINE_QUESTION, chatEntry)
            } else {
                null
            }
        }

    }

    override fun sendTxt(activity: Activity, content: String, currentGid: Int) {
        val myid = QzApplication.get().infoBean!!.uid
        if (myid != 0) {
            var isSuccess = false
            val id = QzApplication.get().msgUUID
            QzApplication.get().msgUUID = "0"
            getMsgId(activity)
            isSuccess = mView?.sendSocketMessage(getSendMsg("gtxt", id, myid.toString(), 0, currentGid, content, ""))!!

            val chatEntry = ChatEntry()
            chatEntry.type = "gtxt"
            chatEntry.layout = ""
            chatEntry.face = QzApplication.get().infoBean!!.face
            chatEntry.created = System.currentTimeMillis().toString()
            chatEntry.content = content
            chatEntry.extra = ""
            chatEntry.from = myid.toLong()
            if (!TextUtils.equals(id, "0")) {
                chatEntry.msgId = id
            } else {
                chatEntry.msgId = System.currentTimeMillis().toString() + ""
            }
            chatEntry.chatId = 0
            chatEntry.to = 0
            chatEntry.userId = myid.toLong()
            chatEntry.gsid = currentGid.toLong()
            chatEntry.isMsgRead = true
            if (!isSuccess) {
                chatEntry.msgStat = 1
            }
            val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_MINE, chatEntry)
            mView?.addItem(chatEntryChatMultiBean)
            mView?.scrollToBottom()
        }
    }

    override fun createMsg(note: String, currentGid: Int) {
        val myid = QzApplication.get().infoBean!!.uid
        if (myid != 0) {
            val chatEntry = ChatEntry()
            chatEntry.type = "gtxt"
            chatEntry.layout = "movie"
            chatEntry.face = ""
            chatEntry.nick="剧情问答"
            chatEntry.created = System.currentTimeMillis().toString()
            chatEntry.content = note
            chatEntry.extra = ""
            chatEntry.from = 0
            chatEntry.msgId = System.currentTimeMillis().toString() + ""
            chatEntry.chatId = 0
            chatEntry.to = myid.toLong()
            chatEntry.userId = 0
            chatEntry.gsid = currentGid.toLong()
            chatEntry.isMsgRead = true
            val chatEntryChatMultiBean = ChatMultiEntry(ChatMultiEntry.CHAT_OTHER, chatEntry)
            mView?.addItem(chatEntryChatMultiBean)
            mView?.scrollToBottom()
        }

    }

    /**
     * @deprecated
     */
    override fun sendLittleTxt(activity: Activity, to: Long, content: String, img: String) {

        val myid = QzApplication.get().infoBean!!.uid
        if (myid != 0) {

            val id = QzApplication.get().msgUUID
            QzApplication.get().msgUUID = "0"
            getMsgId(activity)
            val msg = getSendMsg("littletxt", id, myid.toString(), to, 0, content, img)
            mView?.sendSocketMessage(msg)!!
        }

    }

    override fun sendLittleTxt(activity: Activity, to: Long, txt: String?, type: String, content: String, extra: String?) {
        val myid = QzApplication.get().infoBean!!.uid
        if (myid != 0) {
            val id = QzApplication.get().msgUUID
            QzApplication.get().msgUUID = "0"
            getMsgId(activity)
            val msg = getLittleSendMsg(id, myid.toString(), to, txt, content, extra, type)
            mView?.sendSocketMessage(msg)!!
        }
    }


    /**
     * 获取一次msg UUID
     */
    fun getMsgId(activity: Activity) {
        model.getId(activity)
    }

    private fun getLittleSendMsg(id: String, from: String, to: Long, txt: String?, content: String, extra: String?, type: String): String {
        val `object` = JSONObject()
        `object`.put("type", "message")
        `object`.put("act", "littletxt")
        `object`.put("created", System.currentTimeMillis())
        `object`.put("range", "private")
        `object`.put("from", from)
        `object`.put("to", to)
        `object`.put("id", id)
        val data = JSONObject()
        data.put("created", System.currentTimeMillis())
        data.put("expire", System.currentTimeMillis())
        data.put("content", txt)
        data.put("layout_type", type)
        val conXml = StringBuilder()
        when (type) {
            "gpic" -> {
                val split = extra!!.split("&&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                var height = ""
                var width = ""
                if (split != null && split.size == 2) {
                    height = split[0]
                    width = split[1]
                }
                conXml.append("<pic src=\"")
                        .append(content)
                        .append("\"width=\"")
                        .append(width)
                        .append("\"height=\"")
                        .append(height)
                        .append("\"></pic>")
            }
            "gvideo" -> {
                var img = ""
                var dura = ""
                val split = extra!!.split("&&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if (split.isNotEmpty()) {
                    img = split[0]
                    if (split.size == 2) {
                        dura = split[1]
                    }
                }
                conXml.append("<movie src=\"")
                        .append(content)
                        .append("\"dura=\"")
                        .append(dura)
                        .append("\"poster=\"")
                        .append(img)
                        .append("\"></movie>")
            }
        }
        data.put("layout", conXml.toString())
        `object`.put("data", data)
//        Log.i("groupChat", "${`object`}")
        return `object`.toString()
    }

    /**
     * 聊天消息包装
     * @param from
     * @param to
     * @param content
     * @param extra
     * @return
     */
    fun getSendMsg(act: String, id: String, from: String, to: Long, gid: Int, content: String, extra: String): String {
        val `object` = JSONObject()
        try {
            `object`.put("type", "message")
            `object`.put("act", act)
            `object`.put("created", System.currentTimeMillis())
            `object`.put("range", "private")
            `object`.put("from", from)
            `object`.put("to", to)
            `object`.put("id", id)
            `object`.put("groupid", gid)
            val data = JSONObject()
            data.put("created", System.currentTimeMillis())
            data.put("expire", System.currentTimeMillis())
            data.put("layout", "")
            data.put("extra", extra)

            val conXml = StringBuilder()
            when {
                TextUtils.equals(act, "gtxt") -> conXml.append("<txt>")
                        .append(content)
                        .append("</txt>")
                TextUtils.equals(act, "gpic") -> {
                    val split = extra.split("&&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    var height = ""
                    var width = ""
                    if (split != null && split.size == 2) {
                        height = split[0]
                        width = split[1]

                    }
                    conXml.append("<pic src=\"")
                            .append(content)
                            .append("\"width=\"")
                            .append(width)
                            .append("\"height=\"")
                            .append(height)
                            .append("\"></pic>")
                }
                TextUtils.equals(act, "gaudio") -> {
                    val dura = extra.replace("dura".toRegex(), "")
                    conXml.append("<sound src=\"")
                            .append(content)
                            .append("\"dura=\"")
                            .append(dura)
                            .append("\"></sound>")
                }
                TextUtils.equals(act, "gvideo") -> {
                    var img = ""
                    var dura = ""
                    val split = extra.split("&&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    if (split.isNotEmpty()) {
                        img = split[0]
                        if (split.size == 2) {
                            dura = split[1]
                        }
                    }
                    conXml.append("<movie src=\"")
                            .append(content)
                            .append("\"dura=\"")
                            .append(dura)
                            .append("\"poster=\"")
                            .append(img)
                            .append("\"></movie>")

                }
                TextUtils.equals(act, "littletxt") -> {
                    conXml.append(content)
                    data.put("layout", extra)
                    data.put("extra", "")
                }
            }
            data.put("content", conXml.toString())
            `object`.put("data", data)
            Log.i("groupChat", "${`object`}")
            return `object`.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun littleTxtClick(activity: Activity, fragment: Fragment, requestCode: Int) {
        fragment.startActivityForResult(Intent(activity, LightChatActivity::class.java).putExtra("data", littleTxtData), requestCode)
        activity.overridePendingTransition(R.anim.open_enter2, R.anim.open_exit3)
    }


}
