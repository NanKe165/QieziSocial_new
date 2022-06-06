package com.eggplant.qiezisocial.ui.chat

//
import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Point
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseWebSocketActivity
import com.eggplant.qiezisocial.contract.ChatAvContract
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.presenter.ChatAvPresenter
import com.eggplant.qiezisocial.rtc.PeerConnectionParameters
import com.eggplant.qiezisocial.rtc.WebRtcClient
import com.eggplant.qiezisocial.socket.AbsBaseWebSocketService
import com.eggplant.qiezisocial.socket.WebSocketService
import com.eggplant.qiezisocial.socket.event.CommonResponse
import com.eggplant.qiezisocial.socket.event.WebSocketSendDataErrorEvent
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_chat_av.*
import kotlinx.android.synthetic.main.layout_chat_a_function.*
import kotlinx.android.synthetic.main.layout_chat_a_head.*
import kotlinx.android.synthetic.main.layout_chat_v_function.*
import kotlinx.android.synthetic.main.layout_chat_v_head.*
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.MediaStream
import org.webrtc.SessionDescription
import org.webrtc.VideoRenderer
import org.webrtc.VideoRendererGui
import java.util.*

/**
 * Created by Administrator on 2020/4/9.
 */
class ChatAVActivity : BaseWebSocketActivity<ChatAvPresenter>(), ChatAvContract.View, WebRtcClient.RtcListener {


    private var start: String? = null
    private var from: String? = null
    private var to: String? = null
    private var bean: MainInfoBean? = null
    private var msgId: String? = "0"
    private var invoke: String? = null


    //    private final static int VIDEO_CALL_SENT = 666;
    private val VIDEO_CODEC_VP9 = "VP9"
    private val AUDIO_CODEC_OPUS = "opus"
    // Local preview screen position before call is connected.
    private val LOCAL_X_CONNECTING = 0
    private val LOCAL_Y_CONNECTING = 0
    private val LOCAL_WIDTH_CONNECTING = 100
    private val LOCAL_HEIGHT_CONNECTING = 100
    // Local preview screen position after call is connected.
    private val LOCAL_X_CONNECTED = 8
    private val LOCAL_Y_CONNECTED = 8
    private val LOCAL_WIDTH_CONNECTED = 30
    private val LOCAL_HEIGHT_CONNECTED = 25
    // Remote video screen position
    private val REMOTE_X = 0
    private val REMOTE_Y = 0
    private val REMOTE_WIDTH = 100
    private val REMOTE_HEIGHT = 100
    private val scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL
    private var localRender: VideoRenderer.Callbacks? = null
    private var remoteRender: VideoRenderer.Callbacks? = null
    private var client: WebRtcClient? = null
    private var timer: Timer? = null

    private var mServiceBound = false

    override fun initPresenter(): ChatAvPresenter {
        return ChatAvPresenter()
    }

    override val webSocketClass: Class<out AbsBaseWebSocketService>
        get() = WebSocketService::class.java


    override fun getLayoutId(): Int {
        isUseBaseUi = false
        ScreenUtil.transluteStateBarAndNavBar(this)
        return R.layout.activity_chat_av
    }

    override fun initView() {
        mPresenter.attachView(this)

    }

