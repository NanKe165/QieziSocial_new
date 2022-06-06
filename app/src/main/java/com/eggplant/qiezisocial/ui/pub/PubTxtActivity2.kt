package com.eggplant.qiezisocial.ui.pub

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.PubTxtContract
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.event.PubTxtEvent
import com.eggplant.qiezisocial.presenter.PubTxtPresenter
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import kotlinx.android.synthetic.main.activity_pub_txt2.*
import org.greenrobot.eventbus.EventBus


/**
 * Created by Administrator on 2022/3/7.
 */

class PubTxtActivity2 : BaseMvpActivity<PubTxtPresenter>(), PubTxtContract.View, ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {
    override fun expressionClick(str: String?) {
        pub2_txt_emojikeyboard.input(pub2_edit, str)
    }

    override fun expressionaddRecent(str: String?) {
        pub2_txt_emojikeyboard.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        pub2_txt_emojikeyboard.delete(pub2_edit)
    }

    override fun showTost(msg: String?) {
        TipsUtil.showToast(mContext,msg!!)
    }

    override fun pubQuestionSuccess(record: BoxEntry) {
        pubQuestionSuccess=true
        var intent = Intent()
        intent.putExtra("pubBox", record)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun initPresenter(): PubTxtPresenter {
        return PubTxtPresenter()
    }


    var bottomMargin = 0
    var height = 0
    var width = 0
    var qs=""
    var pubQuestionSuccess=false
    var needFinish=false
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
                if (pub2_edit.filters != null && pub2_edit.filters[0] is InputFilter.LengthFilter) {
                    var max = (pub2_edit.filters[0] as InputFilter.LengthFilter).max
                    max -= before - 1
                    pub2_edit.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max))
                }
            }
        }
    }
    var imgIndex = -1
    var imageSource = -1
    override fun getLayoutId(): Int {
        return R.layout.activity_pub_txt2
    }

    override fun initView() {
        mPresenter.attachView(this)
        pub2_txt_emojikeyboard.setEmojiContent(pub2_edit)
        imgIndex = intent.getIntExtra("index", -1)
        qs=intent.getStringExtra("question")
        if (qs!=null&&qs.isNotEmpty())
            pub2_edit.setHint(qs)
        setImageRes()
        startAnim()
    }

    private fun setImageRes() {

        when (imgIndex) {
            0 -> {
                imageSource = R.drawable.icon_home_dector1
            }
            1 -> {
                imageSource = R.drawable.icon_home_dector2
            }
            2 -> {
                imageSource = R.drawable.icon_home_dector3
            }
            3 -> {
                imageSource = R.drawable.icon_home_dector4

            }
            4 -> {
                imageSource = R.drawable.icon_home_dector5

            }
            5 -> {
                imageSource = R.drawable.icon_home_dector6
            }
            6 -> {
                imageSource = R.drawable.icon_home_dector7

            }
            7-> {
                imageSource = R.drawable.icon_home_dector8
            }
            8 -> {
                imageSource = R.drawable.icon_home_dector9
            }

        }
        if (imageSource != -1) {
            pub2_decorate2.setImageResource(imageSource)
        }
    }


    override fun initData() {

    }

    override fun initEvent() {
        pub2_edit.addTextChangedListener(mTextWatcher)
        pub2_shadow.setOnClickListener {
            needFinish=true
            finishAnim()
        }

        pub2_edit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val sendTxt = pub2_edit.text.toString()
                if (!TextUtils.isEmpty(sendTxt)) {
                    pubQuestion(sendTxt, pub2_edit.getFontFormat())
                    finishAnim()

                }
            }
            false
        }
        pub2_nextfont.setOnClickListener {
            pub2_edit.nextFontFormat()
        }

    }

    private fun pubQuestion(txt: String, font: String) {
        var scenes = ""
        var sid = ""
        if (application.filterData != null) {
            scenes = application.filterData!!.goal
            sid = application.filterData!!.sid
        }
        mPresenter.pubQuestion(activity, scenes, "", txt, font, arrayListOf(), "no", 1, 1, sid)
    }

    private fun startAnim() {
        val pubParams = pub2_pubgp.layoutParams as FrameLayout.LayoutParams
        bottomMargin = ScreenUtil.dip2px(mContext, 20)
//        height = pub2_pubgp.height
//        width = pub2_pubgp.width
        if (height == 0) {
            height = ScreenUtil.dip2px(mContext, 60)
        }
        if (width == 0) {
            width = ScreenUtil.getDisplayWidthPixels(mContext) - ScreenUtil.dip2px(mContext, 30)
        }
        val fbtm = ScreenUtil.getDisplayHeightPixels(mContext) / 2
        val startHeight = ScreenUtil.dip2px(mContext, 60)
        val startWidht = ScreenUtil.dip2px(mContext, 210)
        val translateAnim = ValueAnimator.ofFloat(0f, 1.0f)
        translateAnim.addUpdateListener {
            val animatedValue = it.animatedValue as Float
//            val params = FrameLayout.LayoutParams((startWidht + (width - startWidht) * animatedValue).toInt(), (startHeight + (height - startHeight) * animatedValue).toInt())
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (startHeight + (height - startHeight) * animatedValue).toInt())
            params.setMargins(pubParams.leftMargin, 0, pubParams.rightMargin, 0)
            params.gravity = Gravity.BOTTOM
            pub2_pubgp.layoutParams = params
            val gpParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            gpParams.setMargins(0, 0, 0, (bottomMargin + (fbtm - bottomMargin) * animatedValue).toInt())
            gpParams.gravity = Gravity.BOTTOM
            pub2_decorate_gp.layoutParams = gpParams
        }
        val alphaAnim = ObjectAnimator.ofFloat(pub2_shadow, "alpha", 0.0f, 1.0f)
        val animatorSet = AnimatorSet()
        animatorSet.duration = 600
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                pub2_edit.isFocusable = true
                pub2_edit.isFocusableInTouchMode = true
                pub2_edit.requestFocus()
                showOrHide(mContext)
                pub2_nextfont.setImageResource(R.mipmap.icon_pub2_nextstyle)
                pub2_nextfont.visibility=View.VISIBLE

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                EventBus.getDefault().post(PubTxtEvent(true))
                pub2_edit.isFocusable = false
            }
        })


        var dectorAnim: ValueAnimator? = null
        if (imageSource != -1) {
            var scale: Float = 1.3f / 2.0f
            if (imageSource == R.drawable.icon_home_dector7) {
                scale = 0.9f / 2.0f
            }
            pub2_decorate2.post {
                var startBm = ScreenUtil.dip2px(mContext, 55)
                var finishBm = ScreenUtil.dip2px(mContext, 55)

                val params1 = FrameLayout.LayoutParams((scale * pub2_decorate2.width).toInt(), (scale * pub2_decorate2.height).toInt())
                params1.setMargins(0, 0, 0, startBm)
                params1.gravity = Gravity.CENTER_HORIZONTAL
                pub2_decorate.layoutParams = params1
                pub2_decorate.setImageResource(imageSource)

                dectorAnim = ValueAnimator.ofFloat(0.0f, 1.0f)

                dectorAnim!!.addUpdateListener {
                    val animatedValue = it.animatedValue as Float
                    val w = (animatedValue * (pub2_decorate2.width - (scale * pub2_decorate2.width)))
//                    val params = FrameLayout.LayoutParams((scale * pub2_decorate2.width + (animatedValue * (pub2_decorate2.width - (scale * pub2_decorate2.width)))).toInt()
//                            , (scale * pub2_decorate2.height + (animatedValue * (pub2_decorate2.height - (scale * pub2_decorate2.height)))).toInt())
                    val params=FrameLayout.LayoutParams(pub2_decorate2.width,pub2_decorate2.height)
                    params.setMargins(0, 0, 0, (startBm + (animatedValue * (finishBm - startBm))).toInt())
                    params.gravity = Gravity.CENTER_HORIZONTAL
                    pub2_decorate.layoutParams = params
                }
                animatorSet.playTogether(translateAnim, alphaAnim, dectorAnim)
                animatorSet.start()
            }
        } else {
            animatorSet.playTogether(translateAnim, alphaAnim)
            animatorSet.start()
        }


    }

    var doFinishAnim = false
    private fun finishAnim() {
        val pubParams = pub2_pubgp.layoutParams as FrameLayout.LayoutParams
        height = pub2_pubgp.height
        width = pub2_pubgp.width
        if (height == 0) {
            height = ScreenUtil.dip2px(mContext, 60)
        }
        if (width == 0) {
            width = ScreenUtil.getDisplayWidthPixels(mContext) - ScreenUtil.dip2px(mContext, 30)
        }
        val fbtm = ScreenUtil.getDisplayHeightPixels(mContext) / 2
        val startHeight = ScreenUtil.dip2px(mContext, 60)
        val startWidht = ScreenUtil.dip2px(mContext, 210)
        val translateAnim = ValueAnimator.ofFloat(1.0f, 0f)
        translateAnim.addUpdateListener {
            val animatedValue = it.animatedValue as Float
//            val params = FrameLayout.LayoutParams((startWidht + (width - startWidht) * animatedValue).toInt(), (startHeight + (height - startHeight) * animatedValue).toInt())
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (startHeight + (height - startHeight) * animatedValue).toInt())
            params.setMargins(pubParams.leftMargin, 0, pubParams.rightMargin, 0)
            params.gravity = Gravity.BOTTOM
            pub2_pubgp.layoutParams = params

            val gpParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            gpParams.setMargins(0, 0, 0, (bottomMargin + (fbtm - bottomMargin) * animatedValue).toInt())
            gpParams.gravity = Gravity.BOTTOM
            pub2_decorate_gp.layoutParams = gpParams
        }
        val alphaAnim = ObjectAnimator.ofFloat(pub2_shadow, "alpha", 1.0f, 0f)
        val animatorSet = AnimatorSet()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                doFinishAnim = true
                EventBus.getDefault().post(PubTxtEvent(false))
                finish()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                pub2_edit.setText("")
                pub2_txt_emojikeyboard.reset()
                pub2_edit.isFocusable = false
                pub2_nextfont.visibility=View.GONE
                pub2_nextfont.setImageResource(R.mipmap.home_pub_icon2)

            }
        })
        animatorSet.duration = 600
        var dectorAnim: ValueAnimator? = null
        if (imageSource != -1) {
            var scale: Float = 1.3f / 2.0f
            if (imageSource == R.drawable.icon_home_dector7) {
                scale = 0.9f / 2.0f
            }
            pub2_decorate2.post {
                var startBm = ScreenUtil.dip2px(mContext, 55)
                var finishBm = ScreenUtil.dip2px(mContext, 55)
                dectorAnim = ValueAnimator.ofFloat(1.0f, 0.0f)
                dectorAnim!!.addUpdateListener {
                    val animatedValue = it.animatedValue as Float
                    val w = (animatedValue * (pub2_decorate2.width - (scale * pub2_decorate2.width)))
//                    val params = FrameLayout.LayoutParams((scale * pub2_decorate2.width + (animatedValue * (pub2_decorate2.width - (scale * pub2_decorate2.width)))).toInt()
//                            , (scale * pub2_decorate2.height + (animatedValue * (pub2_decorate2.height - (scale * pub2_decorate2.height)))).toInt())
                    val params=FrameLayout.LayoutParams(pub2_decorate2.width,pub2_decorate2.height)
                    params.setMargins(0, 0, 0, (startBm + (animatedValue * (finishBm - startBm))).toInt())
                    params.gravity = Gravity.CENTER_HORIZONTAL
                    pub2_decorate.layoutParams = params
                }
                animatorSet.playTogether(translateAnim, alphaAnim, dectorAnim)
                animatorSet.start()
            }
        } else {
            animatorSet.playTogether(translateAnim, alphaAnim)
            animatorSet.start()
        }
    }

    override fun finish() {
        if (doFinishAnim&&(pubQuestionSuccess||needFinish)) {
            super.finish()
            overridePendingTransition(0, 0)
        } else {

//            finishAnim()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        needFinish=true
        finishAnim()
    }

    //如果输入法在窗口上已经显示，则隐藏，反之则显示
    fun showOrHide(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onDestroy() {
        pub2_edit.removeTextChangedListener(mTextWatcher)
        super.onDestroy()
    }
}
