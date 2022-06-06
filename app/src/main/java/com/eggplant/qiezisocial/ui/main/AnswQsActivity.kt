package com.eggplant.qiezisocial.ui.main

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseWebSocketActivity
import com.eggplant.qiezisocial.contract.AnswQsContract
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.MediaEntry
import com.eggplant.qiezisocial.event.AnswQsEvent
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.presenter.AnswQsPresenter
import com.eggplant.qiezisocial.socket.AbsBaseWebSocketService
import com.eggplant.qiezisocial.socket.WebSocketService
import com.eggplant.qiezisocial.socket.event.CommonResponse
import com.eggplant.qiezisocial.socket.event.WebSocketSendDataErrorEvent
import com.eggplant.qiezisocial.utils.FileUtils
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.utils.mp3.Mp3RecorderUtils
import com.eggplant.qiezisocial.widget.AudioPlayView
import com.eggplant.qiezisocial.widget.flow.BezierEvaluator
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xiao.nicevideoplayer.NiceVideoPlayerManager
import com.xiao.nicevideoplayer.TxVideoPlayerController
import kotlinx.android.synthetic.main.activity_answqs.*
import org.greenrobot.eventbus.EventBus
import sj.keyboard.utils.EmoticonsKeyboardUtils

/**
 * Created by Administrator on 2020/4/24.
 * 回复问题
 */

class AnswQsActivity : BaseWebSocketActivity<AnswQsPresenter>(), AnswQsContract.View, ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {
    override val webSocketClass: Class<out AbsBaseWebSocketService>
        get() = WebSocketService::class.java

    override fun onCommonResponse(response: CommonResponse) {

    }

    override fun onErrorResponse(response: WebSocketSendDataErrorEvent) {

    }

    private var bean: BoxEntry? = null
    private var mainBean: MainInfoBean? = null
    private var lintener: TextWatcher? = null
    var adPath: String? = null
    var dura: Int = 0
    var startY = 0
    var finalY = 0F
    var msgX = 0
    var msgY = 0
    var finishAnimStart = false
    var replySuccess = false


    override fun initPresenter(): AnswQsPresenter {
        return AnswQsPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_answqs
    }

    private lateinit var controller: TxVideoPlayerController
    override fun initView() {
        mPresenter.attachView(this)
        answqs_title.movementMethod = ScrollingMovementMethod.getInstance()
        controller = TxVideoPlayerController(activity)
        answqs_videoplayer.controller = controller
        answqs_cent.isClickable = true
    }

//    private val boxEntry: BoxEntry?
//        get() {
//            var bean = intent.getSerializableExtra("bean") as BoxEntry?
//            return bean
//        }

