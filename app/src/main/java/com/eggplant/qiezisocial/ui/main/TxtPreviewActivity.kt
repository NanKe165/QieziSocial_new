package com.eggplant.qiezisocial.ui.main

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.event.SocketMsgEvent
import com.eggplant.qiezisocial.model.ChatModel
import com.eggplant.qiezisocial.model.UmSetting
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_preview_txt.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * Created by Administrator on 2021/7/2.
 */

class TxtPreviewActivity : BaseActivity(), ViewTreeObserver.OnPreDrawListener, ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {
    override fun expressionClick(str: String?) {

    }

    override fun expressionaddRecent(str: String?) {

    }

    override fun expressionDeleteClick(v: View?) {

    }

    private val mTextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable) {

        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//            Log.i("emojitest", "beforeTextChanged s=$s  start:$start  after:$after  count:$count")
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//            Log.i("emojitest", "onTextChanged s=$s  start:$start  befor:$before  count:$count")
            if (before >= 7 && count == 0) {
                if (txt_prev_edit.filters != null && txt_prev_edit.filters[0] is InputFilter.LengthFilter) {
                    var max = (txt_prev_edit.filters[0] as InputFilter.LengthFilter).max
                    max -= before - 1
//                    Log.i("emojitest", "onTextChanged max :$max")
                    txt_prev_edit.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max))
                }
            }
        }
    }

    val ANIMATE_DURATION = 300
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var vHeight = 0
    private var vWidth = 0
    private var vX = 0
    private var vY = 0
    var gpHeight = 0
    var gpWidth = 0
    var uid = 0L
    var txt = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_preview_txt
    }

    override fun initView() {
        screenWidth = ScreenUtil.getDisplayWidthPixels(mContext)
        screenHeight = ScreenUtil.getDisplayHeightPixels(mContext)
        vWidth = intent.getIntExtra("w", 0)
        vHeight = intent.getIntExtra("h", 0)
        vX = intent.getIntExtra("viewX", 0)
        vY = intent.getIntExtra("viewY", 0)
        uid = intent.getLongExtra("uid", 0)
        val p = intent.getIntExtra("p", 0)
        setBgColor(p)
        txt = intent.getStringExtra("txt")
        txt_prev_content.text = txt

        Log.i("txtPre", "vW:$vWidth  vH:$vHeight  vX:$vX  vY:$vY")
        txt_prev_keyboard.setEmojiContent(txt_prev_edit)
        txt_prev_rootView.viewTreeObserver.addOnPreDrawListener(this)
    }

    private fun setBgColor(p: Int) {
        when (p) {
            0 -> {
                txt_prev_bg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gchat_label4))
            }
            1 -> {
                txt_prev_bg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gchat_label3))
            }
            2 -> {
                txt_prev_bg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gchat_label2))
            }
            3 -> {
                txt_prev_bg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_label4))
            }
            4 -> {
                txt_prev_bg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gchat_label1))
            }
            5 -> {
                txt_prev_bg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_label1))
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        txt_prev_rootView.setOnClickListener {
            if (txt_prev_bottom.visibility == View.VISIBLE) {
                txt_prev_bottom.visibility = View.GONE
                txt_prev_keyboard.reset()
            } else {
                finishActivityAnim()
            }
        }
        txt_prev_bclose.setOnClickListener {
            txt_prev_bottom.visibility = View.GONE
            txt_prev_keyboard.reset()
        }
        txt_prev_ltxt.setOnClickListener {
            txt_prev_bottom.visibility = View.VISIBLE
            txt_prev_edit.isFocusable = true
            txt_prev_edit.isFocusableInTouchMode = true
            txt_prev_edit.requestFocus()
//            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            var imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(txt_prev_edit, InputMethodManager.SHOW_IMPLICIT)
        }
        txt_prev_edit.addTextChangedListener(mTextWatcher)
        txt_prev_edit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val sendTxt = txt_prev_edit.text.toString()
                if (!TextUtils.isEmpty(sendTxt)) {
                    val id = application.msgUUID
                    application.msgUUID = "0"
                    val from = application.infoBean!!.uid
                    getMsgId(activity)
                    val littleTxt = getSendLittleMsg(id, from, uid, sendTxt, txt)
                    EventBus.getDefault().post(SocketMsgEvent(littleTxt))
                    //设置友盟自定义事件统计
                    val map = HashMap<String, Any>()
                    map.put("action_type", "little_chat_send_txt")
                    map.put("user_info", "${application.infoBean!!.uid} ${application.infoBean!!.nick}")
                    MobclickAgent.onEventObject(activity, UmSetting.EVENT_LITTLE_CHAT_SEND_TXT, map)
                    txt_prev_edit.setText("")
                    txt_prev_edit.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
                    txt_prev_bottom.visibility = View.GONE
                    txt_prev_keyboard.reset()
                    TipsUtil.showToast(mContext,"消息已发送")
                }
            }
            false
        }

    }

    override fun onPreDraw(): Boolean {
        txt_prev_rootView.viewTreeObserver.removeOnPreDrawListener(this)
        computeViewWidthAndHeight()
        var loca = IntArray(2)
        txt_prev_gp.getLocationOnScreen(loca)
        val valueAnimator = ValueAnimator.ofFloat(0f, 1.0f)
        val vx = vWidth * 1.0f / gpWidth
        val vy = vHeight * 1.0f / gpHeight
        valueAnimator.addUpdateListener { animation ->
            val duration = animation.duration
            val playTime = animation.currentPlayTime
            var fraction = if (duration > 0) playTime.toFloat() / duration else 1f
            if (fraction > 1) {
                fraction = 1f
            }
            txt_prev_gp.translationX = evaluateInt(fraction, vX + vWidth / 2 - txt_prev_gp.measuredWidth / 2, 0).toFloat()
            txt_prev_gp.translationY = evaluateInt(fraction, vY + vHeight / 2 - txt_prev_gp.measuredHeight / 2, 0).toFloat()
            txt_prev_gp.scaleX = evaluateFloat(fraction, vx, 1f)
            txt_prev_gp.scaleY = evaluateFloat(fraction, vy, 1f)
            txt_prev_gp.alpha = fraction
            txt_prev_rootView.setBackgroundColor(evaluateArgb(fraction, Color.TRANSPARENT, ContextCompat.getColor(mContext, R.color.transparent_white2)))
        }
        addIntoListener(valueAnimator)
        valueAnimator.duration = ANIMATE_DURATION.toLong()
        valueAnimator.start()
        return true
    }

    private fun getSendLittleMsg(id: String, from: Int, to: Long, txt: String, lastTxt: String): String {
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
        data.put("extra", lastTxt)
        `object`.put("data", data)
//        Log.i("groupChat", "${`object`}")
        return `object`.toString()
    }

    var model: ChatModel = ChatModel()
    /**
     * 获取一次msg UUID
     */
    fun getMsgId(activity: Activity) {
        model.getId(activity)
    }

    /**
     * activity的退场动画
     */
    fun finishActivityAnim() {
        //
        val valueAnimator = ValueAnimator.ofFloat(0f, 1.0f)
        computeViewWidthAndHeight()
        val vx = vWidth * 1.0f / gpWidth
        val vy = vHeight * 1.0f / gpHeight
        valueAnimator.addUpdateListener { animation ->
            val duration = animation.duration
            val playTime = animation.currentPlayTime
            var fraction = if (duration > 0) playTime.toFloat() / duration else 1f
            if (fraction > 1) {
                fraction = 1f
            }
            txt_prev_gp.translationX = evaluateInt(fraction, 0, vX + vWidth / 2 - txt_prev_gp.measuredWidth / 2).toFloat()
            txt_prev_gp.translationY = evaluateInt(fraction, 0, vY + vHeight / 2 - txt_prev_gp.measuredHeight / 2).toFloat()
            txt_prev_gp.scaleX = evaluateFloat(fraction, 1f, vx)
            txt_prev_gp.scaleY = evaluateFloat(fraction, 1f, vy)
            txt_prev_gp.alpha = 1 - fraction
            txt_prev_rootView.setBackgroundColor(evaluateArgb(fraction, ContextCompat.getColor(mContext, R.color.transparent_white2), Color.TRANSPARENT))
        }
        addOutListener(valueAnimator)
        valueAnimator.duration = ANIMATE_DURATION.toLong()
        valueAnimator.start()

    }

    private fun computeViewWidthAndHeight() {
        val measuredWidth = txt_prev_gp.measuredWidth
        val measuredHeight = txt_prev_gp.measuredHeight
        var h = screenHeight * 1.0f / measuredHeight
        var w = screenWidth * 1.0f / measuredWidth
        if (h > w)
            h = w
        else
            w = h

        // 得出当宽高至少有一个充满的时候图片对应的宽高
        gpHeight = (measuredHeight * h).toInt()
        gpWidth = (measuredWidth * w).toInt()

        Log.i("txtPre", " measuredWidth:$measuredWidth measuredHeight :$measuredHeight gpHeight:$gpHeight  gpWidth:$gpWidth  ")
    }

    /**
     * 进场动画过程监听
     */
    private fun addIntoListener(valueAnimator: ValueAnimator) {
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                txt_prev_rootView.setBackgroundColor(0x0)
            }

            override fun onAnimationEnd(animation: Animator) {
                if (uid != 0L && uid.toInt() != application.infoBean!!.uid)
                    txt_prev_ltxt.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    /**
     * 退场动画过程监听
     */
    private fun addOutListener(valueAnimator: ValueAnimator) {
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                txt_prev_rootView.setBackgroundColor(0x0)
                txt_prev_ltxt.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }


    /**
     * Integer 估值器
     */
    fun evaluateInt(fraction: Float, startValue: Int, endValue: Int): Int {
        val startInt = startValue
        return (startInt + fraction * (endValue - startInt)).toInt()
    }

    /**
     * Float 估值器
     */
    fun evaluateFloat(fraction: Float, startValue: Float, endValue: Float): Float {
        val startFloat = startValue.toFloat()
        return startFloat + fraction * (endValue.toFloat() - startFloat)
    }

    /**
     * Argb 估值器
     */
    fun evaluateArgb(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff

        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff

        return (startA + (fraction * (endA - startA)).toInt() shl 24
                or (startR + (fraction * (endR - startR)).toInt() shl 16//
                )
                or (startG + (fraction * (endG - startG)).toInt() shl 8//
                )
                or startB + (fraction * (endB - startB)).toInt())
    }

    override fun onBackPressed() {
        finishActivityAnim()
    }

    override fun onDestroy() {
        txt_prev_edit.removeTextChangedListener(mTextWatcher)
        super.onDestroy()
    }
}
