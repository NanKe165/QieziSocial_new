package com.eggplant.qiezisocial.ui.main

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.EditorInfo
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseWebSocketActivity
import com.eggplant.qiezisocial.contract.LightChatContract
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.entry.GchatParcelEntry
import com.eggplant.qiezisocial.event.RemoveEvent
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.UmSetting
import com.eggplant.qiezisocial.presenter.LightPresenter
import com.eggplant.qiezisocial.socket.AbsBaseWebSocketService
import com.eggplant.qiezisocial.socket.WebSocketService
import com.eggplant.qiezisocial.socket.event.CommonResponse
import com.eggplant.qiezisocial.socket.event.WebSocketSendDataErrorEvent
import com.eggplant.qiezisocial.ui.main.adapter.LchatAdapter
import com.eggplant.qiezisocial.utils.PrevUtils
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_lightchat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Administrator on 2021/4/14.
 */

class LightChatActivity : BaseWebSocketActivity<LightPresenter>(), LightChatContract.View, ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {


    lateinit var adapter: LchatAdapter
    var data: ArrayList<ChatEntry>? = null

    /**
     * 当edittext设置最大输入限制时，在此处进行监听，以便在删除emoji时调整最大限制数
     *
     * @see  com.mabeijianxi.jianxiexpression.core.ExpressionTransformEngine.input emoji输入时最大数调整
     */