    override fun initData() {
        bean = intent.getSerializableExtra("bean") as BoxEntry?
        mainBean = intent.getSerializableExtra("mainBean") as MainInfoBean?
        startY = intent.getIntExtra("startY", 0)
        msgX = intent.getIntExtra("msgX", 0)
        msgY = intent.getIntExtra("msgY", 0)
        finalY = resources!!.getDimension(R.dimen.qb_px_193)
        if (bean == null || mainBean == null) {
            finish()
            return
        }
        bean?.let {
            mPresenter.initData(it)
            mPresenter.setRead(it.id)
        }
        if (startY != 0) {
            answ_qs_rootview.post {
                var params = answ_qs_rootview.layoutParams as FrameLayout.LayoutParams
                Log.i("answQs", "startY:$startY  ${answ_qs_rootview.height}")
                params.setMargins(params.leftMargin, startY, params.rightMargin, 0)
                answ_qs_rootview.layoutParams = params
                startTranAinm()
            }

        } else {
            answ_qs_rootview.visibility = View.VISIBLE
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
//        startBgAnim(savedInstanceState)
        answqs_emojikeyboard.setEmojiContent(answqs_edit)
    }


    override fun initEvent() {
        answqs_rootview.setOnClickListener {
            if (!finishAnimStart)
                finish()

        }
        answ_qs_head.setOnClickListener {
            startActivity(Intent(mContext, OtherSpaceActivity::class.java).putExtra("bean", bean!!.userinfor))
            overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
        }
        answqs_send.setOnClickListener {
            if (model == 0) {
                var reply = answqs_edit.text.toString()
                if (TextUtils.isEmpty(reply)) {
                    TipsUtil.showToast(mContext, "请先说点什么吧!")
                } else {
//                    bean?.let { mPresenter.reply(activity, it, reply, "", 0) }
                    mPresenter.send(activity, bean!!, mainBean!!, reply, "", 0)
                }
            } else {
                if (adPath == null) {
                    TipsUtil.showToast(mContext, "请先说点什么吧!")
                } else {
//                    bean?.let { mPresenter.reply(activity, it, "", adPath!!, dura) }
                    mPresenter.send(activity, bean!!, mainBean!!, "", adPath!!, dura)
                }
            }
        }
        answ_qs_send.setOnClickListener {
            var reply = answqs_edit.text.toString()
            if (TextUtils.isEmpty(reply)) {
                TipsUtil.showToast(mContext, "请先说点什么吧!")
            } else {
                mPresenter.send(activity, bean!!, mainBean!!, reply, "", 0)
            }
        }
        answ_qs_emoji1.setOnClickListener {
            answqs_edit.text.append("[qzxs4]")
        }
        answ_qs_emoji2.setOnClickListener {
            answqs_edit.text.append("[qzxs49]")
        }
        answ_qs_emoji3.setOnClickListener {
            answqs_edit.text.append("[qzxs1]")
        }
        lintener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    sendClickAble(true)
                } else {
                    sendClickAble(false)
                }
            }

        }
        answqs_edit.addTextChangedListener(lintener)

        answqs_swich.setOnClickListener {
            switchModel()
        }
        answqs_audioPlayr.setAudioPlayListener(object : AudioPlayView.AudioPlayListener {
            override fun releaseVoice() {
                answqs_videoplayer.releasePlayer()
                FileUtils.deleteFile(adPath)
                adPath = null
                sendClickAble(false)
            }

            override fun review() {
                answqs_videoplayer.release()
                adPath?.let {
                    answqs_videoplayer.setUp(it, null)
                    answqs_videoplayer.start()
                }

            }

            override fun stopVoice() {
                answqs_videoplayer.release()
            }

        })
        answqs_audioPlayr.recordTv.setOnTouchListener(object : View.OnTouchListener {
            var canStartRecord = true
            @TargetApi(Build.VERSION_CODES.O)
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        canStartRecord = true
                        RxPermissions(activity)
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                                .subscribe { b ->
                                    if (b) {
                                        answqs_audioPlayr.postDelayed({
                                            if (canStartRecord) {
                                                mPresenter.startRecord(activity)
                                                if (answqs_audioPlayr != null)
                                                    answqs_audioPlayr.statRecordAinm()
                                            }
                                        }, 300)

                                    }
                                }
                    }
                    MotionEvent.ACTION_UP -> {
                        canStartRecord = false
                        Mp3RecorderUtils.stopRecording()
                        if (answqs_audioPlayr != null)
                            answqs_audioPlayr.stopRecordAnim()
                    }
                    MotionEvent.ACTION_MOVE -> {

                    }
                }
                return true
            }
        })
        controller.setOnCompleteListener(object : TxVideoPlayerController.OnCompleteListener {
            override fun onComplete() {
                answqs_audioPlayr.stopPlayingAnim()
            }

            override fun onReset() {

            }

            override fun onPrepared() {

            }
        })
    }

    private fun sendClickAble(b: Boolean) {
//        if (b) {
//            answqs_send.isClickable = true
//            answqs_send.background = ContextCompat.getDrawable(mContext, R.drawable.tv_yellow_bg2)
//            answqs_send.setTextColor(ContextCompat.getColor(mContext, R.color.tv_black))
//        } else {
//            answqs_send.isClickable = false
//            answqs_send.background = ContextCompat.getDrawable(mContext, R.drawable.tv_gray_bg)
//            answqs_send.setTextColor(ContextCompat.getColor(mContext, R.color.tv_gray6))
//        }
        if (b) {
            answ_qs_send.isClickable = true
            answ_qs_send.background = ContextCompat.getDrawable(mContext, R.drawable.tv_yellow_bg2)
            answ_qs_send.setTextColor(ContextCompat.getColor(mContext, R.color.tv_black))
        } else {
            answ_qs_send.isClickable = false
            answ_qs_send.background = ContextCompat.getDrawable(mContext, R.drawable.tv_yellow_bg)
            answ_qs_send.setTextColor(ContextCompat.getColor(mContext, R.color.tv_gray2))
        }
    }


    override fun setHead(s: String) {
//        Glide.with(mContext).load(s).into(answqs_head)
        Glide.with(mContext).load(s).into(answ_qs_head)
        Glide.with(mContext).load(s).into(answ_qs_star)
    }

    override fun setSex(sex: String?) {
//        if (!TextUtils.equals(sex, "男")) {
//            answqs_sex.setImageResource(R.mipmap.sex_boy)
//        } else {
//            answqs_sex.setImageResource(R.mipmap.sex_girl)
//        }
        if (TextUtils.equals(sex, "男")) {
            answ_qs_sex.setImageResource(R.mipmap.sex_boy)
            answqs_edit.hint = "他在等你回复哦~"
        } else if (TextUtils.equals(sex, "女")) {
            answ_qs_sex.setImageResource(R.mipmap.sex_girl)
            answqs_edit.hint = "她在等你回复哦~"
        } else {
            answqs_edit.hint = "Ta在等你回复哦~"
        }
    }

    override fun setNick(nick: String?) {
//        nick?.let { answqs_nick.text = it }
        nick?.let { answ_qs_name.text = it }
    }

    override fun setTitle(text: String?) {
//        text.let { answqs_title.text = text }
        text.let { answ_qs_content.text = text }
    }

    override fun setFont(font: String?) {
//        answqs_title.setFontFormat(font)
        answ_qs_content.setFontFormat(font)
    }

    override fun setState(mood: String) {
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
        answ_qs_state.text = mood
        answ_qs_state.setTextColor(textColor)
        answ_qs_state.background = textbg
        answ_qs_state.setCompoundDrawablesWithIntrinsicBounds(statedraw, 0, 0, 0)
    }

    var hasMedia = false
    override fun setMedia(media: List<MediaEntry>?) {
        media?.let {

            var finalType = "pic"
            it.forEach {
                hasMedia = true
                if (TextUtils.equals(it.type, "video")) {
                    finalType = it.type
                }
            }
            if (TextUtils.equals(finalType, "pic")) {
                answqs_mediaview.setImages(it)
            } else if (TextUtils.equals(finalType, "video")) {
                answqs_mediaview.setVideo(it)
            }
        }

    }

    override fun setHint(s: String) {
//        answqs_edit.hint = "好的回复能获得对方的好感哦"
    }

    override fun showTost(message: String?) {
        message?.let {
            TipsUtil.showToast(mContext, it)
        }
    }


    override fun setAudioDura(dura: Int) {
        answqs_audioPlayr.setDuraTv(dura)
        this.dura = dura
    }

    override fun setAudioPath(filePath: String) {
        adPath = filePath
        sendClickAble(true)
    }

    var model = 0
    private fun switchModel() {
        if (model == 0) {
            answqs_swich.setImageResource(R.mipmap.verify_chat_voice)
            answqs_edit.visibility = View.GONE
            answqs_audioPlayr.visibility = View.VISIBLE
            model = 1
            EmoticonsKeyboardUtils.closeSoftKeyboard(answqs_edit)
            if (!TextUtils.isEmpty(adPath)) {
                sendClickAble(true)
            } else {
                sendClickAble(false)
            }
        } else {
            answqs_swich.setImageResource(R.mipmap.verify_chat_keyboard)
            answqs_edit.visibility = View.VISIBLE
            answqs_audioPlayr.visibility = View.GONE
            model = 0
            EmoticonsKeyboardUtils.openSoftKeyboard(answqs_edit)
            var txt = answqs_edit.text.toString().trimEnd()
            if (!TextUtils.isEmpty(txt)) {
                sendClickAble(true)
            } else {
                sendClickAble(false)
            }
        }

    }

    private fun startBgAnim(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) run {

            val viewTreeObserver = answqs_rootview.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealAnim()
                        answqs_rootview.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }

    }

    private fun revealAnim() {
        var anim = ObjectAnimator.ofFloat(answqs_emojikeyboard, "translationY", ScreenUtil.getDisplayHeightPixels(mContext).toFloat(), 0f)
        anim.duration = 450
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
//                EmoticonsKeyboardUtils.openSoftKeyboard(answqs_edit)
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        anim.start()
    }

    override fun expressionClick(str: String?) {
        answqs_emojikeyboard.input(answqs_edit, str)
    }

    override fun expressionaddRecent(str: String?) {
        answqs_emojikeyboard.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        answqs_emojikeyboard.delete(answqs_edit)
    }

    override fun onReplySuccess(boxEntry: BoxEntry?) {
        var replyInt = Intent()
        replyInt.putExtra("answBean", boxEntry)
        setResult(Activity.RESULT_OK, replyInt)
//        replySuccess = true
        finish()
    }

    override fun finish() {
        if (doFinishAnim) {
            super.finish()
//            overridePendingTransition(R.anim.close_enter2, R.anim.close_exit2)
            overridePendingTransition(0, 0)
        } else {

            startFinishAnim()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            val isConsum = answqs_emojikeyboard.dispatchKeyEventInFullScreen(event)
            return if (isConsum) isConsum else super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }

    var doFinishAnim = false
    private fun finishAnim() {
        var anim = ObjectAnimator.ofFloat(answqs_emojikeyboard, "translationY", 0f, ScreenUtil.getDisplayHeightPixels(mContext).toFloat())
        anim.duration = 450
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                doFinishAnim = true
                finish()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        anim.start()
    }

    private fun startTranAinm() {
        var setAinm = AnimatorSet()
        var anim = ObjectAnimator.ofFloat(answ_qs_rootview, "translationY", finalY - startY)
        var gpMineHeight= resources!!.getDimension(R.dimen.qb_px_93).toInt()
        var gpHeight = resources!!.getDimension(R.dimen.qb_px_115).toInt() + resources!!.getDimension(R.dimen.qb_px_93).toInt()
        var bottomHeight = resources!!.getDimension(R.dimen.qb_px_115).toInt()
        if (hasMedia) {
            gpHeight = resources!!.getDimension(R.dimen.qb_px_95).toInt() + resources!!.getDimension(R.dimen.qb_px_93).toInt() + resources!!.getDimension(R.dimen.qb_px_42).toInt()
            bottomHeight = resources!!.getDimension(R.dimen.qb_px_95).toInt()
        }

        var heightAnim = ValueAnimator.ofInt(0, bottomHeight)
        var bottomParams = answ_qs_bottom.layoutParams as LinearLayout.LayoutParams
        heightAnim.addUpdateListener { anim ->
            val height = anim.animatedValue as Int
            bottomParams.height = height
            answ_qs_bottom.layoutParams = bottomParams

        }

        var gpHeightAnim= ValueAnimator.ofInt(gpMineHeight, gpHeight)
        var gpParams = answ_qs_rootview.layoutParams as FrameLayout.LayoutParams
        gpHeightAnim.addUpdateListener { anim ->
            val height = anim.animatedValue as Int
            gpParams.height = height
            answ_qs_rootview.layoutParams = gpParams

        }


        val gpAlpha=ObjectAnimator.ofFloat(answqs_rootview,"alpha",0.0f,1.0f)
        setAinm.duration = 450
        setAinm.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
//                EmoticonsKeyboardUtils.openSoftKeyboard(answqs_edit)
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                answ_qs_rootview.visibility = View.VISIBLE
                if (hasMedia) {
                    answqs_mediaview.visibility = View.VISIBLE
                }
                EventBus.getDefault().post(AnswQsEvent(true))
            }
        })

        if (hasMedia) {
            val mediaHeight = resources!!.getDimension(R.dimen.qb_px_42).toInt()
            val topGpminHeight = resources!!.getDimension(R.dimen.qb_px_93).toInt()
            val topGpmaxHeight = resources!!.getDimension(R.dimen.qb_px_140).toInt()

            val mediaAnim = ValueAnimator.ofInt(0, mediaHeight)
            val topAnim = ValueAnimator.ofInt(topGpminHeight, topGpmaxHeight)

            mediaAnim.addUpdateListener { anim ->
                val height = anim.animatedValue as Int
                var params = answqs_mediaview.layoutParams
                params.height = height
                answqs_mediaview.layoutParams = params
            }
            topAnim.addUpdateListener { anim ->
                val height = anim.animatedValue as Int
                var params = answ_qs_top_gp.layoutParams as LinearLayout.LayoutParams
                params.height = height
                answ_qs_top_gp.layoutParams = params
            }
            setAinm.playTogether(anim, heightAnim, mediaAnim, topAnim,gpHeightAnim,gpAlpha)
        } else {
            setAinm.playTogether(anim, heightAnim,gpHeightAnim,gpAlpha)
        }


        setAinm.start()
    }

    private fun startFinishAnim() {
        var setAinm = AnimatorSet()
        var anim = ObjectAnimator.ofFloat(answ_qs_rootview, "translationY", finalY - startY, 0F)

        var gpMineHeight= resources!!.getDimension(R.dimen.qb_px_93).toInt()
        var gpHeight = resources!!.getDimension(R.dimen.qb_px_115).toInt() + resources!!.getDimension(R.dimen.qb_px_93).toInt()

        var bottomHeight = resources!!.getDimension(R.dimen.qb_px_115).toInt()
        if (hasMedia) {
            gpHeight = resources!!.getDimension(R.dimen.qb_px_95).toInt() + resources!!.getDimension(R.dimen.qb_px_93).toInt() + resources!!.getDimension(R.dimen.qb_px_42).toInt()
            bottomHeight = resources!!.getDimension(R.dimen.qb_px_95).toInt()
        }
        var gpHeightAnim= ValueAnimator.ofInt(gpHeight, gpMineHeight)
        var gpParams = answ_qs_rootview.layoutParams as FrameLayout.LayoutParams
        gpHeightAnim.addUpdateListener { anim ->
            val height = anim.animatedValue as Int
            gpParams.height = height
            answ_qs_rootview.layoutParams = gpParams

        }


        var heightAnim = ValueAnimator.ofInt(bottomHeight, 0)
        var bottomParams = answ_qs_bottom.layoutParams as LinearLayout.LayoutParams
        heightAnim.addUpdateListener { anim ->
            val height = anim.animatedValue as Int
            bottomParams.height = height
            answ_qs_bottom.layoutParams = bottomParams
        }

        val evaluator = BezierEvaluator(PointF(answ_qs_star.x / 2, answ_qs_star.y / 2), PointF(answ_qs_star.x / 3, answ_qs_star.y / 3))
        val translateAnim = ValueAnimator.ofObject(evaluator, PointF(answ_qs_star.x, answ_qs_star.y), PointF(msgX.toFloat(), msgY.toFloat()))
        translateAnim.addUpdateListener { animation ->
            val pointF = animation.animatedValue as PointF
            answ_qs_star.x = pointF.x
            answ_qs_star.y = pointF.y
        }
        val gpAlpha=ObjectAnimator.ofFloat(answqs_rootview,"alpha",1.0f,0f)
        setAinm.duration = 450
        setAinm.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
//                EmoticonsKeyboardUtils.openSoftKeyboard(answqs_edit)
                EventBus.getDefault().post(AnswQsEvent(false, replySuccess))
                doFinishAnim = true
                answ_qs_rootview.visibility = View.GONE
                answqs_mediaview.visibility = View.GONE
                finish()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                answqs_emojikeyboard.reset()
                finishAnimStart = true
                if (replySuccess) {
                    answ_qs_star.visibility = View.VISIBLE
                }
            }
        })