    override fun initData() {
        start = intent.getStringExtra("start")
        invoke = intent.getStringExtra("invoke")
        from = intent.getStringExtra("from")
        to = intent.getStringExtra("to")
        msgId = intent.getStringExtra("id")
        if (TextUtils.equals(from, "") || TextUtils.equals(to, "")) {
            TipsUtil.showToast(mContext, "error")
            finish()
        } else {
            bean = if (TextUtils.equals("initiator", invoke)) {
                MainDBManager.getInstance(mContext).queryMainUser(to + "")
            } else {
                MainDBManager.getInstance(mContext).queryMainUser(from + "")
            }
        }
//
//
        //视频
        if (TextUtils.equals(start, "video")) {
            createVideo()

            video_head_gp.visibility = View.VISIBLE
            if (bean != null) {
                video_name.text = bean?.nick
                Glide.with(mContext).load(API.PIC_PREFIX + bean?.face!!).into(video_img)
            }
            //发起者
            if (TextUtils.equals("initiator", invoke)) {
                video_func_gp.visibility = View.VISIBLE
                chat_handUp.visibility = View.VISIBLE
                chat_av_surfacev.visibility = View.VISIBLE
                chat_av_bg.visibility = View.GONE

            } else {
                //接收者
                chat_requset_gp.visibility = View.VISIBLE
            }
        } else if (TextUtils.equals(start, "audio")) {
            initRtcAudio()
            chat_av_bg.visibility = View.GONE
            if (bean != null) {
                audio_name.text = bean?.nick
                Glide.with(mContext).load(API.PIC_PREFIX + bean?.face!!).into(audio_img)
            }
            //音频
            audio_head_gp.visibility = View.VISIBLE
            //发起者
            if (TextUtils.equals("initiator", invoke)) {
                audio_func_gp.visibility = View.VISIBLE
                chat_handUp.visibility = View.VISIBLE
            } else {
                //接收者
                chat_requset_gp.visibility = View.VISIBLE
            }
        }

    }

    //
    override fun initEvent() {
        video_swap.setOnClickListener {
            client?.switchCamera()
        }
        video_mute.setOnClickListener {
            client?.mute()
        }
        chat_minimize.setOnClickListener {
            startVideoService()
        }
//        audio_mute.setOnClickListener {
//
//        }
//        audio_hs.setOnClickListener {
//
//        }
        chat_handUp.setOnClickListener {
            touchEvent("callhangup")
            finish()
        }
        chat_reject.setOnClickListener {
            touchEvent("callreject")
            finish()
        }
        chat_allow.setOnClickListener {
            touchEvent("callaccept")
            chat_requset_gp.visibility = View.GONE
            chat_handUp.visibility = View.VISIBLE
            setStateTxt("", "正在连接")
            if (TextUtils.equals(start, "vido")) {
                video_func_gp.visibility = View.VISIBLE
                chat_av_surfacev.visibility = View.VISIBLE
                chat_av_surfacev.visibility = View.VISIBLE
            } else if (TextUtils.equals(start, "audio")) {
                audio_func_gp.visibility = View.VISIBLE
            }
            try {
                client?.connect("1")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

    }


    private var isVideo: Boolean = false

    private fun createVideo() {
        isVideo = true
        chat_av_surfacev.keepScreenOn = true
        VideoRendererGui.setView(chat_av_surfacev) { initRtcVideo() }

        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false)
        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true)
    }

    private fun initRtcVideo() {
        val displaySize = Point()
        windowManager.defaultDisplay.getSize(displaySize)
        val params = PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true)

        client = WebRtcClient(this, params, VideoRendererGui.getEGLContext())
        runOnUiThread {
            RxPermissions(activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    .subscribe { b ->
                        if (!b) {
                            TipsUtil.showToast(mContext, "权限申请失败")
                        } else {
                            client?.start()
                        }
                    }
        }

    }

