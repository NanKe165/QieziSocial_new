package com.eggplant.qiezisocial.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.utils.ScreenUtil

/**
 * Created by Administrator on 2020/6/29.
 */

class WordView : FrameLayout {
    var maxWidth = ScreenUtil.getDisplayWidthPixels(context) - resources.getDimension(R.dimen.qb_px_40).toInt()
    var wordPadding = resources.getDimension(R.dimen.qb_px_20).toInt()
    var labelTBPadding = resources.getDimension(R.dimen.qb_px_7).toInt()
    var labelLRPadding = resources.getDimension(R.dimen.qb_px_17).toInt()
    var labelMargin = resources.getDimension(R.dimen.qb_px_10).toInt()

    //所有的labelView
    var labelList = ArrayList<QzTextView>()
    //每行所盛放的labelview
    var lineViews = ArrayList<QzTextView>()
    //每行的view的总宽度
    var currentLineWidth = 0
    //当前行的TopMargin
    var currentTopMargin = labelMargin

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    init {
        setPadding(wordPadding, 0, labelMargin, wordPadding)
    }

    fun setData(data: List<String>) {
        data?.map {
            var labelView = createLabelView(it)
            labelList.add(labelView)
        }
        labelList?.map {
            setLabelLayoutParams(it)
            addView(it)
        }

    }

    /**
     * 创建labelView
     */
    private fun createLabelView(data: String): QzTextView {
        var labelView = QzTextView(context)
        labelView.text = data
        labelView.setPadding(labelLRPadding, labelTBPadding, labelLRPadding, labelTBPadding)
        labelView.background = ContextCompat.getDrawable(context, R.drawable.login_label_unselect)
        labelView.setTextColor(ContextCompat.getColor(context, R.color.tv_black2))
        labelView.textSize = 15F
        labelView.setOnClickListener { v ->
            labelClickListener?.invoke(v)
        }
        return labelView
    }

    /**
     * 设置labelView的params
     */
    private fun setLabelLayoutParams(label: QzTextView) {
        var params = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        var leftMargin = currentLineWidth
        label.measure(0, 0)
        Log.i("wordView", " currentLineWidth:${currentLineWidth} labelWidth:${label.measuredWidth + label.paddingLeft + label.paddingRight + labelMargin} maxWidth=$maxWidth")
        if ((currentLineWidth + label.measuredWidth + labelMargin) <= maxWidth) {
            currentLineWidth += label.measuredWidth + labelMargin
            lineViews.add(label)
        } else {
            leftMargin = 0
            resetLastLineViewsParams()
            currentTopMargin += labelMargin + label.measuredHeight
            currentLineWidth = label.measuredWidth + labelMargin
            lineViews.add(label)
        }
//        Log.i("wordView", "${label.text}  leftMargin:$leftMargin   topMargin:$currentTopMargin")
        params.setMargins(leftMargin, currentTopMargin, 0, 0)
        label.layoutParams = params
    }

    /**
     * 重新设置 当前行中的每个labeview的params 使其左右对其
     */
    private fun resetLastLineViewsParams() {
        var viewWidth = labelMargin
        lineViews.forEach {
            it.measure(0, 0)
            viewWidth += it.measuredWidth
        }
        var size = if (lineViews.size - 1 <= 0) {
            1
        } else {
            lineViews.size - 1
        }
        var newLabelMargin = (maxWidth - viewWidth) / size
        var newLeftMargin = 0
        lineViews.forEachIndexed { index, qzTextView ->
            var params = qzTextView.layoutParams as FrameLayout.LayoutParams
            if (index != lineViews.size - 1) {
                params.setMargins(newLeftMargin, params.topMargin, 0, 0)
                newLeftMargin += newLabelMargin + qzTextView.measuredWidth
            } else {
                newLeftMargin - newLabelMargin + labelMargin + qzTextView.measuredWidth
                params.setMargins(newLeftMargin, params.topMargin, 0, 0)
            }

            qzTextView.layoutParams = params
        }

        lineViews.clear()
    }

    var labelClickListener: ((View) -> Unit)? = null
    fun setOnLabelClickListener(listener: (View) -> Unit) {
        this.labelClickListener = listener
    }
}
