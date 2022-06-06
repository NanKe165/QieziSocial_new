package com.eggplant.qiezisocial.widget.textview

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.text.*
import android.text.style.AlignmentSpan
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan.ALIGN_BASELINE
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.widget.QzTextView

/**
 * Created by Administrator on 2021/9/8.
 */

class ExpandableTextView : QzTextView {
    @Volatile internal var animating = false
    internal var isClosed = false
    private var mMaxLines = DEFAULT_MAX_LINE
    /** TextView可展示宽度，包含paddingLeft和paddingRight  */
    private var initWidth = 0
    /** 原始文本  */
    private var originalText: CharSequence? = null

    private var mOpenSpannableStr: SpannableStringBuilder? = null
    private var mCloseSpannableStr: SpannableStringBuilder? = null

    private var hasAnimation = false
    private var mOpenAnim: Animation? = null
    private var mCloseAnim: Animation? = null
    private var mOpenHeight: Int = 0
    private var mCLoseHeight: Int = 0
    private var mExpandable: Boolean = false
    private var mCloseInNewLine: Boolean = false
    private var mOpenSuffixSpan: SpannableString? = null
    private var mCloseSuffixSpan: SpannableString? = null
    private var mOpenSuffixStr = DEFAULT_OPEN_SUFFIX
    private var mCloseSuffixStr = DEFAULT_CLOSE_SUFFIX
    private var mOpenSuffixColor: Int = 0
    private var mCloseSuffixColor: Int = 0

    private var mOnClickListener: View.OnClickListener? = null

    private var mCharSequenceToSpannableHandler: CharSequenceToSpannableHandler? = null