    private fun initRtcAudio() {
        isVideo = false
        val displaySize = Point()
        windowManager.defaultDisplay.getSize(displaySize)
        val params = PeerConnectionParameters(
                false, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true)

        client = WebRtcClient(this, params, VideoRendererGui.getEGLContext())
        runOnUiThread {
            RxPermissions(activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                    .subscribe { b ->
                        if (!b) {
                            TipsUtil.showToast(mContext, "权限申请失败")
                        } else {
                            client?.startAudio()
                        }
                    }
        }
    }


    override fun onCommonResponse(response: CommonResponse) {
        try {
            val `object` = JSONObject(response.txt)
            val act = `object`.getString("act")
            val msgType = `object`.getString("type")
            when (act) {
                "gcallhangup" -> {
                    TipsUtil.showToast(mContext, "对方已挂断")
                    activity.finish()
                }
                "gcallreject" -> {
                    TipsUtil.showToast(mContext, "对方拒绝了您的请求")
                    activity.finish()
                }
                "gcallaccept" -> {
                    //TODO 连接webRtc
                    val device = `object`.getString("device")
                    if (TextUtils.equals(device, "iOS")) {
                        client?.connect("1")
                    }
                    TipsUtil.showToast(mContext, "对方已同意")
                    setStateTxt("", "正在连接")
                }
            }
            if (TextUtils.equals(msgType, "webrtc")) {
                if (TextUtils.equals(act, "sdp") || TextUtils.equals(act, "candidate")) {
//                    val from = `object`.getString("from")
//                    val to = `object`.getString("to")

                    val data = `object`.getJSONObject(act)
                    val payload = data.getJSONObject("payload")
                    val type = data.getString("type")
                    val id = data.getString("id")
                    client?.onEvent(id, type, payload)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun onErrorResponse(response: WebSocketSendDataErrorEvent) {

    }

    override fun onServiceBindSuccess() {
        super.onServiceBindSuccess()
        if (TextUtils.equals("initiator", invoke)) {
            sendCall()
        } else {
            setStateTxt("视频来电", "语音来电")
        }
    }

    override fun onStatusChanged(newStatus: String) {
        runOnUiThread {
            TipsUtil.showToast(mContext, newStatus)
            if (TextUtils.equals(newStatus.trim(), "DISCONNECTED")) {
                finish()
            }
        }
    }

    override fun onLocalStream(localStream: MediaStream) {
        Log.d("chatava","localStream--${Thread.currentThread()} ")
        localStream.videoTracks[0].addRenderer(VideoRenderer(localRender))
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, false)
    }

    private lateinit var remoteStream: MediaStream
    override fun onAddRemoteStream(remoteStream: MediaStream, endPoint: Int) {

        runOnUiThread {
            video_head_gp.visibility = View.GONE
            chat_av_bg.visibility = View.GONE
            video_func_gp.visibility = View.VISIBLE
            startTime()
        }

        this.remoteStream = remoteStream
        remoteStream.videoTracks[0].addRenderer(VideoRenderer(remoteRender))
        VideoRendererGui.update(remoteRender,
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false)

        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
                scalingType, false)


    }

    override fun onAudioStream() {
        runOnUiThread {
            setStateTxt("", "已接通")
            startTime()
        }

    }

    override fun onRemoveRemoteStream(endPoint: Int) {
        runOnUiThread {
            chat_time.text = ""
        }
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, false)
    }

    override fun sendSDPMsg(id: String, sdp: SessionDescription, payload: JSONObject?) {
        val data = JSONObject()
        val `object` = JSONObject()
        try {
            data.put("type", sdp.type.canonicalForm())
            data.put("id", id)
            data.put("payload", payload)
            `object`.put("type", "webrtc")
            `object`.put("act", "sdp")
            `object`.put("created", System.currentTimeMillis())
            `object`.put("sdpType", sdp.type.canonicalForm())
            `object`.put("device", "android")
            if (TextUtils.equals("initiator", invoke)) {
                `object`.put("from", from)
                `object`.put("to", to)
            } else {
                `object`.put("from", to)
                `object`.put("to", from)
            }

            `object`.put("sdp", data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
//        Log.i("chatava","sendSDPMsg----${`object`.toString()}")
        sendSocketMessage(`object`.toString())
    }

    override fun sendCandidateMsg(id: String?, candiadate: String?, payload: JSONObject?) {
        val data = JSONObject()
        val `object` = JSONObject()
        try {
            data.put("type", candiadate)
            data.put("id", id)
            data.put("payload", payload)
            `object`.put("type", "webrtc")
            `object`.put("act", "candidate")
            `object`.put("created", System.currentTimeMillis())
            if (TextUtils.equals("initiator", invoke)) {
                `object`.put("from", from)
                `object`.put("to", to)
            } else {
                `object`.put("from", to)
                `object`.put("to", from)
            }
            `object`.put("candidate", data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
//        Log.i("chatava","sendCandidateMsg----${`object`.toString()}")
        sendSocketMessage(`object`.toString())
    }

    override fun onPause() {
        super.onPause()
        if (isVideo) {
            chat_av_surfacev.onPause()
            client?.onPause()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (mServiceBound) {
            unbindService(mVideoServiceConnection)
            mServiceBound = false
        }

//        var mGlSurfaceView = ChatConst.mVideoViewLayout
//        if (mGlSurfaceView != null && mGlSurfaceView.parent != null) {
//            (mGlSurfaceView.parent as ViewGroup).removeView(mGlSurfaceView)
//            chat_ava_gp.addView(mGlSurfaceView, 0)
//            ChatConst.mVideoViewLayout = null
//        }
    }

    override fun onResume() {
        super.onResume()
        if (isVideo) {
            chat_av_surfacev.onResume()
            client?.onResume()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onDestroy() {
        if (isVideo) {
            VideoRendererGui.remove(localRender)
        }
        client?.onDestroy()
        client = null
        timer?.cancel()
        //解绑 不显示悬浮框
        if (mServiceBound) {
            unbindService(mVideoServiceConnection)
            ChatConst.mVideoViewLayout = null
            mServiceBound = false
        }
        super.onDestroy()
    }


    private fun sendCall() {
        setStateTxt("正在等待接听...", "正在等待接听...")
        val id = application.msgUUID
        msgId = id
        application.msgUUID = "0"
        mPresenter.getId(activity)
        val `object` = JSONObject()
        try {
            `object`.put("type", "system")
            `object`.put("act", "gcall")
            `object`.put("media", start)
            `object`.put("created", System.currentTimeMillis())
            `object`.put("from", from)
            `object`.put("to", to)
            `object`.put("id", id)
            `object`.put("device", "android")
//            Log.i("chatava","sendCall----${`object`.toString()}")
            sendText(`object`.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun setStateTxt(vidoTxt: String, audioTxt: String) {
        if (TextUtils.equals(start, "video")) {
            video_state.text = vidoTxt
        } else {
            audio_state.text = audioTxt
        }
    }

    private fun touchEvent(type: String) {
        val `object` = JSONObject()
        try {
            `object`.put("type", "system")
            `object`.put("act", "g" + type)//gcallaccept
            `object`.put("created", System.currentTimeMillis())

            if (TextUtils.equals("initiator", invoke)) {
                `object`.put("from", from)
                `object`.put("to", to)
            } else {
                `object`.put("from", to)
                `object`.put("to", from)
            }
            `object`.put("id", msgId)
//            Log.i("chatava","touchEvent----${`object`.toString()}")
            sendSocketMessage(`object`.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun startTime() {
        timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                runOnUiThread { changeTime() }
            }
        }
        timer?.schedule(task, 0, 1000)
    }


    private var timeSecond = 0

    private fun changeTime() {
        timeSecond++
        val minute = timeSecond / 60
        val second = timeSecond % 60
        val timeStr: String
        timeStr = if (timeSecond / 10 > 0) {
            if (minute / 10 > 0) {
                minute.toString() + ":" + second
            } else {
                "0$minute:$second"
            }
        } else {
            if (minute / 10 > 0) {
                minute.toString() + ":0" + second
            } else {
                "0$minute:0$second"
            }

        }
        chat_time.text = timeStr
    }


    var mVideoServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var binder = service as FloatVideoWindowService.MyBinder
            binder.service
        }


    }

    private fun startVideoService() {
        moveTaskToBack(true)
        ChatConst.mVideoViewLayout = chat_av_surfacev
        Log.i("chatava"," holder is empty = ${ chat_av_surfacev.holder==null}")

        //开启服务显示悬浮框
        var floatVideoIntent = Intent(this, FloatVideoWindowService::class.java)
        mServiceBound = bindService(floatVideoIntent, mVideoServiceConnection, Context.BIND_AUTO_CREATE)

    }

}