//        if (replySuccess) {
//            setAinm.duration = 650
//            setAinm.playTogether(anim, heightAnim, translateAnim)
//        } else {
//            setAinm.playTogether(anim, heightAnim)
//        }

        if (hasMedia) {
            val mediaHeight = resources!!.getDimension(R.dimen.qb_px_42).toInt()
            val topGpminHeight = resources!!.getDimension(R.dimen.qb_px_93).toInt()
            val topGpmaxHeight = resources!!.getDimension(R.dimen.qb_px_140).toInt()

            val mediaAnim = ValueAnimator.ofInt(mediaHeight, 0)
            val topAnim = ValueAnimator.ofInt(topGpmaxHeight, topGpminHeight)

            mediaAnim.addUpdateListener { anim ->
                val height = anim.animatedValue as Int
                var params = answqs_mediaview.layoutParams
                params.height = height
                answqs_mediaview.layoutParams = params
            }
            topAnim.addUpdateListener { anim ->
                val height = anim.animatedValue as Int
                var params = answ_qs_top_gp.layoutParams as LinearLayout.LayoutParams
                params.height = height
                answ_qs_top_gp.layoutParams = params
            }
            setAinm.playTogether(anim, heightAnim, mediaAnim, topAnim,gpHeightAnim,gpAlpha)
        } else {
            setAinm.playTogether(anim, heightAnim,gpHeightAnim,gpAlpha)
        }

        setAinm.start()
    }

    override fun onStop() {
        super.onStop()
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer()
    }

    override fun onDestroy() {
        answqs_edit.removeTextChangedListener(lintener)
        super.onDestroy()
    }
}