    var mOpenCloseCallback: OpenAndCloseCallback? = null

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    /** 初始化  */
    private fun initialize() {
        mCloseSuffixColor = Color.parseColor("#ffbebebe")
        mOpenSuffixColor = mCloseSuffixColor
        movementMethod = OverLinkMovementMethod.getInstance()
        includeFontPadding = false
        updateOpenSuffixSpan()
        updateCloseSuffixSpan()
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    fun setOriginalText(originalText: CharSequence) {
        this.originalText = originalText

        mExpandable = false
        mCloseSpannableStr = SpannableStringBuilder()
        val maxLines = mMaxLines
        val tempText = charSequenceToSpannable(originalText)
        mOpenSpannableStr = charSequenceToSpannable(originalText)

        if (maxLines != -1) {
            val layout = createStaticLayout(tempText)
            mExpandable = layout.lineCount > maxLines
            if (mExpandable) {
                //拼接展开内容
                if (mCloseInNewLine) {
                    mOpenSpannableStr!!.append("\n")
                }
                if (mCloseSuffixSpan != null) {
                    mOpenSpannableStr!!.append(mCloseSuffixSpan)
                }
                //计算原文截取位置
                val endPos = layout.getLineEnd(maxLines - 1)
                if (originalText.length <= endPos) {
                    mCloseSpannableStr = charSequenceToSpannable(originalText)
                } else {
                    mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, endPos))
                }
                var tempText2 = charSequenceToSpannable(mCloseSpannableStr!!).append(ELLIPSIS_STRING)
                if (mOpenSuffixSpan != null) {
                    tempText2.append(mOpenSuffixSpan)
                }
                //循环判断，收起内容添加展开后缀后的内容
                var tempLayout = createStaticLayout(tempText2)
                while (tempLayout.lineCount > maxLines) {
                    val lastSpace = mCloseSpannableStr!!.length - 1
                    if (lastSpace == -1) {
                        break
                    }
                    if (originalText.length <= lastSpace) {
                        mCloseSpannableStr = charSequenceToSpannable(originalText)
                    } else {
                        mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, lastSpace))
                    }
                    tempText2 = charSequenceToSpannable(mCloseSpannableStr!!).append(ELLIPSIS_STRING)
                    if (mOpenSuffixSpan != null) {
                        tempText2.append(mOpenSuffixSpan)
                    }
                    tempLayout = createStaticLayout(tempText2)

                }
                var lastSpace = mCloseSpannableStr!!.length - mOpenSuffixSpan!!.length
                if (lastSpace >= 0 && originalText.length > lastSpace) {
                    val redundantChar = originalText.subSequence(lastSpace, lastSpace + mOpenSuffixSpan!!.length)
                    val offset = hasEnCharCount(redundantChar) - hasEnCharCount(mOpenSuffixSpan!!) + 1
                    lastSpace = if (offset <= 0) lastSpace else lastSpace - offset
                    mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, lastSpace))
                }
                //计算收起的文本高度
                mCLoseHeight = tempLayout.height + paddingTop + paddingBottom
                layoutParams.height =mCLoseHeight
                mCloseSpannableStr!!.append(ELLIPSIS_STRING)
                if (mOpenSuffixSpan != null) {
                    mCloseSpannableStr!!.append(mOpenSuffixSpan)
                }
            }else{
                layoutParams.height =ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }else{
            layoutParams.height =ViewGroup.LayoutParams.WRAP_CONTENT
        }
        isClosed = mExpandable
        if (mExpandable) {
            text = mCloseSpannableStr
            //设置监听
            super.setOnClickListener {
//                Log.i("httpTextview", "exPclick" )
                //                    switchOpenClose();
                //                    if (mOnClickListener != null) {
                //                        mOnClickListener.onClick(v);
                //                    }
            }
        } else {
            text = mOpenSpannableStr
        }
    }

    private fun hasEnCharCount(str: CharSequence): Int {
        var count = 0
        if (!TextUtils.isEmpty(str)) {
            for (i in 0 until str.length) {
                val c = str[i]
                if (c >= ' ' && c <= '~') {
                    count++
                }
            }
        }
        return count
    }

    private fun switchOpenClose() {
        if (mExpandable) {
            isClosed = !isClosed
            if (isClosed) {
                close()
            } else {
                open()
            }
        }
    }

    /**
     * 设置是否有动画
     *
     * @param hasAnimation
     */
    fun setHasAnimation(hasAnimation: Boolean) {
        this.hasAnimation = hasAnimation
    }

    /** 展开  */
    private fun open() {
        if (hasAnimation) {
            val layout = createStaticLayout(mOpenSpannableStr)
            mOpenHeight = layout.height + paddingTop + paddingBottom
            executeOpenAnim()
        } else {
            super@ExpandableTextView.setMaxLines(Integer.MAX_VALUE)
            text = mOpenSpannableStr
            if (mOpenCloseCallback != null) {
                mOpenCloseCallback!!.onOpen()
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
    }

    /** 收起  */
    private fun close() {
        if (hasAnimation) {
            executeCloseAnim()
        } else {
            super@ExpandableTextView.setMaxLines(mMaxLines)
            text = mCloseSpannableStr
            if (mOpenCloseCallback != null) {
                mOpenCloseCallback!!.onClose()
            }
        }
    }

    /** 执行展开动画  */
    private fun executeOpenAnim() {
        //创建展开动画
        if (mOpenAnim == null) {
            mOpenAnim = ExpandCollapseAnimation(this, mCLoseHeight, mOpenHeight)
            mOpenAnim!!.fillAfter = true
            mOpenAnim!!.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    super@ExpandableTextView.setMaxLines(Integer.MAX_VALUE)
                    text = mOpenSpannableStr
                }

                override fun onAnimationEnd(animation: Animation) {
                    //  动画结束后textview设置展开的状态
                    layoutParams.height = mOpenHeight
                    requestLayout()
                    animating = false
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }

        if (animating) {
            return
        }
        animating = true
        clearAnimation()
        //  执行动画
        startAnimation(mOpenAnim)
    }

    /** 执行收起动画  */
    private fun executeCloseAnim() {
        //创建收起动画
        if (mCloseAnim == null) {
            mCloseAnim = ExpandCollapseAnimation(this, mOpenHeight, mCLoseHeight)
            mCloseAnim!!.fillAfter = true
            mCloseAnim!!.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    animating = false
                    super@ExpandableTextView.setMaxLines(mMaxLines)
                    text = mCloseSpannableStr
                    layoutParams.height = mCLoseHeight
                    requestLayout()
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }

        if (animating) {
            return
        }
        animating = true
        clearAnimation()
        //  执行动画
        startAnimation(mCloseAnim)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    /**
     * @param spannable
     *
     * @return
     */
    private fun createStaticLayout(spannable: SpannableStringBuilder?): Layout {
        val contentWidth = initWidth - paddingLeft - paddingRight
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val builder = StaticLayout.Builder.obtain(spannable, 0, spannable!!.length, paint, contentWidth)
            builder.setAlignment(Layout.Alignment.ALIGN_NORMAL)
            builder.setIncludePad(includeFontPadding)
            builder.setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
            return builder.build()
        } else return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            StaticLayout(spannable, paint, contentWidth, Layout.Alignment.ALIGN_NORMAL,
                    lineSpacingMultiplier, lineSpacingExtra, includeFontPadding)
        } else {
            StaticLayout(spannable, paint, contentWidth, Layout.Alignment.ALIGN_NORMAL,
                    getFloatField("mSpacingMult", 1f), getFloatField("mSpacingAdd", 0f), includeFontPadding)
        }
    }

    private fun getFloatField(fieldName: String, defaultValue: Float): Float {
        var value = defaultValue
        if (TextUtils.isEmpty(fieldName)) {
            return value
        }
        try {
            // 获取该类的所有属性值域
            val fields = this.javaClass.declaredFields
            for (field in fields) {
                if (TextUtils.equals(fieldName, field.name)) {
                    value = field.getFloat(this)
                    break
                }
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return value
    }


    /**
     * @param charSequence
     *
     * @return
     */
    private fun charSequenceToSpannable(charSequence: CharSequence): SpannableStringBuilder {
        var spannableStringBuilder: SpannableStringBuilder? = null
        if (mCharSequenceToSpannableHandler != null) {
            spannableStringBuilder = mCharSequenceToSpannableHandler!!.charSequenceToSpannable(charSequence)
        }
        if (spannableStringBuilder == null) {
            spannableStringBuilder = SpannableStringBuilder(charSequence)
        }
        return spannableStringBuilder
    }


    /**
     * 初始化TextView的可展示宽度
     *
     * @param width
     */
    fun initWidth(width: Int) {
        initWidth = width
    }

    override fun setMaxLines(maxLines: Int) {
        this.mMaxLines = maxLines
        super.setMaxLines(maxLines)
    }

    /**
     * 设置展开后缀text
     *
     * @param openSuffix
     */
    fun setOpenSuffix(openSuffix: String) {
        mOpenSuffixStr = openSuffix
        updateOpenSuffixSpan()
    }

    /**
     * 设置展开后缀文本颜色
     *
     * @param openSuffixColor
     */
    fun setOpenSuffixColor(@ColorInt openSuffixColor: Int) {
        mOpenSuffixColor = openSuffixColor
        updateOpenSuffixSpan()
    }

    /**
     * 设置收起后缀text
     *
     * @param closeSuffix
     */
    fun setCloseSuffix(closeSuffix: String) {
        mCloseSuffixStr = closeSuffix
        updateCloseSuffixSpan()
    }

    /**
     * 设置收起后缀文本颜色
     *
     * @param closeSuffixColor
     */
    fun setCloseSuffixColor(@ColorInt closeSuffixColor: Int) {
        mCloseSuffixColor = closeSuffixColor
        updateCloseSuffixSpan()
    }

    /**
     * 收起后缀是否另起一行
     *
     * @param closeInNewLine
     */
    fun setCloseInNewLine(closeInNewLine: Boolean) {
        mCloseInNewLine = closeInNewLine
        updateCloseSuffixSpan()
    }

    /** 更新展开后缀Spannable  */
    private fun updateOpenSuffixSpan() {
        if (TextUtils.isEmpty(mOpenSuffixStr)) {
            mOpenSuffixSpan = null
            return
        }
        mOpenSuffixSpan = SpannableString(mOpenSuffixStr)

        val span = ImageSpan(context, R.mipmap.icon_tv_loadmore, ALIGN_BASELINE)
        mOpenSuffixSpan!!.setSpan(span, mOpenSuffixStr.length - 1, mOpenSuffixStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//        mOpenSuffixSpan!!.setSpan( RelativeSizeSpan(0.5f), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        mOpenSuffixSpan!!.setSpan(StyleSpan(android.graphics.Typeface.NORMAL), 0, mOpenSuffixStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mOpenSuffixSpan!!.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                switchOpenClose()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = mOpenSuffixColor
                ds.isUnderlineText = false
            }
        }, 0, mOpenSuffixStr.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
    }

    /** 更新收起后缀Spannable  */
    private fun updateCloseSuffixSpan() {
        if (TextUtils.isEmpty(mCloseSuffixStr)) {
            mCloseSuffixSpan = null
            return
        }
        mCloseSuffixSpan = SpannableString(mCloseSuffixStr)
        val span = ImageSpan(context, R.mipmap.icon_tv_packup, ALIGN_BASELINE)
        mCloseSuffixSpan!!.setSpan(span, mCloseSuffixStr.length - 1, mCloseSuffixStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        //设置字体大小（相对值,单位：像素） 参数表示为默认字体大小的多少倍   ,0.5表示一半
//        mCloseSuffixSpan!!.setSpan( RelativeSizeSpan(0.5f), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        mCloseSuffixSpan!!.setSpan(StyleSpan(android.graphics.Typeface.NORMAL), 0, mCloseSuffixStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (mCloseInNewLine) {
            val alignmentSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)
            mCloseSuffixSpan!!.setSpan(alignmentSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        mCloseSuffixSpan!!.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                switchOpenClose()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = mCloseSuffixColor
                ds.isUnderlineText = false
            }
        }, 1, mCloseSuffixStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun setOnClickListener(onClickListener: View.OnClickListener?) {
        mOnClickListener = onClickListener
    }

    fun setOpenAndCloseCallback(callback: OpenAndCloseCallback) {
        this.mOpenCloseCallback = callback
    }

    interface OpenAndCloseCallback {
        fun onOpen()
        fun onClose()
    }

    /**
     * 设置文本内容处理
     *
     * @param handler
     */
    fun setCharSequenceToSpannableHandler(handler: CharSequenceToSpannableHandler) {
        mCharSequenceToSpannableHandler = handler
    }

    interface CharSequenceToSpannableHandler {
        fun charSequenceToSpannable(charSequence: CharSequence): SpannableStringBuilder
    }

    internal inner class ExpandCollapseAnimation(private val mTargetView: View//动画执行view
                                                 , private val mStartHeight: Int//动画执行的开始高度
                                                 , private val mEndHeight: Int//动画结束后的高度
    ) : Animation() {

        init {
            duration = 400
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            mTargetView.scrollY = 0
            //计算出每次应该显示的高度,改变执行view的高度，实现动画
            mTargetView.layoutParams.height = ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight).toInt()
            mTargetView.requestLayout()
        }
    }

    companion object {
        private val TAG = ExpandableTextView::class.java.simpleName

        val ELLIPSIS_STRING = String(charArrayOf('\u2026'))
        private val DEFAULT_MAX_LINE = 3
        private val DEFAULT_OPEN_SUFFIX = " 展开  "
        private val DEFAULT_CLOSE_SUFFIX = " 收起  "
    }
}