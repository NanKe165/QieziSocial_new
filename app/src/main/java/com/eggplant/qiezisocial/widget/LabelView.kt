package com.eggplant.qiezisocial.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.utils.ScreenUtil

/**
 * Created by Administrator on 2020/5/11.
 * 他人空间-兴趣标签
 */

class LabelView : LinearLayout {
    var labelBgs = ArrayList<Int>()
    var labelLines = 1
    var maxLabelLines = 2
    var labelBgPosition = 0
    var lastLinearView: LinearLayout = LinearLayout(context)
    var maxWidth = ScreenUtil.getDisplayWidthPixels(context) - resources.getDimension(R.dimen.qb_px_60).toInt()

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        addView(lastLinearView)
        labelBgs.add(R.drawable.label_yellow_bg)
        labelBgs.add(R.drawable.label_bule_bg)
        labelBgs.add(R.drawable.label_orange_bg)
        labelBgs.add(R.drawable.label_purple_bg)
        labelBgs.add(R.drawable.label_green_bg)
        labelBgs.add(R.drawable.label_pink_bg)
    }

    fun setData(birht: String?, sex: String?, wh: String, data: List<String>?) {
        resetData()

        data?.map {
            if (!TextUtils.isEmpty(it.trim())) {
                var label = getLabelView(it)
                addLabelView(label)
            }
        }
//        data?.map { getLabelView(it) }
//                ?.forEach { addLabelView(it) }
        if (!TextUtils.isEmpty(wh)) {
            var whLabel = getLabelView(wh)
            addLabelView(whLabel)
        }
        var label: QzTextView = getSexLabelView(sex, birht)
        addLabelView(label)
        if (labelLines > maxLabelLines) {
            for (i in 0 until labelLines - maxLabelLines) {
                removeViewAt(2)
            }
        }
    }

    private fun resetData() {
        removeAllViews()
        lastLinearView = LinearLayout(context)
        addView(lastLinearView)
        labelLines = 1
        labelBgPosition = 0
    }


    private fun addLabelView(label: QzTextView) {
        label.measure(0, 0)
        lastLinearView.measure(0, 0)
        var groupWidth = lastLinearView.measuredWidth

//        Log.i("labelView", "maxWidth:$maxWidth  groupWidth:$groupWidth  viewWidth:${label.measuredWidth+label.paddingLeft+label.paddingRight}")
        if (maxWidth != 0 && (groupWidth + label.measuredWidth + label.paddingLeft + label.paddingRight) > maxWidth) {
            lastLinearView = LinearLayout(context)
            lastLinearView.gravity = Gravity.CENTER_HORIZONTAL
            addView(lastLinearView, 0)
            addLabelView(label)
            labelLines++
        } else {
            lastLinearView.gravity = Gravity.CENTER_HORIZONTAL
            lastLinearView.addView(label, 0)
        }

    }

    private fun getLabelView(data: String): QzTextView {
        if (labelBgPosition >= labelBgs.size)
            labelBgPosition = 0
        var tv = QzTextView(context)
        tv.setPadding(resources.getDimension(R.dimen.qb_px_14).toInt(), resources.getDimension(R.dimen.qb_px_5).toInt(), resources.getDimension(R.dimen.qb_px_14).toInt(), resources.getDimension(R.dimen.qb_px_5).toInt())
        tv.setTextColor(ContextCompat.getColor(context, R.color.tv_black))
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        tv.maxLines = 1
        tv.text = data
        tv.setBackgroundResource(labelBgs[0])
        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.rightMargin = resources.getDimension(R.dimen.qb_px_7).toInt()
        params.topMargin = resources.getDimension(R.dimen.qb_px_15).toInt()
        tv.layoutParams = params
        return tv
    }

    private fun getSexLabelView(sex: String?, birth: String?): QzTextView {
        if (labelBgPosition >= labelBgs.size)
            labelBgPosition = 0
        var data = ""
        if (!TextUtils.isEmpty(birth)) {
            data = "${DateUtils.dataToAge(birth)}岁"
        }


        var tv = QzTextView(context)
        tv.setPadding(resources.getDimension(R.dimen.qb_px_14).toInt(), resources.getDimension(R.dimen.qb_px_5).toInt(), resources.getDimension(R.dimen.qb_px_14).toInt(), resources.getDimension(R.dimen.qb_px_5).toInt())
        tv.setTextColor(ContextCompat.getColor(context, R.color.tv_black))
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        tv.maxLines = 1
        tv.text = data
        tv.setBackgroundResource(labelBgs[0])
        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.rightMargin = resources.getDimension(R.dimen.qb_px_7).toInt()
        params.topMargin = resources.getDimension(R.dimen.qb_px_15).toInt()
        tv.layoutParams = params
//        sex?.let {
//            if (it.contains("女")) {
//                tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_girl, 0, 0, 0)
//            } else {
//                tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_boy, 0, 0, 0)
//            }
//            tv.compoundDrawablePadding = 10
//        }
        return tv
    }
}