    private val mTextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable) {
            // 先去掉监听器，否则会出现栈溢出
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//            Log.i("emojitest", "beforeTextChanged s=$s  start:$start  after:$after  count:$count")
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//            Log.i("emojitest", "onTextChanged s=$s  start:$start  befor:$before  count:$count")
            if (before >= 7 && count == 0) {
                if (lchat_edit.filters != null && lchat_edit.filters[0] is InputFilter.LengthFilter) {
                    var max = (lchat_edit.filters[0] as InputFilter.LengthFilter).max
                    max -= before - 1
                    Log.i("emojitest", "onTextChanged max :$max")
                    lchat_edit.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max))
                }
            }
        }
    }

    override val webSocketClass: Class<out AbsBaseWebSocketService>
        get() = WebSocketService::class.java

    override fun onCommonResponse(response: CommonResponse) {

    }

    override fun onErrorResponse(response: WebSocketSendDataErrorEvent) {

    }

    override fun initPresenter(): LightPresenter {
        return LightPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_lightchat
    }

    override fun initView() {
        mPresenter.attachView(this)
        lchat_keyboard.setEmojiContent(lchat_edit)
        adapter = LchatAdapter(null)
        lchat_ry.layoutManager = LinearLayoutManager(mContext)
        lchat_ry.adapter = adapter
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        startBgAnim(savedInstanceState)
    }

    override fun initData() {
//        adapter.addData(ChatMultiEntry(ChatMultiEntry.CHAT_MINE,ChatEntry()))
//        adapter.addData(ChatMultiEntry(ChatMultiEntry.CHAT_OTHER, ChatEntry()))
//        startTimer()

        data = intent.getSerializableExtra("data") as ArrayList<ChatEntry>?
        if (data != null && data!!.isNotEmpty()) {
            mPresenter.contentData(data!!)
            startTimer()
        } else {
            finish()
        }
    }

    override fun initEvent() {
        lchat_close.setOnClickListener {
            setNextData()
        }
        lchat_edit.addTextChangedListener(mTextWatcher)
        lchat_bg.setOnClickListener { finish() }
        lchat_edit.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                stopTimer()
        }
        lchat_edit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val sendTxt = lchat_edit.text.toString()
                if (!TextUtils.isEmpty(sendTxt)) {
                    var lastTxt = ""
                    if (adapter.data.size > 0) {
                        lastTxt = adapter.data[adapter.data.size - 1].bean.content
                    }
//                    mPresenter.sendLittleTxt(activity, sendTxt, currentChatId, lastTxt, curentImg)
                    mPresenter.sendLittleTxt(activity, sendTxt, lastTxt)
                    //设置友盟自定义事件统计
                    val map = HashMap<String, Any>()
                    map.put("action_type", "little_chat_send_txt")
                    map.put("user_info","${application.infoBean!!.uid} ${application.infoBean!!.nick}")
                    MobclickAgent.onEventObject(activity, UmSetting.EVENT_LITTLE_CHAT_SEND_TXT, map)
                    lchat_edit.setText("")
                    lchat_edit.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
                }
            }
            false
        }

    }


    override fun setImg(img: String) {
        lchat_video_play.visibility = View.GONE
        if (img.contains(API.PIC_PREFIX)) {
            Glide.with(mContext).load(img).into(lchat_img)
        } else {
            Glide.with(mContext).load(API.PIC_PREFIX + img).into(lchat_img)
        }
    }

    override fun setVideo(src: String, dura: String, poster: String) {
        lchat_video_play.visibility = View.VISIBLE
        if (poster.contains(API.PIC_PREFIX)) {
            Glide.with(mContext).load(poster).into(lchat_img)
        } else {
            Glide.with(mContext).load(API.PIC_PREFIX + poster).into(lchat_img)
        }
        lchat_img.setOnClickListener { v ->
            PrevUtils.onVideoItemClick(mContext, v, src, poster)
            stopTimer()
        }
    }


    override fun addItem(data: ChatMultiEntry<ChatEntry>) {
        if (adapter.data.size == 2) {
            adapter.remove(0)
        }
        adapter.addData(data)
    }

    override fun setMulMediaVisiable(b: Boolean) {
        if (b)
            lchat_mul.visibility = View.VISIBLE
        else
            lchat_mul.visibility = View.GONE
    }

    override fun setEmptyMedia() {
        lchat_img_gp.visibility=View.GONE
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun newMsg(entry: GchatParcelEntry) {
        if (data == null) {
            data = ArrayList()
        }
        data!!.add(entry.entry)
    }

    override fun expressionClick(str: String?) {
        lchat_keyboard.input(lchat_edit, str)
    }

    override fun expressionaddRecent(str: String?) {
        lchat_keyboard.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        lchat_keyboard.delete(lchat_edit)
    }

    var countTimer: CountDownTimer? = null
    private fun startTimer() {
        if (countTimer != null) {
            countTimer?.cancel()
        }
        countTimer = object : CountDownTimer(5 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                lchat_time.text = "(${millisUntilFinished / 1000 + 1}s)"
            }

            override fun onFinish() {
                setNextData()
            }
        }
        countTimer?.start()
    }

    private fun stopTimer() {
        if (countTimer != null) {
            countTimer?.cancel()
        }
    }

    override fun setNextData() {
        if (data == null || data!!.isEmpty()) {
            finish()
        } else {
            EventBus.getDefault().post(RemoveEvent(data!![0]))
            data!!.removeAt(0)
            if (data == null || data!!.isEmpty()) {
                finish()
                return
            } else {
                adapter.data.clear()
                adapter.notifyDataSetChanged()
                mPresenter.contentData(data!!)
                if (!lchat_edit.hasFocus()) {
                    startTimer()
                }
            }
        }
    }

    private fun startBgAnim(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) run {
            lchat_bg?.visibility = View.INVISIBLE
            val viewTreeObserver = lchat_bg.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealAnim()
                        lchat_bg?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        } else {
            lchat_bg?.visibility = View.VISIBLE
        }
    }

    private fun revealAnim() {
        var anim = AlphaAnimation(0f, 1f)
        anim.duration = 600
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                lchat_bg?.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
            }

        })
        lchat_bg?.startAnimation(anim)
    }


    override fun onDestroy() {
        lchat_edit.removeTextChangedListener(mTextWatcher)
        stopTimer()
        super.onDestroy()
    }

    override fun finish() {
        if (doFinishAnim) {
            super.finish()
            overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
        } else {
            finishAnim()
        }
    }

    var doFinishAnim = false
    private fun finishAnim() {
        var anim = AlphaAnimation(1f, 0f)
        anim.duration = 100
        lchat_bg?.let {
            it.startAnimation(anim)
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                lchat_keyboard.reset()
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                lchat_bg?.visibility = View.INVISIBLE
                doFinishAnim = true
                finish()
            }
        })
    }
}
